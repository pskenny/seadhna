package io.github.pskenny.seadhna.feed;

import java.util.Collection;

public class Controller {
    private Repository repo;

    public Controller() {
        repo = Repository.getRepository();
        repo.loadFeeds();
    }

    public Collection<Feed> getFeeds() {
        return repo.getFeeds();
    }

    public void update(FeedItem item) {
        repo.update(item);
    }
}
