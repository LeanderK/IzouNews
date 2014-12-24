package leanderk.izou.news.RSS;

import intellimate.izou.addon.PropertiesContainer;

import java.time.LocalTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This class manages all the
 * @author LeanderK
 * @version 1.0
 */
public class RSSManager {
    PropertiesContainer propertiesContainer;
    HashMap<String, List<String>> guids = new HashMap<>();
    List<String> feeds = new ArrayList<>();
    List<Feed> newMessages = new ArrayList<>();
    LocalTime time = LocalTime.now();

    public RSSManager(PropertiesContainer propertiesContainer) {
        this.propertiesContainer = propertiesContainer;
        Pattern pattern = Pattern.compile("rss_feed_\\d+");
        feeds = propertiesContainer.getProperties().stringPropertyNames().stream()
                    .filter(key -> pattern.matcher(key).matches())
                    .map(key -> propertiesContainer.getProperties().getProperty(key))
                    .collect(Collectors.toList());
    }

    /**
     * returns all the new Entries
     * <p>
     * This method will not query, if the last query was 5 minutes ago!
     * </p>
     * @return a List of Feeds containing the new FeedMessages (limited to 10)
     */
    public List<Feed> getNewEntries() {
        if(LocalTime.now().plusMinutes(5).isAfter(time)) {
            return newMessages;
        }
        time = LocalTime.now();
        List<RSSFeedParser> parsers =
                feeds.stream()
                    .map(RSSFeedParser::new)
                    .collect(Collectors.toList());

        List<Feed> feeds =
                parsers.stream()
                        .filter(Objects::nonNull)
                        .map(RSSFeedParser::readFeed)
                        .collect(Collectors.toList());
        //initialize guids
        feeds.stream()
                .filter(feed -> !guids.containsKey(feed.getLink()))
                .forEach(feed -> guids.put(feed.getLink(), new ArrayList<>()));

        //returns all new Entries
        newMessages = feeds.stream()
                    .map(feed -> {
                        List<String> registeredGuids = guids.get(feed.getLink());
                        feed.entries = feed.getMessages().stream()
                                .filter(message -> !registeredGuids.contains(message.getGuid()))
                                .peek(message -> registeredGuids.add(message.getGuid()))
                                .limit(10)
                                .collect(Collectors.toList());
                        return feed;
                    })
                    .filter(feed -> !feed.getMessages().isEmpty())
                    .collect(Collectors.toList());
        return newMessages;
    }

    /**
     * returns whether there are any new FeedMessages waiting for you!
     * <p>
     * This method will only query, if the last query is over 5 minutes ago.
     * </p>
     * @return true if there are new FeedMessages, false if not
     */
    public boolean hasNewEntries() {
        return getNewEntries().isEmpty();
    }

    /**
     * returns a list of feeds containing only todays Message (limited to 10 each)
     * @return
     */
    public List<Feed> getTodaysMessages() {

    }
}
