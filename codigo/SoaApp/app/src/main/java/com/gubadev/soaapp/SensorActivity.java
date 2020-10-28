package com.gubadev.soaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gubadev.soaapp.client.APICatedraSOA;
import com.gubadev.soaapp.client.CatedraClient;
import com.gubadev.soaapp.constant.Constants;
import com.gubadev.soaapp.dto.Event;
import com.gubadev.soaapp.dto.ResponseGeneric;
import com.gubadev.soaapp.singleton.MySingleton;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SensorActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private TextView      gyroscope;
    private TextView      orientation;
    private TextView      magnetic;


    DecimalFormat decimalFormat = new DecimalFormat("###.###");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        // Defino los botones
        Button limpia   = findViewById(R.id.limpia);

        // Defino los TXT para representar los datos de los sensores
        gyroscope = findViewById(R.id.giroscopo);
        orientation = findViewById(R.id.orientacion);
        magnetic      = findViewById(R.id.magnetic);

        // Accedemos al servicio de sensores
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Limpio el texto de la deteccion
        limpia.setOnClickListener(registerEvent);
    }

    private View.OnClickListener registerEvent = view -> {

        APICatedraSOA clientCatedra = CatedraClient.getClient().create(APICatedraSOA.class);

        //OBTENGO EL TOKEN
        //MySingleton singleton = MySingleton.getInstance();
        //String token = singleton.getToken();

        //SETTEO EL HEADER
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer " + MySingleton.getInstance().getToken());

        Event event = new Event(
                Constants.ENV,
                "sensor",
                "my fisrt sensor");

        Call<ResponseGeneric> call = clientCatedra.saveEvent(headers, event);

        //ENCOLO EL PROCESO, COMPARTE CICLO DE RECLOJ CON EL HILO PRINCIPAL
        call.enqueue(new Callback<ResponseGeneric>() {

            @Override
            public void onResponse(Call<ResponseGeneric> call, Response<ResponseGeneric> response) {

                if (!response.isSuccessful() || !response.body().getSuccess()) {

                    return;
                }

            }

            @Override
            public void onFailure(Call<ResponseGeneric> call, Throwable t) {

                call.cancel();
            }
        });

    };

    // Metodo para iniciar el acceso a los sensores
    protected void initSensors() {
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),       SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),     SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),  SensorManager.SENSOR_DELAY_NORMAL);
    }

    // PARAR DE ESCUCHAR DE LOS SENSORES
    private void stopSensors() {

        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION));
        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
    }

    // ESCUCHAR EL CAMBIO DE SENSIBILIDAD DE LOS SENSORES
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // ESCUCHAR EL CAMBIO DE LOS SENSORES
    @Override
    public void onSensorChanged(SensorEvent event) {
        String txt = "";

        // CADA SENSOR PUEDE LANZAR UN THREAD QUE PASE POR AQUI
        // PARA ASEGURARNOS ANTE LOS ACCESOS SIMULTANEOS SINCRONIZAMOS

        synchronized (this)
        {
            Log.d("sensor", event.sensor.getName());

            switch(event.sensor.getType())
            {
                case Sensor.TYPE_ORIENTATION :
                    txt += "Orientacion:\n";
                    txt += "azimut: " + getDirection(event.values[0]) + "\n";
                    txt += "x: " + decimalFormat.format(event.values[0]) + "\n";
                    txt += "y: " + decimalFormat.format(event.values[1]) + "\n";
                    txt += "z: " + decimalFormat.format(event.values[2]) + "\n";
                    orientation.setText(txt);
                    break;

                case Sensor.TYPE_GYROSCOPE:
                    txt += "Giroscopo:\n";
                    txt += "x: " + decimalFormat.format(event.values[0]) + " deg/s \n";
                    txt += "y: " + decimalFormat.format(event.values[1]) + " deg/s \n";
                    txt += "z: " + decimalFormat.format(event.values[2]) + " deg/s \n";
                    gyroscope.setText(txt);
                    break;

                case Sensor.TYPE_MAGNETIC_FIELD :
                    txt += "Campo Magnetico:\n";
                    txt += "x: " + decimalFormat.format(event.values[0]) + " uT" + "\n";
                    txt += "y: " + decimalFormat.format(event.values[1]) + " uT" + "\n";
                    txt += "z: " + decimalFormat.format(event.values[2]) + " uT" + "\n";

                    magnetic.setText(txt);
                    break;

            }
        }
    }

    private String getDirection(float values) {
        String txtDirection = "";
        if (values < 22)
            txtDirection = "N";
        else if (values >= 22 && values < 67)
            txtDirection = "NE";
        else if (values >= 67 && values < 112)
            txtDirection = "E";
        else if (values >= 112 && values < 157)
            txtDirection = "SE";
        else if (values >= 157 && values < 202)
            txtDirection = "S";
        else if (values >= 202 && values < 247)
            txtDirection = "SO";
        else if (values >= 247 && values < 292)
            txtDirection = "O";
        else if (values >= 292 && values < 337)
            txtDirection = "NO";
        else if (values >= 337)
            txtDirection = "N";

        return txtDirection;
    }

    @Override
    protected void onStop() {
        stopSensors();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        stopSensors();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        stopSensors();
        super.onPause();
    }

    @Override
    protected void onRestart() {
        initSensors();
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initSensors();
    }

}
