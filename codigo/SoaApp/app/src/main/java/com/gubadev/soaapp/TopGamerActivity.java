package com.gubadev.soaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
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
    private TextView title;

    private Map<Integer, TextView> map = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_gamer);
        setTitle("Top Five Gamer");

        title = findViewById(R.id.topTitle);
        first = findViewById(R.id.topOne);
        second = findViewById(R.id.topSecond);
        third = findViewById(R.id.topThird);
        fourth = findViewById(R.id.topFourth);
        fifth = findViewById(R.id.topFifth);

        instanceMap();

        SQLiteTopGamerAsyncTask hilo = new SQLiteTopGamerAsyncTask();
        hilo.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void instanceMap() {
        map.put(0, title);
        map.put(1, first);
        map.put(2, second);
        map.put(3, third);
        map.put(4, fourth);
        map.put(5, fifth);
    }

    class SQLiteTopGamerAsyncTask extends AsyncTask<Void, Void, List<Score>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<Score> scores) {
            int i = 0;
            String value = "Top" + "|Date Hour___________|"+ format("Name",13) + "|Score";
            map.get(i).setText(Html.fromHtml("<b>" + value +"</b>"));
            i++;
            for (Score s: scores) {
                if (i<6) {
                    value  = map.get(i).getText() + "|" +
                            s.getDate() + "|" +
                            format(s.getNameGamer().trim(), 15) + "|" +
                            format(s.getScore().toString(),5);
                    map.get(i).setText(value);
                }

                i++;
            }

        }

        private String format(String s, int cant) {
            String resul = "";
            if(s.length()<cant) {
                resul = s + multiString(cant-s.length(), "_");
            } else {
                resul = s.substring(0,cant);
            }
            return resul;
        }

        private String multiString(int cant, String str){
            StringBuilder sb = new StringBuilder("");
            for (int i=0; i<cant; i++) {
                sb.append(str);
            }
            return sb.toString();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected List<Score> doInBackground(Void... voids) {

            List<Score> scores = SQLiteDao.getTopGamer(SQLiteDao.builder(TopGamerActivity.this));

            return scores;
        }

    }
}
