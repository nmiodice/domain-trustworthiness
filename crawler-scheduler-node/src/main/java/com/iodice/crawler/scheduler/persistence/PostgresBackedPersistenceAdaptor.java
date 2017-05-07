package com.iodice.crawler.scheduler.persistence;

import com.iodice.crawler.scheduler.utils.URLFacade;
import lombok.SneakyThrows;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class PostgresBackedPersistenceAdaptor implements PersistenceAdaptor {

    static {
        try {
            // initialize domain count table
            PostgresDBFacade.preparedStatement(SQL.DomainCount.createStatement()).executeUpdate();

            // initialize work queue table
            PostgresDBFacade.preparedStatement(SQL.WorkQueue.createStatement()).executeUpdate();
            PostgresDBFacade.preparedStatement(SQL.WorkQueue.createDomainIndex()).executeUpdate();


        } catch (SQLException e) {
            throw new RuntimeException("error initializing DB: " + e.getMessage(), e);
        }
    }

    @Override
    public void storeDomainEdges(String source, Collection<String> destinations) {

    }

    @Override
    public void storeURLEdges(String source, Collection<String> destinations) {

    }

    @Override
    public Map<String, Boolean> isInEdgeGraph(Collection<String> urls) {
        return urls.stream().collect(Collectors.toMap(url -> url, url -> Boolean.FALSE));
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
        PreparedStatement statement = PostgresDBFacade.preparedStatement(SQL.WorkQueue.insertStatement());
        for (String url : urlToDomainMap.keySet()) {
            statement.setString(1, urlToDomainMap.get(url));
            statement.setString(2, url);
            statement.addBatch();
        }
        statement.executeBatch();
    }

    private void incrementDomainScheduledCountBatch(Map<String, String> urlToDomainMap) throws SQLException {
        PreparedStatement statement = PostgresDBFacade.preparedStatement(SQL.DomainCount.incrementCount());
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
        PreparedStatement statement = PostgresDBFacade.preparedStatement(SQL.WorkQueue.dequeueDomainsStatement(count));
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
        PreparedStatement statement = PostgresDBFacade.preparedStatement(SQL.DomainCount.getCount());
        statement.setString(1, domain);
        ResultSet rs = statement.executeQuery();
        if (!rs.next()) {
            return 0;
        }
        return rs.getInt(1);
    }
}
