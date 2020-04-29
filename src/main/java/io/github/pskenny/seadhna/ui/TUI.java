package io.github.pskenny.seadhna.ui;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import com.sun.syndication.feed.synd.*;

public class TUI {

    private Hashtable<String, SyndFeed> feeds;
    private HashSet<String> marked = new HashSet<String>();

    public TUI(Hashtable<String, SyndFeed> feeds) throws Exception {
        this.feeds = feeds;

        // Setup terminal and screen layers
        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();

        CheckBoxList<String> checkBoxList = new CheckBoxList<String>();
        
        // Display feeds
        for(Map.Entry entry : feeds.entrySet()) 
            checkBoxList.addItem((String) entry.getKey());
        
        checkBoxList.addItem("item 1");

        // Create window to hold the panel
        BasicWindow window = new BasicWindow();
        window.setComponent(checkBoxList);

        // Create gui and start gui
        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(),
                new EmptySpace(TextColor.ANSI.BLUE));
        gui.addWindowAndWait(window);
    }

    public void displayFeeds() {

    }

    public void displayFeedItems() {

    }

    public HashSet<String> getMarked() {
        return marked;
    }
}
