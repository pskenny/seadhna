package io.github.pskenny.seadhna.ui;

import java.io.IOException;
import java.util.*;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import com.sun.syndication.feed.synd.*;

import io.github.pskenny.seadhna.io.NIOUtils;

public class TUI {
    private Controller controller;

    private static final int MINIMUM_COLUMNS = 25;
    private static final int MINIMUM_ROWS = 5;

    private HashSet<String> marked;
    private BasicWindow feedsWindow;
    private FeedItemsWindow feedItemsWindow;

    private MultiWindowTextGUI gui;
    private Screen screen;

    public TUI() {
        controller = new Controller();
        marked = new HashSet<>();

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
            gui.addWindowAndWait((getFeedsWindow());
        } catch (IOException ex) {
            System.err.println("Couldn't output to terminal");
        }
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
        for (Map.Entry<String, SyndFeed> entry : controller.getFeeds().entrySet()) {
            SyndFeed feed = entry.getValue();
            actionListBox.addItem(feed.getTitle(), () -> gui.addWindowAndWait(new FeedItemsWindow(feed)));
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
     * 
     */
    private TerminalSize getTerminalSize() {
        return new TerminalSize(screen.getTerminalSize().getColumns() - 5, screen.getTerminalSize().getRows() - 4);
    }

    /**
     * 
     */
    public Set<String> getMarkedLinks() {
        return marked;
    }
}
