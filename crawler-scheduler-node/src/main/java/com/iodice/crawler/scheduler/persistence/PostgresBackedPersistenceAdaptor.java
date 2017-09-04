package com.iodice.crawler.scheduler.persistence;

import com.iodice.crawler.scheduler.utils.URLFacade;
import lombok.SneakyThrows;
import org.apache.commons.lang3.Validate;
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
            dbFacade.preparedStatement(SQL.UrlGraph.createSourceIndex()).executeUpdate();
            dbFacade.preparedStatement(SQL.UrlGraph.createIdIndex()).executeUpdate();
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
        PreparedStatement statement = toStatement(SQL.UrlGraph.outgoingEdgeCount(urls.size()));
        int i = 1;
        for (String url : urls) {
            statement.setString(i, url);
            statement.setString(i + 1, url);
            i += 2;
        }

        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            seen.put(rs.getString(1), rs.getInt(2) > 0);
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

        // sorting the urls here is necessary because it avoids deadlocks across different db processes.
        List<String> urls = new ArrayList<>(urlToDomainMap.keySet());
        urls.sort(String::compareTo);

        for (String url : urls) {
            statement.setString(1, urlToDomainMap.get(url));
            statement.addBatch();
        }
        statement.executeBatch();
    }

    @Override
    @SneakyThrows
    public List<String> dequeueURLs(int count) {
        PreparedStatement statement = toStatement(SQL.WorkQueue.dequeueDomainsStatement(count));
        long a = System.currentTimeMillis();
        ResultSet rs = statement.executeQuery();
        long b = System.currentTimeMillis();

        logger.info("query took " + (b - a) + " ms");

        List<String> urls = new ArrayList<>();
        while (rs.next()) {
            urls.add(rs.getString(SQL.WorkQueue.URL_COLUMN));
        }
        return urls;
    }

    @Override
    @SneakyThrows
    public Map<String, Integer> getDomainScheduledCount(Collection<String> domains) {
        ResultSet rs = constructDomainScheduleCountQuery(domains).executeQuery();

        Map<String, Integer> counts = new HashMap<>();
        while (rs.next()) {
            counts.put(rs.getString(SQL.DomainCount.DOMAIN_COLUMN), rs.getInt(SQL.DomainCount.COUNT_COLUMN));
        }

        for (String domain : domains) {
            if (!counts.containsKey(domain)) {
                counts.put(domain, 0);
            }
        }

        return counts;
    }

    private PreparedStatement constructDomainScheduleCountQuery(Collection<String> domains) throws SQLException {
        PreparedStatement statement = toStatement(SQL.DomainCount.getCount(domains.size()));
        int idx = 1;
        for (String domain : domains) {
            statement.setString(idx, domain);
            idx++;
        }
        return statement;
    }

    private PreparedStatement toStatement(String sql) throws SQLException {
        return dbFacade.preparedStatement(sql);
    }
}
