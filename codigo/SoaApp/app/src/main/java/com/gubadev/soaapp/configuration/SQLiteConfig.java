package com.gubadev.soaapp.configuration;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.gubadev.soaapp.constant.Constants;

public class SQLiteConfig extends SQLiteOpenHelper {

    private static final String USERS = ("CREATE TABLE " + Constants.TABLE_USER + " (" +
                                             Constants.EMAIL + " VARCHAR(50), " +
                                             Constants.FIRST_NAME + " VARCHAR(50), " +
                                             Constants.LAST_NAME + " VARCHAR(50), " +
                                             Constants.DNI + " INTEGER, " +
                                             Constants.PASSWORD +" VARCHAR(30), " +
                                             Constants.ID + " INTEGER PRIMARY KEY AUTOINCREMENT);");

    private static final String SCORE = ("CREATE TABLE " + Constants.TABLE_SCORE + " (" +
                                          Constants.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                          Constants.DATE + " TIMESTAMP  NOT NULL, " +
                                          Constants.SCORE + " INTEGER, " +
                                          Constants.TIME + " INTEGER, " +
                                          Constants.NAME_GAMER + " VARCHAR(60), " +
                                          Constants.USER_ID + " INTEGER NOT NULL, "+
                                         "FOREIGN KEY (" + Constants.USER_ID + ") REFERENCES " +
                                          Constants.TABLE_USER + "(" + Constants.ID + "));");

    private static final String DB_NAME = "db.sqlite";
    private static final int DB_VERSION = 3;
    private static final String TAG_DB_SQLITE = "DB_SQLITE";

    public SQLiteConfig(Context context) {

        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG_DB_SQLITE, "onCreate: CREATE TABLE");
        db.execSQL(USERS);
        db.execSQL(SCORE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.i(TAG_DB_SQLITE, "onUpgrade: DROP TABLE");
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_SCORE);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.setVersion(oldVersion);
    }

}