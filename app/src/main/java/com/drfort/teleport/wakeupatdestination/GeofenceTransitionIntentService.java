package com.drfort.teleport.wakeupatdestination;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Circle;

import java.util.List;

/**
 * Created by ssres on 12/8/15.
 */
public class GeofenceTransitionIntentService extends WakefulBroadcastReceiver {
    private static final String TAG = "GeofenceTransitService";
    private static Context context;
    public GeofenceTransitionIntentService(){
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"OnReceive");
        this.context = context;
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if(geofencingEvent.hasError()){
            String errorMessage = GeofenceStatusCodes
                    .getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e(TAG,errorMessage);
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        Log.d("GeofenceTransaction",String.valueOf(geofenceTransition));
        if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL){

            List trigerringGeofences = geofencingEvent.getTriggeringGeofences();
            Log.d("--In Geofence:","Inside");
            GeofenceTrigger.clearGeofencingDetails();
        }
    }

}
