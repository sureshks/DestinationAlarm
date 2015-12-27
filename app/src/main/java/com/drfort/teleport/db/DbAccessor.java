package com.drfort.teleport.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by ssres on 12/26/15.
 */
public class DbAccessor {
    private DbHelper dbHelper;
    private Context context;
    public DbAccessor(Context context) {
        this.context = context;
        dbHelper = new DbHelper(context);
        createDb();
    }

    private void createDb(){
        dbHelper.getWritableDatabase().execSQL(DbCommands.SQL_DELETE_ENTRIES);
        dbHelper.getWritableDatabase().execSQL(DbCommands.SQL_CREATE_ENTRIES);
    }

    public long insertIntoDb(final String locationName,
                             final float locLatitude, final float locLongitude){
        SQLiteDatabase locationDb = dbHelper.getWritableDatabase();

        ContentValues dbValues = new ContentValues();
        dbValues.put(DbCommands.LocationsEntry.COLUMN_LOCATION_NAME,locationName);
        dbValues.put(DbCommands.LocationsEntry.COLUMN_LOCATION_LAT,locLatitude);
        dbValues.put(DbCommands.LocationsEntry.COLUMN_LOCATION_LONG,locLongitude);

        if(isLocationInDb(locationName)){
            deleteFromDb(locationName);
        }

        long newRowId;
        newRowId = locationDb.insert(DbCommands.LocationsEntry.TABLE_NAME,
                null, dbValues);
        return newRowId;
    }

    public void readFromDb(final String locationName){
        float latitudeLoc=0f, longitudeLoc=0f;

        if(isLocationInDb(locationName)){
            Cursor searchCursor = getSearchResultCursor(locationName);
            searchCursor.moveToFirst();

            int latitudeIndex = searchCursor.getColumnIndex(DbCommands.LocationsEntry.COLUMN_LOCATION_LAT);
            int longitudeIndex = searchCursor.getColumnIndex(DbCommands.LocationsEntry.COLUMN_LOCATION_LONG);
            if(latitudeIndex != -1 && longitudeIndex != -1){
                latitudeLoc = searchCursor.getFloat(latitudeIndex);
                longitudeLoc = searchCursor.getFloat(longitudeIndex);
            }
        }

        Log.d("LatitudeLoc", String.valueOf(latitudeLoc));
        Log.d("LongitudeLoc",String.valueOf(longitudeLoc));
    }

    public void deleteFromDb(final String locationName){
        SQLiteDatabase locationDb = dbHelper.getWritableDatabase();

        /*final String deleteQuery = "DELETE FROM "+DbCommands.LocationsEntry.TABLE_NAME +
                " WHERE " + DbCommands.LocationsEntry.COLUMN_LOCATION_NAME + "='" +locationName + "';";
        if(isLocationInDb(locationName)){
            locationDb.rawQuery(deleteQuery, null);
        }*/
        String selection = DbCommands.LocationsEntry.COLUMN_LOCATION_NAME + " LIKE ? ";
        locationDb.delete(DbCommands.LocationsEntry.TABLE_NAME,
                selection,new String[]{locationName});
    }

    public String[][] readAllFromDb(){
        SQLiteDatabase locationDb = dbHelper.getReadableDatabase();

        String[] projection = {
                DbCommands.LocationsEntry.COLUMN_LOCATION_NAME,
                DbCommands.LocationsEntry.COLUMN_LOCATION_LAT,
                DbCommands.LocationsEntry.COLUMN_LOCATION_LONG
        };

        Cursor cur = locationDb.query(
                DbCommands.LocationsEntry.TABLE_NAME,
                projection, null, null, null, null, null);

        //Cursor cur = locationDb.rawQuery(DbCommands.SQL_SEARCHALL_ENTRIES,null);

        String[][] resultSavedLocations = null;
        if(cur.getCount() > 0){
            cur.moveToFirst();
            int rowCount = cur.getCount();
            int columnCount = cur.getColumnCount();
            resultSavedLocations = new String[rowCount][columnCount];

            int nameIndex = cur.getColumnIndex(DbCommands.LocationsEntry.COLUMN_LOCATION_NAME);
            int latitudeIndex = cur.getColumnIndex(DbCommands.LocationsEntry.COLUMN_LOCATION_LAT);
            int longitudeIndex = cur.getColumnIndex(DbCommands.LocationsEntry.COLUMN_LOCATION_LONG);

            for(int i=0; i<rowCount; i++){
                resultSavedLocations[i][0] = cur.getString(nameIndex);
                resultSavedLocations[i][1] = cur.getString(latitudeIndex);
                resultSavedLocations[i][2] = cur.getString(longitudeIndex);
                if(!cur.isLast()){
                    cur.moveToNext();
                }
            }
        }
        return resultSavedLocations;
    }

    public boolean isLocationInDb(final String locationName){
        Cursor cur = getSearchResultCursor(locationName);

        if(cur.getCount() > 0){
            return true;
        }
        return false;
    }

    private Cursor getSearchResultCursor(final String locationName){
        SQLiteDatabase locationDb = dbHelper.getReadableDatabase();

        String[] projection = {
                DbCommands.LocationsEntry.COLUMN_LOCATION_NAME,
                DbCommands.LocationsEntry.COLUMN_LOCATION_LAT,
                DbCommands.LocationsEntry.COLUMN_LOCATION_LONG
        };

        Cursor dbCursor = locationDb.query(
                DbCommands.LocationsEntry.TABLE_NAME,
                projection,
                DbCommands.LocationsEntry.COLUMN_LOCATION_NAME+" LIKE ?",
                new String[]{locationName},
                null, null, null);
        return dbCursor;
        /*final String SEARCH_QUERY = "SELECT Latitude FROM Locations;";

        return locationDb.rawQuery(SEARCH_QUERY, null);*/

    }
}
