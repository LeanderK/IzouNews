package leanderk.izou.news.RSS;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class Feed {

    private final String title;
    private final String name;
    private final String link;
    private final String description;
    private final String language;
    private final String copyright;
    private final String pubDate;
    private final int id;
    private final RSSManager rssManager;

    private List<FeedMessage> entries = new ArrayList<>();
    private List<FeedMessage> newEntries = null;

    public Feed(RSSManager rssManager, String title, String name, String link, String description, String language,
                String copyright, String pubDate, int id) {
        this.rssManager = rssManager;
        this.title = title;
        this.name = name;
        this.link = link;
        this.description = description;
        this.language = language;
        this.copyright = copyright;
        this.pubDate = pubDate;
        this.id = id;
    }

    /**
     * returns all the Messages from the Feed.
     * <p>
     * This method will mark all new Messages as read, although they can still be retrieved by {@link #hasNewMessages()}.
     * </p>
     * @return a List containing all the FeedMessages
     */
    public List<FeedMessage> getMessages() {
        markReadMessages();
        return entries;
    }

    /**
     * sets the FeedMessages (replaces all existing).
     * @param entries the FeedMessages
     */
    public void setFeedMessages(List<FeedMessage> entries) {
        this.entries = entries;
    }

    /**
     * marks the new messages as read.
     */
    private void markReadMessages() {
        if (newEntries != null)
            return;
        if(!rssManager.guids.containsKey(getLink()))
            rssManager.guids.put(getLink(), new ArrayList<>());
        List<String> registeredGuids = rssManager.guids.get(getLink());

        this.newEntries = entries.stream()
                .filter(message -> !registeredGuids.contains(message.getGuid()))
                .peek(message -> registeredGuids.add(message.getGuid()))
                .collect(Collectors.toList());
    }

    /**
     * returns all the new Messages.
     * <p>
     * This method will mark all new Messages as read, although they can still be retrieved by {@link #hasNewMessages()}.
     * </p>
     * @return a list containing FeedMessages
     */
    public List<FeedMessage> getNewMessages() {
        markReadMessages();
        return newEntries;
    }

    /**
     * returns whether the Feed has new Messages.
     * <p>
     * This method will NOT mark the new Messages as read.
     * </p>
     * @return true if there are new Messages, false if not
     */
    public boolean hasNewMessages() {
        if (newEntries != null)
            return !newEntries.isEmpty();

        if(!rssManager.guids.containsKey(getLink()))
            rssManager.guids.put(getLink(), new ArrayList<>());
        List<String> registeredGuids = rssManager.guids.get(getLink());
        if (registeredGuids.size() > 200) {
            for (int i = 200; i < registeredGuids.size(); i++) {
                registeredGuids.remove(200);
            }
        }
        return entries.stream()
                .anyMatch(message -> !registeredGuids.contains(message.getGuid()));
    }

    /**
     * a list of FeedMessages containing only todays Messages
     * <p>
     * If there are fewer than 3 Messages, it will also add the Message from 20:00 to midnight the day before.
     * </p>
     * @return a list of FeedMessages containg only todays messages
     */
    public List<FeedMessage> getTodaysMessages() {
        markReadMessages();

        LocalDate now = LocalDate.now();

        List<FeedMessage> messages = getMessages().stream()
                .filter(message -> message.getPubDate().isEqual(now))
                .collect(Collectors.toList());

        if (messages.size() < 3) {
            LocalDate yesterday = now.minusDays(1);
            messages.addAll(getTodaysMessages().stream()
                    .filter(message -> message.getPubDate().isEqual(yesterday))
                    .filter(message -> message.getPubDateTime().getHour() >= 20)
                    .collect(Collectors.toList()));
        }
        return messages;
    }

    public String getTitle() {
        if(title.contains("</p>")) {
            Document doc = Jsoup.parseBodyFragment(title);
            return doc.body().text();
        }
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        if(description.contains("</p>")) {
            Document doc = Jsoup.parseBodyFragment(description);
            return doc.body().text();
        }
        return description;
    }

    public String getLanguage() {
        return language;
    }

    public Locale getLocale() {
        return new Locale(language);
    }

    public String getCopyright() {
        return copyright;
    }

    public String getPubDate() {
        return pubDate;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Feed [copyright=" + copyright + ", description=" + description
                + ", language=" + language + ", link=" + link + ", pubDate="
                + pubDate + ", title=" + title + "]";
    }

}