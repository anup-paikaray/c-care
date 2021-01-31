package com.main.c_care.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;

import androidx.annotation.Nullable;

public class LocalDatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "c-care.db";
    public static final String LOCATION_TABLE = "location";
    public static final String CREDENTIAL_TABLE = "credential";
    public static final String RESPONSE_TABLE = "response";
    public static final String QUESTION_TABLE = "question";

    public LocalDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + LOCATION_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, TIMESTAMP DATETIME DEFAULT CURRENT_TIMESTAMP, LAT FLOAT, LNG FLOAT, RAD INTEGER, TAG STRING, COUNT INTEGER)");
        db.execSQL("create table " + CREDENTIAL_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME CHAR(10), EMAIL NVARCHAR(320), PASSWORD VARCHAR(255), PHONE VARCHAR(10))");
        db.execSQL("create table " + RESPONSE_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, RESP1 INT, RESP2 INT, RESP3 INT, RESP4 INT, DT DATETIME DEFAULT CURRENT_TIMESTAMP)");
        db.execSQL("create table " + QUESTION_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, QUESTION CHAR(100), DT DATETIME DEFAULT CURRENT_TIMESTAMP)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + LOCATION_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CREDENTIAL_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + RESPONSE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + QUESTION_TABLE);
        onCreate(db);
    }

    ///////////////////////////**************LOCATION TABLE**************///////////////////////////
    public boolean insertData(int tag, LatLng latLng, int radius) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("LAT", latLng.latitude);
        contentValues.put("LNG", latLng.longitude);
        contentValues.put("RAD", radius);
        contentValues.put("TAG", String.valueOf(tag));
        long result = db.insert(LOCATION_TABLE, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    public Cursor getLocationData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + LOCATION_TABLE, null);
        return res;
    }

    public boolean updateData(int tag, LatLng latLng, int radius) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("LAT", latLng.latitude);
        contentValues.put("LNG", latLng.longitude);
        contentValues.put("RAD", radius);
        int result = db.update(LOCATION_TABLE, contentValues, "TAG = ?", new String[] {String.valueOf(tag)});
        if (result > 0)
            return true;
        else
            return false;
    }

    public boolean updateCount(int id, int count) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("COUNT", count);
        int result = db.update(LOCATION_TABLE, contentValues, "ID = ?", new String[] {String.valueOf(id)});
        if (result > 0)
            return true;
        else
            return false;
    }

    public int deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(LOCATION_TABLE, "1", null);
    }

    //////////////////////////**************CREDENTIAL TABLE**************//////////////////////////
    public boolean insertData(String Name, String Email, String Password, String Phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", Name);
        contentValues.put("EMAIL", Email);
        contentValues.put("PASSWORD", Password);
        contentValues.put("PHONE", Phone);
        long result = db.insert(CREDENTIAL_TABLE, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    public Cursor getCredentialData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + CREDENTIAL_TABLE, null);
        return res;
    }

    public boolean updateData(String Name, String Email, String Password, String Phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", Name);
        contentValues.put("EMAIL", Email);
        contentValues.put("PASSWORD", Password);
        contentValues.put("PHONE", Phone);
        int result = db.update(CREDENTIAL_TABLE, contentValues, "NAME = ?", new String[] {Name});
        if (result > 0)
            return true;
        else
            return false;
    }

    //////////////////////////**************RESPONSE TABLE**************//////////////////////////
    public boolean insertData(int [] Response) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        for (int i = 0; i < Response.length; i++)
            contentValues.put("Q" + i, Response[i]);

        long result = db.insert(RESPONSE_TABLE, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    public Cursor getResponseData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + RESPONSE_TABLE, null);
        return res;
    }

    //////////////////////////**************QUESTION TABLE**************//////////////////////////
    public boolean insertData(String Question) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("QUESTION", Question);

        long result = db.insert(QUESTION_TABLE, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    public Cursor getQuestionData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + QUESTION_TABLE, null);
        return res;
    }
}
