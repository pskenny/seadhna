package io.github.pskenny.seadhna.feed;

public class FeedItemImpl implements FeedItem {
    
    private boolean read;
    
    private final String title;
    
    private final String url;

    public FeedItemImpl(String title, String url) {
        this(false, title, url);
    }

    public FeedItemImpl(boolean read, String title, String url) {
        this.read = read;
        this.title = title;
        this.url = url;
    }

    @Override
    public String getURL() {
        return url;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public boolean getRead() {
        return read;
    }

    @Override
    public void setRead(boolean read) {
        this.read = read;
    }

    @Override
    public String toString() {
        return read ? title : "•" + title;
    }
}