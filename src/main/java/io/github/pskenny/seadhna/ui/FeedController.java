package io.github.pskenny.seadhna.ui;

import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.rometools.rome.feed.synd.*;

import io.github.pskenny.seadhna.io.IOUtils;
import io.github.pskenny.seadhna.rss.FeedBuilder;

public class FeedController {
    private ConcurrentHashMap<String, SyndFeed> feeds;

    public FeedController() {
        feeds = new ConcurrentHashMap<>();

        loadUrls();
    }

    public void loadUrls() {
        HashSet<String> feedUrls = null;
        try {
            feedUrls = IOUtils.pathToLines(IOUtils.URL_FILE);
            // Make feed items from all RSS feed URLs
            feedUrls.stream().forEach(url -> {
                feeds.put(url, null);
            });
        } catch (IOException ex) {
            System.err.println("Error reading from URLs file: " + IOUtils.URL_FILE);
        }
    }

    public ConcurrentMap<String, SyndFeed> getFeeds() {
        return feeds;
    }

    public void loadFeeds() {
        feeds.forEach((url, feed) -> {

        });;
    }

    public void loadFeed(String url) {
        SyndFeed feed = FeedBuilder.getFeed(url);
        // Only add feeds which URL worked
        if (feed != null)
            feeds.put(url, feed);
    }

}
