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


    public static SQLiteDatabase builder(Context context) {
        SQLiteConfig dbHelper = new SQLiteConfig(context);
        return dbHelper.getWritableDatabase();
    }

    public static void saveUser(UserDTO user, @Nullable SQLiteDatabase db) {

        try {

            c = db.rawQuery("Select * from " + Constants.TABLE_USER +
                                " WHERE " + Constants.EMAIL + " =  ?",
                                new String[]{user.getEmail()});

            if (c.getCount() != 0) {
                Log.e(TAG_DAO_SQLITE, "error: email exist");
                return;
            }

            ContentValues values = new ContentValues();
            values.put(Constants.FIRST_NAME, user.getFirstName());
            values.put(Constants.LAST_NAME, user.getLastName());
            values.put(Constants.DNI, user.getDni());
            values.put(Constants.EMAIL, user.getEmail());
            values.put(Constants.PASSWORD, user.getPassword());
            Long result = db.insert(Constants.TABLE_USER, Constants.ID, values);

            Log.i(TAG_DAO_SQLITE, "DAO SQLite result: " + result);

        } catch (Exception e) {
            Log.e(TAG_DAO_SQLITE, "DAO SQLite ERROR: " + e.getMessage(), e);
        } finally {
            c.close();
            db.close();
        }
    }

    public static void saveScore(@Nullable SQLiteDatabase db) {

        int idUser = -1;
        String firstName = "";
        String lastName = "";


        c = db.rawQuery("Select * from " + Constants.TABLE_USER +
                        " WHERE " + Constants.EMAIL + " =  ?",
                new String[]{MySingleton.getInstance().getEmail()});

        if (c.moveToFirst()){
            do {
                firstName = c.getString(c.getColumnIndex(Constants.FIRST_NAME));
                lastName = c.getString(c.getColumnIndex(Constants.LAST_NAME));
                idUser = c.getInt(c.getColumnIndex(Constants.ID));
            } while(c.moveToNext());
        }


        String pattern = "YYYY-MM-DD HH:MM:SS";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        String date = simpleDateFormat.format(new Date());

        ContentValues values = new ContentValues();
        values.put(Constants.DATE, date);
        values.put(Constants.SCORE, MySingleton.getInstance().getScore());
        values.put(Constants.TIME, MySingleton.getInstance().getTime());
        values.put(Constants.NAME_GAMER, firstName + "_" + lastName);
        values.put(Constants.USER_ID, idUser);
        Long result = db.insert(Constants.TABLE_SCORE, Constants.ID, values);
    }

    public static List<Score> getTopGamer(@Nullable SQLiteDatabase db) {

        List<Score> scores = new ArrayList<>();
        c = db.rawQuery("Select * from " + Constants.TABLE_SCORE, null);

        if (c.moveToFirst()){
            do {
                Score score = new Score(
                        c.getInt(c.getColumnIndex(Constants.SCORE)),
                        c.getInt(c.getColumnIndex(Constants.TIME)),
                        c.getString(c.getColumnIndex(Constants.NAME_GAMER)),
                        c.getString(c.getColumnIndex(Constants.DATE)));
                scores.add(score);
            } while(c.moveToNext());
        }

        return scores;
    }
}
