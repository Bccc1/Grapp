package com.dsi11.grapp.Core;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polygon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by David on 10.01.2015.
 */
public class GangRegion{
    public static float gridLongitude =  	0.0000425f; //longitude ^= X-Achse, -180° - 180° //old 0.0085f;
    public static float gridLatitude =  	0.000025f; //latitude ^= Y-Achse, -90° - 90° //old 0.00005f;
    boolean somethingChanged = true;
    Map<String,Integer> counts = new HashMap<>();
    //gebiet ist identifiziert über xy als erster eckpunkt des gebiets.
    //hierbei gilt eine betrachtung von -180/-90 nach 180/90.
    public int x, y; // gebietbegin geteilt durch gridLong/gridLat
    LatLngBounds bounds;
    ArrayList<Tag> tags = new ArrayList<Tag>();

    String bossId;
    Integer bossCount = 0;
    Gang currentlyRuledBy;

    public Polygon regionPolygon;

    public void setXY(int x, int y){
        this.x=x;
        this.y=y;
        bounds = new LatLngBounds(new LatLng(y*gridLatitude,x*gridLongitude),new LatLng((y+1)*gridLatitude,(x+1)*gridLongitude));
    }

    public int getBackgroundColor() {
        if (currentlyRuledBy != null) {
            int transparentColor = getTransparentColor(currentlyRuledBy.color, 150);
            return transparentColor;
        } else {
            return getTransparentColor(Color.GRAY,150);
        }
    }

    private int getTransparentColor(int color, int transparency){
        return Color.argb(transparency,
                Color.red(color),
                Color.green(color),
                Color.blue(color));
    }

    public int getColor(){
        if(currentlyRuledBy!=null){
            return currentlyRuledBy.color;
        }else{
            return Color.GRAY;
        }
    }

    public boolean inRegion(LatLng latLng){
        return bounds.contains(latLng);
    }

    public void addTag(Tag tag){
        tags.add(tag);
        somethingChanged = true;
    }

    public List<Tag> getTags(){
        return tags;
    }

    public LatLng getLeftBottom(){
        return new LatLng(y*gridLatitude,x*gridLongitude);
    }

    public LatLng getRightBottom(){
        return new LatLng(y*gridLatitude,(x+1)*gridLongitude);
    }

    public LatLng getLeftTop(){
        return new LatLng((y+1)*gridLatitude,x*gridLongitude);
    }

    public LatLng getRightTop(){
        return new LatLng((y+1)*gridLatitude,(x+1)*gridLongitude);
    }



    /** simply returns the Gang with the most tags */
    public Gang whoIsTheBoss(){
        if(somethingChanged) {
            counts = new HashMap<>();
            for (Tag t : tags) {
                if (counts.containsKey(t.gang.id)) {
                    counts.put(t.gang.id, counts.get(t.gang.id) + 1);
                } else {
                    counts.put(t.gang.id, 1);
                }
            }
            for(Map.Entry<String, Integer> e : counts.entrySet()){
                if(e.getValue()>bossCount) {
                    bossId = e.getKey();
                    bossCount = e.getValue();
                }
            }

            for(Tag t : tags){
                if(t.gang.id.equals(bossId)){
                    currentlyRuledBy = t.gang;
                    break;
                }
            }
            somethingChanged = false;
        }

        return currentlyRuledBy;
    }

}
