package com.gubadev.soaapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import com.gubadev.soaapp.dto.Score;
import com.gubadev.soaapp.singleton.MySingleton;
import com.gubadev.soaapp.configuration.SQLiteConfig;
import com.gubadev.soaapp.constant.Constants;
import com.gubadev.soaapp.dto.UserDTO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SQLiteDao {

    private static final String TAG_DAO_SQLITE = "DAO SQLITE";

    private static Cursor c;


    /**
     * BUILDER SQLiteDao
     */
    public static SQLiteDatabase builder(Context context) {
        SQLiteConfig dbHelper = new SQLiteConfig(context);
        return dbHelper.getWritableDatabase();
    }

    /**
     * SAVE USER IN SQLITE
     */
    public static void saveUser(UserDTO user, @Nullable SQLiteDatabase db) {

        try {
            /*QUERY*/
            c = db.rawQuery("Select * from " + Constants.TABLE_USER +
                                " WHERE " + Constants.EMAIL + " =  ?",
                                new String[]{user.getEmail()});

            /*CHECK EMAIL EXIST*/
            if (c.getCount() != 0) {
                Log.e(TAG_DAO_SQLITE, "error: email exist");
                return;
            }

            /*SET VALUES*/
            ContentValues values = new ContentValues();
            values.put(Constants.FIRST_NAME, user.getFirstName());
            values.put(Constants.LAST_NAME, user.getLastName());
            values.put(Constants.DNI, user.getDni());
            values.put(Constants.EMAIL, user.getEmail());
            values.put(Constants.PASSWORD, user.getPassword());

            /*SAVE OR INSERT USER*/
            long result = db.insert(Constants.TABLE_USER, Constants.ID, values);

            Log.i(TAG_DAO_SQLITE, "DAO SQLite result: " + result);

        } catch (Exception e) {
            Log.e(TAG_DAO_SQLITE, "DAO SQLite ERROR: " + e.getMessage(), e);
        } finally {
            c.close();
            db.close();
        }
    }

    /**
     * SAVE SCORE IN SQLITE
     */
    public static void saveScore(@Nullable SQLiteDatabase db) {
        try {
            int idUser = -1;
            String firstName = "";
            String lastName = "";

            /*QUERY*/
            c = db.rawQuery("Select * from " + Constants.TABLE_USER +
                            " WHERE " + Constants.EMAIL + " =  ?",
                    new String[]{MySingleton.getInstance().getEmail()});

            /*CHECK EXIST ELEMENT*/
            if (c.moveToFirst()) {
                /*GET USER*/
                do {
                    firstName = c.getString(c.getColumnIndex(Constants.FIRST_NAME));
                    lastName = c.getString(c.getColumnIndex(Constants.LAST_NAME));
                    idUser = c.getInt(c.getColumnIndex(Constants.ID));
                } while (c.moveToNext());
            }

            /*CHECK USER EXIST*/
            if (idUser == -1 || firstName.isEmpty() || lastName.isEmpty()) {
                Log.e(TAG_DAO_SQLITE, "error: user no exist");
                return;
            }

            /*GET DATE NOW*/
            String pattern = "YYYY-MM-DD HH:MM:SS";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
            String date = simpleDateFormat.format(new Date());

            /*SET VALUES*/
            ContentValues values = new ContentValues();
            values.put(Constants.DATE, date);
            values.put(Constants.SCORE, MySingleton.getInstance().getScore());
            values.put(Constants.TIME, MySingleton.getInstance().getTime());
            values.put(Constants.NAME_GAMER, firstName + "_" + lastName);
            values.put(Constants.USER_ID, idUser);

            /*SAVE OR INSERT SCORE*/
            long result = db.insert(Constants.TABLE_SCORE, Constants.ID, values);

            Log.i(TAG_DAO_SQLITE, "DAO SQLite result: " + result);
        } catch (Exception e) {
            Log.e(TAG_DAO_SQLITE, "DAO SQLite ERROR: " + e.getMessage(), e);
        } finally {
            c.close();
            db.close();
        }
    }

    /**
     * GET TOP GAMER OF SQLITE
     */
    public static List<Score> getTopGamer(@Nullable SQLiteDatabase db) {
        /*LIST OF SCORES*/
        List<Score> scores = new ArrayList<>();

        try {


            /*QUERY*/
            c = db.rawQuery("Select * from " + Constants.TABLE_SCORE, null);

            /*CHECK EXIST ELEMENT*/
            if (c.moveToFirst()) {
                do {
                    /*INSTANCE SCORE*/
                    Score score = new Score(
                            c.getInt(c.getColumnIndex(Constants.SCORE)),
                            c.getInt(c.getColumnIndex(Constants.TIME)),
                            c.getString(c.getColumnIndex(Constants.NAME_GAMER)),
                            c.getString(c.getColumnIndex(Constants.DATE)));

                    /*ADD SCORE TO LIST*/
                    scores.add(score);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG_DAO_SQLITE, "DAO SQLite ERROR: " + e.getMessage(), e);
        } finally {
            c.close();
            db.close();
        }

        return scores;
    }
}
