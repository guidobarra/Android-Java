package com.gubadev.soaapp;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.gubadev.soaapp.broadcast.ReceptorOperacion;
import com.gubadev.soaapp.constant.Constants;
import com.gubadev.soaapp.service.HTTPService;
import com.gubadev.soaapp.util.AlertDialog;
import com.gubadev.soaapp.util.Internet;
import com.gubadev.soaapp.util.Util;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG_FIREBASE_REGISTER = "FIREBASE_REGISTER";

    Button register;

    TextInputEditText firstNameText;
    TextInputEditText lastNameText;
    EditText dniText;
    EditText emailText;
    EditText passwordText;

    String firstName;
    String lastName;
    Long dni;
    String email;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("Register");
        register = findViewById(R.id.register);

        firstNameText = findViewById(R.id.firstname);
        lastNameText = findViewById(R.id.lastname);
        dniText = findViewById(R.id.dni);
        emailText = findViewById(R.id.email);
        passwordText = findViewById(R.id.pass);

        register.setOnClickListener(registerUser);

        HTTPService.builder(RegisterActivity.this);

        configurationBroadcastReceiver();
    }

    private View.OnClickListener registerUser = view -> {

        firstName = Util.getValue(firstNameText);
        lastName = Util.getValue(lastNameText);
        email = Util.getValue(emailText);
        password = Util.getValue(passwordText);
        String valueDni = Util.getValue(dniText);
        dni = Long.valueOf( Util.isEmptyOrNull(valueDni) ? "0" : valueDni);

        if (!validateData()) {
            return;
        }

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(RegisterActivity.this,
        task -> {

            if (!task.isSuccessful()) {
                showAlert();
                Log.e(TAG_FIREBASE_REGISTER, "createUserWithEmailAndPassword:failure", task.getException());
                return;
            }
            Log.i(TAG_FIREBASE_REGISTER, "createUserWithEmailAndPassword:success");

            try {
                JSONObject requestJSON = new JSONObject();

                requestJSON.put("env", Constants.ENV);
                requestJSON.put("name", firstName);
                requestJSON.put("lastname", lastName);
                requestJSON.put("dni", dni);
                requestJSON.put("email", email);
                requestJSON.put("password", password);
                requestJSON.put("commission", Constants.NUM_COMMISSION);

                Intent intent = new Intent(RegisterActivity.this, HTTPService.class);

                intent.putExtra("requestJSON", requestJSON.toString());
                intent.putExtra("url", Constants.URI_CATEDRA_SOA_REGISTER);

                startService(intent);


            } catch (Exception e ) {
                e.printStackTrace();
            }

        });


    };

    private void configurationBroadcastReceiver() {
        IntentFilter filter = new IntentFilter("com.example.intentservice.intent.action.RESPUESTA_OPERACION");

        filter.addCategory(Intent.CATEGORY_DEFAULT);

        registerReceiver(new ReceptorOperacion(), filter);
    }

    private void showAlert() {
        // Use the Builder class for convenient dialog construction
        AlertDialog.displayAlertDialog(RegisterActivity.this,
                "Error",
                "Se ha producido un error autenticando el usuario",
                "OK");
    }

    public boolean validateData() {

        boolean internetConnection = Internet.isInternetAvailable(this);

        if (!internetConnection) {
            AlertDialog.displayAlertDialog(this,
                                            "Error de conexión",
                                       "Verifique su conexión de internet.",
                                       "OK");

            Log.e("INTERNET", "NO HAY CONEXION INTERNET");
            return false;
        }

        if ( Util.isEmptyOrNull(firstName) || Util.isEmptyOrNull(lastName) ||
                Util.isEmptyOrNull(email) || Util.isEmptyOrNull(password) ){

            AlertDialog.displayAlertDialog(this,
                                            "Datos incompletos",
                                       "Todos los campos son requeridos, complete todos los campos.",
                                       "OK");

            Log.e("Inputs Error", "onClick: Error en valores de resgistro");
            return false;
        }

        if ( password.length() < 8){
            AlertDialog.displayAlertDialog(this,
                                            "Datos incorrectos",
                                       "La constraseña tiene que tener mayor a 8 caracteres.",
                                       "OK");

            Log.i("Password Error", "onClick: Password Menor a 8 caracteres");
            return false;
        }

        return true;
    }

}
