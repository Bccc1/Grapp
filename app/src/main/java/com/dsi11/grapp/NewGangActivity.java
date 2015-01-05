package com.dsi11.grapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.dsi11.grapp.Core.Gang;
import com.dsi11.grapp.Core.Player;
import com.dsi11.grapp.Core.TagImage;


public class NewGangActivity extends ActionBarActivity implements ColorPickerDialog.OnColorChangedListener {

    static final int EDIT_TAG_REQUEST = 1;

    private Button btnColorPicker;
    private ImageView imageViewTag;
    private Button btnCreateGang;
    private EditText editTextName;
    private Gang gang;
    private SerializablePath path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_gang);
        btnColorPicker = (Button) findViewById(R.id.newGang_btn_ColorPicker);
        btnColorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });
        imageViewTag = (ImageView) findViewById(R.id.newGang_imageView_tag);
        imageViewTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTagEditor();
            }
        });
        btnCreateGang = (Button) findViewById(R.id.newGang_btn_createGang);
        btnCreateGang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGang();
                finish();
            }
        });
        editTextName = (EditText) findViewById(R.id.newGang_editText_name);
        gang = new Gang();
        gang.color = Color.GRAY;
    }

    private void openTagEditor(){
        Intent tagIntent = new Intent(this,DrawTagActivity.class);
        tagIntent.putExtra(DrawTagActivity.PARAM_COLOR,btnColorPicker.getDrawingCacheBackgroundColor());
        startActivityForResult(tagIntent,EDIT_TAG_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == EDIT_TAG_REQUEST){
            if(resultCode == RESULT_OK){
                SerializablePath path = (SerializablePath) data.getSerializableExtra(DrawTagActivity.PARAM_PATH);
                this.path = path;
                refreshTagView();
            }
        }
    }

    private void refreshTagView(){
        Bitmap bm = TagImageHelper.tagAsBitmap(path, gang.color);
        imageViewTag.setImageBitmap(bm);
    }

    private void createGang(){
        //TODO Prüfe ob Felder leer sind
        TagImage tag = new TagImage();
        tag.image = path;
        gang.tag=tag;
        gang.name=editTextName.getText().toString();
        gang.leader=LocalDao.getPlayer();
        //TODO Prüfe ob Gang existiert -> Fehlerbehandlung

        Gang newGang = ParseDao.saveGang(gang);
        Player player = ParseDao.getPlayerById(LocalDao.getPlayer().id);
        player.gang = newGang;
        Player newPlayer = ParseDao.savePlayer(player);
        LocalDao.savePlayer(newPlayer);
    }

    private void openColorPicker(){
        ColorPickerDialog color = new ColorPickerDialog(this,this, "picker", Color.BLACK, btnColorPicker.getDrawingCacheBackgroundColor());
        color.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_gang, menu);
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

    @Override
    public void colorChanged(String key, int color) {
        gang.color=color;
        btnColorPicker.setBackgroundColor(color);
        //TODO Tag neuzeichnen
    }
}
