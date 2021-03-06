package leanderk.izou.news;

import leanderk.izou.news.RSS.RSSManager;
import org.intellimate.izou.sdk.activator.Activator;
import org.intellimate.izou.sdk.addon.AddOn;
import org.intellimate.izou.sdk.contentgenerator.ContentGenerator;
import org.intellimate.izou.sdk.events.EventsController;
import org.intellimate.izou.sdk.output.OutputExtensionArgument;
import org.intellimate.izou.sdk.output.OutputPlugin;
import ro.fortsoft.pf4j.Extension;

/**
 * @author LeanderK
 * @version 1.0
 */
@Extension
public class NewsAddOn extends AddOn {
    public static final String ID = NewsAddOn.class.getCanonicalName();
    public static final String EVENT_NEW_NEWS = ID + ".NewNews";
    public static final String EVENT_NEWS = ID + ".News";

    private RSSManager rssManager;

    /**
     * the default constructor for AddOns
     */
    public NewsAddOn() {
        super(ID);
    }

    /**
     * use this method to build your instances etc.
     */
    @Override
    public void prepare() {
        rssManager = new RSSManager(getContext());
    }

    /**
     * use this method to register (if needed) your Activators.
     *
     * @return Array containing Instances of Activators
     */
    @Override
    public Activator[] registerActivator() {
        Activator[] activators = new Activator[1];
        activators[0] = new NewsActivator(getContext(), rssManager);
        return activators;
    }

    /**
     * use this method to register (if needed) your ContentGenerators.
     *
     * @return Array containing Instances of ContentGenerators
     */
    @Override
    public ContentGenerator[] registerContentGenerator() {
        ContentGenerator[] contentGenerators = new ContentGenerator[1];
        contentGenerators[0] = new NewsContentGenerator(getContext(), rssManager);
        return contentGenerators;
    }

    /**
     * use this method to register (if needed) your EventControllers.
     *
     * @return Array containing Instances of EventControllers
     */
    @Override
    public EventsController[] registerEventController() {
        return new EventsController[0];
    }

    /**
     * use this method to register (if needed) your OutputPlugins.
     *
     * @return Array containing Instances of OutputPlugins
     */
    @Override
    public OutputPlugin[] registerOutputPlugin() {
        return new OutputPlugin[0];
    }

    /**
     * use this method to register (if needed) your Output.
     *
     * @return Array containing Instances of OutputExtensions
     */
    @Override
    public OutputExtensionArgument[] registerOutputExtension() {
        OutputExtensionArgument[] outputExtensions = new OutputExtensionArgument[1];
        outputExtensions[0] = new NewsTTSOutputExtension(getContext());
        return outputExtensions;
    }
}
