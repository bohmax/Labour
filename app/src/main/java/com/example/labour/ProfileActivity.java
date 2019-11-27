package com.example.labour;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;

public class ProfileActivity extends AppCompatActivity implements DialogInterface.OnDismissListener {

    private String user_ID="pippotest";
    private String[] userInfo;
    private String pathfile;
    private SubscribeFragment sf;
    private MenuFragment menuf;
    private TextView nome, carratteristiche;
    private CircularImageView civ;
    private MyDatabase db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);
        //set view
        db = new MyDatabase(getApplicationContext());
        nome = findViewById(R.id.nome);
        carratteristiche = findViewById(R.id.caratteristiche);
        civ = findViewById(R.id.pic);

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
            sf = (SubscribeFragment) getSupportFragmentManager().findFragmentByTag("SubFG TAG");
            if (sf != null)
                sf = (SubscribeFragment) getSupportFragmentManager().getFragment(savedInstanceState, "SubscribeFragment");
        } else {
            menuf = new MenuFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_menu, menuf).commit();
        }

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
        if (str[0].length()!=0 || str[1].length()!=0)
            nome.setText(String.format("%s %s", str[0], str[1]));
        carratteristiche.setText(String.format("%s Anni, %s", str[2], str[3]));
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        setView(db.searchById(user_ID));
        File pic = new File(pathfile);
        if(pic.exists())
            civ.setImageBitmap(File_utility.getBitMap(getApplicationContext(), Uri.fromFile(pic), 120, 120));
    }

    public void onEditClick(View v){
        if(sf==null)
            sf = new SubscribeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("ID", user_ID);
        bundle.putString("nome", userInfo[0]);
        bundle.putString("cognome", userInfo[1]);
        bundle.putString("anni", userInfo[2]);
        bundle.putString("sesso", userInfo[3]);
        sf.setArguments(bundle);
        sf.setCancelable(false);
        sf.show(getSupportFragmentManager(), "SubFG TAG");
    }

    public void showPopup(View v) { //viene invocato dal bottone, dichiarato nel xml
        sf.showPopup(v);
    }

    public void onImageClick(View v) {
        sf.onImageClick();
    }
}
