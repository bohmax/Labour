package com.example.labour;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.Objects;

public class PackageFragment extends Fragment {

    private boolean exist = false;
    private String user_ID = "pippotest";
    private String pathfile;
    private SubscribeFragment sf;
    private FragmentManager fm;
    private TextView tw;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.package_layout, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extra = this.getArguments();
        if (extra != null) {
            exist = extra.getBoolean("Exist");
            user_ID = extra.getString("ID");
            pathfile = extra.getString("Path_Photo");
        }


        if (savedInstanceState != null) {
            SubscribeFragment fragmentA = (SubscribeFragment) fm.findFragmentByTag("SubFG TAG1");
            if (fragmentA != null)
                sf = (SubscribeFragment) fm.getFragment(savedInstanceState, "SubscribeFragment");
        } else {
            sf = new SubscribeFragment();
            Bundle bundle = new Bundle();
            bundle.putString("ID", user_ID);
            bundle.putString("Path_Folder", pathfile);
            sf.setArguments(bundle);
            if (!exist) {
                sf.setCancelable(false);
                sf.show(fm, "SubFG TAG1");
            }
        }
    }

    //chiamata dopo la on create
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tw = Objects.requireNonNull(getView()).findViewById(R.id.text);
        String prova = "era presente nel db? " + exist + " value " + user_ID;
        tw.setText(prova);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fm = getFragmentManager();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (fm.findFragmentByTag("SubFG TAG1") != null)
            fm.putFragment(outState, "SubscribeFragment", sf);
    }

    void showPopup(View v) { //viene invocato dal bottone, dichiarato nel xml
        sf.showPopup(v);
    }

    void onImageClick(View v) {
        sf.onImageClick();
    }

}
