package com.dsi11.grapp;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.dsi11.grapp.Core.Gang;
import com.dsi11.grapp.Core.Player;
import com.dsi11.grapp.Core.Tag;
import com.dsi11.grapp.Parse.PGang;
import com.dsi11.grapp.Parse.PPlayer;
import com.dsi11.grapp.Parse.PTag;
import com.dsi11.grapp.Parse.PTagImage;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.Parse;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable Local Datastore.
        ParseObject.registerSubclass(PGang.class);
        ParseObject.registerSubclass(PTag.class);
        ParseObject.registerSubclass(PPlayer.class);
        ParseObject.registerSubclass(PTagImage.class);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "SrnX83VPWR6P4iTiwvpjm5juIRACjkzWawoYcCii", "UwuqEExvBR3Qb7CKBBtBdY39421OewdU6R5q9YxD");

        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        //ParseObject testObject = new ParseObject("TestObject");
        //testObject.put("foo", "bar");
        //testObject.saveInBackground();

        //start the draw activity
        //startActivity(new Intent(this, DrawTagActivity.class));

        //start the newGang activity
        //startActivity(new Intent(this, NewGangActivity.class));

        //TODO Parse verhalten analysieren, dh create, update delete auf objekte mit referenzbäumen.

        LocalDao.initFakeData2();
        Player savedPlayer = ParseDao.savePlayer(LocalDao.getPlayer());

        if(isUserConfigured()){
            //TODO lade Nutzerdaten (Sinnvoll?)
        }else{
            Intent intent = new Intent(this, NewUserActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Sollte prüfen ob die Anwendung bereits konfiguriert wurde,
     * bzw ein User im lokalen Cache existiert
     */
    private boolean isUserConfigured() {
        Player player = LocalDao.getPlayer();
        return player != null && player.gang != null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
//        for(Tag t : createDummyTags()){
//            ParseDao.saveTag(t);
//        }
        List<Tag> tags;
        tags = ParseDao.getAllTags();
        for(Tag tag : tags){
            mMap.addMarker(new MarkerOptions().position(new LatLng(tag.latitude, tag.longitude)).title(tag.id));
            //TODO Bilder verwenden
        }
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }


    public static List<Tag> createDummyTags(){
        Gang g1 = new Gang();
        g1.id="8RWHvwbf8f"; //Bloods

        Gang g2 = new Gang();
        g1.id="3f2rWWjeZP"; //Crips

        List<Tag> tags = new ArrayList<Tag>();

        Tag t1 = new Tag();
        t1.latitude=52.852318;
        t1.longitude=8.723670;
        t1.gang=g1;
        t1.timestamp = Calendar.getInstance().getTime();
        tags.add(t1);

        Tag t2 = new Tag();
        t2.latitude=53.055261;
        t2.longitude=8.783041;
        t2.gang=g1;
        t2.timestamp = Calendar.getInstance().getTime();
        tags.add(t2);

        Tag t3 = new Tag();
        t3.latitude=53.072167;
        t3.longitude=8.792756;
        t3.gang=g1;
        t3.timestamp = Calendar.getInstance().getTime();
        tags.add(t3);

        Tag t4 = new Tag();
        t4.latitude=53.077838;
        t4.longitude=8.809849;
        t4.gang=g2;
        t4.timestamp = Calendar.getInstance().getTime();
        tags.add(t4);
        return tags;
    }
}
