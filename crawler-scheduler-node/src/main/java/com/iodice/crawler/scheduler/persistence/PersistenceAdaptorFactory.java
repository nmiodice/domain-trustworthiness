package com.iodice.crawler.scheduler.persistence;

import com.iodice.config.Config;
import org.apache.commons.lang3.Validate;

public final class PersistenceAdaptorFactory {
    private static final String STORAGE_TYPE_MONGO = "mongo";
    private static final String STORAGE_TYPE_POSTGRES = "postgres";

    public static PersistenceAdaptor defaultAdaptor() {
        String configuredType = Config.getString("scheduler.storage_type");
        Validate.notNull(configuredType);

        switch (configuredType) {
        case STORAGE_TYPE_MONGO:
            return mongoBackedAdaptor();
        case STORAGE_TYPE_POSTGRES:
            return postgresBackedAdaptor();
        default:
            throw new IllegalStateException("unknown value for storage_type: " + configuredType);
        }
    }

    public static PersistenceAdaptor mongoBackedAdaptor() {
        return new MongoBackedPersistenceAdaptor();
    }

    public static PersistenceAdaptor postgresBackedAdaptor() {
        return new PostgresBackedPersistenceAdaptor();
    }
}
