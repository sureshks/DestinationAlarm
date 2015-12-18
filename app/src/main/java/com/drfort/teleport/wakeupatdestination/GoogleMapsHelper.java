package com.drfort.teleport.wakeupatdestination;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * Created by ssres on 12/6/15.
 */
public class GoogleMapsHelper
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {


    protected GoogleMap mMap;
    protected GoogleApiClient mGoogleApiClient;
    private LocationRequest locationRequest;
    private Context context;
    private Marker currentLocationMarker;

    public GoogleMapsHelper(Context context,GoogleMap mMap){
        this.context = context;
        this.mMap = mMap;
    }

    protected synchronized GoogleApiClient getGoogleClient(){
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

        if(mGoogleApiClient.isConnected()==false){
            Log.d("---ConnectionStatus","Not connected");
        }
        if(getLastLocation() == null){
            Log.d("MapsLastLocation","Null Location");
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
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
        LatLng currentPosition = getPosition(location);
        markLocation(currentPosition,"You're here");
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
        mMap.animateCamera(zoom);

    }

    protected LatLng getPosition(Location location){
        MainActivityMaps.location = location;
        Log.d("Longitude",String.valueOf(location.getLongitude()));
        Log.d("Latitude",String.valueOf(location.getLatitude()));
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    protected void markLocation(LatLng position, String positionName){
        if(currentLocationMarker!=null){
            currentLocationMarker.remove();
        }
        currentLocationMarker = mMap.addMarker(new MarkerOptions().position(position));
        currentLocationMarker.setTitle(positionName);
        currentLocationMarker.setDraggable(false);

    }

    protected Location getLastLocation(){
        return LocationServices.
                FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    protected void startLocationUpdate(){
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


}
