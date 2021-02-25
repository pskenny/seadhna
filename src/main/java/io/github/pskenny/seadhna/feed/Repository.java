package io.github.pskenny.seadhna.feed;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import com.rometools.rome.feed.synd.SyndFeed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.pskenny.seadhna.db.Database;
import io.github.pskenny.seadhna.io.IOUtils;
import io.github.pskenny.seadhna.rss.FeedBuilder;

public class Repository {
    private static Logger LOGGER = LoggerFactory.getLogger(Repository.class);
    private static Repository repo;
    private Database db;

    private Repository() {
        db = new Database();
    }

    public static Repository getRepository() {
        if (repo == null)
            repo = new Repository();

        return repo;
    }

    public Collection<Feed> getFeeds() {
        return db.getFeeds();
    }

    public void loadFeeds() {
        HashSet<String> feedUrls = null;
        try {
            feedUrls = IOUtils.pathToLines(IOUtils.URL_FILE);
            // Make feed items from all RSS feed URLs
            feedUrls.parallelStream().forEach(url -> {
                SyndFeed feed = FeedBuilder.getFeed(url);
                // Only add feeds which URL worked
                if (feed != null)
                    db.load(feed);
            });
        } catch (IOException ex) {
            LOGGER.error("Error reading from URLs file: {}", IOUtils.URL_FILE);
        }
    }

    public void update(FeedItem item) {
        db.setRead(item.getURL(), item.getRead());
    }
}
