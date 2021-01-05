package io.github.pskenny.seadhna.ui;

import java.util.HashSet;
import java.util.Set;

import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.gui2.ActionListBox;

public class FeedItemsWindow extends ListenableBasicWindow {
    private HashSet<String> marked;
    private ActionListBox actionListBox;
    private HashSet<io.github.pskenny.seadhna.feed.FeedItem> items;

    public FeedItemsWindow(io.github.pskenny.seadhna.feed.Feed feed, Set<String> marked) {
        actionListBox = new ActionListBox();
        items = feed.getFeedItems();
        this.marked = new HashSet<>();

        // Add feed item titles to list and add action to add their links to the marked
        // list
        items.forEach(feedItem -> {
            String url = feedItem.getURL();
            FeedItem fi = new FeedItem(feedItem.getTitle(), url);
            if (marked.contains(url)) {
                fi.toggleMarked();
            }

            actionListBox.addItem(fi);
        });

        // Add Back button to open feeds window
        actionListBox.addItem("Back", this::close);

        setComponent(actionListBox);
    }

    public Set<String> getMarked() {
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

        /**
         * Toggle is feed item is marked.
         */
        public void toggleMarked() {
            marked = !marked;
            if(marked) {
                FeedItemsWindow.this.marked.add(url);
            } else {
                FeedItemsWindow.this.marked.remove(url);
            }
        }

        @Override
        public String toString() {
            return marked ? Symbols.BULLET + title : title;
        }
    }
}
