package com.dsi11.grapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;

import com.dsi11.grapp.Core.Gang;
import com.dsi11.grapp.Core.Player;

/**
 * Created by David on 03.01.2015.
 */
public class LocalDao {
    /** TODO Dummy speicher, soll durch SQLite DB ersetzt werden */
    private static Player player = null;
    private static Activity activity;
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String PREFS_PLAYER_ID = "PlayerId";

    public static void init(Activity activity){
        LocalDao.activity = activity;
        SharedPreferences settings = activity.getSharedPreferences(PREFS_NAME, 0);
        String playerId = settings.getString(PREFS_PLAYER_ID,null);
        loadPlayerById(playerId);
    }


    public static void initFakeData(){
        Player player = new Player();
        player.name="Player4Life";
        player.id="6jI0lYlUWB";
        player.leader=true;
        Gang gang = new Gang();
        gang.id="8RWHvwbf8f";
        gang.name="Bloods";
        player.gang=gang;
        savePlayer(player);
    }

    public static void initFakeData2(){
        Player player = new Player();
        player.name="Testplayer";
        player.leader=true;
        Gang gang = new Gang();
        gang.name="Testgang";
        gang.color= Color.MAGENTA;
        player.gang=gang;
        savePlayer(player);
    }
    public static Player getPlayer(){
        return player;
    }



    public static void savePlayer(Player player) {
        LocalDao.player = player;
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = activity.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PREFS_PLAYER_ID, player.id);

        // Commit the edits!
        editor.commit();

    }

    public static void loadPlayerById(String id) {
        if(id!=null) {
            savePlayer(ParseDao.getPlayerByIdWithAllData(id));
        }
    }

    public static void reset() {
        SharedPreferences settings = activity.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PREFS_PLAYER_ID, null);

        // Commit the edits!
        editor.commit();
    }
}
