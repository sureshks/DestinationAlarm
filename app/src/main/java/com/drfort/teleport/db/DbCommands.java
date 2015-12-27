package com.drfort.teleport.db;

import android.provider.BaseColumns;

/**
 * Created by ssres on 12/26/15.
 */
public final class DbCommands {
    public DbCommands(){

    }

    public static abstract class LocationsEntry implements BaseColumns{
        protected static final String TABLE_NAME = "Locations";
        protected static final String COLUMN_LOCATION_NAME = "LocationName";
        protected static final String COLUMN_LOCATION_LAT = "Latitude";
        protected static final String COLUMN_LOCATION_LONG = "Longitude";
    }

    private static final String TEXT_TYPE = " TEXT NOT NULL";
    private static final String FLOAT_TYPE = " REAL NOT NULL";
    private static final String COMMA_SEP = ",";
    protected static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS "+LocationsEntry.TABLE_NAME+" ("+
                LocationsEntry._ID+" INTEGER PRIMARY KEY"+ COMMA_SEP +
                    LocationsEntry.COLUMN_LOCATION_NAME + TEXT_TYPE + COMMA_SEP +
                    LocationsEntry.COLUMN_LOCATION_LAT + FLOAT_TYPE + COMMA_SEP +
                    LocationsEntry.COLUMN_LOCATION_LONG + FLOAT_TYPE +");";
    protected static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS "+LocationsEntry.TABLE_NAME+";";
    protected static final String SQL_SEARCHALL_ENTRIES =
            "SELECT "+LocationsEntry.COLUMN_LOCATION_NAME + COMMA_SEP+
            LocationsEntry.COLUMN_LOCATION_LAT + COMMA_SEP+
            LocationsEntry.COLUMN_LOCATION_LONG + " FROM " + LocationsEntry.TABLE_NAME+";";

}
