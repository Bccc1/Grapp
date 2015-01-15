package com.dsi11.grapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.dsi11.grapp.Core.Tag;

import java.io.IOException;
import java.util.ArrayList;


public class DrawTagActivity extends ActionBarActivity {

    public static final String PARAM_COLOR = "color";
    public static final String PARAM_PATH = "path";
    public static final String PARAM_SPRAY_MODE = "sprayMode";
    private Button btnReset;
    private Button btnOk;
    private Button btnUndo;
    private BrushView brushView;
    private Tag tag;
    private boolean sprayMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_tag);
        btnReset = (Button) findViewById(R.id.btn_reset);
        btnOk = (Button) findViewById(R.id.btn_ok);
        brushView = (BrushView) findViewById(R.id.brushView);
        btnUndo = (Button) findViewById(R.id.drawTag_btn_undo);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                brushView.reset();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWithResult();
            }
        });
        btnUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                brushView.undo();
            }
        });


        Bundle b = getIntent().getExtras();
        if(b!=null){
            Integer color = b.getInt(PARAM_COLOR);
            if (color != null) {
                brushView.setColor(color);
            }
            if(b.containsKey(PARAM_SPRAY_MODE)){
                sprayMode = b.getBoolean(PARAM_SPRAY_MODE);
            }
            if(b.containsKey(PARAM_PATH)){
                SerializablePath path = (SerializablePath) b.getSerializable(PARAM_PATH);
                if(path!=null){
                    if(sprayMode) { //Taggen aus der Map
                        brushView.setBackgroundPath(path);
                    }else{  //Tag Editor für die Gang
                        brushView.setPath(path);
                    }
                }
            }
        }
    }

    private void finishWithResult(){
        Intent data = new Intent();
        Bundle b = new Bundle();
        byte[] ba = null;
        try {
            ba = Utils.serialize(brushView.getPath());
            if(ba != null)
                Log.i("DrawTagActivity", "serialization successfull");
            SerializablePath newPath = (SerializablePath) Utils.deserialize(ba);
            if(newPath != null){
                Log.i("DrawTagActivity", "deserialization successfull");
            }

            Log.i("DrawTagActivity", "test successfull");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        b.putSerializable(PARAM_PATH, brushView.getPath());
        data.putExtras(b);
        setResult(RESULT_OK,data);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_draw_tag, menu);
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
