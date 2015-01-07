package com.dsi11.grapp.Parse;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;

import org.json.JSONObject;

/**
 * Created by David on 03.01.2015.
 */
@ParseClassName("Player")
public class PPlayer extends ParseObject {
    public static final String CLASS_NAME = "Player";
    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_GANG = "Gang";
    public static final String COLUMN_LEADER = "Leader";

    public String getName(){
        return getString(COLUMN_NAME);
    }

    public void setName(String name){
        put(COLUMN_NAME,name);
    }

    public PGang fetchGang(){
        try{
            return (PGang) getParseObject(COLUMN_GANG).fetch();
        }catch (ParseException e){
            e.printStackTrace();
        }
        return null;
    }

    public PGang getGang(){
        return (PGang) getParseObject(COLUMN_GANG);
    }

    public void setGang(PGang gang){
        if(gang==null)
            put(COLUMN_GANG,JSONObject.NULL); //notwendig?
        put(COLUMN_GANG,gang);
    }

    public String getGangId(){
        return getString(COLUMN_GANG);
    }

    public void setGangId(String id){
        put(COLUMN_GANG,ParseObject.createWithoutData(PGang.class,id));
    }

    public void setLeader(boolean leader){
        put(COLUMN_LEADER,leader);
    }

    public boolean isLeader(){
        return getBoolean(COLUMN_LEADER);
    }

    public static PPlayer createWithoutData(String id){
        return ParseObject.createWithoutData(PPlayer.class, id);
    }

    public static PPlayer create(){
        return ParseObject.create(PPlayer.class);
    }
}
