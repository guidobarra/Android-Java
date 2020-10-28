package com.gubadev.soaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.gubadev.soaapp.dao.SQLiteDao;
import com.gubadev.soaapp.dto.Score;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TopGamerActivity extends AppCompatActivity {


    private TextView first;
    private TextView second;
    private TextView third;
    private TextView fourth;
    private TextView fifth;
    private TextView sixth;

    private Map<Integer, TextView> map = new HashMap<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_gamer);

        first = findViewById(R.id.topOne);
        second = findViewById(R.id.topSecond);
        third = findViewById(R.id.topThird);
        fourth = findViewById(R.id.topFourth);
        fifth = findViewById(R.id.topFifth);
        sixth = findViewById(R.id.topSixth);
        instanceMap();

        ThereadAsynctask hilo = new ThereadAsynctask();
        hilo.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void instanceMap() {
        map.put(0, first);
        map.put(1, second);
        map.put(2, third);
        map.put(3, fourth);
        map.put(4, fifth);
        map.put(5, sixth);
    }

    class ThereadAsynctask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            List<Score> scores = SQLiteDao.getTopGamer(SQLiteDao.builder(TopGamerActivity.this));
            int i = 0;
            for (Score s: scores) {
                if (i<6) {
                    String value = map.get(i).getText() + " " +
                            s.getNameGamer() + " " +
                            s.getScore() + " " +
                            s.getTime();
                    map.get(i).setText(value);
                }

                i++;
            }
            return null;
        }
    }
}
