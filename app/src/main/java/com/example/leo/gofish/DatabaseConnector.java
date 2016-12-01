package com.example.leo.gofish;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Karlo on 11/29/2016.
 */

public class DatabaseConnector {
    private DatabaseOpenHelper databaseOpenHelper;

    public DatabaseConnector(Context context) {
        databaseOpenHelper = new DatabaseOpenHelper(context);
    }

    public void open() throws SQLException {
        database = databaseOpenHelper.getWritableDatabase();
    }

    public void close() {
        if(database != null) database.close();
    }

    public boolean checkIfExists(String stationId) {
        String [] column = { "stationid" };
        String selection = "stationid=?";
        String [] args = { stationId };

        Cursor cursor = database.query(TABLE_NAME, column, selection, args, null, null, null, null);
        if(cursor.moveToFirst()) {
            return true;
        }
        else {
            return false;
        }
    }

    public void insertStation(String id, String name, String province) {
        ContentValues favStation = new ContentValues();
        favStation.put("stationid", id);
        favStation.put("name", name);
        favStation.put("province", province);

        open();
        if(!checkIfExists(id)) {
            Log.i("List Checked", "Adding " + id + " to database");
            database.insert(TABLE_NAME, null, favStation);
        }
        close();
    }

    public void deleteStation(String stationId) {
        open();
        int id = database.delete(TABLE_NAME, "stationid=?", new String [] { stationId });
        Log.i("Station deleted: ", "" + id);
        close();
    }





    private SQLiteDatabase database;
    static final String DATABASE_NAME = "stations_db";
    static final String TABLE_NAME = "favourite";
    static final int DATABASE_VERSION = 4;
    static final String CREATE_DB_TABLE = "CREATE TABLE " + TABLE_NAME  + " (_id integer primary key autoincrement, stationid TEXT, name TEXT, province TEXT);";
    private static class DatabaseOpenHelper extends SQLiteOpenHelper
    {

        DatabaseOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion)
        {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}
