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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        //TODO Prüfen ob lokal schon unvollständige Userdaten existieren und dann direkt zum Gangdialog
    }

    private boolean usernameExists(String username){
        //TODO Frage DB ab, ob User schon existiert
        return false;
    }

    private Player saveUser(Player player){
        ParseDao.savePlayer(player);
        //Eigentlich sollte hier das Ergebnis von savePlayer zurückgebeben werden,
        //allerdings ist das evtl noch kaputt
        return player;
    }

    public void onContinueButtonClicked(View view){
        String username = editTextUsername.getText().toString();
        if(usernameExists(username)){
            Toast.makeText(getApplicationContext(), "Der Name ist schon vergeben.", Toast.LENGTH_LONG).show();
        }else{
            Player player = new Player();
            player.name = username;
            Player playerNew = saveUser(player);
            if(playerNew!=null){
                //Wahl ob neue Gang oder Gang beitreten
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(this)
                        .setTitle("Die Gang")
                        .setMessage("Willst du eine eigene Gang gründen oder dich einer bestehenden anschließen?")
                        .setPositiveButton("Neue Gang", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getApplicationContext(), NewGangActivity.class);
                                startActivity(intent);
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("bestehender Gang anschließen", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //TODO Gang übersicht Activity starten
                                dialog.cancel();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert);
                AlertDialog alert11 = builder1.create();
                alert11.show();
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
