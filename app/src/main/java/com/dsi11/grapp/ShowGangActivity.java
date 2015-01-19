package com.dsi11.grapp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dsi11.grapp.Core.Gang;
import com.dsi11.grapp.Core.Player;

import java.util.ArrayList;
import java.util.List;


public class ShowGangActivity extends ActionBarActivity {

    public static final String PARAM_GANG = "gang";
    private ImageView imageViewTag;
    private TextView textViewGangName;
    private TextView textViewMemberList;
    private Button btnJoinGang;
    private Boolean userIsMember = false;
    private String gangId = null;
    private Player player = null;
    private Gang gang = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_gang);
        imageViewTag = (ImageView) findViewById(R.id.showGang_imageView_tag);
        textViewGangName = (TextView) findViewById(R.id.showGang_txt_gangName);
        textViewMemberList = (TextView) findViewById(R.id.showGang_txt_memberList);
        btnJoinGang = (Button) findViewById(R.id.showGang_btn_joinGang);
        btnJoinGang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinGang();
            }
        });

        Bundle b = getIntent().getExtras();
        if(b!=null){
            if(b.containsKey(PARAM_GANG)){
                gangId = b.getString(PARAM_GANG,null);
            }
        }
        fillView(gangId);
    }

    private void fillView(String gangId){
        player = LocalDao.getPlayer();
        userIsMember = (gangId == null || (player.gang!= null && player.gang.id == gangId));
        if(userIsMember){
            gang = player.gang;
            if(!(gang.tag!=null && gang.tag.id!=null)){
                LocalDao.loadPlayerById(player.id);
                player = LocalDao.getPlayer();
                gang = player.gang;
            }
            btnJoinGang.setVisibility(View.INVISIBLE);
        }else{
            gang = ParseDao.getGangById(gangId);
            btnJoinGang.setVisibility(View.VISIBLE);
        }
        textViewGangName.setText(gang.name);
        imageViewTag.setImageBitmap(TagImageHelper.tagAsBitmap(gang.tag.image,gang.color,TagImageHelper.RenderSettings.SquareZoomed));
        List<Player> players = ParseDao.getGangMembersByGangId(gang.id);
        StringBuilder playerString = new StringBuilder();
        playerString.append("Deine Grang\n\n");
        for(Player p : players){
            playerString.append(p.name);
            playerString.append("\n");
        }
        textViewMemberList.setText(playerString.toString());
    }

    private void joinGang(){
        player.gang = gang;
        player.leader=false;
        Player newPlayer = ParseDao.updatePlayer(player);
        LocalDao.savePlayer(newPlayer);
        setResult(RESULT_OK);
        finish();
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
