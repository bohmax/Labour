package com.example.labour.fragment;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.labour.interfacce.BitmapReadyListener;
import com.example.labour.interfacce.FileInterfaceListener;
import com.example.labour.MyDatabase;
import com.example.labour.R;
import com.example.labour.interfacce.WorkListener;
import com.example.labour.utility.Permission_utility;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.DOWNLOAD_SERVICE;

public class ProfileFragment extends Fragment implements FileInterfaceListener, WorkListener, BitmapReadyListener {

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
    private DownloadReceiver dr = new DownloadReceiver();

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
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());

        if (savedInstanceState != null){
            layout = savedInstanceState.getParcelable("list_state");
            ArrayList<Package_item> list_data = savedInstanceState.getParcelableArrayList("list_data");
            if (list_data != null){
                rv.setLayoutManager(llm);

                adapter = new PackAdapter(null, list_data);
                adapter.setImageDownload(this);
                rv.setAdapter(adapter);
            }
            else setRecyclerView(llm);
        }
        else {
            setRecyclerView(llm);
        }
        //-------
        new Thread(new updateUser()).start();
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
        if (rv.getLayoutManager() != null && layout != null)
            rv.getLayoutManager().onRestoreInstanceState(layout);
        context.registerReceiver(dr, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)); //voglio essere notificato quando il download finisce
    }

    @Override
    public void onPause() {
        super.onPause();
        context.unregisterReceiver(dr);
    }

    private void setView(String[] str){
        userInfo = str;
        if (str[0].length()!=0 || str[1].length()!=0)
            nome.setText(String.format("%s %s", str[0], str[1]));
        carratteristiche.setText(String.format("%s Anni, %s", str[2], str[3]));
        File pic = new File(pathpic);
        if(pic.exists()) {
            progress.setVisibility(View.VISIBLE);
            new PhotoLoader(context ,this, null, 120, 120).execute(Uri.fromFile(pic));
        }
    }

    //se il subscribe fragment viene dismesso devo aggiornare la foto, se questa è stata aggiornata
    public void Dismiss() {
        new Thread(new updateUser()).start();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Permission_utility.EXTERNAL_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                //fai partire il download
                ArrayList<Package_item> pi = adapter.getPacks();
                downloadImage(pi.get(pi.size()-1), pi.size()-1);
                adapter.setImageDownload(this);
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(context, "Permessi necessari per scaricare l'immagine del pacco", Toast.LENGTH_LONG).show();
                adapter.disableImageDownload();
                adapter.notifyDataSetChanged();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void showPopup(View v) { //viene invocato dal bottone, dichiarato nel xml
        sf.showPopup(v);
    }

    public void onImageClick() {
        sf.onImageClick();
    }

    @Override
    public void getTempPath(File file) { }

    @Override
    public void saveResult(Bitmap bitmpa, Boolean result){
        progress.setVisibility(View.GONE);
        if (result)
            civ.setImageBitmap(bitmpa);
    }

    @Override
    public void newWork(List<Package_item> list, int pos) { }

    @Override
    public void updateAfterStep(float coordinata) { }

    @Override
    public void workCompleted(Package_item item) {
        adapter.addElement(item);
        startImageRequest(item, adapter.getPacks().size()-1);
    }

    private void setRecyclerView(RecyclerView.LayoutManager llm){
        rv.setLayoutManager(llm);
        ArrayList<Package_item> list = new ArrayList<>();
        adapter = new PackAdapter(null, list);
        adapter.setImageDownload(this);
        new RequestCompletedPack((MainActivity) context, this).execute(user_ID);
    }

    //index è l'indice dell'elemento da scaricare dell'adapter
    private void downloadImage(Package_item item, int index){
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(context.getExternalFilesDir(null), user_ID + "_" + index);
            if (!file.exists()) {
                if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("Download", true)) {
                    try {
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(item.getUrl()))
                                .setTitle("Download " + item.getTitle())
                                .setDescription("Downloading")
                                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                                .setDestinationUri(Uri.fromFile(file))
                                .setAllowedOverMetered(true)
                                .setAllowedOverRoaming(true);
                        DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
                        downloadManager.enqueue(request);
                    } catch (IllegalArgumentException ignored) { }
                }
            }
            else {
                new PhotoLoader(context,this, index, 75, 75).execute(Uri.fromFile(file));
            }
        }
    }

    @Override
    public void startImageRequest(Package_item item, int index){
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("Download", true)) { // non scaricare se l'utente non lo richiede
            if (Permission_utility.requestPermission(this, getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Permission_utility.EXTERNAL_PERMISSION, "Concedi questi permessi per modificare la foto profilo")) {
                new Thread(new download(item, index)).start();
            }
            else {
                adapter.disableImageDownload();
            }
        }
    }

    @Override
    public void loadedBitmap(Bitmap bitmap, int index) {
        Package_item it= adapter.getPacks().get(index);
        if (it != null) {
            it.setPhoto(bitmap);
            adapter.notifyItemChanged(index);
        }
    }

    private class DownloadReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            String act = intent.getAction();

            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(act)) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,0);
                DownloadManager dm =(DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
                Uri u =  dm.getUriForDownloadedFile(id);
                String path = getNamefromUri(u, dm, id);
                if (path != null) {
                    int index = Integer.valueOf(path.split("_")[1]);
                    new PhotoLoader(context, ProfileFragment.this, index, 75, 75).execute(u);
                }
            }
        }
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
            while ((list = db.searchByIdPacchi(id, range, offset)) != null && range <= 50) {
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

    private class updateUser implements Runnable {
        @Override
        public void run() {
            MyDatabase db = new MyDatabase(getContext());
            String[] ris = db.searchByIdOperai(user_ID);
            ((MainActivity)context).runOnUiThread(() -> setView(ris));
        }
    }

    private class download implements Runnable {

        Package_item item;
        int index; //index for the start of the request

        download(Package_item items,int index) throws NullPointerException {
            if (items == null) throw new NullPointerException();
            this.item = items;
            this.index = index;
        }

        @Override
        public void run() {
            //Controlla se la memoria esterna è scrivibile & Controlla che il file esista
            downloadImage(item, index++);
        }
    }

    private String getNamefromUri(Uri uri, DownloadManager dm, long downloadID){
        String result = null;
        if (uri == null || uri.getScheme() == null) return null;
        if (uri.getScheme().equals("content")) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadID);
            try (Cursor cursor = dm.query(query)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                }
            }
        }
        if (result == null)
            result = uri.getPath();
        if (result == null) return null;
        int cut = result.lastIndexOf('/');
        if (cut != -1) {
            result = result.substring(cut + 1);
        }
        return result;
    }
}
