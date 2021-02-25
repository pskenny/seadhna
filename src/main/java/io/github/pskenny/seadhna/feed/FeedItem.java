package io.github.pskenny.seadhna.feed;

public interface FeedItem {
    public boolean getRead();
    public void setRead(boolean read);
    public String getURL();
    public String getTitle();
}
