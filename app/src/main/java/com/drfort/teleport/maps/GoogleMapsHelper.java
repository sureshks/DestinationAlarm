package com.drfort.teleport.maps;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.drfort.teleport.wakeupatdestination.MainActivityMaps;
import com.drfort.teleport.wakeupatdestination.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * Created by ssres on 12/6/15.
 */
public class GoogleMapsHelper
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, SensorEventListener{


    protected GoogleMap mMap;
    protected GoogleApiClient mGoogleApiClient;
    private LocationRequest locationRequest = null;
    private Context context;
    private Marker currentLocationMarker;

    private float declination;

    public GoogleMapsHelper(Context context,GoogleMap mMap){
        this.context = context;
        this.mMap = mMap;
    }

    public synchronized GoogleApiClient getGoogleClient(){
        Log.d("Maps:", "---GoogleClient");
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        return mGoogleApiClient;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("Maps:", "----OnConnected");
        if(!isLocationServiceEnabled()){
            showLocationSettingsAlert();
        }
        startLocationUpdate();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        GeomagneticField magneticField = new GeomagneticField(
                (float)location.getLatitude(),
                (float)location.getLongitude(),
                (float)location.getAltitude(),
                System.currentTimeMillis());

        declination = magneticField.getDeclination();

        CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
        LatLng currentPosition = getPosition(location);
        markLocation(currentPosition,"You're here");
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
        mMap.animateCamera(zoom);

    }

    protected LatLng getPosition(Location location){
        MainActivityMaps.location = location;
        //Log.d("Longitude",String.valueOf(location.getLongitude()));
        //Log.d("Latitude",String.valueOf(location.getLatitude()));
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    protected void markLocation(LatLng position, String positionName){
        if(currentLocationMarker!=null){
            currentLocationMarker.remove();
        }
        currentLocationMarker = mMap.addMarker(new MarkerOptions().position(position));
        currentLocationMarker.setTitle(positionName);
        currentLocationMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location));
        currentLocationMarker.setFlat(false);
        currentLocationMarker.setDraggable(false);

    }

    protected Location getLastLocation(){
        return LocationServices.
                FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    protected void startLocationUpdate(){
        Log.d("GoogleMaps","StartLocationupdate");
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.
                requestLocationUpdates(mGoogleApiClient, locationRequest, this);

    }

    private boolean isLocationServiceEnabled(){
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(locationManager.GPS_PROVIDER))
            return true;
        return false;
    }

    public void showLocationSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setTitle("Error!");
        alertDialog.setMessage("Please enable your location settings");
        alertDialog.setPositiveButton(
                context.getResources().getString(R.string.button_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(intent);
                    }
                });

        alertDialog.show();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("SensorChange","true");
        float[] rotationMatrix = null;
        if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR){
            SensorManager.getRotationMatrixFromVector(
                    rotationMatrix, event.values);
            float[] orientation = new float[3];
            SensorManager.getOrientation(rotationMatrix, orientation);
            float bearing = ((float) Math.toDegrees(orientation[0])) + declination;
            updateCamera(bearing);
        }
    }

    private void updateCamera(float bearing){
        CameraPosition oldPos = mMap.getCameraPosition();

        CameraPosition pos = CameraPosition.builder(oldPos)
                .bearing(bearing).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
