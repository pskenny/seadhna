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
        ActionListBox actionListBox = new ActionListBox(getTerminalSize());
        // Add feeds to list
        for (Map.Entry<String, SyndFeed> entry : controller.getFeeds().entrySet()) {
            actionListBox.addItem(entry.getValue().getTitle(),
                    () -> {
                        FeedItemsWindow fiw = new FeedItemsWindow(entry.getValue(), getTerminalSize(), marked);
                        fiw.addWindowListener(new ListenableBasicWindow.BasicWindowListener(){
                            @Override
                            public void onClosing() {
                                marked.addAll(fiw.getMarked());
                            }
                        });
                        gui.addWindowAndWait(fiw);
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

    private TerminalSize getTerminalSize() {
        return new TerminalSize(screen.getTerminalSize().getColumns() - 5, screen.getTerminalSize().getRows() - 4);
    }

    public Set<String> getMarkedLinks() {
        return marked;
    }
}
