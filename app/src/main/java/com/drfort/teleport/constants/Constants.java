package com.drfort.teleport.constants;

/**
 * Created by ssres on 12/8/15.
 */
public final class Constants {
    public static final int RESULT_SUCCESS = 0;
    public static final int RESULT_FAILURE = 1;

    public static final String PACKAGE_NAME=
            "com.drfort.teleport.wakeupatdestination";
    public static final String RECEIVER =
            PACKAGE_NAME + ".receiver";
    public static final String RESULT_DATA_KEY =
            PACKAGE_NAME + ".result_data_key";
    public static final String LOCATION_DATA_EXTRA =
            PACKAGE_NAME + ".location_data_extra";

    public static int GEOFENCE_RADIUS = 1000;
    public static int GEOFENCE_LOITERING_TIME = 10000;
    public static int GEOFENCE_EXPIRE = 60000;
    public static final int ALARM_DELAY = 5;

    public static final String GOOGLE_API_CLIENT =
            PACKAGE_NAME + ".google_api_client";
    public static final String GEOFENCE_PENDING_INTENT =
            PACKAGE_NAME + ".geofence_pending_intent";
    public static final String GEOFENCE_CIRCLE =
            PACKAGE_NAME + ".circle";

    //DB Constants
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SavedLocations.db";

}
