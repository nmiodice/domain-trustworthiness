package com.iodice.crawler.pagegraph;

public abstract class BasePageGraph implements PageGraph {
    PageGraphUtil pageGraphUtil = new PageGraphUtil();

    @Override
    public String domainFromPageID(Integer id) {
        return pageGraphUtil.domain(id);
    }
}
