package io.github.pskenny.seadhna.ui;

import java.io.InterruptedIOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.ActionListBox;
import com.googlecode.lanterna.gui2.BasicWindow;

import io.github.pskenny.seadhna.io.NIOUtils;

import com.sun.syndication.feed.synd.*;

public class FeedItemsWindow extends BasicWindow {
    private HashSet<String> marked;

    public FeedItemsWindow(SyndFeed feed, TerminalSize size) {
        marked = new HashSet<>();
        List<SyndEntry> items = feed.getEntries();

        ActionListBox actionListBox = new ActionListBox(size) {
            // Add 'v' keystroke on this list to open feed item link in VLC
            @Override
            public Result handleKeyStroke(com.googlecode.lanterna.input.KeyStroke keyStroke) {
                // Try and open VLC on 'v' input
                if (keyStroke.getCharacter().equals(Character.valueOf('v'))) {
                    // TODO handle indexoutofboundsexception,
                    String url = items.get(this.getSelectedIndex()).getLink();
                    // Check if list item at list index refers to a valid URL
                    if (url != null && NIOUtils.isValidUrlPath(url)) {
                        // Open VLC
                        ProcessBuilder processBuilder = new ProcessBuilder("vlc",
                                items.get(this.getSelectedIndex()).getLink());
                        try {
                            processBuilder.start();
                        } catch (InterruptedIOException ex) {
                            System.err.println("Couldn't open VLC");
                        }
                    }
                }
                return super.handleKeyStroke(keyStroke);
            }
        };
        // Add feed item titles to list and add action to add their links to the marked
        // list
        items.forEach(entry -> actionListBox.addItem(entry.getTitle(), () -> 
            marked.add(entry.getLink())));

        // Add Back button to open feeds window
        actionListBox.addItem("Back", this::close);
        setComponent(actionListBox);
    }

    public Set getMarked() {
        return marked;
    }
}
