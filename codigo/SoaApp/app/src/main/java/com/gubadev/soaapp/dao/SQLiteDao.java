package com.gubadev.soaapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import com.gubadev.soaapp.configuration.SQLiteConfig;
import com.gubadev.soaapp.constant.Constants;
import com.gubadev.soaapp.dto.UserDTO;

public class SQLiteDao {

    private static final String TAG_DAO_SQLITE = "TAG DAO SQLITE";

    private static Cursor c;


    public static SQLiteDatabase builder(Context context) {
        SQLiteConfig dbHelper = new SQLiteConfig(context);
        return dbHelper.getWritableDatabase();
    }

    public static boolean saveUser(UserDTO user, @Nullable SQLiteDatabase db) {

        try {

            c = db.rawQuery("Select * from " + Constants.TABLE_USER +
                                " WHERE " + Constants.EMAIL + " =  ?",
                                new String[]{Constants.EMAIL});
            //Cursor cur = db.rawQuery("Select * from " + Constants.TABLE_USER, null);
            //cur.moveToFirst();

            if (c.getCount() != 0) {
                return false;
            }

            ContentValues values = new ContentValues();
            values.put(Constants.FIRST_NAME, user.getFirstName());
            values.put(Constants.LAST_NAME, user.getLastName());
            values.put(Constants.DNI, user.getDni());
            values.put(Constants.EMAIL, user.getEmail());
            values.put(Constants.PASSWORD, user.getPassword());
            Long result = db.insert(Constants.TABLE_USER, Constants.ID, values);

            Log.i(TAG_DAO_SQLITE, "DAO SQLite OK");

        } catch (Exception e) {
            Log.e(TAG_DAO_SQLITE, "DAO SQLite ERROR: " + e.getMessage(), e);
            return false;
        } finally {
            c.close();
            db.close();
        }

        return true;
    }
}
