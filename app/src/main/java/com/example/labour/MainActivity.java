package com.example.labour;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static androidx.fragment.app.DialogFragment.STYLE_NORMAL;

public class MainActivity extends AppCompatActivity {

    private boolean exist = false;
    private String user_ID;
    private SubscribeFragment sf;
    TextView tw;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extra = getIntent().getExtras();
        if(extra != null) {
            Log.i("Fuc","quando");
            exist = extra.getBoolean("Exist");
            user_ID = extra.getString("ID");
        }
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_menu, new MenuFragment()).commit();

        tw = findViewById(R.id.text);

        if(!exist){
            sf = new SubscribeFragment();
            sf.setCancelable(false);
            Bundle bundle = new Bundle();
            bundle.putString("ID", user_ID);
            sf.setArguments(bundle);
            sf.show(getSupportFragmentManager(), "SubFG TAG");
        }
        String prova = "era presente nel db? " + exist + " value " + user_ID;
        tw.setText(prova);
    }

    public void showPopup(View v) { //viene invocato dal bottone, dichiarato nel xml
        sf.showPopup(v);
    }

    public void onImageClick(View v) {
        sf.onImageClick(v);
    }

}
