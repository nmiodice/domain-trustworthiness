package com.iodice.crawler.scheduler.persistence;

final class SQL {
    static class DomainCount {
        static final String TABLE_NAME = "domain_scheduled_count";
        static final String DOMAIN_COLUMN = "domain";
        static final String COUNT_COLUMN = "count";

        static String createStatement() {
            return String.format("CREATE TABLE IF NOT EXISTS %s (%s text NOT NULL primary key, %s bigint default 0);",
                TABLE_NAME, DOMAIN_COLUMN, COUNT_COLUMN);
        }

        static String incrementCount() {
            return String.format(
                "INSERT INTO %s (%s, %s) VALUES (?, 1) ON CONFLICT (%s) DO UPDATE SET %s = %s.count + 1;", TABLE_NAME,
                DOMAIN_COLUMN, COUNT_COLUMN, DOMAIN_COLUMN, COUNT_COLUMN, TABLE_NAME);
        }

        static String getCount() {
            return String.format("SELECT %s FROM %s WHERE %s = ?;", COUNT_COLUMN, TABLE_NAME, DOMAIN_COLUMN);
        }
    }

    static class WorkQueue {
        static final String TABLE_NAME = "work_queue";
        static final String PK = "id";
        static final String DOMAIN_COLUMN = "domain";
        static final String URL_COLUMN = "url";

        static String createStatement() {
            return String.format(
                "CREATE TABLE IF NOT EXISTS %s (id serial primary key, %s text NOT NULL, %s text NOT NULL);",
                TABLE_NAME, DOMAIN_COLUMN, URL_COLUMN);
        }

        static String createDomainIndex() {
            return String.format("CREATE INDEX IF NOT EXISTS domain_index ON %s (%s);", TABLE_NAME, DOMAIN_COLUMN);
        }

        static String insertStatement() {
            return String.format("INSERT INTO %s (%s, %s) VALUES (?, ?);", TABLE_NAME, DOMAIN_COLUMN, URL_COLUMN);
        }

        static String dequeueDomainsStatement(int maxToDequeue) {
            String sql = "";
            sql += "DELETE FROM %s WHERE %s IN (\n";
            sql += "    SELECT * FROM (SELECT DISTINCT ON (%s) %s FROM %s ORDER BY %s, random()) as tmp\n";
            sql += "order by random() limit %d) returning *;";

            System.out.println(String.format(sql, TABLE_NAME, PK, DOMAIN_COLUMN, PK, TABLE_NAME, DOMAIN_COLUMN,
                maxToDequeue));
            return String.format(sql, TABLE_NAME, PK, DOMAIN_COLUMN, PK, TABLE_NAME, DOMAIN_COLUMN, maxToDequeue);
        }
    }
}
