import com.google.common.io.Files;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.StoreConfig;

import java.io.Closeable;
import java.io.File;

public class PersistentMap<K, V> {

    private File homeDir;
    private Environment dbEnvironment;
    private EntityStore dbStore;
    private String storeName;
    private Class<K> keyClass;
    private Class<V> valueClass;
    private PrimaryIndex<K, V> primaryIndex;

    public PersistentMap(String storeName, Class<K> keyClass, Class<V> valueClass) {
        try {
            this.homeDir = Files.createTempDir();
            this.storeName = storeName;
            this.keyClass = keyClass;
            this.valueClass = valueClass;
            setup();
        } catch (Exception e) {
            closeQuietly();
        }
    }

    private void setup() throws DatabaseException {
        EnvironmentConfig envConfig = new EnvironmentConfig();
        StoreConfig storeConfig = new StoreConfig();

        envConfig.setReadOnly(false);
        storeConfig.setReadOnly(false);

        envConfig.setAllowCreate(true);
        storeConfig.setAllowCreate(true);

        dbEnvironment = new Environment(homeDir, envConfig);
        dbStore = new EntityStore(dbEnvironment, storeName, storeConfig);
        primaryIndex = dbStore.getPrimaryIndex(keyClass, valueClass);
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
