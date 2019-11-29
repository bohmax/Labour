package com.example.labour;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class WorkFragment extends Fragment implements SensorEventListener {

    private MenuFragment menuf;
    private String passcount;
    private int passi;
    private Context context;
    private FragmentManager fm;
    private TextView count;
    private BottomNavigationView bnv;
    private SensorManager sm;

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
            //menuf =(MenuFragment) fm.getFragment(savedInstanceState, "MenuFragmente");
            passi = savedInstanceState.getInt("passi");
        }
        else {
            //menuf = new MenuFragment();
            //fm.beginTransaction().add(R.id.fragment_menu, menuf).commit();
            passi = (int)(Math.random() * 10);
        }
        passcount = getString(R.string.passi);

        sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        count = view.findViewById(R.id.passi);
        count.setText(String.format("%s%s", passcount, String.valueOf(passi)));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fm = getFragmentManager();
        this.context = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        Sensor sens = sm.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (sens != null){
            sm.registerListener(this, sens, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sm.unregisterListener(this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //fm.putFragment(outState, "MenuFragmente", menuf);
        outState.putInt("passi", passi);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //viene generato un evento per ogni passo
        passi -= 1;
        count.setText(String.format("%s%s", passcount, String.valueOf(passi)));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
