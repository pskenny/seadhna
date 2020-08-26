package io.github.pskenny.seadhna;

import java.io.File;
import java.io.IOException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import io.github.pskenny.seadhna.io.IOUtils;
import io.github.pskenny.seadhna.rss.*;
import io.github.pskenny.seadhna.ui.TUI;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import com.sun.syndication.feed.synd.*;

/**
 * Give me Youtube videos, just like Séadhna Of House Clam does.
 * 
 * @author Paul Kenny
 */
public class App {
    // Naive XDG path for url file
    public static String URL_FILE = System.getProperty("user.home") + File.separator + ".config/seadhna/urls";

    public App(String file) {
        // Get valid feeds
        ConcurrentHashMap<String, SyndFeed> feeds = initFeeds();
        // Display UI
        HashSet<String> marked = displayUI(feeds);
        // output marked links
        if(file == null) {
            marked.forEach(System.out::println);
        } else {
            boolean success = IOUtils.writeIteratorToFile(marked.iterator(), file);
            if(!success) {
                System.err.println("Could not write to file: \"" + file + "\"");
            }
        }
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

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("seadhna").build()
                .description("Mark YouTube channel video URLs to write out.").version("0.1");
        parser.addArgument("-f", "-file").metavar("FILE").type(String.class).help("File to write URLs to");

        try {
            Namespace res = parser.parseArgs(args);
            // fileArgument is null if no file given
            String fileArgument = res.getString("f");

            new App(fileArgument);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
        }
    }
}
