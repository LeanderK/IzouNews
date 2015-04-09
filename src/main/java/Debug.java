import leanderk.izou.news.NewsAddOn;
import leanderk.izou.tts.TTS;
import org.intellimate.izou.addon.AddOnModel;
import org.intellimate.izou.main.Main;

import java.util.LinkedList;

/**
 * Use this class to debug
 */
public class Debug {
    public static void main(String[] args) {
        LinkedList<AddOnModel> addOns = new LinkedList<>();
        addOns.add(new NewsAddOn());
        addOns.add(new TTS());
        Main main = new Main(addOns, true);
    }
}
