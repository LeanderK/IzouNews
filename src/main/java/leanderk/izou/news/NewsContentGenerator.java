package leanderk.izou.news;

import leanderk.izou.news.RSS.RSSManager;
import org.intellimate.izou.events.EventModel;
import org.intellimate.izou.resource.ResourceModel;
import org.intellimate.izou.sdk.Context;
import org.intellimate.izou.sdk.contentgenerator.ContentGenerator;
import org.intellimate.izou.sdk.contentgenerator.EventListener;
import org.intellimate.izou.sdk.events.CommonEvents;
import org.intellimate.izou.sdk.resource.Resource;

import java.util.ArrayList;
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
     * this method is called when an object wants to get a Resource.
     * <p>
     * Don't use the Resources provided as arguments, they are just the requests.
     * There is a timeout after 1 second.
     * </p>
     *
     * @param list     a list of resources without data
     * @param optional if an event caused the action, it gets passed. It can also be null.
     * @return a list of resources with data
     */
    @Override
    public List<? extends Resource> triggered(List<? extends ResourceModel> list, Optional<EventModel> optional) {
        return optionalToList(createResource(RESOURCE_ID, rssManager.parseFeeds()));
    }

    /**
     * this method returns a List of EventListener, which indicate for which Events the ContentGenerator should be
     * triggered.
     *
     * @return a List of EventListeners
     */
    @Override
    public List<? extends EventListener> getTriggeredEvents() {
        List<EventListener> eventListeners = new ArrayList<>();
        CommonEvents commonEvents = CommonEvents.get(this);
        commonEvents.getResponse().fullResponseListener().ifPresent(eventListeners::add);
        commonEvents.getResponse().majorResponseListener().ifPresent(eventListeners::add);
        EventListener.createEventListener(
                NewsAddOn.EVENT_NEW_NEWS,
                "adds new news to the Event",
                "new_news",
                this
        ).ifPresent(eventListeners::add);
        EventListener.createEventListener(
                NewsAddOn.EVENT_NEWS,
                "adds an overview of the news to the Event",
                "news",
                this
        ).ifPresent(eventListeners::add);
        return eventListeners;
    }

    /**
     * This method is called to register what resources the object provides.
     * just pass a List of Resources without Data in it.
     *
     * @return a List containing the resources the object provides
     */
    @Override
    public List<? extends Resource> getTriggeredResources() {
        return optionalToList(createResource(RESOURCE_ID));
    }
}
