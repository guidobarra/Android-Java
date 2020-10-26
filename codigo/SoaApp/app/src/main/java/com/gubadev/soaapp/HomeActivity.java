package com.gubadev.soaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.gubadev.soaapp.util.AlertDialog;


public class HomeActivity extends AppCompatActivity {

    private TextView emailEditText;
    private TextView providerEditText;

    private Button logOut;
    private Button playGame;
    private Button sensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle("Home");

        emailEditText = findViewById(R.id.emailTextView);
        providerEditText = findViewById(R.id.providerTextView);

        logOut = findViewById(R.id.logOutButton);
        playGame = findViewById(R.id.playButton);
        sensor = findViewById(R.id.sensorButton);

        logOut.setOnClickListener(signOut);
        playGame.setOnClickListener(playGamer);
        sensor.setOnClickListener(sensorView);

        Bundle bundle = this.getIntent().getExtras();

        String email = bundle.getString("email");
        String provider = bundle.getString("provider");

        int level = new Intent().getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        providerEditText.setText(String.valueOf(level) + "%");
        showAlert();

    }

    private View.OnClickListener signOut = view -> {
        FirebaseAuth.getInstance().signOut();

        Intent instant = new Intent(HomeActivity.this, LogInActivity.class);
        startActivity(instant);
    };

    private View.OnClickListener playGamer = view -> {
        Intent intent = new Intent( HomeActivity.this, GameActivity.class);
        startActivity(intent);
    };

    private View.OnClickListener sensorView = view -> {
        Intent intent = new Intent( HomeActivity.this, SensorActivity.class);
        startActivity(intent);
    };



    private void showAlert() {

        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, iFilter);

        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level * 100 / (float) scale;

        String batteryLevel = String.valueOf(batteryPct) + "%";
        providerEditText.setText(batteryLevel);
        AlertDialog.displayAlertDialog(HomeActivity.this,
                "Battery",
                "El nivel de la bateria es: " + batteryLevel,
                "OK");
    }


}
