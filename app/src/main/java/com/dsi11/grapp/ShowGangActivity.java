package com.dsi11.grapp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.dsi11.grapp.Core.Gang;
import com.dsi11.grapp.Core.Player;

import java.util.ArrayList;
import java.util.List;


public class ShowGangActivity extends ActionBarActivity {

    private ImageView imageViewTag;
    private TextView textViewGangName;
    private TextView textViewMemberList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_gang);
        imageViewTag = (ImageView) findViewById(R.id.showGang_imageView_tag);
        textViewGangName = (TextView) findViewById(R.id.showGang_txt_gangName);
        textViewMemberList = (TextView) findViewById(R.id.showGang_txt_memberList);
        fillView();
    }

    private void fillView(){
        Player player = LocalDao.getPlayer();
        Gang gang = player.gang;
        if(!(gang.tag!=null && gang.tag.id!=null)){
            LocalDao.loadPlayerById(player.id);
            player = LocalDao.getPlayer();
        }
        textViewGangName.setText(gang.name);
        imageViewTag.setImageBitmap(TagImageHelper.tagAsBitmap(gang.tag.image,gang.color,TagImageHelper.RenderSettings.SquareZoomed));
        List<Player> players = ParseDao.getGangMembersByGangId(gang.id);
        String playerString = "";
        for(Player p : players){
            playerString += p.name;
            playerString += "\n";
        }
        textViewMemberList.setText(playerString);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_gang, menu);
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
