package com.example.labour.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labour.PackAdapter;
import com.example.labour.Package_item;
import com.example.labour.R;

import java.util.ArrayList;
import java.util.List;

public class PackageFragment extends Fragment {

    private boolean exist = false;
    private List<Package_item> packs;
    private String user_ID = "pippotest";
    private String pathfile;
    private SubscribeFragment sf;
    private FragmentManager fm;

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
        RecyclerView rv = view.findViewById(R.id.rv);
        rv.setHasFixedSize(true);

        initializeData();

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

        PackAdapter adapter = new PackAdapter(getActivity(), packs);
        rv.setAdapter(adapter);
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

    private void initializeData(){
        packs = new ArrayList<>();
        packs.add(new Package_item("Iphone 6", "Lezzo", R.drawable.package_default));
        packs.add(new Package_item("Galaxy", "Puzza", R.drawable.package_default));
        packs.add(new Package_item("Cristina", "Cabras", R.drawable.package_default));
        packs.add(new Package_item("Swag", "Prova", R.drawable.package_default));
        packs.add(new Package_item("Iphone 6", "Lezzo", R.drawable.package_default));
        packs.add(new Package_item("Galaxy", "Puzza", R.drawable.package_default));
        packs.add(new Package_item("Cristina", "Cabras", R.drawable.package_default));
        packs.add(new Package_item("Swag", "Prova", R.drawable.package_default));
        packs.add(new Package_item("Iphone 6", "Lezzo", R.drawable.package_default));
        packs.add(new Package_item("Galaxy", "Puzza", R.drawable.package_default));
        packs.add(new Package_item("Cristina", "Cabras", R.drawable.package_default));
        packs.add(new Package_item("Swag", "Prova", R.drawable.package_default));
        packs.add(new Package_item("Iphone 6", "Lezzo", R.drawable.package_default));
        packs.add(new Package_item("Galaxy", "Puzza", R.drawable.package_default));
        packs.add(new Package_item("Cristina", "Cabras", R.drawable.package_default));
        packs.add(new Package_item("Mente", "MUHAHAHHA", R.drawable.package_default));
    }

    public void showPopup(View v) { //viene invocato dal bottone, dichiarato nel xml
        sf.showPopup(v);
    }

    public void onImageClick() {
        sf.onImageClick();
    }

    public List<Package_item> getPacks() {
        return packs;
    }
}
