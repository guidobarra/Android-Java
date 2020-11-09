package com.gubadev.soaapp.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import com.gubadev.soaapp.constant.Constants;
import com.gubadev.soaapp.dao.SQLiteDao;
import com.gubadev.soaapp.dto.UserDTO;
import com.gubadev.soaapp.util.Util;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPService extends IntentService {

    private static final String TAG_HTTP_SERVICE = "HTTPService";

    private static final String TAG_JSON = "JSON";

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
        String responseJSON = "";
        String email = "";
        try {
            Log.i(TAG_HTTP_SERVICE, "onHandleIntent");

            /*GET VALUES*/
            String requestJSON = intent.getExtras().getString("requestJSON", "");
            String urlPath = intent.getExtras().getString("url", "");
            boolean isSaveUser = intent.getExtras().getBoolean("isSaveUser", false);

            /*INSTANCE JSON OBJECT*/
            JSONObject requestJSONObject = new JSONObject(requestJSON);

            Log.i(TAG_HTTP_SERVICE, "requestJSON: "+requestJSON);
            Log.i(TAG_HTTP_SERVICE, "urlPath: "+urlPath);

            URL url = new URL(urlPath);

            /*CONFIGURATION REST*/
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setConnectTimeout(5000);
            urlConnection.setRequestMethod("POST");

            DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());

            /*SET REQUEST IN BYTE*/
            wr.write(requestJSON.getBytes("UTF-8"));

            /*CLEAN*/
            wr.flush();

            /*CONNECTION*/
            urlConnection.connect();

            /*GET RESPONSE*/
            int responseCode = urlConnection.getResponseCode();

            /*CHECK RESPONSE*/
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                InputStreamReader inputStream = new InputStreamReader(urlConnection.getInputStream());
                responseJSON = convertInputStreamToString(inputStream);
            } else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                InputStreamReader inputStream = new InputStreamReader(urlConnection.getErrorStream());
                responseJSON = convertInputStreamToString(inputStream);
            }

            /*CLOSE CONNECTIONS*/
            wr.close();
            urlConnection.disconnect();

            JSONObject responseJson = new JSONObject(responseJSON);

            /*CHECK RESPONSE JSON*/
            if (Util.isEmptyOrNull(responseJSON) || !responseJson.getBoolean("success")) {
                Log.e(TAG_JSON, "error json failure or JSON empty");
                throw new Exception("ERROR: error json failure or JSON empty");
            }

            /*CHECK IF SAVE USER*/
            if (isSaveUser) {
                /*INSTANCE USER*/
                UserDTO user = new UserDTO(
                        requestJSONObject.getString("env"),
                        requestJSONObject.getString("name"),
                        requestJSONObject.getString("lastname"),
                        requestJSONObject.getLong("dni"),
                        requestJSONObject.getString("email"),
                        requestJSONObject.getString("password"),
                        requestJSONObject.getLong("commission")
                );

                /*SAVE USER WITH SQLITE*/
                SQLiteDao.saveUser(user, SQLiteDao.builder(activity));
            }

            email = requestJSONObject.getString("email");
            Log.i(TAG_HTTP_SERVICE, Constants.STATE_SUCCESS);
        } catch (Exception e) {
            Log.e(TAG_HTTP_SERVICE, Constants.STATE_ERROR);
            responseJSON = "";
            email = "";
            e.printStackTrace();
        }

        /*INSTANCE INTENT, BROADCAST RECEPTOR OPERATION*/

        Intent i = new Intent(intent.getExtras().getString("action"));
        i.putExtra("responseJSON", responseJSON);
        i.putExtra("email", email);

        /*SEND BROADCAST */
        sendBroadcast(i);
    }

    /**
     * convert Input Stream To String
     */
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

}
