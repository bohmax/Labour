package com.example.labour;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;

public class ProfileFragment extends Fragment implements DialogInterface.OnDismissListener {

    private String user_ID="pippotest";
    private String[] userInfo;
    private String pathfile;
    private FragmentManager fm;
    private SubscribeFragment sf;
    private Context context;
    private MenuFragment menuf;
    private TextView nome, carratteristiche;
    private CircularImageView civ;
    private MyDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_layout, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extra = this.getArguments();
        if (extra != null) {
            user_ID = extra.getString("ID");
            pathfile = Environment.getDataDirectory()+"/files/profile_"+user_ID+".jpg";
        }

        if (savedInstanceState != null) {
            //menuf = (MenuFragment) fm.getFragment(savedInstanceState, "MenuFragmente");
            sf = (SubscribeFragment) fm.findFragmentByTag("SubFG TAG");
            if (sf != null)
                sf = (SubscribeFragment) fm.getFragment(savedInstanceState, "SubscribeFragment");
        } /*else {
            menuf = new MenuFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_menu, menuf).commit();
        }*/

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        db = new MyDatabase(getContext());
        nome = view.findViewById(R.id.nome);
        carratteristiche = view.findViewById(R.id.caratteristiche);
        civ = view.findViewById(R.id.pic);
        File pic = new File(pathfile);
        if(pic.exists())
            civ.setImageBitmap(File_utility.getBitMap(context, Uri.fromFile(pic), 120, 120));
        setView(db.searchById(user_ID));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fm = getFragmentManager();
        this.context = context;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        //fm.putFragment(outState, "MenuFragmente", menuf);
        SubscribeFragment fragmentA =(SubscribeFragment) fm.findFragmentByTag("SubFG TAG");
        if (fragmentA != null)
            fm.putFragment(outState, "SubscribeFragment", sf);
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
            civ.setImageBitmap(File_utility.getBitMap(context, Uri.fromFile(pic), 120, 120));
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
        sf.show(fm, "SubFG TAG");
    }

    public void showPopup(View v) { //viene invocato dal bottone, dichiarato nel xml
        sf.showPopup(v);
    }

    public void onImageClick(View v) {
        sf.onImageClick();
    }
}
