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
        setTitle("Game Spacecraft");

        /*INSTANCE GAME VIEW WITH ACTIVITY GAME*/
        entertainment = new GameView(this);

        /*SET VIEW WITH MY GAME VIEW*/
        setContentView(entertainment);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * FOR PAUSE, STOP Y DESTROY HAVE STOP GAME
     */
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

    @Override
    protected void onRestart() {
        super.onRestart();

        /*INSTANCE GAME VIEW WITH ACTIVITY GAME*/
        entertainment = new GameView(this);

        /*SET VIEW WITH MY GAME VIEW*/
        setContentView(entertainment);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
