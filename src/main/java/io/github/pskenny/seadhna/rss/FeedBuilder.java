package io.github.pskenny.seadhna.rss;

import java.net.MalformedURLException;
import java.net.URL;

import com.rometools.rome.feed.synd.*;
import com.rometools.rome.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * 
 */
public class FeedBuilder {
    private static Logger LOGGER = LoggerFactory.getLogger(FeedBuilder.class);

    private FeedBuilder() {
    }

    public static synchronized SyndFeed getFeed(String url) {
        try {
            LOGGER.debug("Loading url: {}", url);

            XmlReader xml = new XmlReader(new URL(url));
            SyndFeedInput input = new SyndFeedInput();

            return input.build(xml);
        } catch (MalformedURLException ex) {
            LOGGER.error("URL error: {}", url);
        } catch (IOException ex) {
            // Probably can't reach URL
            LOGGER.error("URL error: {}", url);
        } catch (FeedException ex) {
            // Feed's bust, skip it
            LOGGER.error("Feed error: {}", url);
        }

        return null;
    }
}