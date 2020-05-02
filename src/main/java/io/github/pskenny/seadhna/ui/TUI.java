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

public class TUI implements Runnable {

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
    }

    @Override
    public void run() {
        try {
            Terminal terminal = new DefaultTerminalFactory().createTerminal();
            if (terminal.getTerminalSize().getColumns() < MINIMUM_COLUMNS
                    || terminal.getTerminalSize().getRows() < MINIMUM_ROWS) {
                System.err.println("Does not meet minimum terminal size: " + MINIMUM_COLUMNS + "x" + MINIMUM_ROWS);
                return;
            }
            screen = new TerminalScreen(terminal);
            screen.startScreen();

            // Create and start gui
            gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));
            displayWindow(getFeedsWindow());
        } catch (Exception e) {
            System.err.println("Couldn't start terminal user-interface");
        }
    }

    private void displayWindow(Window window) {
        Window w = gui.getActiveWindow();
        if (w != null)
            gui.removeWindow(w);
        gui.addWindowAndWait(window);
    }

    private BasicWindow getFeedsWindow() {
        if (feedsWindow == null)
            feedsWindow = new BasicWindow();
        feedsWindow.setComponent(getFeedsList());

        return feedsWindow;
    }

    private ActionListBox getFeedsList() {
        ActionListBox actionListBox = new ActionListBox(getTerminalSize());
        // Add feeds to list
        for (Map.Entry<String, SyndFeed> entry : feeds.entrySet()) {
            SyndFeed feed = entry.getValue();

            actionListBox.addItem(feed.getTitle(), () -> {
                displayWindow(getFeedItemsWindow(feed));
            });
        }
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

    private BasicWindow getFeedItemsWindow(SyndFeed feed) {
        if (feedItemsWindow == null)
            feedItemsWindow = new BasicWindow();

        feedItemsWindow.setComponent(getFeedItemList(feed));

        return feedItemsWindow;
    }

    private ActionListBox getFeedItemList(SyndFeed feed) {
        ActionListBox actionListBox = new ActionListBox(getTerminalSize());
        List<SyndEntry> items = feed.getEntries();

        for (SyndEntry entry : items) {
            actionListBox.addItem(entry.getTitle(), () -> {
                marked.add(entry.getLink());
            });
        }
        actionListBox.addItem("Back", () -> {
            displayWindow(getFeedsWindow());
        });
        return actionListBox;
    }

    private TerminalSize getTerminalSize() {
        return new TerminalSize(screen.getTerminalSize().getColumns() - 5, screen.getTerminalSize().getRows() - 4);
    }

    public HashSet<String> getMarkedLinks() {
        return marked;
    }
}
