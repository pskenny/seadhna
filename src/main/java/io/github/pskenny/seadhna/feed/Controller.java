package io.github.pskenny.seadhna.feed;

import java.util.HashSet;

public class Controller {
    private Repository repo;
    private HashSet<Feed> feeds;

    public Controller() {
        repo = Repository.getRepository();
        feeds = new HashSet<>();

        feeds = repo.loadFeeds();
    }

    public HashSet<Feed> getFeeds() {
        return feeds;
    }
}
