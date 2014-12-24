import intellimate.izou.addon.AddOn;
import intellimate.izou.main.Main;
import leanderk.izou.news.NewsAddOn;

import java.util.LinkedList;

/**
 * Use this class to debug
 */
public class Debug {
    public static void main(String[] args) {
        LinkedList<AddOn> addOns = new LinkedList<>();
        addOns.add(new NewsAddOn());
        Main main = new Main(addOns);
    }
}
