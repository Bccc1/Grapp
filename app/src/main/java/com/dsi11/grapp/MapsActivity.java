package com.dsi11.grapp;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.dsi11.grapp.Core.GangRegion;
import com.dsi11.grapp.Core.Player;
import com.dsi11.grapp.Core.Tag;
import com.dsi11.grapp.Parse.PGang;
import com.dsi11.grapp.Parse.PPlayer;
import com.dsi11.grapp.Parse.PTag;
import com.dsi11.grapp.Parse.PTagImage;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.parse.Parse;
import com.parse.ParseObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MapsActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    static final int SPRAY_TAG_REQUEST = 5;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private ImageView imageViewSprayBtn;
    private ImageView imageViewSplashScreen;
    private ImageButton imageButtonShowGang;
    private Button btnReset;
    private static final String TAG = "MapsActivity";
    private List<GangRegion> regions = new ArrayList<>();
    private List<Tag> tags;
    private List<Tag> oldTags = new ArrayList<Tag>();
    private LocationRequest mLocationRequest;
    private String mLastUpdateTime;
    private Marker userPos;
    private Tag mTempTag;
    private AtomicBoolean parseInitialized = new AtomicBoolean(false);

    @Override
    protected void onDestroy() {
        //Debug.stopMethodTracing();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Debug.startMethodTracing("GrappStartup");

        setContentView(R.layout.activity_maps);
        imageViewSprayBtn = (ImageView) findViewById(R.id.maps_imageView_sprayBtn);
        imageViewSprayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spray();
            }
        });
        imageViewSplashScreen = (ImageView) findViewById(R.id.maps_imageView_splashScreen);
        imageButtonShowGang = (ImageButton) findViewById(R.id.maps_imageButton_showGang);
        imageButtonShowGang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGang();
            }
        });
        btnReset = (Button) findViewById(R.id.maps_btn_reset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetApp();
            }
        });


        //TODO START Async Task here
        AsyncStartup asyncStartup = new AsyncStartup();
        asyncStartup.execute();

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

        showSplashScreen();
    }

    private void showSplashScreen(){
        imageViewSplashScreen.setVisibility(View.VISIBLE);
    }
    private void hideSplashScreen(){
        imageViewSplashScreen.setVisibility(View.INVISIBLE);
    }

    public void afterLoadingFinished(){
        if(isUserConfigured()){
            //TODO lade Nutzerdaten (Sinnvoll?)
        }else{
            Intent intent = new Intent(this, NewUserActivity.class);
            startActivity(intent);
        }
        buildGoogleApiClient();
        setUpMapIfNeeded();
        mGoogleApiClient.connect();

        hideSplashScreen();
    }

    private void resetApp() {
        LocalDao.reset();
    }

    private void spray() {
        if(mLastLocation != null) {//Sprayen ist möglich Prüfung
            if(checkSprayable()){
                Tag tag = new Tag();
                Player player = LocalDao.getPlayer();
                tag.gang=player.gang;
                tag.latitude=mLastLocation.getLatitude();
                tag.longitude=mLastLocation.getLongitude();
                tag.timestamp=Calendar.getInstance().getTime();
                mTempTag = tag; //ekliger workaround, da ich den Tag nicht an die Activity geben will wg Serializable und so

                Intent tagIntent = new Intent(this,DrawTagActivity.class);
                tagIntent.putExtra(DrawTagActivity.PARAM_COLOR,player.gang.color);
                //Es sollte ein Flag gesetzt werden, das es sich ums taggen handelt
                tagIntent.putExtra(DrawTagActivity.PARAM_SPRAY_MODE,true);
                //Der Pfad sollte mitgeliefert werden, um den Hintergrund zu zeichnen
                if(tag.gang.tag.image!=null){
                    tagIntent.putExtra(DrawTagActivity.PARAM_PATH,tag.gang.tag.image);
                }
                startActivityForResult(tagIntent,SPRAY_TAG_REQUEST);
            }
        }
    }

    private void addTag(){
        if(mTempTag!=null) {
            ParseDao.addTagEventually(mTempTag);
            addTempTagMarkerToMap();    //TODO alten Tag marker entfernen
            recalculateRegions();

            //get the region of the tag, remove from Map and add again
            //TODO evtl. einfach updaten statt neu machen
            GangRegion gr = getRegion(mTempTag.longitude,mTempTag.latitude);
            if(gr.regionPolygon != null) {
                gr.regionPolygon.remove();
            }
            addRegionToMap(gr);
            //setUpMap();
            mTempTag = null;
            Toast.makeText(getApplicationContext(), "Fettes Tag bro", Toast.LENGTH_LONG).show();
        }
    }

    private void addTempTagMarkerToMap(){
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(mTempTag.latitude, mTempTag.longitude))
                .anchor(0.5f,0.5f)//Center the Marker on the Position
                .title(mTempTag.gang.name+" - "+mTempTag.timestamp))
                .setIcon(BitmapDescriptorFactory.fromBitmap(TagImageHelper.tagAsBitmapIcon(mTempTag.gang.tag.image, mTempTag.gang.color)));
    }

    private boolean checkSprayable(){
    /** FIXME Dürfen tags untereinander einen Abstand<2xtolerance haben?
     * Wenn ja, muss noch implementiert werden, dass spray das nähere der beiden tags nimmt */
        float tolerance = 10f; //10m abweichungen erlaubt.
        GangRegion region = getRegion(mLastLocation.getLongitude(), mLastLocation.getLatitude());
        if(region.getTags()==null || region.getTags().size()<3){
            return true;
        }else{
            for(Tag tag : region.getTags()){
                if(!tag.gang.id.equals(LocalDao.getPlayer().gang.id)){
                float[] result = new float[1];
                Location.distanceBetween(mLastLocation.getLatitude(),mLastLocation.getLongitude(),tag.latitude,tag.longitude,result);
                if(result[0]<tolerance){
                    return true;
                }
                }
            }
        }
        return false;
    }

    private void showGang(){
        Intent intent = new Intent(this,ShowGangActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if(mGoogleApiClient != null){
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
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
        if(parseInitialized.get()){
            setUpMapIfNeeded();
        }
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

    @Override
    protected void onPause() {
        super.onPause();
        //stopLocationUpdates();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        //if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
        //    startLocationUpdates();
        //}

    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {//TODO Performance messung der einzelnen Abschnitte
        mMap.clear();
        tags = ParseDao.getAllTags();
        oldTags.clear();
        separateNewAndOldTags();
        addTagsToMap();
        recalculateRegions();
        addRegionsToMap();
        showUserPos();
    }

    private void addTagsToMap() {
        for(Tag tag : tags){
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(tag.latitude, tag.longitude))
                    .anchor(0.5f,0.5f)//Center the Marker on the Position
                    .title(tag.gang.name+" - "+tag.timestamp))
                    .setIcon(BitmapDescriptorFactory.fromBitmap(TagImageHelper.tagAsBitmapIcon(tag.gang.tag.image, tag.gang.color)));
        }
    }

    private void addRegionsToMap() {
        for(GangRegion gr : regions){
            addRegionToMap(gr);
        }
    }

    private void addRegionToMap(GangRegion gr) {
        gr.whoIsTheBoss();
        Polygon regionPolygon = mMap.addPolygon(new PolygonOptions()
                .add(gr.getLeftBottom())
                .add(gr.getLeftTop())
                .add(gr.getRightTop())
                .add(gr.getRightBottom())
                .fillColor(gr.getBackgroundColor())
                .strokeWidth(3f)
                .strokeColor(gr.getColor()));
        gr.regionPolygon = regionPolygon;
    }

    private void recalculateRegions() {
        regions = new ArrayList<GangRegion>();
        for(Tag tag : tags){
            GangRegion region = getRegion(tag.longitude,tag.latitude);
            region.addTag(tag);
        }
    }

    private void separateNewAndOldTags() {
        for(Tag t : tags){
            Double latitude = t.latitude;
            Double longitude = t.longitude;
            Date timestamp = t.timestamp;
            for(Tag t2 : tags){
                if(t2.latitude==latitude && t2.longitude==longitude && timestamp.before(t2.timestamp)){
                    oldTags.add(t);
                    Log.i(TAG,"Tag was added to oldTags"+t.id+" - "+t.gang.name+" - "+t.timestamp);
                    break;
                }
            }
        }
        tags.removeAll(oldTags);
    }

    /** Searches for the region and if not exists creates a new */
    private GangRegion getRegion(double longitude, double latitude){
        //berechne Region xy (das ist sozusagen die ID der Region)
        int x = (int) (longitude / GangRegion.gridLongitude);
        int y = (int) (latitude / GangRegion.gridLatitude);
        GangRegion result = null;
        boolean found=false;
        for(GangRegion gr : regions){
            if(gr.x == x && gr.y == y){
                result = gr;
                found = true;
                break;
            }
        }
        if(!found){
            GangRegion r = new GangRegion();
            r.setXY(x, y);
            result = r;
            regions.add(r);
        }
        return result;
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
        startLocationUpdates();
        showUserPos();
    }

    protected void startLocationUpdates() {
        if(mLocationRequest==null)
            createLocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    private void showUserPos(){
        if(mMap!=null){
        if (mLastLocation != null) {
            if(userPos!=null)
                userPos.remove();
            LatLng position = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            userPos=mMap.addMarker(new MarkerOptions().position(position).title("Meine Position"));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(position)
                    .zoom(15)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
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

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();

    }

    private void updateUI(){
        if(checkSprayable()){
            imageViewSprayBtn.setImageBitmap(BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.ic_spray_possible));
        }else{
            imageViewSprayBtn.setImageBitmap(BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.ic_spray_impossible));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SPRAY_TAG_REQUEST){
            if(resultCode == RESULT_OK){
                addTag();

//                if(data != null && data.hasExtra(DrawTagActivity.PARAM_PATH)) {
//                    Bundle b = data.getExtras();
//                    SerializablePath path = (SerializablePath) b.getSerializable(DrawTagActivity.PARAM_PATH);
//                    if (path != null) {
//                        this.path = path;
//                        refreshTagView();
//                    } else {
//                        Log.w("NewGangActivity", "EditTag resulted in path being empty");
//                    }
//                }
            }
        }
    }

    public class AsyncStartup extends AsyncTask<Void,Void,Boolean>{
        @Override
        protected Boolean doInBackground(Void... params) {
            // Enable Local Datastore.
            ParseObject.registerSubclass(PGang.class);
            ParseObject.registerSubclass(PTag.class);
            ParseObject.registerSubclass(PPlayer.class);
            ParseObject.registerSubclass(PTagImage.class);
            Parse.enableLocalDatastore(MapsActivity.this);
            Parse.initialize(MapsActivity.this, "SrnX83VPWR6P4iTiwvpjm5juIRACjkzWawoYcCii", "UwuqEExvBR3Qb7CKBBtBdY39421OewdU6R5q9YxD");
            parseInitialized.set(true);

            LocalDao.init(MapsActivity.this);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            afterLoadingFinished();
        }
    }
}
