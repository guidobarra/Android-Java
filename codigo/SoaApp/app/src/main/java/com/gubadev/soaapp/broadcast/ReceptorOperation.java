package com.gubadev.soaapp.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gubadev.soaapp.HomeActivity;
import com.gubadev.soaapp.singleton.MySingleton;
import com.gubadev.soaapp.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class ReceptorOperation extends BroadcastReceiver {

    private static final String TAG_JSON = "JSON RECEPTOR OPERATION";

    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            String responseJSON = intent.getStringExtra("responseJSON");
            JSONObject responseJson = new JSONObject(responseJSON);

            /*CHECK RESPONSE*/
            if (Util.isEmptyOrNull(responseJSON) || !responseJson.getBoolean("success")) {
                Log.e(TAG_JSON, "error json faurile or JSON empty");
                return;
            }

            /*SET EMAIL, TOKEN AND TOKEN REFRESH IN MYSINGLENTON */
            MySingleton.getInstance().setEmail(intent.getStringExtra("email"));
            MySingleton.getInstance().setToken(responseJson.getString("token"));
            MySingleton.getInstance().setTokenRefresh(responseJson.getString("token_refresh"));

            /*INSTANCE INTENT, LOGINACTIVITY -> HomeActivity*/
            Intent home = new Intent(context, HomeActivity.class);
            home.putExtra("email", responseJson.getString("email"));
            home.putExtra("provider", "BASICO");

            /*START INTENT*/
            context.startActivity(home);

        } catch (JSONException e) {
            Log.e(TAG_JSON, "error json");
            e.printStackTrace();
        }
    }
}