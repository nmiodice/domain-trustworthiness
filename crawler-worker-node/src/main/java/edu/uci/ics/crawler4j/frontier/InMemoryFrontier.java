/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.uci.ics.crawler4j.frontier;

import edu.uci.ics.crawler4j.url.WebURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class InMemoryFrontier implements Frontier {
    private static final Logger logger = LoggerFactory.getLogger(InMemoryFrontier.class);
    private boolean isShutdown = false;
    private Queue<WebURL> workQueue = new LinkedList<>();

    InMemoryFrontier() {
    }

    @Override
    public List<WebURL> getNextURLs() {
        WebURL u = workQueue.isEmpty() ? null : workQueue.remove();
        if (u == null)
            return Collections.emptyList();

        return Collections.singletonList(u);
    }

    @Override
    public void scheduleAll(Collection<WebURL> destinations, WebURL source) {
        workQueue.addAll(destinations);
    }

    @Override
    public void schedule(WebURL destination, WebURL source) {
        scheduleAll(Collections.singletonList(destination), source);
    }

    @Override
    public boolean isShutdown() {
        return isShutdown;
    }

    @Override
    public void shutdown() {
        isShutdown = true;
    }
}
