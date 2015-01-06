package com.dsi11.grapp.Parse;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by David on 03.01.2015.
 */
@ParseClassName("TagImage")
public class PTagImage extends ParseObject {
    public static final String CLASS_NAME = "TagImage";
    public static final String COLUMN_ID = "objectId";
    public static final String COLUMN_IMAGE = "Image";

    public String getId(){
        return getObjectId();
    }

    public void setId(String id){
        setObjectId(id);
    }

    public byte[] getImage(){
        return getBytes(COLUMN_IMAGE);
    }

    public void setImage(byte[] image){
        put(COLUMN_IMAGE,image);
    }

    public static PTagImage createWithoutData(String id){
        return ParseObject.createWithoutData(PTagImage.class, id);
    }
}
