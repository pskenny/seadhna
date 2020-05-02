package io.github.pskenny.seadhna.rss;

import java.net.MalformedURLException;
import java.net.URL;
import com.sun.syndication.io.*;

import com.sun.syndication.feed.synd.*;
import java.io.*;

/**
 * 
 */
public class FeedBuilder {

    public static SyndFeed getFeed(String url) {
        SyndFeed feed = null;
        
        try {
            SyndFeedInput input = new SyndFeedInput();
            feed = input.build(new XmlReader(new URL(url)));
        } catch (MalformedURLException ex) {
            System.err.printf("URL error: %s\n", url);
        }  catch (IOException ex) {
            // Probably can't reach URL
            System.err.printf("URL error: %s\n", url);
        } catch (FeedException ex) {
            // Feed's bust, skip it
            System.err.printf("Feed error: %s\n", url);
        }

        return feed;
    }
}