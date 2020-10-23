package com.gubadev.soaapp.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import com.gubadev.soaapp.constant.Constants;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPService extends IntentService {

    private static final String TAG_HTTP_SERVICE = "HTTPService";

    private static Activity activity;

    public HTTPService() {
        super("HTTPService");
    }

    public static void builder(Activity ac) {
        activity = ac;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG_HTTP_SERVICE, "onCreate");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            Log.i(TAG_HTTP_SERVICE, "onHandleIntent");

            /**OBTENGO LAS VARIABLES*/
            String requestJSON = intent.getExtras().getString("requestJSON");
            String urlPath = intent.getExtras().getString("url");
            JSONObject request = new JSONObject(requestJSON);

            Log.i(TAG_HTTP_SERVICE, requestJSON);
            Log.i(TAG_HTTP_SERVICE, urlPath);

            String responseJSON = "";
            URL url = new URL(urlPath);

            /**CONFIGURACION DEL REST*/
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setConnectTimeout(5000);
            urlConnection.setRequestMethod("POST");

            DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());

            /**LE PASO EL RESQUEST*/
            wr.write(requestJSON.getBytes("UTF-8"));

            /**LIMPIO*/
            wr.flush();

            /**CONECTO*/
            urlConnection.connect();

            /**VARIFICO LA RESPUESTA*/
            int responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                InputStreamReader inputStream = new InputStreamReader(urlConnection.getInputStream());
                responseJSON = convertInputStreamToString(inputStream);
            } else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                InputStreamReader inputStream = new InputStreamReader(urlConnection.getErrorStream());
                responseJSON = convertInputStreamToString(inputStream);
            }

            /**CIERRO LAS CONECCIONES*/
            wr.close();
            urlConnection.disconnect();

            Intent i = new Intent("com.example.intentservice.intent.action.RESPUESTA_OPERACION");
            i.putExtra("responseJSON", responseJSON);
            sendBroadcast(i);

            Log.i(TAG_HTTP_SERVICE, Constants.STATE_SUCCESS);
        } catch (Exception e) {
            Log.e(TAG_HTTP_SERVICE, Constants.STATE_ERROR);
            e.printStackTrace();
        }

    }

    private String convertInputStreamToString(InputStreamReader input) throws Exception{
        BufferedReader br = new BufferedReader(input);
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null) {
            sb.append(line + '\n');
        }

        br.close();

        return sb.toString();
    }

    private Context getContext() {
        return activity;
    }
}
