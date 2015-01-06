package com.dsi11.grapp.Parse;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;

/**
 * Created by David on 03.01.2015.
 */
@ParseClassName("Player")
public class PPlayer extends ParseObject {
    public static final String CLASS_NAME = "Player";
    public static final String COLUMN_ID = "objectId";
    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_GANG = "Gang";

    public String getId(){
        return getObjectId();
    }

    public void setId(String id){
        setObjectId(id);
    }

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
        put(COLUMN_GANG,gang);
    }

    public String getGangId(){
        return getString(COLUMN_GANG);
    }

    public void setGangId(String id){
        put(COLUMN_GANG,ParseObject.createWithoutData(PGang.class,id));
    }

    public static PPlayer createWithoutData(String id){
        return ParseObject.createWithoutData(PPlayer.class, id);
    }

    public static PPlayer create(){
        return ParseObject.create(PPlayer.class);
    }
}
