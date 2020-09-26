package io.github.pskenny.seadhna.ui;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.ActionListBox;

import com.sun.syndication.feed.synd.*;

public class FeedItemsWindow extends ListenableBasicWindow {
    private HashSet<String> marked;
    private ActionListBox actionListBox;
    private List<SyndEntry> items;

    public FeedItemsWindow(SyndFeed feed, TerminalSize size, Set<String> marked) {
        actionListBox = new ActionListBox(size);
        items = feed.getEntries();
        this.marked = new HashSet<>();

        // Add feed item titles to list and add action to add their links to the marked
        // list
        items.forEach(entry -> {
            String url = entry.getLink();
            FeedItem fi = new FeedItem(entry.getTitle(), url);
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
            if (marked) {
                return Symbols.BULLET + title;
            } else {
                return title;
            }
        }
    }
}
