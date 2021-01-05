package io.github.pskenny.seadhna.feed;


import lombok.Data;
import lombok.Getter;

@Data public class FeedItem {
    @Getter private final String title;
    @Getter private final String url;

    public FeedItem(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getURL() {
        return url;
    }

    public String getTitle() {
        return title;
    }
}