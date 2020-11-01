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

    private static final String INSERT_USER = ("INSERT INTO " + Constants.TABLE_USER + " (" +
                                                Constants.EMAIL + ", " +
                                                Constants.PASSWORD + ", " +
                                                Constants.DNI + ", " +
                                                Constants.FIRST_NAME + ", " +
                                                Constants.FIRST_NAME + ") VALUES " +
                                                "('guidoguba@gmail.com'  , 'guidoguba', 123342211, 'albert' , 'barra'   ), "+
                                                "('testtest@test.com'    , 'testtest' , 123342212, 'nicolas', 'gonzales'), "+
                                                "('testguido@hotmail.com', 'testguido', 123342213, 'jose'   , 'Skypo'   ), "+
                                                "('guidotest@test.com'   , 'guidotest', 123342214, 'pepe'   , 'Ceilo'   );");

    private static final String DB_NAME = "db.sqlite";
    private static final int DB_VERSION = 5;
    private static final String TAG_DB_SQLITE = "DB_SQLITE";

    public SQLiteConfig(Context context) {

        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG_DB_SQLITE, "onCreate: CREATE TABLE");
        db.execSQL(USERS);
        db.execSQL(SCORE);
        db.execSQL(INSERT_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.i(TAG_DB_SQLITE, "onUpgrade: DROP TABLE");
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_SCORE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
        db.setVersion(oldVersion);
    }

}