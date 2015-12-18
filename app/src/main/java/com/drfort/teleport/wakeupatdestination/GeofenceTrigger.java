package com.drfort.teleport.wakeupatdestination;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ssres on 12/8/15.
 */
public class GeofenceTrigger implements ResultCallback<Status> {

    private static final String TAG = GeofenceTrigger.class.getName();
    private CircleOptions circleOptions;
    private Circle circle;
    private Context context;
    private Location location;
    private GoogleApiClient googleApiClient;
    public static PendingIntent geofencePendingIntent = null;
    private List<Geofence> geofenceList = new ArrayList<Geofence>();
    public GeofenceTrigger(Context context, GoogleApiClient googleApiClient,
                           Location location){
        this.context = context;
        this.googleApiClient = googleApiClient;
        this.location = location;
    }


    private GeofencingRequest getGeofencingRequest(){
        geofenceList.add(buildGeofence());
        GeofencingRequest.Builder geofencingBuilder = new GeofencingRequest.Builder();
        geofencingBuilder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_EXIT |
            GeofencingRequest.INITIAL_TRIGGER_DWELL | GeofencingRequest.INITIAL_TRIGGER_ENTER);
        geofencingBuilder.addGeofences(geofenceList);
        return geofencingBuilder.build();
    }
    private Geofence buildGeofence(){
        return new Geofence.Builder()
                .setRequestId(String.valueOf(R.string.destination_key))
                .setCircularRegion(
                        location.getLatitude(),
                        location.getLongitude(),
                        Constants.GEOFENCE_RADIUS)
                .setLoiteringDelay(Constants.GEOFENCE_LOITERING_TIME)
                .setExpirationDuration(Constants.GEOFENCE_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_DWELL |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
    }

    private PendingIntent getGeofencePendingIntent(){
        if(geofencePendingIntent != null)
            return geofencePendingIntent;
        Intent intent = new Intent(context,GeofenceTransitionIntentService.class);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void addGeofence(){
        geofencePendingIntent = getGeofencePendingIntent();
        PendingResult<Status> result = LocationServices.GeofencingApi
                .addGeofences(googleApiClient,
                        getGeofencingRequest(),
                        geofencePendingIntent);
        drawGeofences(geofenceList);
        result.setResultCallback(this);
        //startGeofenceIntentService();
    }


    @Override
    public void onResult(Status status) {
        if(status.isSuccess())
            Log.d("ResultCallBack","Success");
        else
            Log.d("ResultCallBack", "Not Success");
    }

    private void drawGeofences(List<Geofence> geofenceList){
        for(Geofence geofenceLocation : geofenceList){
            circleOptions = new CircleOptions()
                    .center(new LatLng(location.getLatitude(),location.getLongitude()))
                    .radius(Constants.GEOFENCE_RADIUS)
                    .fillColor(0x40ff0000)
                    .strokeColor(Color.TRANSPARENT)
                    .strokeWidth(2);
            MainActivityMaps.circle = MainActivityMaps.mMap.addCircle(circleOptions);
        }
    }


    protected static void clearGeofencingDetails(){
        Log.d(TAG, "ClearGeofenceDetails");
        LocationServices.GeofencingApi.removeGeofences(
                MainActivityMaps.googleApiClient,
                geofencePendingIntent);
        if(MainActivityMaps.circle!=null)
            MainActivityMaps.circle.setVisible(false);
    }
}
