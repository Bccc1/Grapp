package com.dsi11.grapp.Parse;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import org.json.JSONObject;

import java.util.Date;

/**
 * Created by David on 03.01.2015.
 */
@ParseClassName("Tag")
public class PTag extends ParseObject {
    public static final String CLASS_NAME = "Tag";
    public static final String COLUMN_POSITION = "Position";
    public static final String COLUMN_TIMESTAMP = "Timestamp";
    public static final String COLUMN_GANG = "Gang";

    public ParseGeoPoint getPosition(){
        return getParseGeoPoint(COLUMN_POSITION);
    }

    public void setPosition(ParseGeoPoint position){
        put(COLUMN_POSITION,position);
    }

    public Date getTimestamp(){
        return getDate(COLUMN_TIMESTAMP);
    }

    public void setTimestamp(Date timestamp){
        put(COLUMN_TIMESTAMP,timestamp);
    }

    public String getGangId(){
        return getParseObject(COLUMN_GANG).getObjectId();
    }

    public void setGangId(String id){
        put(COLUMN_GANG,PGang.createWithoutData(id));
    }

    public PGang getGang(){
        return (PGang) getParseObject(COLUMN_GANG);
    }

    public void setGang(PGang gang){
        if(gang==null)
            put(COLUMN_GANG, JSONObject.NULL); //notwendig?
        put(COLUMN_GANG,gang);
    }

    public static PTag createWithoutData(String id){
        return ParseObject.createWithoutData(PTag.class, id);
    }

    public static PTag create(){
        return ParseObject.create(PTag.class);
    }
}
