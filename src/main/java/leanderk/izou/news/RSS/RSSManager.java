package leanderk.izou.news.RSS;

import org.intellimate.izou.identification.Identification;
import org.intellimate.izou.identification.IdentificationManager;
import org.intellimate.izou.identification.IllegalIDException;
import org.intellimate.izou.sdk.Context;
import org.intellimate.izou.sdk.properties.PropertiesAssistant;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This class manages all the
 * @author LeanderK
 * @version 1.0
 */
public class RSSManager {
    HashMap<String, List<String>> guids = new HashMap<>();
    //the keys are the links, and the ID's the integers in the properties-file
    private Map<String, Integer> feedsLinks;
    private PropertiesAssistant propertiesContainer;
    private Context context;

    public RSSManager(Context context) {
        this.context = context;
        this.propertiesContainer = context.getPropertiesAssistant();
        Optional<Identification> identification = IdentificationManager.getInstance()
                .getIdentification(context.getAddOn());
        if (identification.isPresent())
            try {
                context.getFiles().register(context.getPropertiesAssistant(),this::loadProperties, identification.get());
            } catch (IllegalIDException e) {
                context.getLogger().error(e);
            }

        Pattern pattern = Pattern.compile("rss_feed_\\d+");
        loadProperties();
    }

    public List<Feed> parseFeeds() {
        return feedsLinks.keySet().stream()
            .map(link -> new RSSFeedParser(link, feedsLinks.get(link), getFeedName(feedsLinks.get(link)), this, context))
            .map(RSSFeedParser::readFeed)
            .collect(Collectors.toList());
    }

    public String getFeedName(int id) {
        String key = "rss_feed_name_" + id;
        String value = propertiesContainer.getProperties().getProperty(key);
        if (value != null) {
            return value;
        } else {
            return "";
        }
    }

    /**
     * returns whether there are any new FeedMessages waiting for you!
     * @return true if there are new FeedMessages, false if not
     */
    public boolean hasNewEntries() {
        return parseFeeds().stream()
                .anyMatch(Feed::hasNewMessages);
    }

    /**
     * loads the feeds from the properties file
     */
    public void loadProperties() {
        Pattern pattern = Pattern.compile("rss_feed_\\d+");
        feedsLinks = propertiesContainer.getProperties().stringPropertyNames().stream()
                .filter(key -> pattern.matcher(key).matches())
                .collect(Collectors.toMap(
                        key -> propertiesContainer.getProperties().getProperty(key),
                        key -> Integer.valueOf(key.replace("rss_feed_", ""))));

        feedsLinks = new LinkedHashMap<>(feedsLinks);
    }
}
