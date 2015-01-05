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
    public static final String COLUMN_ID = "objectId";
    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_TAG = "Tag";
    public static final String COLUMN_COLOR = "Color";
    public static final String COLUMN_LEADER = "Leader";

    public String getId(){
        return getString(COLUMN_ID);
    }

    public void setId(String id){
        put(COLUMN_ID,id);
    }

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

    public PTag getTag(){
        try {
            return (PTag) getParseObject(COLUMN_TAG).fetch();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setTag(PTag tag){
        put(COLUMN_TAG,tag);
    }

    public Integer getColor(){
        return getInt(COLUMN_COLOR);
    }

    public void setColor(Integer color){
        put(COLUMN_COLOR,color);
    }

    public String getLeaderId(){
        return getString(COLUMN_LEADER);
    }

    public void setLeaderId(String id){
        put(COLUMN_LEADER,ParseObject.createWithoutData(PPlayer.class,id));
    }

    public PPlayer getLeader(){
        try{
            return (PPlayer) getParseObject(COLUMN_LEADER).fetch();
        }catch (ParseException e){
            e.printStackTrace();
        }
        return null;
    }

    public void setLeader(PPlayer player){
        put(COLUMN_LEADER,player);
    }

    public static PGang createWithoutData(String id){
        return ParseObject.createWithoutData(PGang.class, id);
    }
}
