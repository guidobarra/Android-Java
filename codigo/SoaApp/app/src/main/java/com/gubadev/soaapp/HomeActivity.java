package com.gubadev.soaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.gubadev.soaapp.client.APICatedraSOA;
import com.gubadev.soaapp.client.CatedraClient;
import com.gubadev.soaapp.constant.Constants;
import com.gubadev.soaapp.dto.Event;
import com.gubadev.soaapp.dto.ResponseGeneric;
import com.gubadev.soaapp.singleton.MySingleton;
import com.gubadev.soaapp.util.AlertDialog;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeActivity extends AppCompatActivity {

    private static final String TAG_RETROFIT = "RETROFIT";

    private TextView emailEditText;
    private TextView batteryEditText;

    private Button logOut;
    private Button playGame;
    private Button sensor;
    private Button topGamer;

    private String batteryLevel;

    private BroadcastReceiver batteryBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            batteryEditText.setText("Battery level: "+ level + "%");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle("Home");

        emailEditText = findViewById(R.id.emailTextView);
        batteryEditText = findViewById(R.id.batteryText);

        logOut = findViewById(R.id.logOutButton);
        playGame = findViewById(R.id.playButton);
        sensor = findViewById(R.id.sensorButton);
        topGamer = findViewById(R.id.topButton);

        logOut.setOnClickListener(signOut);
        playGame.setOnClickListener(playGamer);
        sensor.setOnClickListener(sensorView);
        topGamer.setOnClickListener(topGamerView);

        emailEditText.setText(Html.fromHtml("Welcome <b>" + MySingleton.getInstance().getEmail() +"</b>"));
        registerReceiver(batteryBroadcastReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        saveEvent();
    }

    /**
     * action OnClickListener for sign out
     */
    private View.OnClickListener signOut = view -> {
        FirebaseAuth.getInstance().signOut();

        Intent instant = new Intent(HomeActivity.this, LogInActivity.class);
        startActivity(instant);
    };

    /**
     * action OnClickListener for play game view
     */
    private View.OnClickListener playGamer = view -> {
        Intent intent = new Intent( HomeActivity.this, GameActivity.class);
        startActivity(intent);
    };

    /**
     * action OnClickListener for sensor view
     */
    private View.OnClickListener sensorView = view -> {
        Intent intent = new Intent( HomeActivity.this, SensorActivity.class);
        startActivity(intent);
    };

    /**
     * action OnClickListener for top gamer view
     */
    private View.OnClickListener topGamerView = view -> {
        Intent intent = new Intent( HomeActivity.this, TopGamerActivity.class);
        startActivity(intent);
    };

    /**
     * show level battery
     */
    private void showLevelBattery() {

        batteryLevel = "Battery level: "+ getLevelBattery() + "%";
        batteryEditText.setText(batteryLevel);
        AlertDialog.displayAlertDialog(HomeActivity.this,
                "Battery",
                "Level of battery is: " + getLevelBattery() + "%",
                "OK");
    }

    private float getLevelBattery() {
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, iFilter);

        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level * 100 / (float) scale;
        return batteryPct;
    }

    /**
     * save event to REST /event
     */
    private void saveEvent() {

        APICatedraSOA clientCatedra = CatedraClient.getClient().create(APICatedraSOA.class);

        //SET HEADER
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer " + MySingleton.getInstance().getToken());

        //INSTANCE EVENT
        Event event = new Event(
                Constants.ENV,
                "login",
                "level battery: " + getLevelBattery());

        //CALL API REST CATEDRA /event
        Call<ResponseGeneric> call = clientCatedra.saveEvent(headers, event);

        //ENQUEUE PROCESS, SHARE CLOCK CYCLE WITH MAIN THREAD
        call.enqueue(new Callback<ResponseGeneric>() {

            @Override
            public void onResponse(Call<ResponseGeneric> call, Response<ResponseGeneric> response) {

                //CHECK RESPONSE
                if (!response.isSuccessful() || !response.body().getSuccess()) {
                    Log.e(TAG_RETROFIT, "onResponse: response error");
                    return;
                }
                Log.i(TAG_RETROFIT, "onResponse: response Success");
            }

            @Override
            public void onFailure(Call<ResponseGeneric> call, Throwable t) {
                Log.e(TAG_RETROFIT, "onFailure");
                call.cancel();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
