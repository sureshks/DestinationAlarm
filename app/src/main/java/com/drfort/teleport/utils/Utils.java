package com.drfort.teleport.utils;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Calendar;

/**
 * Created by ssres on 12/21/15.
 */
public class Utils {

    public static boolean isGoogleApiConnected(GoogleApiClient googleApiClient){
        Calendar cal = Calendar.getInstance();
        long startTime = cal.getTimeInMillis();
        long minInMills = 60 * 1000;
        long expectedTimeDiff = 5 * minInMills;
        while(googleApiClient.isConnecting()){
            Log.d("Utils","--Connecting");
            if(isEndTimeReached(startTime,cal.getTimeInMillis(),expectedTimeDiff))
                return false;
        }

        if(googleApiClient.isConnected())
            return true;

        return false;
    }

    public static boolean isEndTimeReached(long startTime, long endTime, long timeDiff){
        if((endTime - startTime) >= timeDiff)
            return true;
        return false;
    }
}
