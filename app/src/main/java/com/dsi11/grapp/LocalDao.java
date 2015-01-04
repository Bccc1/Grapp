package com.dsi11.grapp;

import com.dsi11.grapp.Core.Gang;
import com.dsi11.grapp.Core.Player;

/**
 * Created by David on 03.01.2015.
 */
public class LocalDao {
    public static Player getPlayer(){
        Player player = new Player();
        player.name="Player4Life";
        player.id="6jI0lYlUWB";
        Gang gang = new Gang();
        gang.id="8RWHvwbf8f";
        gang.name="Bloods";
        gang.leader=player;
        player.gang=gang;
        return player;
    }
}
