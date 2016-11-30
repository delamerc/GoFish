package com.example.leo.gofish;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

    public Cursor getAllStations() {
        return database.query(TABLE_NAME, new String[] {"_id, stationid, name, province"}, null, null, null, null, "name");
    }

    public void insertStation(String id, String name, String province) {
        ContentValues newStation = new ContentValues();
        newStation.put("stationid", id);
        newStation.put("name", name);
        newStation.put("province", province);

        open();
        database.insert(TABLE_NAME, null, newStation);
        close();
    }

    public void deleteStation(String stationId) {
        open();
        database.delete(TABLE_NAME, "stationid=" + stationId, null);
        close();
    }





    private SQLiteDatabase database;
    static final String DATABASE_NAME = "stations_db";
    static final String TABLE_NAME = "favourite";
    static final int DATABASE_VERSION = 1;
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
