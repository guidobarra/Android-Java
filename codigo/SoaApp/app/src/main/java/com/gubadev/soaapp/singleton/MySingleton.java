package com.gubadev.soaapp.singleton;

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

    private Integer score;

    private Integer time;

    private static final Integer DELAY = 5*1000*60;

    private static final Integer PERIOD = 25*1000*60;

    private Timer timer = new Timer();

    private RefreshTokenTime refreshTokenTime = new RefreshTokenTime();

    private static final MySingleton ourInstance = new MySingleton();

    public synchronized static MySingleton getInstance() {
        return ourInstance;
    }

    private MySingleton() {
        timer.schedule(refreshTokenTime, DELAY, PERIOD);
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

    public Integer getScore() {
        return score;
    }

    public Integer getTime() {
        return time;
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

    public void setScore(Integer score) {
        this.score = score;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public void stopTimerTask() {
        timer.cancel();
        refreshTokenTime.cancel();
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
                        Log.e("error", "");
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
