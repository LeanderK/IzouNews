package leanderk.izou.news;

import leanderk.izou.news.RSS.RSSManager;
import org.intellimate.izou.sdk.Context;
import org.intellimate.izou.sdk.activator.Activator;
import org.intellimate.izou.sdk.events.Event;

/**
 * @author LeanderK
 * @version 1.0
 */
public class NewsActivator extends Activator {
    public static final String ID = NewsActivator.class.getCanonicalName();
    private RSSManager rssManager;

    public NewsActivator(Context context, RSSManager rssManager) {
        super(context, ID);
        this.rssManager = rssManager;
        //initialization
        rssManager.parseFeeds().forEach(feed -> feed.getMessages());
    }

    /**
     * Starting an Activator causes this method to be called.
     */
    @Override
    public void activatorStarts() {
        if(rssManager.hasNewEntries()) {
            boolean fire = fire(Event.NOTIFICATION, NewsAddOn.EVENT_NEW_NEWS);
            if (!fire)
                error("unable to fire Event");
        }
        //sleep for 5 min
        try {
            Thread.sleep(300000);
        } catch (InterruptedException e) {
            error("interrupted", e);
        }
    }
}
