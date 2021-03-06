package leanderk.izou.news.RSS;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*
 * Represents one RSS message
 */
public class FeedMessage {

    private String title;
    private String description;
    private String link;
    private String author;
    private String guid;
    private Feed feed;
    private String pubDate;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        if(description.contains("</p>")) {
            Document doc = Jsoup.parseBodyFragment(description);
            return doc.body().text();
        }
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Feed getFeed() {
        return feed;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

    public LocalDate getPubDate() {
        return LocalDate.parse(pubDate, DateTimeFormatter.RFC_1123_DATE_TIME);
    }

    public LocalDateTime getPubDateTime() {
        return LocalDateTime.parse(pubDate, DateTimeFormatter.RFC_1123_DATE_TIME);
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    @Override
    public String toString() {
        return "FeedMessage [title=" + title + ", description=" + description
                + ", link=" + link + ", author=" + author + ", guid=" + guid
                + "]";
    }

}