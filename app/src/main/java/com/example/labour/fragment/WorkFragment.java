package com.example.labour.fragment;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.labour.Package_item;
import com.example.labour.R;
import com.example.labour.interfacce.WorkListener;
import com.example.labour.utility.Orientation_utility;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.List;

import static android.app.Activity.RESULT_OK;

public class WorkFragment extends Fragment implements SensorEventListener, WorkListener, View.OnClickListener {

    private String passcount;
    private int passi;
    private Context context;
    private TextView titolo, descrizione, count, coordinata, direzione;
    private ImageView image;
    private Button scansiona;
    private SensorManager sm;
    private Sensor steps;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private Orientation_utility orientation = new Orientation_utility();
    private final int QRCODE = 0;

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
        titolo = view.findViewById(R.id.titolo);
        descrizione = view.findViewById(R.id.descr);
        image = view.findViewById(R.id.image);
        scansiona = view.findViewById(R.id.scansiona);
        count.setText(String.format("%s%s", passcount, String.valueOf(passi)));

        scansiona.setOnClickListener(this);
        setScansionaOn();
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
                if (passi != 0)
                    passi -= 1;
                else setScansionaOn();
                count.setText(String.format("%s%s", passcount, String.valueOf(passi)));
                return;
            }
            case Sensor.TYPE_ACCELEROMETER:
                orientation.setmGravity(event.values);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                orientation.setmGeomagnetic(event.values);
                break;
        }
        float media = orientation.getRotatioMedia();
        coordinata.setText(String.valueOf(Math.round(media)));
        direzione.setText(orientation.getDirection(media));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onClick(View v) {
        if (v == scansiona){
            startActivityForResult(IntentIntegrator.forSupportFragment(this).createScanIntent(), QRCODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == QRCODE) {
            if (resultCode == RESULT_OK) {
                //String contents = data.getStringExtra("SCAN_RESULT");
                setScansionaOff();
            }
        }
    }

    @Override
    public void newWork(List<Package_item> list, int pos) {
        Package_item item = list.get(pos);
        titolo.setText(item.getTitle());
        descrizione.setText(item.getDescription());
        scansiona.setText(R.string.arrive_per);
    }

    private void setScansionaOn(){
        scansiona.setText(getString(R.string.scansionamento));
        scansiona.setAlpha(1);
        scansiona.setClickable(true);
    }

    private void setScansionaOff(){
        scansiona.setText(getString(R.string.seleziona_pacco));
        scansiona.setAlpha((float) 0.5);
        scansiona.setClickable(false);
    }

}
