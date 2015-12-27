package com.drfort.teleport.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.drfort.teleport.constants.Constants;

/**
 * Created by ssres on 12/26/15.
 */
public class DbHelper extends SQLiteOpenHelper {

    public DbHelper(Context context){
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DbCommands.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DbCommands.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

}
