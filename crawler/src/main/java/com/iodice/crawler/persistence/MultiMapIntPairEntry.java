package com.iodice.crawler.persistence;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
class MultiMapIntPairEntry {
    @PrimaryKey
    private CompositeKey values;

    MultiMapIntPairEntry() {

    }

    MultiMapIntPairEntry(int key, int val) {
        values = new CompositeKey(key, val);
    }

    Integer getKey() {
        return values.first;
    }

    Integer getValue() {
        return values.second;
    }
}
