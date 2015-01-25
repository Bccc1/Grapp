package com.dsi11.grapp.Core;

/**
 * Created by David on 03.01.2015.
 */
public class Player {


    public String id;
    public String name;
    public Gang gang;
    public Boolean leader;

    @Override
    public String toString() {
        return name != null ? name : (id == null ? super.toString()+" ID:null" : super.toString()+" ID:"+id);
    }
}
