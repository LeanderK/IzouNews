package leanderk.izou.news.RSS;

import intellimate.izou.addon.PropertiesContainer;
import intellimate.izou.system.Context;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This class manages all the
 * @author LeanderK
 * @version 1.0
 */
public class RSSManager {
    private HashMap<String, List<String>> guids = new HashMap<>();
    private List<String> feedsLinks = new ArrayList<>();

    private Context context;

    public RSSManager(PropertiesContainer propertiesContainer, Context context) {
        this.context = context;
        Pattern pattern = Pattern.compile("rss_feed_\\d+");
        feedsLinks = propertiesContainer.getProperties().stringPropertyNames().stream()
                    .filter(key -> pattern.matcher(key).matches())
                    .map(key -> propertiesContainer.getProperties().getProperty(key))
                    .collect(Collectors.toList());
    }

    /**
     * returns all the new Entries
     * @return a List of Feeds containing the new FeedMessages
     */
    public List<Feed> getNewEntries() {
        List<RSSFeedParser> parsers = feedsLinks.stream()
                    .map(feed -> new RSSFeedParser(feed, context))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        List<Feed> feeds = parsers.stream()
                    .map(RSSFeedParser::readFeed)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        //initialize guids
        feeds.stream()
                .filter(feed -> !guids.containsKey(feed.getLink()))
                .forEach(feed -> guids.put(feed.getLink(), new ArrayList<>()));

        //returns all new Entries
        return feeds.stream()
                    .map(feed -> {
                        List<String> registeredGuids = guids.get(feed.getLink());
                        feed.entries = feed.getMessages().stream()
                                .filter(message -> !registeredGuids.contains(message.getGuid()))
                                .peek(message -> registeredGuids.add(message.getGuid()))
                                .collect(Collectors.toList());
                        return feed;
                    })
                    .filter(feed -> !feed.getMessages().isEmpty())
                    .collect(Collectors.toList());
    }

    /**
     * returns whether there are any new FeedMessages waiting for you!
     * @return true if there are new FeedMessages, false if not
     */
    public boolean hasNewEntries() {
        return !getNewEntries().isEmpty();
    }

    /**
     * a list of feedsLinks containing only todays Message
     * @return a list of feedsLinks containg only todays messages
     */
    public List<Feed> getTodaysMessages() {
        List<RSSFeedParser> parsers = feedsLinks.stream()
                .map(feed -> new RSSFeedParser(feed, context))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<Feed> feeds = parsers.stream()
                .map(RSSFeedParser::readFeed)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        LocalDate now = LocalDate.now();

        return feeds.stream()
                .map(feed -> {
                    feed.entries = feed.getMessages().stream()
                            .filter(message -> message.getPubDate().isEqual(now))
                            .collect(Collectors.toList());
                    return feed;
                })
                .filter(feed -> !feed.getMessages().isEmpty())
                .collect(Collectors.toList());
    }
}
