package com.dsi11.grapp;

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
    }

    public static void loadPlayerById(String id) {
        savePlayer(ParseDao.getPlayerById(id));
    }
}
