package io.github.pskenny.seadhna.feed;

import java.util.HashSet;

public class Feed {
    private String title;
    private HashSet<FeedItem> feedItems;

    public Feed(String title, HashSet<FeedItem> feedItems) {
        this.title = title;
        this.feedItems = feedItems;
    }

    public String getTitle() {
        return title;
    }

    public HashSet<FeedItem> getFeedItems() {
        return feedItems;
    }
}
