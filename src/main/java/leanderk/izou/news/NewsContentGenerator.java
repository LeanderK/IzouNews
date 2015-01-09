package leanderk.izou.news;

import intellimate.izou.contentgenerator.ContentGenerator;
import intellimate.izou.events.Event;
import intellimate.izou.resource.Resource;
import intellimate.izou.system.Context;
import intellimate.izou.system.IdentificationManager;
import leanderk.izou.news.RSS.Feed;
import leanderk.izou.news.RSS.RSSManager;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author LeanderK
 * @version 1.0
 */
public class NewsContentGenerator extends ContentGenerator {
    public static final String ID = NewsContentGenerator.class.getCanonicalName();
    public static final String RESOURCE_ID = ID + ".Resource";

    private RSSManager rssManager;

    public NewsContentGenerator(Context context, RSSManager rssManager) {
        super(ID, context);
        this.rssManager = rssManager;
    }

    /**
     * this method is called to register what resources the object provides.
     * just pass a List of Resources without Data in it.
     *
     * @return a List containing the resources the object provides
     */
    @Override
    public List<Resource> announceResources() {
        return IdentificationManager.getInstance().getIdentification(this)
                .map(id -> new Resource<List<Feed>>(RESOURCE_ID, id))
                .orElse(new Resource<List<Feed>>(RESOURCE_ID))
                .map(Arrays::asList);
    }

    /**
     * this method is called to register for what Events it wants to provide Resources.
     *
     * @return a List containing ID's for the Events
     */
    @Override
    public List<String> announceEvents() {
        return Arrays.asList(Event.FULL_WELCOME_EVENT,
                Event.MAJOR_WELCOME_EVENT,
                NewsAddOn.EVENT_NEW_NEWS,
                NewsAddOn.EVENT_NEWS);
    }

    /**
     * this method is called when an object wants to get a Resource.
     * it has as an argument resource instances without data, which just need to get populated.
     *
     * @param resources a list of resources without data
     * @param event     if an event caused the action, it gets passed. It can also be null.
     * @return a list of resources with data
     */
    @Override
    public List<Resource> provideResource(List<Resource> resources, Optional<Event> event) {
        return IdentificationManager.getInstance().getIdentification(this)
                .map(id -> new Resource<List<Feed>>(RESOURCE_ID, id))
                .orElse(new Resource<List<Feed>>(RESOURCE_ID))
                .setResource(rssManager.parseFeeds())
                .map(Arrays::asList);
    }
}
