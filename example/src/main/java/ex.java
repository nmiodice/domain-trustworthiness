import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.KeyField;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.SecondaryKey;


import java.util.SortedSet;

import static com.sleepycat.persist.model.Relationship.MANY_TO_ONE;

/**
 * Created by nickio on 3/14/17.
 */
public class ex {
//https://docs.oracle.com/cd/E17277_02/html/GettingStartedGuide/mydbenv-persist.html
    @Entity
    private static class ToStore {
        public ToStore(int a, int b) {
            this.a = a;
            this.b = b;
        }
        @KeyField(1)
        private int a;

        @KeyField(2)
        private int b;
    }

    public static void main(String[] args) {
    }
}
