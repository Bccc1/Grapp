package com.dsi11.grapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.dsi11.grapp.Core.Gang;
import com.dsi11.grapp.Core.Player;
import com.dsi11.grapp.Core.Tag;
import com.dsi11.grapp.Parse.PGang;
import com.dsi11.grapp.Parse.PPlayer;
import com.dsi11.grapp.Parse.PTag;
import com.dsi11.grapp.Parse.PTagImage;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.Parse;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MapsActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private ImageView imageViewSprayBtn;
    private static final String TAG = "MapsActivity";

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
        imageViewSprayBtn = (ImageView) findViewById(R.id.maps_imageView_sprayBtn);
        imageViewSprayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spray();
            }
        });
        setUpMapIfNeeded();

        //ParseObject testObject = new ParseObject("TestObject");
        //testObject.put("foo", "bar");
        //testObject.saveInBackground();

        //start the draw activity
        //startActivity(new Intent(this, DrawTagActivity.class));

        //start the newGang activity
        //startActivity(new Intent(this, NewGangActivity.class));

        //LocalDao.initFakeData2();
        //Player savedPlayer = ParseDao.addPlayer(LocalDao.getPlayer());

        //LocalDao.loadPlayerById("MiOAHt5gai");
        LocalDao.init(this);

        if(isUserConfigured()){
            //TODO lade Nutzerdaten (Sinnvoll?)
        }else{
            Intent intent = new Intent(this, NewUserActivity.class);
            startActivity(intent);
        }

        buildGoogleApiClient();
    }

    private void spray() {
        if(mLastLocation != null) {//TODO Sprayen ist möglich Prüfung
            Tag tag = new Tag();
            Player player = LocalDao.getPlayer();
            tag.gang=player.gang;
            tag.latitude=mLastLocation.getLatitude();
            tag.longitude=mLastLocation.getLongitude();
            tag.timestamp=Calendar.getInstance().getTime();
            ParseDao.addTag(tag);
            setUpMap();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
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
        mMap.clear();
        List<Tag> tags;
        tags = ParseDao.getAllTags();
        for(Tag tag : tags){
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(tag.latitude, tag.longitude))
                    .title(tag.gang.name+" - "+tag.id))
                    .setIcon(BitmapDescriptorFactory.fromBitmap(TagImageHelper.tagAsBitmapIcon(tag.gang.tag.image,tag.gang.color)));
        }
        showUserPos();
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        showUserPos();
    }

    private void showUserPos(){
        if (mLastLocation != null) {
            LatLng position = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(position).title("Meine Position"));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(position)
                    .zoom(15)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    /*
    * Called by Google Play services if the connection to GoogleApiClient drops because of an
    * error.
    */
    public void onDisconnected() {
        Log.i(TAG, "Disconnected");
    }

    @Override
    public void onConnectionSuspended(int cause) {
    // The connection to Google Play services was lost for some reason. We call connect() to
    // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }
}
