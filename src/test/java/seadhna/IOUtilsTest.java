package seadhna;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.pskenny.seadhna.io.*;
import jdk.jfr.Name;

@Name("Test io.github.pskenny.seadhna.io.IOUtils")
public class IOUtilsTest {

    @Test
    public void testLoadFeedUrls() {
        Path resourceDirectory = Paths.get("src", "test", "resources", "urls");
        String urlPath = resourceDirectory.toFile().getAbsolutePath();

        HashSet<String> expected = new HashSet<String>(Arrays.asList(
            "https://www.youtube.com/feeds/videos.xml?channel_id=UCTIoOLzT1jbxAcPW99xn0zQ",
            "https://www.youtube.com/feeds/videos.xml?channel_id=UC-2YHgc363EdcusLIBbgxzg",
            "https://www.youtube.com/feeds/videos.xml?channel_id=UC-notvalid123456abcdefg"));
        
        assertDoesNotThrow(() -> {
            HashSet<String> actual = IOUtils.loadFeedUrls(urlPath);

            Assertions.assertEquals(expected, actual);
        });
    }
}