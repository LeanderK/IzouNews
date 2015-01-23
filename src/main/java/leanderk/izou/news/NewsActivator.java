package leanderk.izou.news;

import intellimate.izou.activator.Activator;
import intellimate.izou.events.Event;
import intellimate.izou.system.Context;
import intellimate.izou.system.IdentificationManager;
import leanderk.izou.news.RSS.Feed;
import leanderk.izou.news.RSS.RSSManager;

/**
 * @author LeanderK
 * @version 1.0
 */
public class NewsActivator extends Activator {
    public static final String ID = NewsActivator.class.getCanonicalName();
    private RSSManager rssManager;

    public NewsActivator(Context context, RSSManager rssManager) {
        super(context);
        this.rssManager = rssManager;
    }

    /**
     * Starting an Activator causes this method to be called.
     *
     * @throws InterruptedException will be caught by the Activator implementation, used to stop the Activator Thread
     */
    @Override
    public void activatorStarts() throws InterruptedException {
        //initialization
        rssManager.parseFeeds().forEach(Feed::getMessages);
        while (true) {
            if(rssManager.hasNewEntries()) {
                try {
                    IdentificationManager.getInstance().getIdentification(this)
                            .flatMap(id -> Event.createEvent(Event.NOTIFICATION, id))
                            .orElseThrow(() -> new IllegalStateException("Unable to create Event"))
                            .addDescriptor(NewsAddOn.EVENT_NEW_NEWS)
                            .fire(getCaller(), (event, counter) -> counter <= 3,
                                    event -> getContext().logger.getLogger().error("failed to fire Event"));
                } catch (IllegalStateException e) {
                    getContext().logger.getLogger().error("Unable to create Event");
                }
            }
            //sleep for 5 min
            Thread.sleep(300000);
        }
    }

    /**
     * This method gets called when the Activator Thread got exceptionThrown.
     * <p>
     * This is an unusual way of ending a thread. The main reason for this should be, that the activator was interrupted
     * by an uncaught exception.
     *
     * @param e if not null, the exception, which caused the termination
     * @return true if the Thread should be restarted
     */
    @Override
    public boolean terminated(Exception e) {
        getContext().logger.getLogger().error("NewsActivator crashed", e);
        return true;
    }

    /**
     * An ID must always be unique.
     * A Class like Activator or OutputPlugin can just provide their .class.getCanonicalName()
     * If you have to implement this interface multiple times, just concatenate unique Strings to
     * .class.getCanonicalName()
     *
     * @return A String containing an ID
     */
    @Override
    public String getID() {
        return ID;
    }
}
