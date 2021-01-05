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

import io.github.pskenny.seadhna.feed.*;

public class TUI {
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

            gui.addWindowAndWait(feedsWindow);
        } catch (IOException ex) {
            System.err.println("Couldn't output to terminal");
        }
    }

    /**
     * Returns list of RSS feeds.
     */
    private ActionListBox getFeedsList() {
        ActionListBox actionListBox = new ActionListBox();
        // Add feeds to list
        for (Feed feed : controller.getFeeds()) {
            actionListBox.addItem(feed.getTitle(),
                    () -> {
                        FeedItemsWindow feedItemsWindow = new FeedItemsWindow(feed, marked);
                        feedItemsWindow.addWindowListener(new ListenableBasicWindow.BasicWindowListener(){
                            @Override
                            public void onClosing() {
                                marked.addAll(feedItemsWindow.getMarked());
                            }
                        });
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
