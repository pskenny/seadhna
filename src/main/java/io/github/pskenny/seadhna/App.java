package io.github.pskenny.seadhna;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.*;

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
        //writeMarked(marked);
    }

    /**
     * Load feed urls if available
     */
    private Hashtable<String, SyndFeed> initFeeds() {
        HashSet<String> feedUrls = loadFeedUrls(URL_FILE);

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
     * Write marked feed links out to file
     */
    public void writeMarked(HashSet<String> marked) {
        if(marked.isEmpty())
            return;

        String outpath = "/path/to/write";

        // generate appropriate string
        StringBuilder sb = new StringBuilder();
        for (String url : marked) {
            sb.append(url);
            sb.append("\n");
        }

        // write to file
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outpath)));
            // bw.write(sb.toString());
            bw.close();
        } catch (IOException ex) {
            System.err.println("Couldn't write output");
        }
    }

    /**
     * Read URLs from file line-by-line and add to feeds table
     * 
     * @param String path Path to URLs file
     * @return HashSet<String> Set with URLs from path
     */
    private HashSet<String> loadFeedUrls(String path) {
        HashSet<String> urls = new HashSet<String>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line;
            while ((line = br.readLine()) != null)
                // TODO do some checking on URL to make sure it's valid
                if (!line.isEmpty())
                    urls.add(line);
            br.close();
        } catch (IOException ex) {
            // TODO suggest to make folder and empty file or do some diagnosing
            System.err.println("Error reading from URLs file: " + URL_FILE);
            System.exit(1);
        }

        return urls;
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
