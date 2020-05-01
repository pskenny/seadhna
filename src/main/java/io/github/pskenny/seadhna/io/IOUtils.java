package io.github.pskenny.seadhna.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

public class IOUtils {

    /**
     * Return set containing each non-empty line as an element from the file path
     * given.
     * 
     * @param path File path
     * @return HashSet containing each line as element
     */
    public static HashSet<String> pathToLines(String path) throws IOException {
        HashSet<String> lines = new HashSet<String>();

        BufferedReader br = new BufferedReader(new FileReader(path));
        String line;
        while ((line = br.readLine()) != null)
            if (!line.isEmpty())
                lines.add(line);
        br.close();

        return lines;
    }

    /**
     * Writes each element on new line to file. toString is called on each element
     * to get value.
     * 
     * @param iterator Iterator containing elements to write to file
     * @param path     Path to file
     * @return Write to file success. Returns true if given empty iterator.
     */
    public static boolean writeIteratorToFile(Iterator<? extends Object> iterator, String path) {
        // Return true if empty
        if (!iterator.hasNext())
            return true;

        // Generate string
        StringBuilder sb = new StringBuilder();
        iterator.forEachRemaining((element) -> {
            sb.append(element.toString());
            sb.append("\n");
        });

        // Write to file
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path)));
            bw.write(sb.toString());
            bw.close();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
}