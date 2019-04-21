package com.example.refael.blueOrganic.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
2019-2018 Refael Beker & Stav Mizrahi --- DataBase SQLite
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "listProgramTap1_0.db";
    public static final String TABLE_NAME = "Programlist_data";
    public static final String TAG = "DatabaseHelper";
    public static final String PRONAME = "ProgramName";
    public static final String S_HOUR = "startHour";
    public static final String S_MIN = "startMin";
    public static final String DURATION = "duration";
    public static final String CHOOSE_DAYS = "Days_Select";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE "+ TABLE_NAME+
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT ,"+PRONAME+" VARCHAR ,"
                +S_HOUR+" INTEGER , "+S_MIN+ " INTEGER , "+
                DURATION +" INTEGER , "+ CHOOSE_DAYS +" VARCHAR)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String proName, Integer start_hour, Integer start_min, Integer duration_time, String choose_Days) {
    // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();
    // Create a new map of values, where column names are the keys
        ContentValues contentValues = new ContentValues();
        contentValues.put(PRONAME, proName);
        contentValues.put(S_HOUR, start_hour);
        contentValues.put(S_MIN, start_min);
        contentValues.put(DURATION, duration_time);
        contentValues.put(CHOOSE_DAYS, choose_Days);
    // Insert the new row, returning the primary key value of the new row
        long result = db.insertOrThrow(TABLE_NAME, null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }
    public Cursor getListContents(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return data;
    }
    /**
     * get ID the name field
     */
    public Cursor getIDProgram(String proName){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT ID FROM " + TABLE_NAME +
                " WHERE " + "ProgramName" + " = '" + proName + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }
    /**
     * get ID the name field
     */
    public Cursor getNameProgram(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + "ProgramName" + " FROM " + TABLE_NAME +
                " WHERE " + "ID" + " = '" + id + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }
    /**
     * Updates the name field
     */
    public void updateName(String proName, Integer start_hour, Integer start_min,
                            Integer duration_time, String choose_new_days, String oldName){
        if(choose_new_days == ""){
            choose_new_days = null;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET "
                + PRONAME + " = '" + proName
                + "' , " + S_HOUR + " = " + start_hour
                + " , " + S_MIN + " = " + start_min
                + " , " + DURATION + " = " + duration_time
                + " , " + CHOOSE_DAYS + " = '" + choose_new_days
                + "' WHERE " + PRONAME +" = '" + oldName + "'";
        Log.d(TAG, "updateName: query: " + query);
        Log.d(TAG, "updateName: Setting name to " + proName);
        db.execSQL(query);
    }

    /**
     * Delete from database
     */
    public void deleteName(String oldName){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE "
                + PRONAME +" = '" + oldName + "'";
        Log.d(TAG, "deleteName: query: " + query);
        db.execSQL(query);
    }
}