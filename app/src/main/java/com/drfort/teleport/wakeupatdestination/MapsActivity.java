package com.drfort.teleport.wakeupatdestination;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private Location mLastLocation;
    private double mLatitudeText;
    private double mLongitudeText;
    private final int LOCATION_ACCESS_CODE = 200;
    private boolean permissionStatus = false;
    private LocationRequest locationRequest;

    private CircleOptions mCircleOptions;
    private Circle mCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);
        mMap = mapFragment.getMap();
        getGoogleClient();
    }

    protected synchronized void getGoogleClient(){
        Log.d("Maps:", "---GoogleClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void createGeoFence(){
        mLatitudeText = 12.1260d;
        mLongitudeText = 78.1540d;
        Geofence geofence = new Geofence.Builder()
                .setRequestId("Home")
                .setCircularRegion(mLatitudeText,
                        mLongitudeText,500)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();
        List<Geofence> geofenceList = new ArrayList<>();
        geofenceList.add(geofence);
        GeofencingRequest request = new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofences(geofenceList)
                .build();

        Intent intent = new Intent(this,GeofenceTransitionIntentService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingResult<Status> result = LocationServices.GeofencingApi.addGeofences(mGoogleApiClient,request,pendingIntent);

        mCircleOptions = new CircleOptions()
                .center(new LatLng(mLatitudeText,mLongitudeText))
                .radius(500)
                .fillColor(0x40ff0000)
                .strokeColor(Color.TRANSPARENT)
                .strokeWidth(2);
        mCircle = mMap.addCircle(mCircleOptions);
    }
    @Override
    public void onConnected(Bundle bundle) {
        Log.d("Maps:", "----OnConnected");
        if(!isLocationServiceEnabled()){
            showLocationSettingsAlert();
        }

        if(getLastLocation() != null){
            startLocationUpdate();
        }
        else
            Log.d("Maps","Null Location");
    }

    protected Location getLastLocation(){
        return LocationServices.
                FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    protected void startLocationUpdate(){
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);

        //mCurrentLocation = location;
        //mLatitudeText = mCurrentLocation.getLatitude();
        mLatitudeText = 12.1260d;
        //mLongitudeText = mCurrentLocation.getLongitude();
        mLongitudeText = 78.1540d;
        Log.d("Maps:", "---Last Latitude is:" + String.valueOf(mLatitudeText));
        Log.d("Maps:", "---Last Longitude is:" + String.valueOf(mLongitudeText));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

        LatLng Room = new LatLng(mLatitudeText, mLongitudeText);
        mMap.addMarker(new MarkerOptions().position(Room).title("Chennai Room"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Room));
        mMap.animateCamera(zoom);
        createGeoFence();
    }

    @Override
    public void onLocationChanged(Location location) {
        mCircle.setVisible(false);
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mLatitudeText, mLongitudeText)));
    }

    public void showLocationSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("Error!");

        // Setting Dialog Message
        alertDialog.setMessage("Please enable your location settings");

        // On pressing Settings button
        alertDialog.setPositiveButton(
                getResources().getString(R.string.button_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });

        alertDialog.show();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    //@Override
    /*public void onMapReady(GoogleMap googleMap){
        Log.d("Maps:", "---OnMapReady");
        mMap = googleMap;

        Log.d("Maps:","---Version:"+String.valueOf(Build.VERSION.SDK_INT));

        if(Build.VERSION.SDK_INT >= 23
                && ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                        isLocationServiceEnabled())
        {
            Log.d("Maps","---Inside permission");
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                    }, LOCATION_ACCESS_CODE);
        }
        else
            permissionStatus = true;
        Log.d("Maps", "----LocationManager");
        if(permissionStatus == true) {
            *//*LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            Location getLastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Log.d("Maps:", "---Last Latitude is:" + String.valueOf(getLastLocation.getLatitude()));
            Log.d("Maps:", "---Last Longitude is:" + String.valueOf(getLastLocation.getLongitude()));*//*
            //getGoogleClient();
            Log.d("Maps:", "---Last Latitude is:" + String.valueOf(mLatitudeText));
            Log.d("Maps:", "---Last Longitude is:" + String.valueOf(mLongitudeText));
        }



        // Add a marker in Sydney and move the camera
        *//*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*//*
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mLatitudeText, mLongitudeText)));

    }*/

    private boolean isLocationServiceEnabled(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(locationManager.GPS_PROVIDER))
            return true;
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode){
            case LOCATION_ACCESS_CODE:{
                if(grantResults.length > 0 &&
                        grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    permissionStatus = true;
                }
            }

        }
    }*/




}
