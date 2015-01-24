package com.dsi11.grapp;

import android.graphics.Bitmap;

import com.dsi11.grapp.Core.Gang;
import com.dsi11.grapp.Parse.PGang;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by David on 24.01.2015.
 */
public class CachedParseData {

    private static Map<String, Gang> gangs = new HashMap<String, Gang>(); //key ist gangId
    private static Map<String,Bitmap> tagImageIcons = new HashMap<String,Bitmap>(); //key ist gangId

    public static Gang getGang(String gangId){
        //Um Requests zu sparen, wird alles auf ein Schlag geladen wenn noch nichts geladen ist
        if(gangs.isEmpty()){
            loadAllGangs();
        }
        //Ansonsten wird auf bedarf geladen
        if(gangs.containsKey(gangId)) {
            return gangs.get(gangId);
        }
        Gang gang = ParseDao.getGangById(gangId);
        if(gang != null){
            gangs.put(gang.id,gang);
        }
        return gang;
    }

    public static void loadAllGangs(){
        for(Gang g : ParseDao.getAllGangsWithTagImages()){
            gangs.put(g.id,g);
        }
    }

    public static Bitmap getTagImageIconByGang(String gangId){
        if(tagImageIcons.containsKey(gangId)){
            return tagImageIcons.get(gangId);
        }
        Gang gang = getGang(gangId);
        if(gang != null) {
            Bitmap bitmap  = TagImageHelper.tagAsBitmapIcon(gang.tag.image, gang.color);
            tagImageIcons.put(gangId, bitmap);
            return bitmap;
        }
        return null;
    }
}
