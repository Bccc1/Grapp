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

import java.util.ArrayList;
import java.util.List;


public class GangListActivity extends ActionBarActivity {

    static final int SHOW_GANG_FOR_JOINING_REQUEST = 5;
    private ListView listViewGangs;
    private ArrayList<Gang> gangs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gang_list);
        listViewGangs = (ListView) findViewById(R.id.gangList_listView_gangs);
        gangs = new ArrayList<>(ParseDao.getAllGangs());
        listViewGangs.setAdapter(new ArrayAdapter<Gang>(this, android.R.layout.simple_list_item_1,gangs));
        listViewGangs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Gang gang = gangs.get(position);
                Intent intent = new Intent(getApplicationContext(),ShowGangActivity.class);
                intent.putExtra(ShowGangActivity.PARAM_GANG,gang.id);
                startActivityForResult(intent,SHOW_GANG_FOR_JOINING_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SHOW_GANG_FOR_JOINING_REQUEST){
            if(resultCode == RESULT_OK){
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gang_list, menu);
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
