package com.dsi11.grapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dsi11.grapp.Core.Player;


public class NewUserActivity extends ActionBarActivity {
    EditText editTextUsername;
    static final int NEW_GANG_REQUEST = 1;
    static final int SELECT_GANG_REQUEST = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        editTextUsername = (EditText) findViewById(R.id.newUser_editText_username);
        //Prüfen ob lokal schon unvollständige Userdaten existieren und dann direkt zum Gangdialog
        if(LocalDao.getPlayer() != null){
            editTextUsername.setText(LocalDao.getPlayer().name);
            showGangDialog();
        }
    }

    private boolean usernameExists(String username){
        //TODO Frage DB ab, ob User schon existiert (Async?)
        return false;
    }

    private Player saveUser(Player player){
        Player newPlayer = ParseDao.addPlayer(player);
        LocalDao.savePlayer(newPlayer);
        return newPlayer;
    }

    public void onContinueButtonClicked(View view){
        String username = editTextUsername.getText().toString();
        if(usernameExists(username)){
            Toast.makeText(getApplicationContext(), getString(R.string.new_user_name_already_exists), Toast.LENGTH_LONG).show();
        }else{
            Player player = new Player();
            player.name = username;
            Player playerNew = saveUser(player);
            if(playerNew!=null){//TODO Fehlerbehandlung
                showGangDialog();
                //finish();//Was passiert, wenn der Dialog mit der Back taste abgebrochen wird?
            }
        }
    }
    /** Wahl ob neue Gang oder Gang beitreten */
    private void showGangDialog(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.title_dialog_choose_new_or_existing_gang))
                .setMessage(getString(R.string.dialog_message_choose_new_or_existing_gang))
                .setPositiveButton(getString(R.string.dialog_positive_choose_new_or_existing_gang), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), NewGangActivity.class);
                        startActivityForResult(intent, NEW_GANG_REQUEST);
                        dialog.cancel();
                    }
                })
                .setNegativeButton(getString(R.string.dialog_negative_choose_new_or_existing_gang), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), GangListActivity.class);
                        startActivityForResult(intent, SELECT_GANG_REQUEST);
                        dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == NEW_GANG_REQUEST){
            if(resultCode == RESULT_OK){
                finish();
            }
        }
        if(requestCode == SELECT_GANG_REQUEST){
            if(resultCode == RESULT_OK){
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_user, menu);
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
