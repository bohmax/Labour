package com.example.labour.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.labour.PackAdapter;
import com.example.labour.Package_item;
import com.example.labour.activity.MainActivity;
import com.example.labour.async.PhotoLoader;
import com.example.labour.interfacce.FileInterfaceListener;
import com.example.labour.MyDatabase;
import com.example.labour.R;
import com.example.labour.interfacce.WorkListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment implements FileInterfaceListener, WorkListener {

    private String user_ID="pippotest";
    private String[] userInfo;
    private String pathfolder;
    private String pathpic;
    private FragmentManager fm;
    private SubscribeFragment sf;
    private Context context;
    private TextView nome, carratteristiche;
    private CircularImageView civ;
    private ProgressBar progress;
    private PackAdapter adapter;
    private RecyclerView rv;
    private Parcelable layout;

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
            pathfolder = extra.getString("Path_Photo");
            pathpic = pathfolder + "profile_"+user_ID+".jpg";
        }

        if (savedInstanceState != null) {
            sf = (SubscribeFragment) fm.findFragmentByTag("SubFG TAG");
            if (sf != null)
                sf = (SubscribeFragment) fm.getFragment(savedInstanceState, "SubscribeFragment");
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //parte profilo
        nome = view.findViewById(R.id.nome);
        carratteristiche = view.findViewById(R.id.caratteristiche);
        civ = view.findViewById(R.id.pic);
        progress = view.findViewById(R.id.scroll);
        progress.setVisibility(View.GONE);
        //-------
        //parte recycleview
        rv = view.findViewById(R.id.rv);
        rv.setNestedScrollingEnabled(false);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());

        if (savedInstanceState != null){
            layout = savedInstanceState.getParcelable("list_state");
            ArrayList<Package_item> list_data = savedInstanceState.getParcelableArrayList("list_data");
            if (list_data != null){
                rv.setLayoutManager(llm);
                //llm.onRestoreInstanceState(layout);
                adapter = new PackAdapter(null, list_data);
                rv.setAdapter(adapter);
            }
            else setRecyclerView(llm);
        }
        else {
            setRecyclerView(llm);
        }
        //-------
        new updateUser((MainActivity) context, this).execute(user_ID);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fm = getFragmentManager();
        this.context = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        //if (rv.getLayoutManager() != null && layout != null)
        //    rv.getLayoutManager().onRestoreInstanceState(layout);

    }

    private void setView(String[] str){
        userInfo = str;
        if (str[0].length()!=0 || str[1].length()!=0)
            nome.setText(String.format("%s %s", str[0], str[1]));
        carratteristiche.setText(String.format("%s Anni, %s", str[2], str[3]));
        File pic = new File(pathpic);
        if(pic.exists()) {
            progress.setVisibility(View.VISIBLE);
            new PhotoLoader(this ,new WeakReference<>(civ), 120, 120).execute(Uri.fromFile(pic));
        }
    }

    //se il subscribe fragment viene dismesso devo aggiornare la foto, se questa è stata aggiornata
    public void Dismiss() {
        new updateUser((MainActivity) context, this).execute(user_ID);
    }

    public void onEditClick(){
        if(sf==null)
            sf = new SubscribeFragment();
        if (userInfo != null) {
            Bundle bundle = new Bundle();
            bundle.putString("ID", user_ID);
            bundle.putString("nome", userInfo[0]);
            bundle.putString("cognome", userInfo[1]);
            bundle.putString("anni", userInfo[2]);
            bundle.putString("sesso", userInfo[3]);
            bundle.putString("Path_Folder", pathfolder);
            sf.setArguments(bundle);
            sf.setCancelable(false);
            sf.show(fm, "SubFG TAG");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (fm.findFragmentByTag("SubFG TAG") != null)
            fm.putFragment(outState, "SubscribeFragment", sf);
        if (rv.getLayoutManager() != null && adapter != null) {
            outState.putParcelable("list_state", rv.getLayoutManager().onSaveInstanceState());
            outState.putParcelableArrayList("list_data", adapter.getPacks());
        }

    }

    public void showPopup(View v) { //viene invocato dal bottone, dichiarato nel xml
        sf.showPopup(v);
    }

    public void onImageClick() {
        sf.onImageClick();
    }

    @Override
    public void getTempPath(File file) {

    }

    @Override
    public void saveResult(Boolean result){
        progress.setVisibility(View.GONE);
    }

    @Override
    public void newWork(List<Package_item> list, int pos) {

    }

    @Override
    public void updateAfterStep(float coordinata) {

    }

    @Override
    public void workCompleted(Package_item item) {
        adapter.addElement(item);
    }

    private void setRecyclerView(RecyclerView.LayoutManager llm){
        rv.setLayoutManager(llm);
        ArrayList<Package_item> list = new ArrayList<>();
        adapter = new PackAdapter(null, list);
        new RequestCompletedPack((MainActivity) context, this).execute(user_ID);
    }

    private static class RequestCompletedPack extends AsyncTask<String, ArrayList<Package_item>, Void> {

        private WeakReference<MainActivity> activityReference;
        private WeakReference<ProfileFragment> profReference;

        RequestCompletedPack(MainActivity act, ProfileFragment prof) {
            activityReference = new WeakReference<>(act);
            profReference = new WeakReference<>(prof);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected Void doInBackground(String... ids) {
            String id = ids[0];
            int range = 5;
            int offset = 0;
            ArrayList<Package_item> list;
            MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return null;

            MyDatabase db = new MyDatabase(activity);
            while ((list = db.searchByIdPacchi(id, range, offset)) != null && range <= 100) {
                offset = range;
                range += offset;
                publishProgress(list);
            }
            return null;
        }

        @SafeVarargs
        @Override
        protected final void onProgressUpdate(ArrayList<Package_item>... values) {
            super.onProgressUpdate(values);
            ProfileFragment prof = profReference.get();
            if (prof == null || prof.isDetached()) return;
            for (Package_item item: values[0]) {
                prof.adapter.addElement(item);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ProfileFragment prof = profReference.get();
            if (prof == null || prof.isDetached()) return;
            prof.rv.setAdapter(prof.adapter);
        }
    }

    private static class updateUser extends AsyncTask<String, Void, String[]> {

        private WeakReference<MainActivity> activityReference;
        private WeakReference<ProfileFragment> profReference;

        updateUser(MainActivity act, ProfileFragment prof) {
            activityReference = new WeakReference<>(act);
            profReference = new WeakReference<>(prof);
        }

        @Override
        protected String[] doInBackground(String... ids) {
            String id = ids[0];
            MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return null;
            ProfileFragment prof = profReference.get();
            if (prof == null || prof.isDetached()) return null;

            MyDatabase db = new MyDatabase(activity);
            return db.searchByIdOperai(id);
        }

        @Override
        protected void onPostExecute(String[] result) {
            ProfileFragment prof = profReference.get();
            if (prof == null || prof.isDetached()) return;
            prof.setView(result);
        }
    }
}
