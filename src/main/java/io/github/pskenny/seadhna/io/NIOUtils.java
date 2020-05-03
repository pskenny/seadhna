package io.github.pskenny.seadhna.io;

import java.net.MalformedURLException;
import java.net.URL;

public class NIOUtils {
    /**
     * Check whether an URL path is a URL. Note: does not check whether this path
     * will get a response.
     * 
     * @param url Path to check.
     * @return Path given is valid (not malformed).
     */
    public static boolean isValidUrlPath(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }
}