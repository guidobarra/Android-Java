package com.gubadev.soaapp.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gubadev.soaapp.HomeActivity;
import com.gubadev.soaapp.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class ReceptorOperacion extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                String responseJSON = intent.getStringExtra("responseJSON");
                JSONObject responseJson = new JSONObject(responseJSON);

                if (Util.isEmptyOrNull(responseJSON) || !responseJson.getBoolean("success")) {
                    return;
                }
                Log.i("responseJSON", responseJSON);
                Intent home = new Intent(context, HomeActivity.class);
                home.putExtra("email", "GUIDO");
                home.putExtra("provider", "BASICO");
                context.startActivity(home);

            } catch (JSONException e) {
                e.printStackTrace();
                Log.i("responseJSON", "error");
            }
        }
}