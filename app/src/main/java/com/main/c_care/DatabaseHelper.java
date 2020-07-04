package com.main.c_care;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "c-trac.db";
    public static final String TABLE_NAME = "location";
    public static final String ID = "ID";
    public static final String TIME = "TIME";
    public static final String LAT = "LAT";
    public static final String LNG = "LNG";
    public static final String TAG = "TAG";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, TIMESTAMP DATETIME DEFAULT CURRENT_TIMESTAMP, LAT FLOAT, LNG FLOAT, TAG STRING)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(LatLng latLng, String tag) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(LAT, latLng.latitude);
        contentValues.put(LNG, latLng.longitude);
        contentValues.put(TAG, tag);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }

    public boolean updateData(String tag, LatLng latLng) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(LAT, latLng.latitude);
        contentValues.put(LNG, latLng.longitude);
        int result = db.update(TABLE_NAME, contentValues, "TAG = ?", new String[] {tag});
        if (result > 0)
            return true;
        else
            return false;
    }

    public int deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "1", null);
    }
}
