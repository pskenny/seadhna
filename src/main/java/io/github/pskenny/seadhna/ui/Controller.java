package io.github.pskenny.seadhna.ui;

import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.sun.syndication.feed.synd.*;

import io.github.pskenny.seadhna.io.IOUtils;
import io.github.pskenny.seadhna.rss.FeedBuilder;

public class Controller {
    private ConcurrentHashMap<String, SyndFeed> feeds;

    public Controller() {
        feeds = new ConcurrentHashMap<>();

        loadFeeds();
    }

    public ConcurrentMap<String, SyndFeed> getFeeds() {
        return feeds;
    }

    public void loadFeeds() {
        HashSet<String> feedUrls = null;
        try {
            feedUrls = IOUtils.pathToLines(IOUtils.URL_FILE);
            // Make feed items from all RSS feed URLs
            feedUrls.parallelStream().forEach(this::loadFeed);
        } catch (IOException ex) {
            System.err.println("Error reading from URLs file: " + IOUtils.URL_FILE);
        }
    }

    public void loadFeed(String url) {
        SyndFeed feed = FeedBuilder.getFeed(url);
        // Only add feeds which URL worked
        if (feed != null)
            feeds.put(url, feed);
    }

}
