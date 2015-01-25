package com.dsi11.grapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dsi11.grapp.Core.Gang;
import com.dsi11.grapp.Core.Player;

import java.util.ArrayList;


public class PlayerListActivity extends ActionBarActivity {

    private ListView listViewPlayers;
    private ArrayList<Player> players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_list);

        listViewPlayers = (ListView) findViewById(R.id.playerList_listView_players);
        players = new ArrayList<Player>(ParseDao.getAllPlayers());

        listViewPlayers.setAdapter(new ArrayAdapter<Player>(this, android.R.layout.simple_list_item_1,players));
        listViewPlayers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Player player = players.get(position);
//                Intent intent = new Intent(getApplicationContext(),ShowGangActivity.class);
//                intent.putExtra(ShowGangActivity.PARAM_GANG,gang.id);
//                startActivityForResult(intent,SHOW_GANG_FOR_JOINING_REQUEST);
                LocalDao.savePlayer(player);
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_player_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
