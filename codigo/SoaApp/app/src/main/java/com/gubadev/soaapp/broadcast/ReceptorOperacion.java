package com.gubadev.soaapp.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gubadev.soaapp.HomeActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class ReceptorOperacion extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                String responseJSON = intent.getStringExtra("responseJSON");
                JSONObject responseJson = new JSONObject(responseJSON);
                Log.i("responseJSON", responseJSON);
                Intent home = new Intent(context, HomeActivity.class);
                home.putExtra("email", responseJson.getString("env"));
                home.putExtra("provider", "BASICO");
                context.startActivity(home);

            } catch (JSONException e) {
                e.printStackTrace();
                Log.i("responseJSON", "error");
            }
        }
}