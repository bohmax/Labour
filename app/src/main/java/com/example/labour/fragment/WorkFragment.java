package com.example.labour.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.labour.Direction;
import com.example.labour.Package_Route;
import com.example.labour.Package_item;
import com.example.labour.R;
import com.example.labour.activity.MainActivity;
import com.example.labour.interfacce.WorkListener;
import com.example.labour.utility.Orientation_utility;
import com.example.labour.utility.Permission_utility;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.integration.android.IntentIntegrator;

import java.text.MessageFormat;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class WorkFragment extends Fragment implements SensorEventListener, WorkListener, View.OnClickListener {

    private Context context;
    private TextView titolo, descrizione, count, coordinata, direzione;
    private Button scansiona;
    private SensorManager sm;
    private Sensor steps;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private Package_item item; //lavoro da completare
    private Package_Route route;
    private WorkListener callback;
    private AlertDialog calibrazione; //allerta che indica all'utente che deve calibrare il dispositivo
    private final int QRCODE = 0;

    private float[] mGravity = new float[3]; //per gestione accelerometro e magnetometro
    private float[] mGeomagnetic = new float[3];
    private float media;
    private static int lastSaveSteps;
    private static long lastSaveTime;
    private final static int steps_offset = 1000;
    private boolean disabilitati; //se il sensore diventa impreciso disabilito per evitare eventi non desiserati

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.work_layout, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null)
            item = savedInstanceState.getParcelable("pack");

        sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sm != null) {
            steps = sm.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            magnetometer = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        count = view.findViewById(R.id.passi);
        coordinata = view.findViewById(R.id.orientation);
        direzione = view.findViewById(R.id.direzione);
        titolo = view.findViewById(R.id.titolo);
        descrizione = view.findViewById(R.id.descr);
        scansiona = view.findViewById(R.id.scansiona);

        if (item != null) {
            titolo.setText(item.getTitle());
            descrizione.setText(item.getDescription());
            route = item.getRoute();
            String coordinata = route.getCurrenteDirection().toString().replace("_", " ");
            count.setText(String.format("Fai %s a %s", route.getCurrenteSteps(), coordinata));
        }

        calibrazione = getCalibrazione();
        scansiona.setOnClickListener(this);
        setScansionaOff();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (steps != null && accelerometer != null && magnetometer != null) {
            sm.registerListener(this, steps, SensorManager.SENSOR_DELAY_UI);
            sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
            sm.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //viene generato un evento per ogni passo
        switch (event.sensor.getType()) {
            case Sensor.TYPE_STEP_DETECTOR: {
                if (event.values[0] > Integer.MAX_VALUE) //valore da scartare probabilmente
                    return;
                int timestamp = (int) event.values[0];
                if (timestamp > lastSaveSteps + steps_offset ||
                        (timestamp > 0 && System.currentTimeMillis() > lastSaveTime + steps_offset)) { //prendi il passo
                    aggiornaPassi(timestamp);
                }
                return;
            }
            case Sensor.TYPE_ACCELEROMETER:
                Orientation_utility.remove_gravity(mGravity, event.values);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                Orientation_utility.remove_gravity(mGeomagnetic, event.values);
                float campo = Orientation_utility.forzaMAgnetica(mGeomagnetic);
                if (campo > 75 && !disabilitati){
                    Log.e("intensità campo", String.valueOf(campo));
                    calibrazione.show();
                    sm.unregisterListener(this, steps);
                    sm.unregisterListener(this, accelerometer);
                    disabilitati = true;
                    return;
                }
                else if (campo < 50 && disabilitati){
                    sm.registerListener(this, steps, SensorManager.SENSOR_DELAY_UI);
                    sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
                    calibrazione.dismiss();
                    disabilitati = false;
                }

                break;
        }
        media = Orientation_utility.getRotatioMedia(mGravity, mGeomagnetic);
        coordinata.setText(String.valueOf(Math.round(media)));
        Direction dir = Orientation_utility.getDirection(media);
        String coordinata = dir.toString().replace("_", " ");
        direzione.setText(MessageFormat.format("Stai puntanto a {0}", coordinata));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    @Override
    public void onClick(View v) {
        if (v == scansiona) {
            if (Permission_utility.requestPermission(this, getActivity(), new String[]{Manifest.permission.CAMERA}, Permission_utility.FOTO_PERMISSION, "Hai bisogno di utilizzare la camera per finire con il pacco"))
                startActivityForResult(IntentIntegrator.forSupportFragment(this).setOrientationLocked(false).createScanIntent(), QRCODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == QRCODE) {
            if (resultCode == RESULT_OK) {
                setScansionaOff();
                titolo.setText(R.string.pacco);
                descrizione.setText(R.string.descizione_lunga);
                callback.workCompleted(item);
                item = null;
                route = null;
            } else {
                MainActivity main = (MainActivity) context;
                Snackbar.make(main.findViewById(R.id.frame), "Scansione fallita, riprovare", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Permission_utility.FOTO_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //fai partire l'intent
                startActivityForResult(IntentIntegrator.forSupportFragment(this).createScanIntent(), QRCODE);
            } else {
                Toast.makeText(getContext(), "Permessi necessari per scannerizzare il qrcode", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sm != null)
            sm.unregisterListener(this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("pack", item);
    }

    public void setonCompledlistener(WorkListener act) {
        callback = act;
    }

    @Override
    public void newWork(List<Package_item> list, int pos) {
        item = list.get(pos);
        titolo.setText(item.getTitle());
        descrizione.setText(item.getDescription());
        scansiona.setText(R.string.arrive_per);
        route = item.getRoute();
        String coordinata = route.getCurrenteDirection().toString().replace("_", " ");
        count.setText(String.format("Fai %s a %s", route.getCurrenteSteps(), coordinata));
    }

    @Override
    public void updateAfterStep(float coordinata) {

    }

    @Override
    public void workCompleted(Package_item item) {

    }

    private void setScansionaOn() {
        scansiona.setText(getString(R.string.scansionamento));
        scansiona.setAlpha(1);
        scansiona.setClickable(true);
    }

    private void setScansionaOff() {
        scansiona.setText(getString(R.string.seleziona_pacco));
        scansiona.setAlpha((float) 0.5);
        scansiona.setClickable(false);
    }

    private void aggiornaPassi(int timestamp) {
        if (route != null) {
            if (callback != null)
                callback.updateAfterStep(media);
            if (route.getCurrenteSteps() == 0) { //abilita scansione, sei arrivato al pacco
                setScansionaOn();
                count.setText("");
            } else {
                if (scansiona.isClickable())
                    setScansionaOff();
                String coordinata = route.getCurrenteDirection().toString().replace("_", " ");
                count.setText(String.format("Fai %s a %s", route.getCurrenteSteps(), coordinata));
            }
            lastSaveSteps = timestamp;
            lastSaveTime = System.currentTimeMillis();
        }
    }

    private AlertDialog getCalibrazione() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(R.layout.image_layout);
        return builder.setCancelable(false).create();
    }
}
