package com.dsi11.grapp.Parse;

import com.dsi11.grapp.Core.Tag;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;

/**
 * Created by David on 03.01.2015.
 */
@ParseClassName("Gang")
public class PGang extends ParseObject {
    public static final String CLASS_NAME = "Gang";
    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_TAG = "Tag";
    public static final String COLUMN_COLOR = "Color";

    private void myFetcher(){
        try {
            fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    };

    public String getName(){
        return getString(COLUMN_NAME);
    }

    public void setName(String name){
        put(COLUMN_NAME,name);
    }

    public String getTagId(){
        return getString(COLUMN_TAG);
    }

    public void setTagId(String id){
        put(COLUMN_TAG,ParseObject.createWithoutData(PTag.class, id));
    }

    public PTagImage getTag(){
        try {
            return (PTagImage) getParseObject(COLUMN_TAG).fetch();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setTag(PTagImage tag){
        put(COLUMN_TAG,tag);
    }

    public Integer getColor(){
        return getInt(COLUMN_COLOR);
    }

    public void setColor(Integer color){
        put(COLUMN_COLOR,color);
    }

    public static PGang createWithoutData(String id){
        return ParseObject.createWithoutData(PGang.class, id);
    }

    public static PGang create(){
        return ParseObject.create(PGang.class);
    }
}
