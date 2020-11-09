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

import com.google.firebase.auth.FirebaseAuth;
import com.gubadev.soaapp.constant.Constants;
import com.gubadev.soaapp.service.HTTPService;
import com.gubadev.soaapp.singleton.MySingleton;
import com.gubadev.soaapp.util.AlertDialog;
import com.gubadev.soaapp.util.Internet;
import com.gubadev.soaapp.util.Util;

import org.json.JSONObject;

public class LogInActivity extends AppCompatActivity {

    private static final String TAG_FIREBASE_LOGIN = "FIREBASE_LOGIN";

    private static final String TAG_JSON = "JSON";

    private static final String ACTION_LOGIN = "com.example.intentservice.intent.action.RESPUESTA_OPERACION_LOGIN";

    private Button registerButton;
    private Button logOutButton;

    private EditText email;
    private EditText password;

    private String emailValue;
    private String passwordValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Log In");

        registerButton = findViewById(R.id.register);
        logOutButton = findViewById(R.id.logIn);

        email = findViewById(R.id.email);
        password = findViewById(R.id.pass);

        registerButton.setOnClickListener(showActivityRegister);
        logOutButton.setOnClickListener(logIn);

        HTTPService.builder(LogInActivity.this);

        configurationBroadcastReceiver();
    }

    /**
     * configuration Broadcast Receiver for communication HTTP REST login of user
    */
    private void configurationBroadcastReceiver() {
        IntentFilter filter = new IntentFilter(ACTION_LOGIN);

        filter.addCategory(Intent.CATEGORY_DEFAULT);

        registerReceiver(new ReceptorOperation(), filter);
    }

    /**
     * action OnClickListener for login
     */
    private View.OnClickListener logIn = view -> {

        /*GET VALUES EMAIL AND PASSWORD*/
        emailValue = email.getText().toString();
        passwordValue = password.getText().toString();

        /*VALIDATE CONNECTION INTERNET, DATA EMAIL AND PASSWORD*/
        if ( !validate() ) {
            return;
        }

        setEnabledButtons(false);

        /*GET INSTANCE FIREBASE*/
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        /*FIREBASE SING IN OR LOG IN WITH EMAIL AND PASSWORD*/
        mAuth.signInWithEmailAndPassword(emailValue, passwordValue)
        .addOnCompleteListener(LogInActivity.this,
        task -> {



            Log.i(TAG_FIREBASE_LOGIN, "signInWithEmailAndPassword: success");

            try {

                /*CHECK IS SUCCESSFUL*/
                if (!task.isSuccessful()) {
                    Log.e(TAG_FIREBASE_LOGIN, "signInWithEmailAndPassword: failure", task.getException());
                    throw new Exception("ERROR: signInWithEmailAndPassword of Firebase failure");
                }

                /*CREATE JSON FOR HTTP REST /login */
                JSONObject requestJSON = new JSONObject();

                /*SET VALUES EMAIL AND PASSWORD*/
                requestJSON.put("email", emailValue);
                requestJSON.put("password", passwordValue);

                /*INSTANCE INTENT WITH ACTIVITY AND INTENT SERVICE*/
                Intent intent = new Intent(LogInActivity.this, HTTPService.class);

                /*SET VALUES*/
                intent.putExtra("requestJSON", requestJSON.toString());
                intent.putExtra("url", Constants.URI_CATEDRA_SOA_LOGIN);
                intent.putExtra("isSaveUser", false);
                intent.putExtra("action", ACTION_LOGIN);

                /*START SERVICE*/
                startService(intent);

            } catch (Exception e ) {
                Log.e(TAG_JSON, "error json");
                showAlert();
                setEnabledButtons(true);
                e.printStackTrace();
            }
        });

    };

    public void setEnabledButtons(boolean b) {
        registerButton.setEnabled(b);
        logOutButton.setEnabled(b);
    }

    /**
     * action OnClickListener for register
     */
    private View.OnClickListener showActivityRegister = view -> {
        Intent home = new Intent(this, RegisterActivity.class);
        startActivity(home);
    };

    /**
     * show Alert
     */
    private void showAlert() {
        AlertDialog.displayAlertDialog(LogInActivity.this,
                "Error",
                "Se ha producido un error autenticando el usuario",
                "OK");
    }

    /**
     * validate connection internet
     * validate values email and password
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

        if ( Util.isEmptyOrNull(emailValue) || Util.isEmptyOrNull(passwordValue) ){

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
            setEnabledButtons(true);
        }
    }

}
