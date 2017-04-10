package com.iodice.crawler.pagerank;

import com.google.common.collect.Lists;
import com.iodice.crawler.pagegraph.PageGraph;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class FilePageRankCalculator extends BasePageRankCalculator {
    private static final Logger logger = LoggerFactory.getLogger(FilePageRankCalculator.class);

    private static final String GRAPH_FOLDER = "graph/";
    private static final int PARTITION_SIZE = 500;
    private Path base;
    private List<List<Integer>> partitions;

    FilePageRankCalculator(Path baseDirectory) {
        Validate.isTrue(baseDirectory.toFile()
            .exists(), "base directory does not exist!");
        Validate.isTrue(baseDirectory.toFile()
            .isDirectory(), "base directory is not a directory!");
        base = baseDirectory;

        logger.info("initializing page rank calculator");
        initFileSystem();
    }

    private void initFileSystem() {
        FileUtils.deleteQuietly(base.toFile());
        base.resolve(GRAPH_FOLDER)
            .toFile()
            .mkdirs();
    }

    private void initPartitionRanges(PageGraph graph) {
        partitions = Lists.partition(new ArrayList<>(graph.getPageIDs()), PARTITION_SIZE);
        logger.info(String.format("found %d partitions for graph", partitions.size()));
    }

    @Override
    @SneakyThrows
    void init(PageGraph graph) {
        long start;

        start = System.currentTimeMillis();
        initFileSystem();
        printTime(start, System.currentTimeMillis(), "init file system");

        start = System.currentTimeMillis();
        initPartitionRanges(graph);
        printTime(start, System.currentTimeMillis(), "init partition ranges");

        start = System.currentTimeMillis();
        serializeGraph(graph);
        printTime(start, System.currentTimeMillis(), "serialize graph");
    }

    private void printTime (long start, long end, String name) {
        String timeFmt = String.format("%.2f", (end - start) / 1000.0);
        logger.info("graph init: %-20s %-20s\n", name, timeFmt);
    }

    @Override
    @SneakyThrows
    PageRank singPageRankIteration(PageRank oldRank, PageGraph graph) {
        long start = System.currentTimeMillis();
        PageRank newRank = new PageRank();
        runTasks(IntStream.range(0, partitions.size())
            .mapToObj(partitionID -> new PageRankWorker(oldRank, newRank, partitionID))
            .collect(Collectors.toSet()), 10);
        printTime(start, System.currentTimeMillis(), "single iteration");
        return newRank;
    }

    private void serializeGraph(PageGraph graph) throws InterruptedException {
        runTasks(IntStream.range(0, partitions.size())
            .mapToObj(partitionID -> new GraphSerializerWorker(graph, partitionID))
            .collect(Collectors.toSet()), 32);
    }

    /**
     * @param tasks       A collection of tasks to be completed
     * @param concurrency number of threads used to parallelize the tasks
     * @throws InterruptedException
     */
    private <T> void runTasks(Collection<? extends Callable<T>> tasks, int concurrency) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(concurrency);
        executor.invokeAll(tasks);
        executor.shutdown();
        executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
    }

    @Override
    public void cleanup() {
        FileUtils.deleteQuietly(base.toFile());
    }

    private File getFile(int pageID) {
        return base.resolve(GRAPH_FOLDER)
            .resolve(Integer.toString(pageID))
            .toFile();
    }

    @AllArgsConstructor
    class PageRankWorker implements Callable<Void> {
        private final PageRank oldRank;
        private final PageRank newRank;
        private final int partitionID;

        @Override
        public Void call() throws Exception {
            try (FileReader fReader = new FileReader(getFile(partitionID));
                BufferedReader bReader = new BufferedReader(fReader)) {

                PageRank localRank = new PageRank();

                String line;
                while ((line = bReader.readLine()) != null) {
                    String[] colonSplit = line.split(":");
                    String[] linkSplit = colonSplit[1].split(",");

                    int pageID = Integer.valueOf(colonSplit[0]);
                    // this ensures that the pageID always shows up in the next round
                    localRank.addRank(pageID, 0.0);

                    for (String link : linkSplit) {
                        double perPageAdditionalRank = oldRank.getRank(pageID) / (double) linkSplit.length;
                        localRank.addRank(Integer.valueOf(link), perPageAdditionalRank);
                    }

                }

                addToGlobalRank(localRank);
            }
            return null;
        }

        private void addToGlobalRank(PageRank rank) {
            synchronized (newRank) {
                newRank.add(rank);
            }
        }
    }

    @AllArgsConstructor
    class GraphSerializerWorker implements Callable<Void> {
        private PageGraph graph;
        private int partitionID;

        @Override
        @SneakyThrows
        public Void call() {
            try (FileWriter fWriter = new FileWriter(getFile(partitionID));
                BufferedWriter bWriter = new BufferedWriter(fWriter)) {

                for (Integer pageID : partitions.get(partitionID)) {
                    String content = graph.getOutboundLinks(pageID)
                        .stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(","));

                    // handle dangling links by adding a self link when necessary
                    content = content.equals("") ? Integer.toString(pageID) : content;

                    content = String.format("%s:%s", pageID.toString(), content);
                    bWriter.write(content);
                    bWriter.newLine();
                }
            }
            return null;
        }
    }
}
