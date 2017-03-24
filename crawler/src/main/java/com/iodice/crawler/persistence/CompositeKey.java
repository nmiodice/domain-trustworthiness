package com.iodice.crawler.persistence;

import com.sleepycat.persist.model.KeyField;
import com.sleepycat.persist.model.Persistent;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Persistent
class CompositeKey {
    CompositeKey() {
    }

    @KeyField(1)
    Integer first;

    @KeyField(2)
    Integer second;
}