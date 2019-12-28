package com.example.labour.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.labour.fragment.MenuFragment;
import com.example.labour.fragment.PackageFragment;
import com.example.labour.fragment.ProfileFragment;
import com.example.labour.R;
import com.example.labour.fragment.WorkFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private String user_ID="pippotest";
    private boolean exist;
    private MenuFragment menuf;
    private PackageFragment packf = new PackageFragment();
    private WorkFragment workf = new WorkFragment();
    private ProfileFragment proff = new ProfileFragment();
    private Fragment active = packf;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            exist = extra.getBoolean("Exist");
            user_ID = extra.getString("ID");
        }

        if (savedInstanceState != null){
            menuf = (MenuFragment) getSupportFragmentManager().getFragment(savedInstanceState, "MenuFragmente");
            packf = (PackageFragment) getSupportFragmentManager().getFragment(savedInstanceState, "Package");
            workf = (WorkFragment) getSupportFragmentManager().getFragment(savedInstanceState, "Passi");
            proff = (ProfileFragment) getSupportFragmentManager().getFragment(savedInstanceState, "Profilo");
            Log.i("Dolore", "msg " + savedInstanceState.getInt("Active"));
            switch (savedInstanceState.getInt("Active")) {
                case 1: {
                    active = packf;
                    break;
                }
                case 2: {
                    active = workf;
                    break;
                }
                case 3: {
                    active = proff;
                    break;
                }
            }
        }
        else {
            menuf = new MenuFragment();
            packf = new PackageFragment();
            workf = new WorkFragment();
            proff = new ProfileFragment();

            Bundle bund = new Bundle();
            bund.putString("ID", user_ID);
            bund.putString("Path_Photo", getApplicationInfo().dataDir+"/files/");
            bund.putBoolean("Exist", exist);
            packf.setArguments(bund);
            proff.setArguments(bund);

            getSupportFragmentManager().beginTransaction().add(R.id.fragment_menu, menuf).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_space, proff, "3").hide(proff).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_space, workf, "2").hide(workf).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_space, packf, "1").commit();
            active = packf;
        }

        BottomNavigationView navigation = findViewById(R.id.bottom);
        navigation.setOnNavigationItemSelectedListener(this);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "MenuFragmente", menuf);
        getSupportFragmentManager().putFragment(outState, "Package", packf);
        getSupportFragmentManager().putFragment(outState, "Passi", workf);
        getSupportFragmentManager().putFragment(outState, "Profilo", proff);
        if(active instanceof PackageFragment)
            outState.putInt("Active", 1);
        else if (active instanceof ProfileFragment)
            outState.putInt("Active", 3);
        else outState.putInt("Active", 2);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.seleziona: {
                getSupportFragmentManager().beginTransaction().hide(active).show(packf).commit();
                active = packf;
                return true;
            }
            case R.id.passi:{
                getSupportFragmentManager().beginTransaction().hide(active).show(workf).commit();
                active = workf;
                return true;
            }
            case R.id.profilo:{
                getSupportFragmentManager().beginTransaction().hide(active).show(proff).commit();
                active = proff;
                return true;
            }
        }
        return false;
    }

    public void showPopup(View v) { //viene invocato dal bottone, dichiarato nel xml
        if(active instanceof PackageFragment) //avrei anche potuto fare in modo che implementino un interfaccio, ma ho preferito questo approccio
            packf.showPopup(v);
        else if (active instanceof ProfileFragment)
            proff.showPopup(v);
    }

    public void onImageClick(View v) {
        if(active instanceof PackageFragment)
            packf.onImageClick(v);
        else if (active instanceof ProfileFragment)
            proff.onImageClick();
    }

    public void onEditClick(View v){
        proff.onEditClick();
    }

    public void Update_profile(){
        proff.Dismiss();
    }

}
