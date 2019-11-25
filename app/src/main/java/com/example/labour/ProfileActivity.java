package com.example.labour;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private String user_ID;
    private SubscribeFragment sf;
    private MenuFragment menuf;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        Bundle extra = getIntent().getExtras();
        if (savedInstanceState != null) {
            menuf = (MenuFragment) getSupportFragmentManager().getFragment(savedInstanceState, "MenuFragmente");
            SubscribeFragment fragmentA = (SubscribeFragment) getSupportFragmentManager().findFragmentByTag("SubFG TAG");
            //if (fragmentA != null)
            //    sf =(SubscribeFragment) getSupportFragmentManager().getFragment(savedInstanceState, "SubscribeFragment");
        } else {
            sf = new SubscribeFragment();
            menuf = new MenuFragment();
            Bundle bundle = new Bundle();
            bundle.putString("ID", user_ID);
            sf.setArguments(bundle);
            /*getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_menu, menuf).commit();
            if(!exist){
                sf.setCancelable(false);
                sf.show(getSupportFragmentManager(), "SubFG TAG");
            }*/
        }

        if (extra != null) {
            user_ID = extra.getString("ID");
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "MenuFragmente", menuf);
        SubscribeFragment fragmentA =(SubscribeFragment) getSupportFragmentManager().findFragmentByTag("SubFG TAG");
        if (fragmentA != null)
            getSupportFragmentManager().putFragment(outState, "SubscribeFragment", sf);
    }
}
