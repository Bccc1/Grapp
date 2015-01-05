package com.dsi11.grapp;

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
        Gang gang = new Gang();
        gang.id="8RWHvwbf8f";
        gang.name="Bloods";
        gang.leader=player;
        player.gang=gang;
        savePlayer(player);
    }
    public static Player getPlayer(){
        return player;
    }

    public static void savePlayer(Player player) {
        LocalDao.player = player;
    }
}
