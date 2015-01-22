package com.dsi11.grapp.dummy;

import android.app.Activity;
import android.graphics.Bitmap;

import com.dsi11.grapp.LocalDao;

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

    public static Map<String, TutorialItem> getItemMap(){
        if(ITEM_MAP.isEmpty())
            init();
        return ITEM_MAP;
    }

    public static List<TutorialItem> getItems(){
        if (ITEMS.isEmpty())
            init();
        return ITEMS;
    }

    private static void init(){
        // Add 3 sample items.
        addItem(new TutorialItem("001"));
        addItem(new TutorialItem("002"));
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
            Activity activity = LocalDao.activity;

            int titleIdentifier = activity.getResources().getIdentifier("tutorial_" + number + "_title", "strings", activity.getPackageName());
            int textIdentifier = activity.getResources().getIdentifier("tutorial_" + number + "_text", "string", activity.getPackageName());;
            int bitmapIdentifier = 0;
            title = activity.getString(titleIdentifier);
            text = activity.getString(textIdentifier);
            //bitmap = activity.getDrawable(bitmapIdentifier);
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
