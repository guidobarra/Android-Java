package com.gubadev.soaapp;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gubadev.soaapp.game.GameView;


public class GameActivity extends AppCompatActivity {

    private GameView entertainment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*INSTANCIO GAMEVIEW CON UN ACTIVITY*/
        entertainment = new GameView(this);

        /*SETTEO el CONTENT VIEW CON MI GAMEVIEW*/
        setContentView(entertainment);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**PARA EL PAUSE, STOP Y DISTROY TENGO QUE PARAR EL SENSOR ACELEROMETRO*/
    @Override
    protected void onPause() {
        super.onPause();
        entertainment.stopGame();
    }

    @Override
    protected void onStop() {
        super.onStop();
        entertainment.stopGame();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        entertainment.stopGame();
    }
}
