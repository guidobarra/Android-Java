package com.gubadev.soaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;


public class HomeActivity extends AppCompatActivity {

    private TextView emailEditText;
    private TextView providerEditText;

    private Button logOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Bundle bundle = this.getIntent().getExtras();

        String email = bundle.getString("email");
        String provider = bundle.getString("provider");
        setup(email, provider);
    }

    private void setup(String emailValue, String providerValue) {
        String title = "Inicio";

        emailEditText = findViewById(R.id.emailTextView);
        providerEditText = findViewById(R.id.providerTextView);

        emailEditText.setText(emailValue);
        providerEditText.setText(providerValue);

        logOut = findViewById(R.id.logOutButton);
        logOut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FirebaseAuth
                        .getInstance()
                        .signOut();
                onBackPressed();
            }
        });
    }
}
