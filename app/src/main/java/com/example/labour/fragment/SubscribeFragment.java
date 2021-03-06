package com.example.labour.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import com.example.labour.activity.MainActivity;
import com.example.labour.async.PhotoLoader;
import com.example.labour.async.RenameFile;
import com.example.labour.async.SettingFotoIntent;
import com.example.labour.async.DeleteFile;
import com.example.labour.interfacce.FileInterfaceListener;
import com.example.labour.MyDatabase;
import com.example.labour.R;
import com.example.labour.utility.Permission_utility;
import com.google.android.material.textfield.TextInputEditText;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class SubscribeFragment extends DialogFragment implements PopupMenu.OnMenuItemClickListener, View.OnClickListener, FileInterfaceListener {

    private static final int FOTO_REQUEST = 0;
    private Context mContext;
    private String ID;
    private Button button, accept, disable;
    private TextInputEditText nome,cognome, age;
    private CircularImageView civ;
    private ProgressBar progress;
    private Intent intent;
    private String mCurrentPhotoPath, mnewTempPath;//rispettivamente, il path della foto che dovrà essere salvata e il path del file candidato per essere il futuro prossimo mCurrentpath
    private String picpath; //immagine attuale di profilo
    private String picfolder; //cartella in cui sono presenti le foto
    private boolean button_pressed; //true se l'utente preme imposta, falsa altrimenti

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getElement();
        File pic = null;
        if(getArguments()!=null){
            ID = getArguments().getString("ID");
            picfolder = getArguments().getString("Path_Folder");
            picpath = picfolder + "profile_"+ ID +".jpg";
            pic = new File(picpath);
            String nomestr = getArguments().getString("nome");
            if (nomestr != null){ //se nomestr è diverso da null anche gli altri elementi sono stati settati
                nome.setText(nomestr);
                cognome.setText(getArguments().getString("cognome"));
                age.setText(getArguments().getString("anni"));
                button.setText(getArguments().getString("sesso"));
            }
        }
        if (savedInstanceState!=null) {
            mCurrentPhotoPath = savedInstanceState.getString("path");
            mnewTempPath = savedInstanceState.getString("new");
        }
        if (mCurrentPhotoPath != null) {
            progress.setVisibility(View.VISIBLE);
            new PhotoLoader(mContext, this, null, 150, 150).execute(Uri.fromFile(new File(mCurrentPhotoPath)));
        }
        else if (pic != null && pic.exists()){
            progress.setVisibility(View.VISIBLE);
            new PhotoLoader(mContext, this, null, 150, 150).execute(Uri.fromFile(pic));
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.set_data);
        builder.setIcon(R.drawable.ic_account_circle_black_24dp);
        builder.setMessage("Altrimenti si può continuare a lavorare in maniera quasi anonima");
        builder.setView(v);

        Dialog dial = builder.create();

        Objects.requireNonNull(dial.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return dial;
    }

    @Override
    public void onClick(View v) {
        if (v == accept) {
            String sesso,eta="0";
            if(button.getText().toString().equals("Sesso"))
                sesso = "Uomo";
            else sesso = button.getText().toString();
            if(Objects.requireNonNull(age.getText()).toString().length()!=0)
                eta =age.getText().toString();
            new UpdateUser((MainActivity) mContext).execute(ID, Objects.requireNonNull(nome.getText()).toString(), Objects.requireNonNull(cognome.getText()).toString(),sesso, eta);

            button_pressed = true;

            //imposta la foto come predefinita se è stata cambiata
            if (mCurrentPhotoPath != null) {
                progress.setVisibility(View.VISIBLE);
                new RenameFile(this).execute(picpath, mCurrentPhotoPath);
            }
            else dismiss();
        }
        else if (v == disable){
            if (mCurrentPhotoPath != null)
                new DeleteFile(this).execute(mCurrentPhotoPath);
            else dismiss();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow()!=null) {
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                int width = ViewGroup.LayoutParams.MATCH_PARENT;
                int height = ViewGroup.LayoutParams.MATCH_PARENT;
                dialog.getWindow().setLayout(width, height);
            }
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        mCurrentPhotoPath = null;
        mnewTempPath = null;
        progress.setVisibility(View.GONE);
        final Activity activity = getActivity();
        if (activity instanceof MainActivity && button_pressed) {
            MainActivity ma = (MainActivity) activity;
            ma.Update_profile();// notifico all'activity che l'utente ha fatto modifiche
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("path", mCurrentPhotoPath);
        outState.putString("new", mnewTempPath);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Permission_utility.FOTO_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //fai partire l'intent
                if (intent != null)
                    startActivityForResult(intent, FOTO_REQUEST);
                intent = null;
            } else {
                Toast.makeText(mContext, "Permessi necessari per cambiare foto profilo", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("richiesta", String.valueOf(requestCode));
        Log.i("result", String.valueOf(resultCode));
        if (requestCode == FOTO_REQUEST) {
            if (resultCode == RESULT_OK) {
                progress.setVisibility(View.VISIBLE);
                if(data==null || data.getData()==null){// l'utente ha selezionato la camera
                    Log.i("sucess", "well");
                    Uri newfile = Uri.fromFile(new File(mnewTempPath));
                    new PhotoLoader(mContext, this, null, 150, 150).
                            execute(newfile, mCurrentPhotoPath == null? null: Uri.fromFile(new File(mCurrentPhotoPath)));
                }
                else
                    //l'utente ha selezionato una foto dalla galleria
                    new PhotoLoader(mContext, this, mnewTempPath, 150, 150).
                            execute(data.getData(), mCurrentPhotoPath == null? null: Uri.fromFile(new File(mCurrentPhotoPath)));
            }
            else {
                new DeleteFile().execute(mnewTempPath);
                mCurrentPhotoPath = null;
            }

        }
    }

    private View getElement(){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.subscribe_fragment, null);
        button = view.findViewById(R.id.buttonsex);
        accept = view.findViewById(R.id.imposta);
        disable = view.findViewById(R.id.annulla);
        age = view.findViewById(R.id.TextEta);
        nome = view.findViewById(R.id.TextNome);
        cognome = view.findViewById(R.id.TextCognome);
        civ = view.findViewById(R.id.image);
        civ.setColorFilter(ContextCompat.getColor(mContext, R.color.grey));
        progress = view.findViewById(R.id.scroll1);
        accept.setOnClickListener(this);
        disable.setOnClickListener(this);
        return view;
    }

    //--------------- popup & image click -----------------
    void showPopup(View v) { //viene invocato dal bottone, dichiarato nel xml
        final PopupMenu popup = new PopupMenu(getContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.sesso, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    //button popup
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        button.setText(item.getTitle());
        return true;
    }

    void onImageClick() {
        progress.setVisibility(View.VISIBLE);
        new SettingFotoIntent(this).execute(picfolder, mCurrentPhotoPath);
    }

    //--------------------------------------------

    @Override
    public void getTempPath(File file) {
        if (file != null){

            mnewTempPath = file.getAbsolutePath();

            Intent chooserIntent;
            List<Intent> intentList = new ArrayList<>();

            Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri photoURI = FileProvider.getUriForFile(mContext,
                    mContext.getPackageName() + ".provider", file);
            camera.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoURI);
            intentList.add(camera);

            intentList.add(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI));

            chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1),
                    "Scatta una foto o prendila da quelle già salvate");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));


            if (Permission_utility.requestPermission(this, getActivity(), new String[]{Manifest.permission.CAMERA},
                    Permission_utility.FOTO_PERMISSION, "Concedi questi permessi per modificare la foto profilo"))
                startActivityForResult(chooserIntent,FOTO_REQUEST);
            else intent = chooserIntent;

        } else Toast.makeText(mContext, "Impossibile preparare l'immagine, controlla i permessi! O riavvia!", Toast.LENGTH_SHORT).show();
        progress.setVisibility(View.GONE);
    }

    @Override
    public void saveResult(Bitmap bitmap, Boolean result) {
        progress.setVisibility(View.GONE);
        if (result) //se android restarta l'app newtemp è uguale a null
            civ.setImageBitmap(bitmap);
            mCurrentPhotoPath = mnewTempPath;
        mnewTempPath = null;
    }

    private static class UpdateUser extends AsyncTask<String, Void, Void> {

        private WeakReference<MainActivity> activityReference;

        UpdateUser(MainActivity act) {
            activityReference = new WeakReference<>(act);
        }

        @Override
        protected Void doInBackground(String... elements) {
            MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return null;

            MyDatabase db = new MyDatabase(activity);
            db.updateRecordsOperai(elements[0], elements[1], elements[2], elements[3], Integer.parseInt(elements[4]));
            return null;
        }
    }
}
