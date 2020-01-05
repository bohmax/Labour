package com.example.labour.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
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
import com.example.labour.interfacce.WorkListener;

import java.util.ArrayList;
import java.util.List;

public class PackageFragment extends Fragment implements WorkListener {

    private boolean exist = false;
    private ArrayList<Package_item> packs;
    private String user_ID = "pippotest";
    private String pathfile;
    private PackAdapter adapter;
    private SubscribeFragment sf;
    private RecyclerView rv;
    private Parcelable layout;
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
        rv = view.findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        //rv.setHasFixedSize(true);

        if (savedInstanceState != null) {
            layout = savedInstanceState.getParcelable("list_state");
            packs = savedInstanceState.getParcelableArrayList("list_data");
            if (packs != null){
                rv.setLayoutManager(llm);
                //llm.onRestoreInstanceState(layout);
                adapter = new PackAdapter(getActivity(), packs);
                rv.setAdapter(adapter);
            } else setRecyclerView(llm);

        }
        else setRecyclerView(llm);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (rv.getLayoutManager() != null && layout != null)
            rv.getLayoutManager().onRestoreInstanceState(layout);

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
        if (rv.getLayoutManager() != null) {
            outState.putParcelable("list_state", rv.getLayoutManager().onSaveInstanceState());
            outState.putParcelableArrayList("list_data", adapter.getPacks());
        }
    }

    private void initializeData(){
        packs = new ArrayList<>();
        //gli elementi devono essere sempre <= 10 ma
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

    public void removeSelectedItem(int pos){
        adapter.removeLastItem(pos);
    }

    private void setRecyclerView(RecyclerView.LayoutManager llm){
        rv.setLayoutManager(llm);
        initializeData();
        adapter = new PackAdapter(getActivity(), packs);
        rv.setAdapter(adapter);
    }

    @Override
    public void newWork(List<Package_item> list, int pos) {

    }

    @Override
    public void updateAfterStep(float coordinate) {
        adapter.updatePassi(coordinate);
    }

    @Override
    public void workCompleted(Package_item item) {

    }
}
