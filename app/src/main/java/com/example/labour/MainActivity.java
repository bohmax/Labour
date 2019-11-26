package com.example.labour;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private boolean exist = false;
    private String user_ID = "pippotest";
    private SubscribeFragment sf;
    Button profilo;
    private MenuFragment menuf;
    private TextView tw;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extra = getIntent().getExtras();
        if(extra != null) {
            exist = extra.getBoolean("Exist");
            user_ID = extra.getString("ID");

        }

        if(savedInstanceState!=null){
            menuf =(MenuFragment) getSupportFragmentManager().getFragment(savedInstanceState, "MenuFragmente");
            SubscribeFragment fragmentA =(SubscribeFragment) getSupportFragmentManager().findFragmentByTag("SubFG TAG");
            if (fragmentA != null)
                sf =(SubscribeFragment) getSupportFragmentManager().getFragment(savedInstanceState, "SubscribeFragment");
        }
        else {
            sf = new SubscribeFragment();
            menuf = new MenuFragment();
            Bundle bundle = new Bundle();
            bundle.putString("ID", user_ID);
            sf.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_menu, menuf).commit();
            if(!exist){
                sf.setCancelable(false);
                sf.show(getSupportFragmentManager(), "SubFG TAG");
            }
        }

        tw = findViewById(R.id.text);
        profilo = findViewById(R.id.profile);
        profilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), ProfileActivity.class);
                i.putExtra("ID", user_ID);
                startActivity(i);
            }
        });
        String prova = "era presente nel db? " + exist + " value " + user_ID;
        tw.setText(prova);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "MenuFragmente", menuf);
        SubscribeFragment fragmentA =(SubscribeFragment) getSupportFragmentManager().findFragmentByTag("SubFG TAG");
        if (fragmentA != null)
            getSupportFragmentManager().putFragment(outState, "SubscribeFragment", sf);
    }

    public void showPopup(View v) { //viene invocato dal bottone, dichiarato nel xml
        sf.showPopup(v);
    }

    public void onImageClick(View v) {
        sf.onImageClick();
    }

}
