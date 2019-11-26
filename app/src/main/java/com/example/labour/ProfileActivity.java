package com.example.labour;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;

public class ProfileActivity extends AppCompatActivity {

    private String user_ID="pippotest";
    private String[] userInfo;
    private String pathfile;
    private SubscribeFragment sf;
    private MenuFragment menuf;
    private TextView nome, anni, sesso;
    private CircularImageView civ;
    private MyDatabase db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            user_ID = extra.getString("ID");
            pathfile = getApplicationInfo().dataDir+"/files/profile_"+user_ID+".jpg";
            File pic = new File(pathfile);
            if(pic.exists())
                civ.setImageBitmap(File_utility.getBitMap(getApplicationContext(), Uri.fromFile(pic), 120, 120));
        }

        if (savedInstanceState != null) {
            menuf = (MenuFragment) getSupportFragmentManager().getFragment(savedInstanceState, "MenuFragmente");
            SubscribeFragment fragmentA = (SubscribeFragment) getSupportFragmentManager().findFragmentByTag("SubFG TAG");
            if (fragmentA != null)
                sf =(SubscribeFragment) getSupportFragmentManager().getFragment(savedInstanceState, "SubscribeFragment");
        } else
            menuf = new MenuFragment();


        db = new MyDatabase(getApplicationContext());
        nome = findViewById(R.id.nome);
        anni = findViewById(R.id.anni);
        sesso = findViewById(R.id.sesso);
        civ = findViewById(R.id.pic);

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
        userInfo = str;
        if(str[0].length()!=0 || str[1].length()!=0)
            nome.setText(String.format("%s %s", str[0], str[1]));
        if(str[2].length()!=0)
            anni.setText(String.format("%s Anni", str[2]));
        if(str[3].length()<=4)//diverso da uomo
            sesso.setText(str[3]);

    }

    public void onEditClick(View v){
        Bundle bundle = new Bundle();
        bundle.putString("ID", user_ID);
        bundle.putString("nome", userInfo[0]);
        bundle.putString("cognome", userInfo[1]);
        bundle.putString("anni", userInfo[2]);
        bundle.putString("sesso", userInfo[3]);
        sf.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_menu, menuf).commit();
        sf = new SubscribeFragment();
        sf.setCancelable(false);
        sf.show(getSupportFragmentManager(), "SubFG TAG");
    }
}
