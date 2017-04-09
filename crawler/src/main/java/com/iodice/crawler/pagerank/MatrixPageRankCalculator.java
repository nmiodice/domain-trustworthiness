package com.iodice.crawler.pagerank;

import com.iodice.crawler.pagegraph.PageGraph;
import com.iodice.crawler.pagegraph.PageGraphFactory;
import org.ujmp.core.DenseMatrix2D;
import org.ujmp.core.Matrix;
import org.ujmp.core.SparseMatrix2D;
import org.ujmp.core.calculation.Calculation;

import java.util.Set;

public class MatrixPageRankCalculator implements PageRankCalculator {

    MatrixPageRankCalculator() {
    }

    @Override
    public PageRank computeMany(PageGraph graph, int iterationCount) {
        PageGraph cached = PageGraphFactory.readOnlyCachedGraph(graph);
        Matrix transitionMatrix = getTransitionMatrix(cached);
        Matrix rankVector = getInitialRankMatrix(cached.size());

        for (int i = 0; i < iterationCount; i++) {
            rankVector = multiply(transitionMatrix, rankVector);
        }

        return vectorToPageRank(rankVector);
    }

    private Matrix multiply(Matrix A, Matrix B) {
        return A.mtimes(Calculation.Ret.NEW, true, B);
    }

    private Matrix getTransitionMatrix(PageGraph graph) {
        int size = graph.size();
        Matrix sparse = SparseMatrix2D.Factory.zeros(size, size);

        for (Integer source : graph.getPageIDs()) {
            Set<Integer> destinations = graph.getOutboundLinks(source);
            double destinationCount = destinations.size();

            if (destinationCount == 0) {
                sparse.setAsDouble(1.0, source, source);
                continue;
            }

            double transitionConstant = 1.0 / destinationCount;
            for (Integer destination : destinations) {
                sparse.setAsDouble(transitionConstant, destination, source);
            }
        }

        return sparse;
    }

    private Matrix getInitialRankMatrix(int size) {
        Matrix rank = DenseMatrix2D.Factory.zeros(size, 1);
        for (int idx = 0; idx < size; idx++) {
            rank.setAsDouble(1.0 / (double) size, idx, 0);
        }
        return rank;
    }

    private PageRank vectorToPageRank(Matrix m) {
        PageRank r = new PageRank();
        for (int idx = 0; idx < m.getSize(0); idx++) {
            r.setRank(idx, m.getAsDouble(idx, 0));
        }

        return r;
    }

    @Override
    public void cleanup() {
    }
}
