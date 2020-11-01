package com.gubadev.soaapp.game;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.gubadev.soaapp.singleton.MySingleton;
import com.gubadev.soaapp.dao.SQLiteDao;
import com.gubadev.soaapp.util.AlertDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameView extends View implements SensorEventListener {

    /*ALTO Y ANCHO DE LA PANTALLA*/
    private Integer height;
    private Integer width;

    /*RADIOS ESTACION ESPACIAL, LA NAVE ESPACIAL y AGUJERO NEGRO*/
    private Integer radiusSpacecraft = 40;
    private static final Integer radiusSpacialStation = 20;
    private static final Integer radiusBlackHole = 20;

    /*POSICION DE LOS AGUJERO NEGRO**/
    private List<Point> blackHoles = new ArrayList<>();

    /*BORDES DE X e Y*/
    private static final Integer BORDER_X = 12;
    private static final Integer BORDER_Y = 80;

    /*VALORE DEL ACELEROMETRO*/
    private float x = 0;
    private float y = 0;
    private float z = 0;

    /*POSICION DEL ESTACION ESPACIAL*/
    private float xSpacialStation = 0;
    private float ySpacialStation = 0;

    /*TIEMPO Y PUNTAJE*/
    private Integer score = 0;
    private Integer timeGameOver = 30;
    private Integer timeTotal = 0;

    private Activity activity;

    private SensorManager mSensorManager;

    /*PERIODO Y DELAY*/
    private static final Integer PERIOD_MILIS = 1000;
    private static final Integer DELAY_MILIS = 0;

    private Timer timer = new Timer();
    private TimerGame timerGame = new TimerGame();
    private Paint p = new Paint();

    public GameView(Activity activity) {
        super(activity);
        this.activity = activity;
        mSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        Sensor sSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, sSensor, SensorManager.SENSOR_DELAY_FASTEST);

        Display viewport = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        width = viewport.getWidth();
        height = viewport.getHeight() - 170;

        setSpacialStation();
        setBlackHole();

        timer.schedule(timerGame, DELAY_MILIS, PERIOD_MILIS);

    }

    /*CAMBIA LOS VALORES DE LOS TRES EJES DEL ACELEROMETRO*/
    @Override
    public void onSensorChanged(SensorEvent event) {

        if (timeGameOver == 0 || isCatchBlackHole()){
            stopGame();
            MySingleton.getInstance().setScore(score);
            MySingleton.getInstance().setTime(timeTotal);

            SQLiteDao.saveScore(SQLiteDao.builder(activity));
            AlertDialog.displayAlertDialogGame(
                    activity,
                   "Game Over",
                    "Score: " + score,
                    "Ok");
        }
        
        /*VALORES DEL ACELEROMETRO*/
        x -= event.values[0];
        y += event.values[1];
        z = event.values[2];


        if (x < (radiusSpacecraft + BORDER_X)) {/*EL X MINIMO*/
            x = (radiusSpacecraft + BORDER_X);
        } else if (x > (width - (radiusSpacecraft + BORDER_X))){/*EL X MAXIMO*/
            x = width - (radiusSpacecraft + BORDER_X);
        }

        if (y < (radiusSpacecraft + BORDER_Y)) {/*EL Y MINIMO*/
            y = (radiusSpacecraft + BORDER_Y);
        } else if (y > (height - (radiusSpacecraft + BORDER_Y))){/*EL Y MAXIMO*/
            y = height - (radiusSpacecraft + BORDER_Y);
        }

        if ( isCatchSpacialStation() ){/*ATRAPO LA ESTACION ESPACIAL, CARGA COMBUSTIBLE*/
            setSpacialStation();
            score++;
            radiusSpacecraft +=1;
            timeGameOver +=3;
        }

        invalidate();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onDraw(Canvas canvas) {

        p.setColor(Color.RED);
        p.setTextSize(50);
        canvas.drawText(
                "Score: " + score,
                50,
                50,
                p);

        p.setColor(Color.RED);
        p.setTextSize(50);
        canvas.drawText(
                "Time: " + timeGameOver,
                450,
                50,
                p);

        p.setColor(Color.BLUE);
        canvas.drawCircle(xSpacialStation, ySpacialStation, radiusSpacialStation, p);

        p.setColor(Color.RED);
        canvas.drawCircle(x, y, radiusSpacecraft, p);


        for (Point bH: blackHoles) {
            p.setColor(Color.BLACK);
            canvas.drawCircle(bH.getX(), bH.getY(), radiusBlackHole, p);
        }

    }

    public void stopGame() {
        timer.cancel();
        timerGame.cancel();
        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    }

    private void setSpacialStation() {
        // create random object
        Random r = new Random();

        int maxWidth = width - 8*BORDER_X;
        int maxHeight = height - 8*BORDER_Y;

        // Print next int value
        // Returns number between 0-bound

        // xSpacialStation value between (4*BORDER_X, width - 8*BORDER_X)
        xSpacialStation = r.nextInt(maxWidth) + 4*BORDER_X;

        // ySpacialStation value between (4*BORDER_Y, height - 8*BORDER_Y)
        ySpacialStation = r.nextInt(maxHeight) + 4*BORDER_Y;
    }

    private boolean isCatchBlackHole() {
        int radius = this.radiusSpacecraft;
        boolean resp = false;
        for(Point bH: blackHoles) {
            if (!resp){
                resp = bH.getX() <= (x + radius) && bH.getX() >=(x -radius) &&
                        bH.getY() <= (y + radius) && bH.getY() >= (y -radius);
            }
        }
        return resp;
    }

    private boolean isCatchSpacialStation() {
        int radius = this.radiusSpacecraft;
        return xSpacialStation <= (x + radius) && xSpacialStation >=(x -radius) &&
                ySpacialStation <= (y + radius) && ySpacialStation >= (y -radius);
    }

    private void setBlackHole() {
        for (int i=0; i<4; i++){
            Random r = new Random();

            int maxWidth = width - 8*BORDER_X;
            int maxHeight = height - 8*BORDER_Y;

            float x = r.nextInt(maxWidth) + 4*BORDER_X;
            float y = r.nextInt(maxHeight) + 4*BORDER_Y;
            blackHoles.add(new Point(x, y));
        }

    }

    class TimerGame extends TimerTask {
        @Override
        public void run() {
            timeGameOver -=1;
            timeTotal +=1;
            Log.i("TimerGame", "ENTRO A TIMER: " + timeGameOver);
        }
    }

}
