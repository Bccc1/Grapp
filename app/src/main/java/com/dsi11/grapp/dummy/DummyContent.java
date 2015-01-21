package com.dsi11.grapp.dummy;

import android.app.Activity;
import android.graphics.Bitmap;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static List<TutorialItem> ITEMS = new ArrayList<TutorialItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, TutorialItem> ITEM_MAP = new HashMap<String, TutorialItem>();

    static {
        // Add 3 sample items.
        addItem(new TutorialItem("1", "Item 1"));
        addItem(new TutorialItem("2", "Item 2"));
        addItem(new TutorialItem("3", "Item 3"));
    }

    private static void addItem(TutorialItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class TutorialItem {
        public String id;
        public String title;
        public String text;
        public Bitmap bitmap;
        public String content;

        public TutorialItem(String id, String content) {
            this.id = id;
            this.content = content;
        }

        public TutorialItem(String id){
            String number = id; //TODO id in dreistelliges Format überführen -> 001
            this.id = id;
            Activity activity = null;
            //title = get ressource by name : "tutorial_+number+"_title"
            //text = get ressource by name : "tutorial_+number+"_text"
            //bitmap = get ressource by name : "tutorial_+number+"_picture"
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
