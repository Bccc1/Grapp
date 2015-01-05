package com.dsi11.grapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class DrawTagActivity extends ActionBarActivity {

    public static final String PARAM_COLOR = "color";
    public static final String PARAM_PATH = "path";
    private String mKey;
    private Button btnReset;
    private Button btnOk;
    private BrushView brushView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_tag);
        btnReset = (Button) findViewById(R.id.btn_reset);
        btnOk = (Button) findViewById(R.id.btn_ok);
        brushView = (BrushView) findViewById(R.id.brushView);

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

        Bundle b = getIntent().getExtras();
        if(b!=null){
            Integer color = b.getInt(PARAM_COLOR);
            if (color != null) {
                brushView.setColor(color);
            }
        }
    }

    private void finishWithResult(){
        Intent data = new Intent();
        data.putExtra(PARAM_PATH,brushView.getPath());
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
