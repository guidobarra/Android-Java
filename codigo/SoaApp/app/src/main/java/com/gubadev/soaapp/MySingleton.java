package com.gubadev.soaapp;

import android.util.Log;

import com.gubadev.soaapp.client.APICatedraSOA;
import com.gubadev.soaapp.client.CatedraClient;
import com.gubadev.soaapp.dto.ResponseGeneric;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MySingleton {

    private String tokenRefresh;

    private String token;

    private String email;

    private Timer timer = new Timer();
    private RefreshTokenTime refreshTokenTime = new RefreshTokenTime();

    private static final MySingleton ourInstance = new MySingleton();

    public synchronized static MySingleton getInstance() {
        return ourInstance;
    }

    private MySingleton() {
        timer.schedule(refreshTokenTime, 0, 20000);
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public String getTokenRefresh() {
        return tokenRefresh;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTokenRefresh(String tokenRefresh) {
        this.tokenRefresh = tokenRefresh;
    }

    class RefreshTokenTime extends TimerTask {
        @Override
        public void run() {
            APICatedraSOA clientCatedra = CatedraClient.getClient().create(APICatedraSOA.class);

            //SETTEO EL HEADER
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Authorization", "Bearer " + tokenRefresh);

            Call<ResponseGeneric> call = clientCatedra.updateToken(headers);

            //ENCOLO EL PROCESO, COMPARTE CICLO DE RECLOJ CON EL HILO PRINCIPAL
            call.enqueue(new Callback<ResponseGeneric>() {

                @Override
                public void onResponse(Call<ResponseGeneric> call, Response<ResponseGeneric> response) {

                    if (!response.isSuccessful() || !response.body().getSuccess()) {

                        return;
                    }

                    token = response.body().getToken();
                    tokenRefresh = response.body().getTokenRefresh();
                    Log.i("RefreshTokenTime", "token: " + token);
                    Log.i("RefreshTokenTime", "tokenRefresh: " + tokenRefresh);
                }

                @Override
                public void onFailure(Call<ResponseGeneric> call, Throwable t) {

                    call.cancel();
                }
            });

        }
    }
}
