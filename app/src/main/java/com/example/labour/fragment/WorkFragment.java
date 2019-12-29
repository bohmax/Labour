package com.example.labour.fragment;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.labour.Package_item;
import com.example.labour.R;
import com.example.labour.interfacce.WorkListener;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.List;

public class WorkFragment extends Fragment implements SensorEventListener, WorkListener {

    private String passcount;
    private int passi;
    private Context context;
    private TextView count, coordinata, direzione;
    private SensorManager sm;
    private Sensor steps;
    private Sensor accelerometer;
    private Sensor magnetometer;

    //Boolean ass = false;

    private float[] mGravity; //per gestione accelerometro e magnetometro
    private float[] mGeomagnetic;
    private final int bufsize = 30; //dimensione della coda
    private CircularFifoQueue<Float> buf = new CircularFifoQueue<>(bufsize); //cerco una media per l'azimuth in modo da non avere un risultato ballerino
    private float somma = 0;//per avitae di sommare ogni volta tutti gli elementi dell array
    private int last_inserted = 0; //indice dell'elemento da rimuovere dalla coda

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.work_layout, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*Bundle extra = getIntent().getExtras();
        if(extra != null) {
            exist = extra.getBoolean("Exist");
            user_ID = extra.getString("ID");

        }*/

        if(savedInstanceState!=null){
            passi = savedInstanceState.getInt("passi");
        }
        else {
            passi = (int)(Math.random() * 10);
        }
        passcount = getString(R.string.passi);

        sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        steps = sm.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        count = view.findViewById(R.id.passi);
        coordinata = view.findViewById(R.id.orientation);
        direzione = view.findViewById(R.id.direzione);
        count.setText(String.format("%s%s", passcount, String.valueOf(passi)));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        sm.registerListener(this, steps, SensorManager.SENSOR_DELAY_UI);
        sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sm.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        super.onPause();
        sm.unregisterListener(this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("passi", passi);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //viene generato un evento per ogni passo
        switch (event.sensor.getType()){
            case Sensor.TYPE_STEP_DETECTOR:{
                passi -= 1;
                count.setText(String.format("%s%s", passcount, String.valueOf(passi)));
                return;
            }
            case Sensor.TYPE_ACCELEROMETER:
                mGravity = event.values;
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mGeomagnetic = event.values;
                break;
        }
        if (mGravity != null && mGeomagnetic != null) {
            float[] R = new float[9];
            float[] I = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float[] orientation = new float[3];
                SensorManager.getOrientation(R, orientation);
                float azimut = orientation[0]; // orientation contains: azimut, pitch and roll, azimut è in radianti
                float rotation = (float)(Math.toDegrees(azimut)+360)%360;
                if (buf.size()==bufsize) { //se la coda è piena rimuovi il primo elemento da somma e aggiungi il nuovo elemento
                    float value = buf.get(last_inserted);
                    somma -= value;
                    somma += rotation;
                    buf.add(rotation);
                    last_inserted = (last_inserted+1)%bufsize;
                    float media = somma /(float) buf.size();
                    coordinata.setText(String.valueOf(Math.round(media)));
                    direzione.setText(getDirection(media));
                } else{
                    somma += rotation;
                    buf.add(rotation);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private String getDirection(float absolute){
        int range = (int) (absolute/16);
        switch (range) {
            case 1:
            case 2:
                return "NE";
            case 3:
            case 4:
                return "E";
            case 5:
            case 6:
                return "SE";
            case 7:
            case 8:
                return "S";
            case 9:
            case 10:
                return "SW";
            case 11:
            case 12:
                return "W";
            case 13:
            case 14:
                return "NW";
            case 15:
            case 0:
            default: return "N";
        }
    }


    @Override
    public void newWork(List<Package_item> list, int pos) {
        Log.i("Cristinata", list.get(pos).getTitle());
    }
}
