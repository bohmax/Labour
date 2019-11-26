package com.example.labour;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private String user_ID="pippotest";
    private SubscribeFragment sf;
    private MenuFragment menuf;
    private TextView nome, anni, sesso;
    private Bitmap bitmap;
    private MyDatabase db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);

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
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_menu, menuf).commit();
            /*if(!exist){
                sf.setCancelable(false);
                sf.show(getSupportFragmentManager(), "SubFG TAG");
            }*/
        }

        db = new MyDatabase(getApplicationContext());
        if (extra != null) {
            user_ID = extra.getString("ID");
        }
        nome = findViewById(R.id.nome);
        anni = findViewById(R.id.anni);
        sesso = findViewById(R.id.sesso);

        setView(db.searchById(user_ID));

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, "MenuFragmente", menuf);
        SubscribeFragment fragmentA =(SubscribeFragment) getSupportFragmentManager().findFragmentByTag("SubFG TAG");
        if (fragmentA != null)
            getSupportFragmentManager().putFragment(outState, "SubscribeFragment", sf);
    }

    private void setView(String[] str){
        if(str[0].length()!=0 || str[1].length()!=0)
            nome.setText(String.format("%s %s", str[0], str[1]));
        if(str[2].length()!=0)
            anni.setText(String.format("%s Anni", str[2]));
        if(str[3].length()<=4)//diverso da uomo
            anni.setText(str[3]);

    }
}
