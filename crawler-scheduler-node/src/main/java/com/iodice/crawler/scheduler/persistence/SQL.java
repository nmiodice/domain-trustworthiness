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
            return String
                .format("INSERT INTO %s (%s, %s) VALUES (?, 1) ON CONFLICT (%s) DO UPDATE SET %s = %s.count + 1;",
                    TABLE_NAME, DOMAIN_COLUMN, COUNT_COLUMN, DOMAIN_COLUMN, COUNT_COLUMN, TABLE_NAME);
        }

        static String getCount(int numberToCount) {
            StringBuilder stringClause = new StringBuilder();
            for (int i = 0; i < numberToCount; i++) {
                stringClause.append("?,");
            }
            String in = stringClause.toString();
            in = in.substring(0, in.length() - 1);

            return String.format("SELECT %s, %s FROM %s WHERE %s in (%s);", DOMAIN_COLUMN, COUNT_COLUMN, TABLE_NAME,
                DOMAIN_COLUMN, in);
        }

    }

    static class WorkQueue {
        static final String TABLE_NAME = "work_queue";
        static final String PK = "id";
        static final String DOMAIN_COLUMN = "domain";
        static final String URL_COLUMN = "url";

        static String createStatement() {
            return String
                .format("CREATE TABLE IF NOT EXISTS %s (id serial primary key, %s text NOT NULL, %s text NOT NULL);",
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
            sql += "DELETE FROM %s WHERE %s IN ( ";
            sql += "	SELECT * FROM (SELECT DISTINCT ON (%s) %s FROM %s) as tmp order by random() limit %d";
            sql += ") RETURNING *;";
            return String.format(sql, TABLE_NAME, PK, DOMAIN_COLUMN, PK, TABLE_NAME, maxToDequeue);
        }
    }

    static class GraphBase {
        static final String SOURCE_COLUMN = "source";
        static final String DESTINATION_COLUMN = "destination";

        static String create(String tableName) {
            return String
                .format("CREATE TABLE IF NOT EXISTS %s (id serial primary key, %s text NOT NULL, %s text NOT NULL);",
                    tableName, SOURCE_COLUMN, DESTINATION_COLUMN);
        }

        static String insert(String tableName) {
            return String
                .format("INSERT INTO %s (%s, %s) VALUES (?, ?);", tableName, SOURCE_COLUMN, DESTINATION_COLUMN);
        }

        static String containsSourcePartial(String tableName) {
            return String
                .format("SELECT ? as %s, COUNT(*) FROM %s where %s = ?", SOURCE_COLUMN, tableName, SOURCE_COLUMN);
        }
    }

    static class DomainGraph extends GraphBase {
        static final String TABLE_NAME = "domain_graph";

        static String create() {
            return create(TABLE_NAME);
        }

        static String insert() {
            return insert(TABLE_NAME);
        }
    }

    static class UrlGraph extends GraphBase {
        static final String TABLE_NAME = "url_graph";

        static String createSourceIndex() {
            return String.format("CREATE INDEX IF NOT EXISTS source_index ON %s (%s);", TABLE_NAME, SOURCE_COLUMN);
        }

        static String create() {
            return create(TABLE_NAME);
        }

        static String insert() {
            return insert(TABLE_NAME);
        }

        static String outgoingEdgeCount(int count) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < count; i++) {
                if (i != 0) {
                    sb.append(" UNION ");
                }
                sb.append(containsSourcePartial(TABLE_NAME));
            }

            sb.append(";");

            return sb.toString();
        }
    }
}
