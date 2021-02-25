package io.github.pskenny.seadhna.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.pskenny.seadhna.feed.Feed;
import io.github.pskenny.seadhna.feed.FeedImpl;
import io.github.pskenny.seadhna.feed.FeedItem;
import io.github.pskenny.seadhna.feed.FeedItemImpl;
import io.github.pskenny.seadhna.io.IOUtils;

/**
 *
 * 
 */
// https://www.sqlitetutorial.net/sqlite-java/
public class Database {

    private static Logger LOGGER = LoggerFactory.getLogger(Database.class);

    private static final String url = "jdbc:sqlite:" + IOUtils.CONFIG_FOLDER + "seadhna.db";
    private static final String createTable = "CREATE TABLE IF NOT EXISTS videos (id integer PRIMARY KEY, read text not null,"
            + "channelname text NOT NULL, videoname text NOT NULL, url text NOT NULL unique)";
    private static final String insert = "INSERT OR IGNORE INTO videos(read, channelname, videoname, url) VALUES(?,?,?,?)";
    private static final String update = "UPDATE videos SET read = ? WHERE url = ?";
    private static final String get = "SELECT read, channelname, videoname, url FROM videos";

    public Database() {
        // create table if doesn't exist
        try (Connection conn = DriverManager.getConnection(url); Statement sqlStatement = conn.createStatement()) {
            // create a new table
            sqlStatement.execute(createTable);
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * Insert feed to db
     */
    public void insert(String channelName, FeedItem feedItem) {
        try (Connection conn = DriverManager.getConnection(url);
                PreparedStatement sqlStatement = conn.prepareStatement(insert)) {
            sqlStatement.setBoolean(1, feedItem.getRead());
            sqlStatement.setString(2, channelName);
            sqlStatement.setString(3, feedItem.getTitle());
            sqlStatement.setString(4, feedItem.getURL());
            sqlStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public synchronized void load(SyndFeed feed) {
        try (Connection conn = DriverManager.getConnection(url);
                PreparedStatement sqlStatement = conn.prepareStatement(insert)) {

            conn.setAutoCommit(false);

            LOGGER.debug("Inserting feed items from: {}", feed.getTitle());

            // Load feed data into statement and execute
            List<SyndEntry> feedEntries = feed.getEntries();
            for (SyndEntry rssEntry : feedEntries) {
                sqlStatement.setBoolean(1, false);
                sqlStatement.setString(2, feed.getTitle());
                sqlStatement.setString(3, rssEntry.getTitle());
                sqlStatement.setString(4, rssEntry.getLink());
                sqlStatement.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            LOGGER.error("SQL exception on inserting feed item:\n{}", e.getMessage());
        }
    }

    /**
     * Update
     */
    public void setRead(String url, boolean read) {
        try (Connection conn = DriverManager.getConnection(Database.url);
                PreparedStatement pstmt = conn.prepareStatement(update)) {
            // set the corresponding param
            pstmt.setBoolean(1, read);
            pstmt.setString(2, url);
            // update
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * Construct feeds from database
     */
    public Collection<Feed> getFeeds() {
        HashMap<String, Feed> feeds = new HashMap<>();

        try (Connection conn = DriverManager.getConnection(url);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(get)) {
            // loop through the result set
            while (rs.next()) {
                boolean read = rs.getBoolean("read");
                String channelName = rs.getString("channelname");
                String title = rs.getString("videoname");
                String url = rs.getString("url");

                // Find correct feed in "feeds" or create it
                Feed feed;
                if (feeds.containsKey(channelName))
                    feed = feeds.get(channelName);
                else
                    feed = new FeedImpl(channelName);

                // Add feed item to feed
                feed.add(new FeedItemImpl(read, title, url));
                feeds.put(channelName, feed);
            }
        } catch (SQLException e) {
            LOGGER.error("SQL Exception on getting feeds:\n{}", e.getMessage());
        }
        return feeds.values();
    }
}