package io.github.pskenny.seadhna.feed;

import java.util.Set;

public interface Feed {
    public Set<FeedItem> getFeedItems();

    public void add(FeedItem feedItem);

    public String getTitle();
}
