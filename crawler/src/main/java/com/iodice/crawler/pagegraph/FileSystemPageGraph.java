package com.iodice.crawler.pagegraph;

import com.google.common.io.Files;
import lombok.SneakyThrows;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class FileSystemPageGraph extends BasePageGraph {
    private Path homePath = Files.createTempDir().toPath();

    @Override
    @SneakyThrows
    public void add(String sourceDomain, String destinationDomain) {
        Integer srcID = pageGraphUtil.toPageID(sourceDomain);
        Integer dstID = pageGraphUtil.toPageID(destinationDomain);

        homePath.resolve(String.valueOf(srcID)).toFile().mkdir();
        homePath.resolve(String.valueOf(srcID)).resolve(String.valueOf(dstID)).toFile().createNewFile();

        homePath.resolve(String.valueOf(dstID)).toFile().mkdir();
        homePath.resolve(String.valueOf(dstID)).resolve(String.valueOf(srcID)).toFile().createNewFile();
    }

    @Override
    public int size() {
        return getPageIDs().size();
    }

    @Override
    public Set<Integer> getPageIDs() {
        return fileListToSet(homePath);
    }

    private Set<Integer> fileListToSet(Path path) {
        String[] files = path.toFile().list();
        if (files == null) {
            return Collections.emptySet();
        }
        return Arrays.stream(files).map(Integer::valueOf).collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public Set<Integer> getOutboundLinks(Integer pageID) {
        return fileListToSet(homePath.resolve(String.valueOf(pageID)));
    }

    @Override
    public void addReverseDanglingPageLinks() {
    }
}
