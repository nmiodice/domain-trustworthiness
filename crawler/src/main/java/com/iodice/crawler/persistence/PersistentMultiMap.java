package com.iodice.crawler.persistence;

import com.google.common.io.Files;
import com.iodice.crawler.Application;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.Transaction;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.StoreConfig;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.SecondaryKey;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.util.HashSet;
import java.util.Set;


public class PersistentMultiMap {
    private static final Logger logger = LoggerFactory.getLogger(PersistentMultiMap.class);

    private File homeDir;
    private Environment dbEnvironment;
    private EntityStore dbStore;
    private PrimaryIndex<CompositeKey, MultiMapIntPairEntry> primaryIndex;

    public PersistentMultiMap() throws Exception {
        try {
            this.homeDir = Files.createTempDir();
            setup();
        } catch (Exception e) {
            logger.error("error initializing storage engine for multi map: " + e.getMessage(), e);
            throw e;
        }
    }

    private void setup() throws DatabaseException {
        EnvironmentConfig envConfig = new EnvironmentConfig();
        StoreConfig storeConfig = new StoreConfig();

        envConfig.setAllowCreate(true);
        storeConfig.setAllowCreate(true);

        dbEnvironment = new Environment(homeDir, envConfig);
        dbStore = new EntityStore(dbEnvironment, "---", storeConfig);
        primaryIndex = dbStore.getPrimaryIndex(CompositeKey.class, MultiMapIntPairEntry.class);
    }

    public void put(int key, int value) {
        primaryIndex.put(new MultiMapIntPairEntry(key, value));
    }

    public Set<Integer> get(int key) {
        CompositeKey low = new CompositeKey(key, Integer.MIN_VALUE);
        CompositeKey high = new CompositeKey(key, Integer.MAX_VALUE);

        Set<Integer> values = new HashSet<>();
        try (EntityCursor<MultiMapIntPairEntry> entries = primaryIndex.entities(low, true, high, true)) {
            for (MultiMapIntPairEntry e : entries) {
                values.add(e.getValue());
            }
        }

        return values;
    }

    public Set<Integer> keys() {
        Set<Integer> values = new HashSet<>();
        try (EntityCursor<MultiMapIntPairEntry> entries = primaryIndex.entities()) {
            for (MultiMapIntPairEntry e : entries) {
                values.add(e.getKey());
            }
        }
        return values;
    }

    public int size() {
        return keys().size();
    }


    public void closeQuietly() {
        closeQuietly(dbStore);
        closeQuietly(dbEnvironment);
        if (!homeDir.delete()) {
            System.out.println("error deleting blah");
        }
    }

    private void closeQuietly(Closeable c) {
        try {
            c.close();
        } catch (Exception e) {
            System.out.println("blah");
        }
    }
}

