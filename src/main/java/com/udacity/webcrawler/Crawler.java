package com.udacity.webcrawler;

import java.time.Clock;
import java.time.Duration;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.RecursiveAction;
import com.udacity.webcrawler.parser.PageParser;
import com.udacity.webcrawler.parser.PageParserFactory;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ForkJoinPool;
import java.util.regex.Pattern;
import java.time.Instant;
import java.util.ArrayList;

public class Crawler extends RecursiveAction{
    private final Clock clock;
    private final Instant timeout;
    private final int maxDepth;
    private final PageParserFactory factory;
    private final List<Pattern> ignoredUrls;
    private final ConcurrentSkipListSet<String> visited;
    private final String url;
    private final Map<String,Integer> counts;

    public Crawler (
            Clock clock,
            int maxDepth,
            PageParserFactory factory,
            Instant timeout,
            List<Pattern> ignoredUrls,
            ConcurrentSkipListSet<String> visited,
            String url,
            Map<String,Integer> counts) {
        this.clock=clock;
        this.maxDepth = maxDepth;
        this.factory=factory;
        this.timeout=timeout;
        this.ignoredUrls = ignoredUrls;
        this.visited = visited;
        this.url = url;
        this.counts = counts;
    }
    @Override
    protected void compute() {
        if (maxDepth == 0 || clock.instant().isAfter(timeout)) {
            return;
        }
        for (Pattern pattern : ignoredUrls) {
            if (pattern.matcher(url).matches()) {
                return;
            }
        }
        if(!visited.add(url))
        {
            return;
        }
        PageParser.Result result = factory.get(url).parse();
        for (ConcurrentMap.Entry<String,Integer> e : result.getWordCounts().entrySet()) {
            if (counts.containsKey(e.getKey())) {
               counts.put(e.getKey(), e.getValue() + counts.get(e.getKey()));
             } else {
               counts.put(e.getKey(), e.getValue());
             }
        }
        List<Crawler> tasks = new ArrayList<>();
        for (String url : result.getLinks()) {
            tasks.add(new Crawler(clock,maxDepth - 1, factory,timeout, ignoredUrls,visited, url, counts));
        }
        invokeAll(tasks);
    }

}
