package io.github.pskenny.seadhna.ui;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.sun.syndication.feed.synd.*;

import io.github.pskenny.seadhna.io.NIOUtils;

public class TUI implements Runnable {
    private Controller controller;

    private final int MINIMUM_COLUMNS = 25;
    private final int MINIMUM_ROWS = 5;

    private ConcurrentHashMap<String, SyndFeed> feeds;
    private HashSet<String> marked = new HashSet<String>();
    private BasicWindow feedsWindow = null;
    private BasicWindow feedItemsWindow = null;

    private MultiWindowTextGUI gui;
    private Screen screen;

    public TUI(ConcurrentHashMap<String, SyndFeed> feeds) {
        this.feeds = feeds;

        controller = new Controller();
    }

    @Override
    public void run() {
        /*
        - Start ui
        - load urls, call backs for updating ui
        */

        try (Terminal terminal = new DefaultTerminalFactory().createTerminal()) {
            if (terminal.getTerminalSize().getColumns() < MINIMUM_COLUMNS
                    || terminal.getTerminalSize().getRows() < MINIMUM_ROWS) {
                System.err.println("Does not meet minimum terminal size: " + MINIMUM_COLUMNS + "x" + MINIMUM_ROWS);
                return;
            }
            screen = new TerminalScreen(terminal);
            screen.startScreen();

            // Create and start GUI
            gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));
            displayWindow(getFeedsWindow());
        } catch (IOException ex) {
            System.err.println("Couldn't output to terminal");
        }
    }

    /**
     * Display window given, removing other windows if present.
     */
    private void displayWindow(Window window) {
        Window w = gui.getActiveWindow();
        if (w != null)
            gui.removeWindow(w);
        gui.addWindowAndWait(window);
    }

    /**
     * Return window with feeds
     */
    private BasicWindow getFeedsWindow() {
        if (feedsWindow == null)
            feedsWindow = new BasicWindow();
        feedsWindow.setComponent(getFeedsList());

        return feedsWindow;
    }

    /**
     * Returns list of RSS feeds.
     */
    private ActionListBox getFeedsList() {
        ActionListBox actionListBox = new ActionListBox(getTerminalSize());
        // Add feeds to list
        for (Map.Entry<String, SyndFeed> entry : feeds.entrySet()) {
            SyndFeed feed = entry.getValue();

            actionListBox.addItem(feed.getTitle(), () -> {

                displayWindow(getFeedItemsWindow(feed));
            });
        }
        // Add "Quit" item
        actionListBox.addItem("Quit", () -> {
            feedsWindow.close();
            try {
                screen.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return actionListBox;
    }

    /**
     * Return window containing feeds items from feed given.
     */
    private BasicWindow getFeedItemsWindow(SyndFeed feed) {
        if (feedItemsWindow == null)
            feedItemsWindow = new BasicWindow();

        feedItemsWindow.setComponent(getFeedItemList(feed));

        return feedItemsWindow;
    }

    /**
     * Return list of feed items from feed given.
     */
    private ActionListBox getFeedItemList(SyndFeed feed) {
        List<SyndEntry> items = feed.getEntries();
        ActionListBox actionListBox = new ActionListBox(getTerminalSize()) {
            // Add 'v' keystroke on this list to open feed item link in VLC
            @Override
            public Result handleKeyStroke(com.googlecode.lanterna.input.KeyStroke keyStroke) {
                // Try and open VLC on 'v' input
                if (keyStroke.getCharacter() == Character.valueOf('v')) {
                    // TODO handle indexoutofboundsexception,
                    String url = items.get(this.getSelectedIndex()).getLink();
                    // Check if list item at list index refers to a valid URL
                    if (url != null && NIOUtils.isValidUrlPath(url)) {
                        // Open VLC
                        ProcessBuilder processBuilder = new ProcessBuilder("vlc",
                                items.get(this.getSelectedIndex()).getLink());
                        try {
                            processBuilder.start();
                        } catch (IOException ex) {
                            System.err.println("Couldn't open VLC");
                        }
                    }
                }
                return super.handleKeyStroke(keyStroke);
            }
        };
        // Add feed item titles to list and add action to add their links to the marked
        // list
        for (SyndEntry entry : items) {
            actionListBox.addItem(entry.getTitle(), () -> marked.add(entry.getLink()));
        }

        // Add Back button to open feeds window
        actionListBox.addItem("Back", () ->

        displayWindow(getFeedsWindow()));

        return actionListBox;
    }

    /**
     * 
     */
    private TerminalSize getTerminalSize() {
        return new TerminalSize(screen.getTerminalSize().getColumns() - 5, screen.getTerminalSize().getRows() - 4);
    }

    /**
     * 
     */
    public HashSet<String> getMarkedLinks() {
        return marked;
    }
}
