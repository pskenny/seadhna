package io.github.pskenny.seadhna.feed;

import java.util.HashSet;
import java.util.Set;

public class FeedImpl implements Feed {
    private String title;
    private Set<FeedItem> feedItems;

    public FeedImpl(String title) {
        this(title, new HashSet<>());
    }

    public FeedImpl(String title, Set<FeedItem> feedItems) {
        this.title = title;
        this.feedItems = feedItems;
    }

    public String getTitle() {
        return title;
    }

    public void add(FeedItem feedItem) {
        feedItems.add(feedItem);
    }

    public Set<FeedItem> getFeedItems() {
        return feedItems;
    }

    @Override
    public String toString() {
        return hasUnread() ? "•" + getTitle() : getTitle();
    }

    private boolean hasUnread() {
        for (FeedItem item : feedItems) {
            if (!item.getRead())
                return true;
        }
        return false;
    }
}
