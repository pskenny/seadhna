package io.github.pskenny.seadhna;

import java.io.File;
import java.io.IOException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import io.github.pskenny.seadhna.io.IOUtils;
import io.github.pskenny.seadhna.rss.*;
import io.github.pskenny.seadhna.ui.TUI;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import com.sun.syndication.feed.synd.*;

/**
 * Give me Youtube videos, just like Séadhna Of House Clam does 👍 🎥
 * 
 * @author Paul Kenny
 */
public class App {

    public App() {
        TUI tui = new TUI();
        tui.getMarkedLinks();
    }

    /**
     * Write URLs to file path or stdout, if path is null.
     */
    private void writeMarkedURLs(String filePath,  HashSet<String> urls) {
        // Write to stdout if no file path
        if(filePath == null) {
            urls.forEach(System.out::println);
        } else {
            boolean success = IOUtils.writeIteratorToFile(urls.iterator(), filePath);
            // check if successful, output error message on failure
            if(!success) {
                System.err.println("Could not write to file: \"" + filePath + "\"");
            }
        }
    }

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("seadhna").build()
                .description("Mark YouTube channel video URLs to write out.").version("0.1");
        parser.addArgument("-f", "-file").metavar("FILE").type(String.class).help("File to write URLs to");

        try {
            Namespace res = parser.parseArgs(args);
            // fileArgument is null if no file given
            String fileArgument = res.getString("f");

            new App();
        } catch (ArgumentParserException e) {
            parser.handleError(e);
        }
    }
}
