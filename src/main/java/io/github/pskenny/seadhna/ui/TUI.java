package io.github.pskenny.seadhna.ui;

import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import com.sun.syndication.feed.synd.*;

public class TUI implements Runnable {

    private Hashtable<String, SyndFeed> feeds;
    private HashSet<String> marked = new HashSet<String>();
    private BasicWindow feedsWindow = null;
    private BasicWindow feedItemsWindow = null;

    private MultiWindowTextGUI gui;
    private Screen screen;

    public TUI(Hashtable<String, SyndFeed> feeds) {
        this.feeds = feeds;
    }

    @Override
    public void run() {
        try {
            Terminal terminal = new DefaultTerminalFactory().createTerminal();
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
        if (feedsWindow == null) {
            feedsWindow = new BasicWindow();
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
            feedsWindow.setComponent(actionListBox);
        }
        return feedsWindow;
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
        return new TerminalSize(screen.getTerminalSize().getColumns() - 5, screen.getTerminalSize().getRows() - 5);
    }

    public HashSet<String> getMarkedLinks() {
        return marked;
    }
}
