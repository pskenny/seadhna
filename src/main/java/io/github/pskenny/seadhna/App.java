package io.github.pskenny.seadhna;

import java.io.File;
import java.io.IOException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import io.github.pskenny.seadhna.io.IOUtils;
import io.github.pskenny.seadhna.rss.*;
import io.github.pskenny.seadhna.ui.TUI;

import com.sun.syndication.feed.synd.*;

/**
 * Give me Youtube videos, just like Séadhna Of House Clam does.
 * 
 * @author Paul Kenny
 */
public class App {
    // Naive XDG path for url file
    public static String URL_FILE = System.getProperty("user.home") + File.separator + ".config/seadhna/urls";
    private enum OUTPUT_MODE {FILE, STDOUT};


    public App() {
        // Get valid feeds
        ConcurrentHashMap<String, SyndFeed> feeds = initFeeds();
        // Display UI
        HashSet<String> marked = displayUI(feeds);
        // output marked links
        writeMarkedLinksToOutput(marked, OUTPUT_MODE.STDOUT);
    }

    /**
     * Load feed urls if available
     */
    private ConcurrentHashMap<String, SyndFeed> initFeeds() {
        HashSet<String> feedUrls = null;
        try {
            feedUrls = IOUtils.pathToLines(URL_FILE);
        } catch (IOException ex) {
            System.err.println("Error reading from URLs file: " + URL_FILE);
            // If you can't get the feeds from file it's a critical problem
            System.exit(1);
        }
        return loadFeeds(feedUrls);
    }

    /**
     * Display feeds in UI
     */
    public HashSet<String> displayUI(ConcurrentHashMap<String, SyndFeed> feeds) {
        HashSet<String> marked = new HashSet<String>();

        try {
            TUI tui = new TUI(feeds);
            Thread ui = new Thread(tui);
            ui.start();
            // Wait for ui to finish before continuing
            ui.join();
            marked = tui.getMarkedLinks();
        } catch (InterruptedException ex) {
            System.err.println("Error during UI: " + ex.getMessage());
        }

        return marked;
    }

    /**
     * Load feed information from URL(s). Only returns elements for valid URLs and
     * valid RSS linked in URLs.
     * 
     * @param feedUrls Set of URLs to retrieve RSS information from.
     * @return HashTable with URL and feed information.
     */
    private ConcurrentHashMap<String, SyndFeed> loadFeeds(HashSet<String> feedUrls) {
        ConcurrentHashMap<String, SyndFeed> feeds = new ConcurrentHashMap<String, SyndFeed>();

        Stream<String> s = feedUrls.parallelStream();
        s.forEach((url) -> {
            SyndFeed feed = FeedBuilder.getFeed(url);
            // Only add feeds which URL worked
            if (feed != null)
                feeds.put(url, feed);
        });

        return feeds;
    }

    private void writeMarkedLinksToOutput(HashSet<String> marked, OUTPUT_MODE mode) {
        switch(mode) {
            case FILE: IOUtils.writeIteratorToFile(marked.iterator(), "path/to/output");
            default: marked.forEach(System.out::println);
        };
    }

    public static void main(String[] args) {
        new App();
    }
}
