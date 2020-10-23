package com.gubadev.soaapp.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

public class Internet {

    public static boolean isInternetAvailable(Activity activity) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        if ( cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected() ){
            Log.i("INTERNET", "isInternetAvailable: there is network");
            try {
                final String command = "ping -c 1 google.com";
                return Runtime.getRuntime().exec(command).waitFor() == 0;

            } catch (Exception e) {
                Log.i("INTERNET", "error: " + e.getMessage());
            }
        }
        return false;
    }


}