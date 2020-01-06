package com.example.labour.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.labour.MyDatabase;
import com.example.labour.Package_item;
import com.example.labour.fragment.MenuFragment;
import com.example.labour.fragment.PackageFragment;
import com.example.labour.fragment.ProfileFragment;
import com.example.labour.R;
import com.example.labour.fragment.WorkFragment;
import com.example.labour.interfacce.CardViewClickListener;
import com.example.labour.interfacce.WorkListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.lang.ref.WeakReference;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, CardViewClickListener, WorkListener {

    private String user_ID = "pippotest";
    private boolean new_user = true; //togli l'assegnazione
    private MenuFragment menuf;
    private PackageFragment packf = new PackageFragment();
    private WorkFragment workf = new WorkFragment();
    private ProfileFragment proff = new ProfileFragment();
    private Fragment active = packf;

    private int pos_lastSelectedItem; //l'ultima cardview selezionata su package fragment

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            new_user = extra.getBoolean("Exist");
            user_ID = extra.getString("ID");
        }

        if (savedInstanceState != null) {
            menuf = (MenuFragment) getSupportFragmentManager().getFragment(savedInstanceState, "MenuFragmente");
            packf = (PackageFragment) getSupportFragmentManager().getFragment(savedInstanceState, "Package");
            workf = (WorkFragment) getSupportFragmentManager().getFragment(savedInstanceState, "Passi");
            proff = (ProfileFragment) getSupportFragmentManager().getFragment(savedInstanceState, "Profilo");
            pos_lastSelectedItem = savedInstanceState.getInt("last_insert");
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
        } else {
            menuf = new MenuFragment();
            packf = new PackageFragment();
            workf = new WorkFragment();
            proff = new ProfileFragment();

            Bundle bund = new Bundle();
            bund.putString("ID", user_ID);
            bund.putString("Path_Photo", getApplicationInfo().dataDir + "/files/");
            bund.putBoolean("Exist", new_user);
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
        workf.setonCompledlistener(this);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "MenuFragmente", menuf);
        getSupportFragmentManager().putFragment(outState, "Package", packf);
        getSupportFragmentManager().putFragment(outState, "Passi", workf);
        getSupportFragmentManager().putFragment(outState, "Profilo", proff);
        if (active instanceof PackageFragment)
            outState.putInt("Active", 1);
        else if (active instanceof ProfileFragment)
            outState.putInt("Active", 3);
        else outState.putInt("Active", 2);
        outState.putInt("last_insert", pos_lastSelectedItem);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.seleziona: {
                getSupportFragmentManager().beginTransaction().hide(active).show(packf).commit();
                active = packf;
                return true;
            }
            case R.id.passi: {
                getSupportFragmentManager().beginTransaction().hide(active).show(workf).commit();
                active = workf;
                return true;
            }
            case R.id.profilo: {
                getSupportFragmentManager().beginTransaction().hide(active).show(proff).commit();
                active = proff;
                return true;
            }
        }
        return false;
    }

    public void showPopup(View v) { //viene invocato dal bottone, dichiarato nel xml
        if (active instanceof PackageFragment) //avrei anche potuto fare in modo che implementino un interfaccio, ma ho preferito questo approccio
            packf.showPopup(v);
        else if (active instanceof ProfileFragment)
            proff.showPopup(v);
    }

    public void onImageClick(View v) {
        if (active instanceof PackageFragment)
            packf.onImageClick();
        else if (active instanceof ProfileFragment)
            proff.onImageClick();
    }

    public void onEditClick(View v) {
        proff.onEditClick();
    }

    public void Update_profile() {
        proff.Dismiss();
    }

    @Override
    public void onCardViewClick(int pos) {
        pos_lastSelectedItem = pos;
        workf.newWork(packf.getPacks(), pos);
    }

    @Override
    public void newWork(List<Package_item> list, int pos) {

    }

    @Override
    public void updateAfterStep(float coordinata) {
        packf.updateAfterStep(coordinata);
    }

    @Override
    public void workCompleted(Package_item item) {
        new SaveWork(this).execute(item);
    }

    private static class SaveWork extends AsyncTask<Package_item, Void, Long> {

        private WeakReference<MainActivity> activityReference;
        private Package_item item;

        // only retain a weak reference to the activity
        SaveWork(MainActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected Long doInBackground(Package_item... items) {
            item = items[0];
            MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return null;

            MyDatabase db = new MyDatabase(activity);
            return db.createRecordsPacchi(item.getTitle(), item.getDescription(), activity.user_ID);
        }

        @Override
        protected void onPostExecute(Long result) {

            MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            if (result != -1) {
                Snackbar sb = Snackbar.make(activity.findViewById(R.id.frame), "Lavoro completato e salvato", Snackbar.LENGTH_LONG);
                sb.setAction("Mostra Profilo", v -> {
                    activity.getSupportFragmentManager().beginTransaction().hide(activity.active).show(activity.proff).commit();
                    activity.active = activity.proff;
                }).show();

                activity.proff.workCompleted(item);
                activity.packf.removeSelectedItem(activity.pos_lastSelectedItem);
                activity.pos_lastSelectedItem = -1;
            } else {
                Snackbar sb = Snackbar.make(activity.findViewById(R.id.frame), "Errore inaspettato, prova a selezionare nuovamente il pacco", Snackbar.LENGTH_LONG);
                sb.setAction("Nascondi", v -> sb.dismiss()).show();
            }
        }
    }
}
