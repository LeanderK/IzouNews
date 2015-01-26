package leanderk.izou.news;

import intellimate.izou.events.Event;
import intellimate.izou.properties.PropertiesContainer;
import intellimate.izou.resource.Resource;
import intellimate.izou.system.Context;
import leanderk.izou.news.RSS.Feed;
import leanderk.izou.news.RSS.FeedMessage;
import leanderk.izou.tts.outputextension.TTSData;
import leanderk.izou.tts.outputextension.TTSOutputExtension;
import leanderk.izou.tts.outputplugin.TTSOutputPlugin;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author LeanderK
 * @version 1.0
 */
public class NewsTTSOutputExtension extends TTSOutputExtension{
    public static final String ID = NewsTTSOutputExtension.class.getCanonicalName();
    public static final String TTS_NORMAL_INTRO = "normalIntro";
    public static final String TTS_NEW_NEWS_INTRO = "newNewsIntro";
    public static final String TTS_Feed_Intro = "feedIntro";
    public static final String TTS_CLOSING = "closing";
    private PropertiesContainer propertiesContainer;
    /**
     * creates a new outputExtension with a new id
     *
     * @param context the context
     */
    public NewsTTSOutputExtension(Context context) {
        super(ID, context);
        this.propertiesContainer = context.properties.getPropertiesContainer();
        setPluginId(TTSOutputPlugin.ID);
        addResourceIdToWishList(NewsContentGenerator.RESOURCE_ID);
    }

    /**
     * override this class to generate the TTSData.
     * it will be called, when canGenerate returns true for the locale
     *
     * @param event the Event which triggered the generation
     * @return an instance of TTSData, which will then be consumed by the TTSOutputPlugin
     */
    @Override
    public TTSData generateSentence(Event event) {
        List<Resource> resources = event.getListResourceContainer().provideResource(NewsContentGenerator.RESOURCE_ID);
        Resource<List<Feed>> resource = resources.get(0);
        if(resource.getResource().isEmpty())
            return null;
        //String locale = resource.getResource().get(0).getLocale().getLanguage();
        StringBuilder words = new StringBuilder();
        if (event.getDescriptors().contains(Event.FULL_WELCOME_EVENT)) {
            if (LocalTime.now().isBefore(LocalTime.of(15, 0))
                    || resource.getResource().stream().anyMatch(feed -> feed.getNewMessages().isEmpty())) {
                constructTodaysNews(words, propertiesContainer, resource.getResource());
            } else {
                constructNewNews(words, propertiesContainer, resource.getResource());
            }
        } else if (event.getDescriptors().contains(Event.MAJOR_WELCOME_EVENT)) {
            constructNewNews(words, propertiesContainer, resource.getResource());
        } else if (event.getDescriptors().contains(NewsAddOn.EVENT_NEW_NEWS)) {
            constructNewNews(words, propertiesContainer, resource.getResource());
        }
        return TTSData.createTTSData(words.toString(), getLocale(), 30, ID);
    }

    private void constructNewNews(StringBuilder words, PropertiesContainer propertiesContainer, List<Feed> feeds) {
        for (Feed feed : feeds) {
            List<FeedMessage> newMessages = feed.getNewMessages();
            if (newMessages.isEmpty())
                continue;
            HashMap<String, String> data = new HashMap<>();
            data.put("name", feed.getName());
            words.append(getWords(TTS_NEW_NEWS_INTRO, data));
            words.append(" ");
            constructMessages(words, feed, feed.getNewMessages(), getMessagesLimit(feed, propertiesContainer));
        }
        if (words.length() > 300) {
            words.append(" ");
            words.append(getWords(TTS_CLOSING, null));
        }

    }

    private void constructTodaysNews(StringBuilder words, PropertiesContainer propertiesContainer, List<Feed> feeds) {
        words.append(getWords(TTS_NORMAL_INTRO, null));
        words.append(" ");
        for (Feed feed : feeds) {
            HashMap<String, String> data = new HashMap<>();
            data.put("name", feed.getName());
            words.append(getWords(TTS_Feed_Intro, data));
            constructMessages(words, feed, feed.getTodaysMessages(), getMessagesLimit(feed, propertiesContainer));
        }
        words.append(" ");
        words.append(getWords(TTS_CLOSING, null));
    }

    private void constructMessages(StringBuilder words, Feed feed, List<FeedMessage> messages, int messagesLimit) {
        words.append(messages.stream()
                .limit(messagesLimit)
                .flatMap(message -> Stream.of(message.getTitle(), message.getDescription()))
                .collect(Collectors.joining(". "))
                .replace("#", ""));
    }

    /**
     * how many Messages of the feed should be read, if none found return 10
     * @param feed the feed
     * @param propertiesContainer the properties to search in
     * @return the limit of the messages read per feed
     */
    private int getMessagesLimit(Feed feed, PropertiesContainer propertiesContainer) {
        String key = "rss_message_limit_" + feed.getId();
        String limit = propertiesContainer.getProperties().getProperty(key);
        if (limit != null) {
            try {
                return Integer.valueOf(limit);
            } catch (NumberFormatException e) {
                getContext().logger.getLogger().error("unable to parse message limit", e);
            }
        }
        return 10;
    }

    /**
     * checks if the TTSOutputExtension can generate TTSData fot the locale
     *
     * @param locale the locale of the request
     * @return true if able to generate, false if not
     */
    @Override
    public boolean canGenerateForLanguage(String locale) {
        return true;
    }
}
