package io.github.pskenny.seadhna.ui;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.gui2.ActionListBox;
import com.googlecode.lanterna.gui2.BasicWindow;

import com.sun.syndication.feed.synd.*;

public class FeedItemsWindow extends BasicWindow {
    private HashSet<String> marked;
    private ActionListBox actionListBox;

    public FeedItemsWindow(SyndFeed feed) {
        marked = new HashSet<>();
        actionListBox = new ActionListBox();
        List<SyndEntry> items = feed.getEntries();

        // Add feed item titles to list and add action to add their links to the marked
        // list
        items.forEach(entry -> actionListBox.addItem(
            new FeedItem(entry.getTitle(), entry.getLink())
        ));

        // Add Back button to open feeds window
        actionListBox.addItem("Back", this::close);

        setComponent(actionListBox);
    }

    public Set<String> getMarked() {
        actionListBox.getItems().iterator().forEachRemaining(item -> {
            FeedItem fi = (FeedItem) item;
            if (fi.isMarked()) {
                marked.add(fi.getUrl());
            }
        });
        return marked;
    }

    private class FeedItem implements Runnable {
        private boolean marked = false;
        private String title;
        private String url;

        public FeedItem(String title, String url) {
            this.title = title;
            this.url = url;
        }

        @Override
        public void run() {
            toggleMarked();
        }

        public String getUrl() {
            return url;
        }

        public boolean isMarked() {
            return marked;
        }

        public void toggleMarked() {
            marked = !marked;
        }

        @Override
        public String toString() {
            if (marked) {
                return Symbols.BULLET + title;
            } else {
                return title;
            }
        }
    }
}
