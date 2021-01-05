package io.github.pskenny.seadhna.feed;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;

import io.github.pskenny.seadhna.io.IOUtils;
import io.github.pskenny.seadhna.rss.FeedBuilder;

public class Repository {
    private static Repository repo;

    private Repository() {
    }

    public static Repository getRepository() {
        if (repo == null)
            repo = new Repository();

        return repo;
    }

    public HashSet<Feed> loadFeeds() {
        HashSet<Feed> feeds = new HashSet<>();

        HashSet<String> feedUrls = null;
        try {
            feedUrls = IOUtils.pathToLines(IOUtils.URL_FILE);
            // Make feed items from all RSS feed URLs
            feedUrls.parallelStream().forEach(url -> {
                SyndFeed syndFeed = FeedBuilder.getFeed(url);
                // Only add feeds which URL worked
                if (syndFeed != null) {
                    syndFeed.getTitle();
                    List<SyndEntry> f = syndFeed.getEntries();
                    HashSet<FeedItem> fi = new HashSet<>();
                    f.forEach(item -> {
                        fi.add(new FeedItem(item.getTitle(), item.getLink()));
                    });
                    Feed feed = new Feed(syndFeed.getTitle(), fi);
                    feeds.add(feed);
                }
            });
        } catch (IOException ex) {
            System.err.println("Error reading from URLs file: " + IOUtils.URL_FILE);
        }

        return feeds;
    }
}
