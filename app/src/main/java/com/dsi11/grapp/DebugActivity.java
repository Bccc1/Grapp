package com.dsi11.grapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dsi11.grapp.Parse.PGang;
import com.dsi11.grapp.Parse.PPlayer;
import com.dsi11.grapp.Parse.PTag;
import com.dsi11.grapp.Parse.PTagImage;
import com.parse.Parse;
import com.parse.ParseObject;


public class DebugActivity extends ActionBarActivity {

    private Button btnReset;
    private Button btnStart;
    private Button btnStartWOTagLoading;
    private Button btnStartHelp;
    private Button btnSetPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        btnReset = (Button) findViewById(R.id.debug_btn_reset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalDao.activity = DebugActivity.this;
                LocalDao.reset();
                Toast.makeText(DebugActivity.this,"Player successfully reset",Toast.LENGTH_LONG).show();
            }
        });
        btnStart = (Button) findViewById(R.id.debug_btn_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGrapp();
            }
        });
        btnStartWOTagLoading = (Button) findViewById(R.id.debug_btn_startWOTagLoading);
        btnStartWOTagLoading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGrappWithoutLoadingTags();
            }
        });
        btnStartHelp = (Button) findViewById(R.id.debug_btn_help);
        btnStartHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startHelpSection();
            }
        });
        btnSetPlayer = (Button) findViewById(R.id.debug_btn_setPlayer);
        btnSetPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPlayer();
            }
        });
    }

    private void setPlayer() {
        if(!LocalDao.parseInitialized.get()) {
            // Enable Local Datastore.
            ParseObject.registerSubclass(PGang.class);
            ParseObject.registerSubclass(PTag.class);
            ParseObject.registerSubclass(PPlayer.class);
            ParseObject.registerSubclass(PTagImage.class);
            Parse.enableLocalDatastore(DebugActivity.this);
            Parse.initialize(DebugActivity.this, "SrnX83VPWR6P4iTiwvpjm5juIRACjkzWawoYcCii", "UwuqEExvBR3Qb7CKBBtBdY39421OewdU6R5q9YxD");
            LocalDao.parseInitialized.set(true);
        }
        LocalDao.activity = this;
        Intent intent = new Intent(this, PlayerListActivity.class);
        startActivity(intent);
    }

    private void startGrapp() {
        startActivity(new Intent(this,MapsActivity.class));
        finish();
    }

    private void startGrappWithoutLoadingTags() {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(MapsActivity.PARAM_DEBUG_DONT_LOAD_TAGS,true);
        startActivity(intent);
        finish();
    }

    private void startHelpSection(){
        LocalDao.activity = this;
        Intent intent = new Intent(this, TutorialEntryListActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_debug, menu);
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
