package com.iodice.crawler.scheduler.persistence;

import com.iodice.crawler.scheduler.utils.URLFacade;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class PostgresBackedPersistenceAdaptor implements PersistenceAdaptor {
    private static final Logger logger = LoggerFactory.getLogger(PostgresBackedPersistenceAdaptor.class);
    private PostgresDBFacade dbFacade = new PostgresDBFacade();

    PostgresBackedPersistenceAdaptor() {
        try {
            // initialize domain count table
            dbFacade.preparedStatement(SQL.DomainCount.createStatement()).executeUpdate();

            // initialize work queue table
            dbFacade.preparedStatement(SQL.WorkQueue.createStatement()).executeUpdate();
            dbFacade.preparedStatement(SQL.WorkQueue.createDomainIndex()).executeUpdate();

            // initialize graph tables
            dbFacade.preparedStatement(SQL.DomainGraph.create()).executeUpdate();
            dbFacade.preparedStatement(SQL.UrlGraph.create()).executeUpdate();


        } catch (SQLException e) {
            throw new RuntimeException("error initializing DB: " + e.getMessage(), e);
        }
    }

    @Override
    @SneakyThrows
    public void storeDomainEdges(String source, Collection<String> destinations) {
        PreparedStatement statement = toStatement(SQL.DomainGraph.insert());
        for (String destination : destinations) {
            statement.setString(1, source);
            statement.setString(2, destination);
            statement.addBatch();
        }

        statement.executeBatch();
    }

    @Override
    @SneakyThrows
    public void storeURLEdges(String source, Collection<String> destinations) {
        PreparedStatement statement = toStatement(SQL.UrlGraph.insert());
        for (String destination : destinations) {
            statement.setString(1, source);
            statement.setString(2, destination);
            statement.addBatch();
        }

        statement.executeBatch();
    }

    @Override
    @SneakyThrows
    public Map<String, Boolean> isInEdgeGraph(Collection<String> urls) {

        Map<String, Boolean> seen = new HashMap<>();
        ResultSet rs;

        for (String url : urls) {
            try {
                PreparedStatement statement = toStatement(SQL.UrlGraph.outgoingEdgeCount());
                statement.setString(1, url);
                rs = statement.executeQuery();

                // this should always return a row even if the source was not found
                rs.next();
                seen.put(url, rs.getInt(1) > 0);

            } catch (Exception e) {
                logger.error("Unable to determine if edge is in graph: " + e.getMessage(), e);
                seen.put(url, false);
            }
        }

        return seen;
    }

    @Override
    @SneakyThrows
    public void enqueueURLs(Collection<String> urls) {
        Map<String, String> urlToDomainMap = urls.stream()
            .map(url -> new AbstractMap.SimpleEntry<>(url, URLFacade.toDomain(url)))
            .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

        incrementDomainScheduledCountBatch(urlToDomainMap);
        enqueueURLsBatch(urlToDomainMap);
    }

    private void enqueueURLsBatch(Map<String, String> urlToDomainMap) throws SQLException {
        PreparedStatement statement = toStatement(SQL.WorkQueue.insertStatement());
        for (String url : urlToDomainMap.keySet()) {
            statement.setString(1, urlToDomainMap.get(url));
            statement.setString(2, url);
            statement.addBatch();
        }
        statement.executeBatch();
    }

    private void incrementDomainScheduledCountBatch(Map<String, String> urlToDomainMap) throws SQLException {
        PreparedStatement statement = toStatement(SQL.DomainCount.incrementCount());
        for (String url : urlToDomainMap.keySet()) {
            statement.setString(1, urlToDomainMap.get(url));
            statement.addBatch();
        }
        statement.executeBatch();
    }

    @Override
    @SneakyThrows
    public List<String> dequeueURLs(int count) {
        long a = System.currentTimeMillis();
        PreparedStatement statement = toStatement(SQL.WorkQueue.dequeueDomainsStatement(count));
        long b = System.currentTimeMillis();
        ResultSet rs = statement.executeQuery();
        long c = System.currentTimeMillis();

        List<String> urls = new ArrayList<>();
        while (rs.next()) {
            urls.add(rs.getString(SQL.WorkQueue.URL_COLUMN));
        }
        long d = System.currentTimeMillis();

        System.out.println("" + (b - a) + ", " + (c - b) + ", " + (d - c));
        return urls;
    }

    @Override
    @SneakyThrows
    public int getDomainScheduledCount(String domain) {
        PreparedStatement statement = toStatement(SQL.DomainCount.getCount());
        statement.setString(1, domain);
        ResultSet rs = statement.executeQuery();
        if (!rs.next()) {
            return 0;
        }
        return rs.getInt(1);
    }

    private PreparedStatement toStatement(String sql) throws SQLException {
        return dbFacade.preparedStatement(sql);
    }
}
