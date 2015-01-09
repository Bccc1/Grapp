package com.dsi11.grapp.Core;

/**
 * Created by David on 03.01.2015.
 */
public class Gang {
    public String id;
    public String name;
    public TagImage tag;
    public Integer color;

    @Override
    public String toString() {
        return name != null ? name : (id == null ? super.toString()+" ID:null" : super.toString()+" ID:"+id);
    }
}
