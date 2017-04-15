package edu.uci.ics.crawler4j.frontier;

public class FrontierFactory {
    public static Frontier newInMemoryFrontier() {
        return new InMemoryFrontier();
    }
}
