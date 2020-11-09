package com.gubadev.soaapp;


import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
//import com.gubadev.soaapp.broadcast.ReceptorOperation;
import com.gubadev.soaapp.constant.Constants;
import com.gubadev.soaapp.service.HTTPService;
import com.gubadev.soaapp.singleton.MySingleton;
import com.gubadev.soaapp.util.AlertDialog;
import com.gubadev.soaapp.util.Internet;
import com.gubadev.soaapp.util.Util;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG_FIREBASE_REGISTER = "FIREBASE_REGISTER";

    private static final String TAG_JSON = "JSON";

    private static final String ACTION_REGISTER = "com.example.intentservice.intent.action.RESPUESTA_OPERACION_REGISTRO";

    Button register;

    private TextInputEditText firstNameText;
    private TextInputEditText lastNameText;
    private EditText dniText;
    private EditText emailText;
    private EditText passwordText;

    private String firstName;
    private String lastName;
    private Long dni;
    private String email;
    private String password;

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

    public void setEnabledButton(boolean b) {
        register.setEnabled(b);
    }

    /**
     * action OnClickListener for register
     */
    private View.OnClickListener registerUser = view -> {

        /*GET VALUES*/
        firstName = Util.getValue(firstNameText);
        lastName = Util.getValue(lastNameText);
        email = Util.getValue(emailText);
        password = Util.getValue(passwordText);
        String valueDni = Util.getValue(dniText);
        dni = Long.valueOf( Util.isEmptyOrNull(valueDni) ? "0" : valueDni);

        /*VALIDATE CONNECTION INTERNET, DATA EMAIL AND PASSWORD*/
        if (!validate()) {
            return;
        }

        setEnabledButton(false);

        /*GET INSTANCE FIREBASE*/
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        /*FIREBASE CREATE WITH EMAIL AND PASSWORD*/
        mAuth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(RegisterActivity.this,
        task -> {
            /*CHECK IS SUCCESSFUL*/
            if (!task.isSuccessful()) {
                showAlert();
                Log.e(TAG_FIREBASE_REGISTER, "createUserWithEmailAndPassword:failure", task.getException());
                return;
            }

            Log.i(TAG_FIREBASE_REGISTER, "createUserWithEmailAndPassword:success");

            try {
                /*CREATE JSON FOR HTTP REST /register */
                JSONObject requestJSON = new JSONObject();

                /*SET VALUES*/
                requestJSON.put("env", Constants.ENV);
                requestJSON.put("name", firstName);
                requestJSON.put("lastname", lastName);
                requestJSON.put("dni", dni);
                requestJSON.put("email", email);
                requestJSON.put("password", password);
                requestJSON.put("commission", Constants.NUM_COMMISSION);

                /*INSTANCE INTENT WITH ACTIVITY AND INTENT SERVICE*/
                Intent intent = new Intent(RegisterActivity.this, HTTPService.class);

                /*SET VALUES*/
                intent.putExtra("requestJSON", requestJSON.toString());
                intent.putExtra("url", Constants.URI_CATEDRA_SOA_REGISTER);
                intent.putExtra("isSaveUser", true);
                intent.putExtra("action", ACTION_REGISTER);

                /*START SERVICE*/
                startService(intent);

            } catch (Exception e ) {
                Log.e(TAG_JSON, "error json");
                showAlert();
                setEnabledButton(true);
                e.printStackTrace();
            }

        });


    };

    /**
     * configuration Broadcast Receiver for communication HTTP REST register of user
     */
    private void configurationBroadcastReceiver() {
        IntentFilter filter = new IntentFilter(ACTION_REGISTER);

        filter.addCategory(Intent.CATEGORY_DEFAULT);

        registerReceiver(new ReceptorOperation(), filter);
    }

    /**
     * show Alert
     */
    private void showAlert() {
        AlertDialog.displayAlertDialog(RegisterActivity.this,
                "Error",
                "Se ha producido un error al registrar el usuario",
                "OK");
    }

    /**
     * validate connection internet
     * validate values
     * validate length password
     */
    public boolean validate() {

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

    class ReceptorOperation extends BroadcastReceiver {

        private static final String TAG_JSON = "JSON RECEPTOR OPERATION";

        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                String responseJSON = intent.getStringExtra("responseJSON");
                String email = intent.getStringExtra("email");
                JSONObject responseJson = new JSONObject(responseJSON);

                /*CHECK RESPONSE*/
                if (Util.isEmptyOrNull(responseJSON) || !responseJson.getBoolean("success")) {
                    Log.e(TAG_JSON, "error json faurile or JSON empty");
                    throw new Exception("ERROR: error json failure or JSON empty");
                }

                /*SET EMAIL, TOKEN AND TOKEN REFRESH IN MYSINGLENTON */
                MySingleton.getInstance().setEmail(intent.getStringExtra("email"));
                MySingleton.getInstance().setToken(responseJson.getString("token"));
                MySingleton.getInstance().setTokenRefresh(responseJson.getString("token_refresh"));

                /*INSTANCE INTENT, LOGINACTIVITY -> HomeActivity*/
                Intent home = new Intent(context, HomeActivity.class);
                home.putExtra("email", email);
                home.putExtra("provider", "BASICO");

                /*START INTENT*/
                context.startActivity(home);

            } catch (Exception e) {
                Log.e(TAG_JSON, "error");
                showAlert();
                e.printStackTrace();
            }
            setEnabledButton(true);
        }
    }

}
