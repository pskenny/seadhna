package io.github.pskenny.seadhna.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

public class IOUtils {

    /**
     * Return set containing each non-empty line as an element from the file path given.
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
}