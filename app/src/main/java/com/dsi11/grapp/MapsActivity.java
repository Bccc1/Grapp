package com.dsi11.grapp;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class MapsActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    boolean debug_dontLoadTags;

    public static final String PARAM_DEBUG_DONT_LOAD_TAGS = "debug_dontLoadTags";

    public static final String PARAM_PARSE_INITIALIZED =  "parseInitialized";

    static final int SPRAY_TAG_REQUEST = 5;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private ImageView imageViewSprayBtn;
    private ImageView imageViewSplashScreen;
    private ImageView imageButtonShowGang;
    private ImageView imageButtonShowInfo;
    private Button btnReset;
    private static final String TAG = "MapsActivity";
    private List<GangRegion> regions = new ArrayList<>();
    private List<Tag> tags;
    private List<Tag> oldTags = new ArrayList<Tag>();
    private LocationRequest mLocationRequest;
    private String mLastUpdateTime;
    private Marker userPos;
    private Tag mTempTag;
    private String checkSprayableMessage;

    private final int grSize = 3;
    private Tag toBeRetagged;
    private HashMap<String,Marker> mMapMarker = new HashMap<>();


    @Override
    protected void onDestroy() {
        //Debug.stopMethodTracing();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(PARAM_PARSE_INITIALIZED, LocalDao.parseInitialized.get());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        if(b!=null) {
            debug_dontLoadTags = b.getBoolean(PARAM_DEBUG_DONT_LOAD_TAGS, false);
        }

        if(savedInstanceState != null){
            LocalDao.parseInitialized.set(savedInstanceState.getBoolean(PARAM_PARSE_INITIALIZED));
        }

        //TODO Move onclickListener addition to afterLoadingFinished
        setContentView(R.layout.activity_maps);
        imageViewSprayBtn = (ImageView) findViewById(R.id.maps_imageView_sprayBtn);
        imageViewSplashScreen = (ImageView) findViewById(R.id.maps_imageView_splashScreen);
        imageButtonShowGang = (ImageView) findViewById(R.id.maps_imageView_gangBtn);
        imageButtonShowInfo = (ImageView) findViewById(R.id.maps_imageView_infoBtn);
        btnReset = (Button) findViewById(R.id.maps_btn_reset);

        AsyncStartup asyncStartup = new AsyncStartup();
        asyncStartup.execute();

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
            LocalDao.init(this);
        }

        attachGuiListeners();

        buildGoogleApiClient();
        setUpMapIfNeeded();
        mGoogleApiClient.connect();

        SoundUtils.init(this);
        resizeGUI();
        hideSplashScreen();

    }

    private void attachGuiListeners(){
        imageButtonShowGang.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageView view = (ImageView) v;
                        view.setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {   //OnClick
                        v.playSoundEffect(android.view.SoundEffectConstants.CLICK);
                        showGang();
                    }
                    case MotionEvent.ACTION_CANCEL: {
                        ImageView view = (ImageView) v;
                        view.clearColorFilter();
                        view.invalidate();
                        break;
                    }
                }
                return true;
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetApp();
            }
        });
        imageViewSprayBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageView view = (ImageView) v;
                        view.setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {   //OnClick
                        v.playSoundEffect(android.view.SoundEffectConstants.CLICK);
                        spray();
                    }
                    case MotionEvent.ACTION_CANCEL: {
                        ImageView view = (ImageView) v;
                        view.clearColorFilter();
                        view.invalidate();
                        break;
                    }
                }
                return true;
            }
        });

        imageButtonShowInfo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageView view = (ImageView) v;
                        view.setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {   //OnClick
                        v.playSoundEffect(android.view.SoundEffectConstants.CLICK);
                        showInfo();
                    }
                    case MotionEvent.ACTION_CANCEL: {
                        ImageView view = (ImageView) v;
                        view.clearColorFilter();
                        view.invalidate();
                        break;
                    }
                }
                return true;
            }
        });
    }

    private void showInfo() {
        Intent intent = new Intent(this,TutorialEntryListActivity.class);
        startActivity(intent);
    }

    private void resizeGUI(){
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();

        //AspectRatio of SprayButton x:y
        float sprayBtnAR = 0.5278048780487805f;//TODO calculate
        int sprayBtnWidth = (int) (width * 0.25f);   //25 % des Bildschirms
        int sprayBtnHeight = (int) (sprayBtnWidth/sprayBtnAR);

        //AspectRatio of Buttons x:y
        float btnAR = 2.943396226415094f; //TODO calculate
        int btnWidth = (int) (width * 0.4f);    //40% des Bildschirms
        int btnHeight = (int)(btnWidth/btnAR);

        imageViewSprayBtn.requestLayout();
        imageViewSprayBtn.getLayoutParams().width = sprayBtnWidth;
        imageViewSprayBtn.getLayoutParams().height = sprayBtnHeight;

        imageButtonShowGang.requestLayout();
        imageButtonShowGang.getLayoutParams().width = btnWidth;
        imageButtonShowGang.getLayoutParams().height = btnHeight;

        imageButtonShowInfo.requestLayout();
        imageButtonShowInfo.getLayoutParams().width = btnWidth;
        imageButtonShowInfo.getLayoutParams().height = btnHeight;
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
                if(toBeRetagged!=null){ //Wenn etwas übersprüht werden soll
                    tag.longitude=toBeRetagged.longitude;
                    tag.latitude=toBeRetagged.latitude;
                }else{ //normalfall, ein neues Tag
                    tag.latitude=mLastLocation.getLatitude();
                    tag.longitude=mLastLocation.getLongitude();
                }
                tag.timestamp=Calendar.getInstance().getTime();
                mTempTag = tag; //ekliger workaround, da ich den Tag nicht an die Activity geben will wg Serializable und so

                Intent tagIntent = new Intent(this,DrawTagActivity.class);
                tagIntent.putExtra(DrawTagActivity.PARAM_COLOR,player.gang.color);
                //Es sollte ein Flag gesetzt werden, das es sich ums taggen handelt
                tagIntent.putExtra(DrawTagActivity.PARAM_SPRAY_MODE,true);
                //Der Pfad sollte mitgeliefert werden, um den Hintergrund zu zeichnen
                if(tag.gang.tag != null && tag.gang.tag.image != null){
                    tagIntent.putExtra(DrawTagActivity.PARAM_PATH,tag.gang.tag.image);
                }
                startActivityForResult(tagIntent,SPRAY_TAG_REQUEST);
            }else{
                if(checkSprayableMessage!=null){
                    showMessage(checkSprayableMessage);
                }else {
                    showMessage("Du kannst hier nix ausrichten, Feindgebiet!!"); //Fallback text
                }
            }
        }
    }

    private void addTag(){
        if(mTempTag!=null) {
            ParseDao.addTagEventually(mTempTag, new SaveCallback(){
                public void done(ParseException e){
                    if (e == null) {
                        tagAddedSuccessfully();
                    } else {
                        tagAddDidNotSucceed();
                    }
                }
            });
            addTempTagMarkerToMap(); //Old Marker gets deleted here
            tags.remove(toBeRetagged);
            tags.add(mTempTag);
            toBeRetagged = null;
            //recalculateRegions(); //Wozu war das gut?

            //get the region of the tag, remove from Map and add again
            //TODO evtl. einfach updaten statt neu machen
            GangRegion gr = getRegion(mTempTag.longitude,mTempTag.latitude);
            if(gr.regionPolygon != null) {
                gr.regionPolygon.remove();
            }else{
                //region is new, do some shit
            }
            gr.addTag(mTempTag);
            gr.whoIsTheBoss();
            addRegionToMap(gr);
            //setUpMap();
            mTempTag = null;
            showMessage("Fettes Tag, Bro!");
            updateUI();
        }
    }

    private void tagAddDidNotSucceed() {
        //showMessage("Tag konnte nicht gespeichert werden");
        Log.i(TAG,"Tag konnte nicht gespeichert werden");
    }

    private void tagAddedSuccessfully() {
        //showMessage("Tag wurde erfolgreich in Datenbank gespeichert");
        Log.i(TAG,"Tag wurde erfolgreich in Datenbank gespeichert");
    }

    private void addTempTagMarkerToMap(){
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(mTempTag.latitude, mTempTag.longitude))
                .anchor(0.5f, 0.5f)//Center the Marker on the Position
                .title(mTempTag.gang.name + " - " + mTempTag.timestamp));
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(TagImageHelper.tagAsBitmapIcon(mTempTag.gang.tag.image, mTempTag.gang.color)));

        //check if marker exists at that exact location and if, delete it
        Marker oldMarker = mMapMarker.get(calcMarkerId(marker));
        if(oldMarker != null) {
            mMapMarker.remove(calcMarkerId(marker));
            oldMarker.remove();
        }

        mMapMarker.put(calcMarkerId(marker),marker);
    }

    private String calcMarkerId(Marker marker){
        if(marker != null && marker.getPosition() != null){
            return "lat"+marker.getPosition().latitude+"long"+marker.getPosition().longitude;
        }
        Log.w(TAG, "calcMarkerId had a nullpointer and returned dummy data. Fix this!!");
        return "Failure"+new Random().nextInt(99999);
    }

      private boolean checkSprayable(){
//        float tolerance = 10f; //10m abweichungen erlaubt.
        float tolerance = 1f; //1m für Vorführung
        GangRegion region = getRegion(mLastLocation.getLongitude(), mLastLocation.getLatitude());
        if(region.getTags()==null || region.getTags().size()<grSize){    //region has less than 3 tags
            Log.d(TAG + " - checkSprayable", "region tags is null: "+(region.getTags()==null)+ (!(region.getTags()==null) ? (", size is: "+region.getTags().size()) : " "));
            boolean tooClose = false;
            for(Tag tag : region.getTags()){
                Log.d(TAG + " - checkSprayable","checking tag "+tag.id+", "+tag.gang.name);
                if(tag.gang.id.equals(LocalDao.getPlayer().gang.id)) { //checked tag is owned by players gang
                    float[] result = new float[1];
                    Location.distanceBetween(mLastLocation.getLatitude(), mLastLocation.getLongitude(), tag.latitude, tag.longitude, result);
                    Log.d(TAG + " - checkSprayable", "distance player-tag is " + result[0]);
                    if (result[0] < (2 * tolerance)) {
                        tooClose = true;
                        checkSprayableMessage = "Warum die Mühe? Hier hast du schon!";
                        Log.d(TAG + " - checkSprayable", "player is too close to a tag.");
                        break;
                    }
                }
            }
            return !tooClose;
        }else{ //region has 3 or more tags -> is full
            for(Tag tag : region.getTags()){
                Log.d(TAG + " - checkSprayable","checking tag "+tag.id+", "+tag.gang.name);
                if(!tag.gang.id.equals(LocalDao.getPlayer().gang.id)){
                    float[] result = new float[1];
                    Location.distanceBetween(mLastLocation.getLatitude(),mLastLocation.getLongitude(),tag.latitude,tag.longitude,result);
                    Log.d(TAG + " - checkSprayable","distance player-tag is "+result[0]);
                    if(result[0]<tolerance){
                        toBeRetagged = tag;
                        Log.d(TAG + " - checkSprayable","toBeRetagged:"+toBeRetagged.id+", "+toBeRetagged.gang+", lat: "+toBeRetagged.latitude+", long: "+toBeRetagged.longitude);
                        return true;
                    }
                }
            }
            checkSprayableMessage =  "Du kannst hier nix ausrichten, Feindgebiet!";
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
        if(LocalDao.parseInitialized.get()){
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
                if(!debug_dontLoadTags)
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
        mMapMarker.clear();
//        tags = ParseDao.getAllTagsFullyLoaded();
        tags = ParseDao.getAllTags();
        oldTags.clear();
        separateNewAndOldTags();
        fillTagsWithGang(tags);
        addTagsToMap();
        recalculateRegions();
        addRegionsToMap();
        showUserPos();
    }

    private void addTagsToMap() {
        for(Tag tag : tags){
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(tag.latitude, tag.longitude))
                    .anchor(0.5f, 0.5f)//Center the Marker on the Position
                    .title(tag.gang.name + " - " + tag.timestamp));
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(CachedParseData.getTagImageIconByGang(tag.gang.id)));
            mMapMarker.put(calcMarkerId(marker),marker);
        }
    }

    private void fillTagsWithGang(List<Tag> tags){
        for(Tag tag : tags){
            tag.gang = CachedParseData.getGang(tag.gang.id); //TODO prüfen ob taggangid nicht null ist
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
            //FIXME Apperently a nullpointer exception is thrown here.
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
                if(t2.latitude.equals(latitude) && t2.longitude.equals(longitude) && timestamp.before(t2.timestamp)){
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
            CameraPosition oldCameraPosition = mMap.getCameraPosition();
            CameraPosition cameraPosition;
            LatLng position = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            if(userPos!=null){
                userPos.remove();
                //User was present -> only pan (keep zoom level)
                cameraPosition = new CameraPosition.Builder()
                        .target(position)
                        .zoom(oldCameraPosition.zoom)
                        .build();
            }else{
                //User wasn't present before -> zoom and pan
                cameraPosition = new CameraPosition.Builder()
                        .target(position)
                        .zoom(15)
                        .build();
            }
            userPos=mMap.addMarker(new MarkerOptions().position(position).title("Meine Position"));
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
        if(LocalDao.playerCompletelyInitialized){
            updateUI();
            showUserPos();
        }
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
            }
        }
    }

    public class AsyncStartup extends AsyncTask<Void,Void,Boolean>{
        @Override
        protected Boolean doInBackground(Void... params) {
            if(!LocalDao.parseInitialized.get()) {
                // Enable Local Datastore.
                ParseObject.registerSubclass(PGang.class);
                ParseObject.registerSubclass(PTag.class);
                ParseObject.registerSubclass(PPlayer.class);
                ParseObject.registerSubclass(PTagImage.class);
                Parse.enableLocalDatastore(MapsActivity.this);
                Parse.initialize(MapsActivity.this, "SrnX83VPWR6P4iTiwvpjm5juIRACjkzWawoYcCii", "UwuqEExvBR3Qb7CKBBtBdY39421OewdU6R5q9YxD");
                LocalDao.parseInitialized.set(true);
            }
            LocalDao.init(MapsActivity.this);
            return true;

            //FIXME Somewhere between this and done() a runtime exception is thrown
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            afterLoadingFinished();
        }
    }

    public void showMessage(String message){
        Display display = getWindowManager().getDefaultDisplay();
        int height = display.getHeight();

        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.TOP,0,(int)(height*0.2));

        LinearLayout linearLayout = (LinearLayout) toast.getView();
        TextView messageTextView = (TextView) linearLayout.getChildAt(0);
        messageTextView.setTextSize(25);

        toast.show();
    }
}
