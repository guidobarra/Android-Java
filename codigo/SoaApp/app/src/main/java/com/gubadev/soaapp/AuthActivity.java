package com.gubadev.soaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.gubadev.soaapp.enums.ProviderType;

public class AuthActivity extends AppCompatActivity {

    private static final String TAG = "EmailPassword";

    private Button singInButton;
    private Button logOutButton;

    private EditText email;
    private EditText password;

    private String emailValue;
    private String passwordValue;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // Analytics Event

        mAuth = FirebaseAuth.getInstance();

        //setup
        setup();
    }

    private void setup() {
        singInButton = findViewById(R.id.signUpButton);
        singInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createUser();
            }
        });

        logOutButton = findViewById(R.id.logInBotton);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                singIn();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void createUser() {
        String title = "Authentication";

        email = (EditText) findViewById(R.id.emailEditText);
        password = (EditText) findViewById(R.id.passEditText);

        emailValue = email.getText().toString();
        passwordValue = password.getText().toString();


        if (!emailValue.isEmpty() && !passwordValue.isEmpty()) {
            mAuth.createUserWithEmailAndPassword(emailValue, passwordValue)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                        showHome(task.getResult().getUser().getEmail(), ProviderType.BASIC);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        updateUI(null);
                        showAlert();
                    }

                    // [START_EXCLUDE]
                    //hideProgressBar();
                    // [END_EXCLUDE]
                }
            });
        }
    }

    private void singIn() {
        String title = "Authentication";

        email = findViewById(R.id.emailEditText);
        password =  findViewById(R.id.passEditText);

        emailValue = email.getText().toString();
        passwordValue = password.getText().toString();


        if (!emailValue.isEmpty() && !passwordValue.isEmpty()) {
            mAuth.signInWithEmailAndPassword(emailValue, passwordValue)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                                showHome(task.getResult().getUser().getEmail(), ProviderType.BASIC);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                updateUI(null);
                                showAlert();
                            }

                            // [START_EXCLUDE]
                            //hideProgressBar();
                            // [END_EXCLUDE]
                        }
                    });
        }
    }

    private void showAlert() {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage("Se ha producido un error autenticando el usuario");
        builder.setPositiveButton("Acetar", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showHome(String email, ProviderType provider) {
        Intent home = new Intent(this, HomeActivity.class);
        home.putExtra("email", email);
        home.putExtra("provider", provider.name());
        startActivity(home);
    }

    private void updateUI(FirebaseUser user) {
        return;
    }

    /*/
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.signUpButton) {
            createUser();
        } else if (i == R.id.logInBotton){
            singIn();
        } else {

        }
    }*/
}
