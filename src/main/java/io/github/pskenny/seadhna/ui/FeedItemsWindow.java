package io.github.pskenny.seadhna.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.ActionListBox;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.WindowListener;
import com.googlecode.lanterna.input.KeyStroke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.pskenny.seadhna.feed.FeedItem;

public class FeedItemsWindow extends ListenableBasicWindow {

    private static Logger LOGGER = LoggerFactory.getLogger(FeedItemsWindow.class);
    private HashSet<String> marked;
    private ActionListBox actionListBox;
    private Set<io.github.pskenny.seadhna.feed.FeedItem> items;
    private ArrayList<ToggleListener> toggleListeners;
    // NOTE: This implementation only allows one action per unique keystroke
    private HashMap<KeyStroke, ListAction> commands;

    public FeedItemsWindow(io.github.pskenny.seadhna.feed.Feed feed) {
        this.marked = new HashSet<>();
        actionListBox = new ActionListBox();
        items = feed.getFeedItems();
        toggleListeners = new ArrayList<>();
        commands = new HashMap<>();

        // Add feed item titles to list and add action to add their links to toggle item
        // read
        items.forEach(feedItem -> actionListBox.addItem(new Runnable() {
            @Override
            public void run() {
                // toggle item read state
                feedItem.setRead(!feedItem.getRead());
                fireToggleListeners(feedItem);

                // Don't select next item if current selected item is already at the end
                if (actionListBox.getSelectedIndex() != actionListBox.getItemCount())
                    // Select next item
                    actionListBox.setSelectedIndex(actionListBox.getSelectedIndex() + 1);
            }

            @Override
            public String toString() {
                return feedItem.toString();
            }
        }));

        setComponent(actionListBox);
        ArrayList<Window.Hint> hints = new ArrayList<>();
        hints.add(Window.Hint.NO_DECORATIONS);
        hints.add(Window.Hint.FULL_SCREEN);
        setHints(hints);

        addKeyCommands();

        this.addWindowListener(new WindowListener() {
            @Override
            public void onInput(Window basePane, KeyStroke keyStroke, AtomicBoolean deliverEvent) {
                // Process commands
                if (commands.containsKey(keyStroke))
                    commands.get(keyStroke).run(getSelected());
            }

            @Override
            public void onUnhandledInput(Window basePane, KeyStroke keyStroke, AtomicBoolean hasBeenHandled) {
            }

            @Override
            public void onResized(Window window, TerminalSize oldSize, TerminalSize newSize) {
            }

            @Override
            public void onMoved(Window window, TerminalPosition oldPosition, TerminalPosition newPosition) {
            }
        });
    }

    private void addKeyCommands() {
        // Template: commands.put(new KeyStroke('', false, false), (feedItem) -> {});
        commands.put(new KeyStroke('q', false, false), feedItem -> close());
        commands.put(new KeyStroke('m', false, false), feedItem -> toggleMarked(((FeedItem) feedItem).getURL()));
        commands.put(new KeyStroke('v', false, false), feedItem -> {
            String url = ((FeedItem) feedItem).getURL();
            try {
                LOGGER.info("Opening mpv: {}", url);
                // Open url in mpv
                new ProcessBuilder("mpv", url).start();
            } catch (IOException e) {
                LOGGER.error("Error opening mpv url: {}", url);
            }
        });
    }

    public FeedItem getSelected() {
        return (FeedItem) items.toArray()[actionListBox.getSelectedIndex()];
    }

    public void addToggleListener(ToggleListener listener) {
        toggleListeners.add(listener);
    }

    private void fireToggleListeners(FeedItem item) {
        for (ToggleListener listener : toggleListeners) {
            listener.toggled(item);
        }
    }

    /**
     * If url already marked remove it, otherwise add url to marked list.
     */
    private void toggleMarked(String url) {
        if (marked.contains(url)) {
            marked.remove(url);
        } else {
            marked.add(url);
        }
    }

    public Set<String> getMarked() {
        return marked;
    }

    public interface ListAction {
        public void run(Object listItem);
    }

    public interface ToggleListener {
        public void toggled(FeedItem item);
    }
}
