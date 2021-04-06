package io.github.pskenny.seadhna.ui;

import java.io.IOException;
import java.util.*;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.pskenny.seadhna.feed.*;

public class TUI {
    private static Logger LOGGER = LoggerFactory.getLogger(TUI.class);
    private Controller controller;

    private HashSet<String> marked;

    private MultiWindowTextGUI gui;
    private Screen screen;
    private BasicWindow feedsWindow;

    public TUI() {
        controller = new Controller();
        marked = new HashSet<>();

        try (Terminal terminal = new DefaultTerminalFactory().createTerminal()) {
            screen = new TerminalScreen(terminal);
            screen.startScreen();

            // Create and start GUI
            gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));

            feedsWindow = new BasicWindow();
            feedsWindow.setComponent(getFeedsList());
            ArrayList<Window.Hint> hints = new ArrayList<>();
            hints.add(Window.Hint.NO_DECORATIONS);
            hints.add(Window.Hint.FULL_SCREEN);
            feedsWindow.setHints(hints);

            gui.addWindowAndWait(feedsWindow);
        } catch (IOException ex) {
            LOGGER.error("Couldn't output to terminal: " + ex.getMessage());
        }
    }

    /**
     * Returns list of RSS feeds.
     */
    private ActionListBox getFeedsList() {
        ActionListBox actionListBox = new ActionListBox();
        // Add feeds to list
        for (Feed feed : controller.getFeeds()) {
            // TODO Fix: feed.toString() is called on creation only
            // If all feed items are marked read, the feeds list still
            // has same text as creation
            actionListBox.addItem(feed.toString(), () -> {
                FeedItemsWindow feedItemsWindow = new FeedItemsWindow(feed);
                feedItemsWindow.addWindowListener(() -> marked.addAll(feedItemsWindow.getMarked()));
                feedItemsWindow.addToggleListener(item -> controller.update(item));
                gui.addWindowAndWait(feedItemsWindow);
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

    public Set<String> getMarkedLinks() {
        return marked;
    }
}
