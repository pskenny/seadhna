package io.github.pskenny.seadhna;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.*;

import io.github.pskenny.seadhna.io.IOUtils;
import io.github.pskenny.seadhna.rss.*;

import com.sun.syndication.feed.synd.*;

/**
 * Give me Youtube videos, just like Séadhna Of House Clam does.
 * 
 * @author Paul Kenny
 */
public class App {
    // Naive XDG path for url file
    public static String URL_FILE = System.getProperty("user.home") + File.separator + ".config/seadhna/urls";

    public App() {
        // Get valid feeds
        Hashtable<String, SyndFeed> feeds = initFeeds();
        // Display UI
        HashSet<String> marked = displayUI(feeds);
        // Write marked feed item links to file
        //IOUtils.writeIteratorToFile(marked.iterator(), "path/to/output");
    }

    /**
     * Load feed urls if available
     */
    private Hashtable<String, SyndFeed> initFeeds() {
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
    public HashSet<String> displayUI(Hashtable<String, SyndFeed> feeds) {
        HashSet<String> marked = new HashSet<String>();

        try {
            io.github.pskenny.seadhna.ui.TUI tui = new io.github.pskenny.seadhna.ui.TUI(feeds);
            marked = tui.getMarked();
        } catch (Exception ex) {
            System.err.println("Couldn't run UI \n" + ex.getLocalizedMessage());
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
    private Hashtable<String, SyndFeed> loadFeeds(HashSet<String> feedUrls) {
        Hashtable<String, SyndFeed> f = new Hashtable<String, SyndFeed>();

        // loop URLs and make feeds
        for (String url : feedUrls) {
            SyndFeed feed = FeedBuilder.getFeed(url);
            // Only add feeds which URL worked
            if (feed != null)
                f.put(url, feed);
        }

        return f;
    }

    public static void main(String[] args) {
        System.out.println("URL file: " + URL_FILE);
        new App();
    }
}
