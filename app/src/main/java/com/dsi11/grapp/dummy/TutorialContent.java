package com.dsi11.grapp.dummy;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.dsi11.grapp.LocalDao;
import com.dsi11.grapp.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class TutorialContent {

    public static List<TutorialItem> ITEMS = new ArrayList<TutorialItem>();
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
        addItem(new TutorialItem("001"));   //Simple Item, 1 Text, 0 or 1 Images
        addItem(new TutorialItem("002"));
        addItem(new TutorialItem("003"));
        addItem(new TutorialItem("004"));
        addItem(new TutorialItem("005"));
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
        public Bitmap picture;
        public List<TutorialEntrySection> content = new ArrayList<TutorialEntrySection>();
        public boolean simple;

        public TutorialItem(String id){
            simple = true;
            String number = id; //TODO id in dreistelliges Format überführen -> 001
            this.id = id;
            Activity activity = LocalDao.activity;

            int titleIdentifier = activity.getResources().getIdentifier("tutorial_" + number + "_title", "string", activity.getPackageName());
            int textIdentifier = activity.getResources().getIdentifier("tutorial_" + number + "_text", "string", activity.getPackageName());
            int bitmapIdentifier = activity.getResources().getIdentifier("tutorial_" + number, "drawable", activity.getPackageName());
            title = activity.getString(titleIdentifier);
            text = activity.getString(textIdentifier);
            picture = BitmapFactory.decodeResource(activity.getResources(),bitmapIdentifier);
//            title = activity.getString(R.string.tutorial_001_title);
//            text = activity.getString(R.string.tutorial_001_text);
        }

        /**
         * Zu nutzen um ein TutorialItem zu erstellen, welches mehrere Texte oder Bilder hat.
         * @param id die id, also 001 für "tutorial_001_text_005"
         * @param maxNumber die höchste Zahl die hinten im Ressource namen vorkommt, also 005 für "tutorial_001_text_005"
         */
        public TutorialItem(String id, int maxNumber){
            simple = false;
            String number = id; //TODO id in dreistelliges Format überführen -> 001
            this.id = id;
            Activity activity = LocalDao.activity;

            int titleIdentifier = activity.getResources().getIdentifier("tutorial_" + number + "_title", "string", activity.getPackageName());
            title = activity.getString(titleIdentifier);

            for(int i = 1; i<=maxNumber; i++){
                String counter = String.format("%03d", i);
                int textIdentifier = activity.getResources().getIdentifier("tutorial_" + number + "_text_"+counter, "string", activity.getPackageName());
                int bitmapIdentifier = activity.getResources().getIdentifier("tutorial_" + number+"_picture_"+counter, "drawable", activity.getPackageName());
                if(textIdentifier!=0){
                    text = activity.getString(textIdentifier);
                    if(text !=null)
                        content.add(new TutorialEntryText(text));
                }
                if(bitmapIdentifier != 0){
                    picture = BitmapFactory.decodeResource(activity.getResources(),bitmapIdentifier);
                    if(picture!=null)
                        content.add(new TutorialEntryPicture(picture));
                }

            }



//            title = activity.getString(R.string.tutorial_001_title);
//            text = activity.getString(R.string.tutorial_001_text);
        }


        @Override
        public String toString() {
            return title;
        }
    }


    public interface TutorialEntrySection {
        public enum TutorialEntrySectionType {TEXT,PICTURE};
        public TutorialEntrySectionType getType();
        public Object getContent();
    }

    public static class TutorialEntryText implements TutorialEntrySection {

        String text;

        public TutorialEntryText(String text){
            this.text = text;
        }

        @Override
        public TutorialEntrySectionType getType() {
            return TutorialEntrySectionType.TEXT;
        }

        @Override
        public Object getContent() {
            return text;
        }
    }

    public static class TutorialEntryPicture implements TutorialEntrySection {

        Bitmap picture;

        public TutorialEntryPicture(Bitmap picture){
            this.picture = picture;
        }

        @Override
        public TutorialEntrySectionType getType() {
            return TutorialEntrySectionType.PICTURE;
        }

        @Override
        public Object getContent() {
            return picture;
        }
    }
}
