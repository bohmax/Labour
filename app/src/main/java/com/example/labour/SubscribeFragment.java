package com.example.labour;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class SubscribeFragment extends DialogFragment implements PopupMenu.OnMenuItemClickListener, View.OnClickListener {

    private static final int FOTO_REQUEST = 0;
    private Context mContext;
    private String ID;
    private MyDatabase mydb;
    private Button button, accept, disable;
    private TextInputEditText nome,cognome, age;
    private CircularImageView civ;
    private String mCurrentPhotoPath;
    private String picpath; //immagine attuale di profilo
    private String picfolder; //cartella in cui sono presenti le foto
    private boolean button_pressed; //true se l'utente preme imposta, falsa altrimenti

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getElement();

        if(getArguments()!=null){
            ID = getArguments().getString("ID");
            picfolder = getArguments().getString("Path_Folder");
            picpath = picfolder + "profile_"+ ID +".jpg";
            File pic = new File(picpath);
            if(pic.exists())
                new PhotoLoader(new WeakReference<>(civ), 150, 150).execute(Uri.fromFile(pic));
            String nomestr = getArguments().getString("nome");
            if (nomestr != null){ //se nomestr è diverso da null anche gli altri elementi sono stati settati
                nome.setText(nomestr);
                cognome.setText(getArguments().getString("cognome"));
                age.setText(getArguments().getString("anni"));
                button.setText(getArguments().getString("sesso"));
            }
        }
        if (savedInstanceState!=null)
            mCurrentPhotoPath = savedInstanceState.getString("path");
        mydb = new MyDatabase(getContext());
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
            mydb.updateRecords(ID, Objects.requireNonNull(nome.getText()).toString(), Objects.requireNonNull(cognome.getText()).toString(),sesso, Integer.parseInt(eta));

            button_pressed = true;

            //imposta la foto come predefinita se è stata cambiata
            if (mCurrentPhotoPath != null)
                new RenameFile(this).execute(picpath, mCurrentPhotoPath);
            else dismiss();
        }
        else dismiss();
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
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            MainActivity ma = (MainActivity) activity;
            ma.setCancel(button_pressed);
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("path", mCurrentPhotoPath);
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("richiesta", String.valueOf(requestCode));
        Log.i("result", String.valueOf(resultCode));
        if (requestCode == FOTO_REQUEST) {
            if (resultCode == RESULT_OK) {

                if(data==null || data.getData()==null){// l'utente ha selezionato la camera
                    Log.i("suces", "well");
                    File f = new File(mCurrentPhotoPath);
                    //if ((f = File_utility.handlePic(picpath, f)) != null){
                    //    Log.i("new path?", f.getAbsolutePath());
                        new PhotoLoader(new WeakReference<>(civ), 150, 150).execute(Uri.fromFile(f));
                    //} else Toast.makeText(mContext, "Operazione fallita, riprovare", Toast.LENGTH_SHORT).show();
                }
                else
                    //l'utente ha selezionato una foto dalla galleria
                    new PhotoLoader(this, new WeakReference<>(civ), 150, 150, mCurrentPhotoPath).
                            execute(data.getData());
            }
            else {
                Log.i("Fail", "prova elimina");
                if(File_utility.destroyTemp(mCurrentPhotoPath))
                    Log.i("Distrutto", "hurra");
                else Log.i("Stack", "overflow");
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
        File photoFile;
        try {
            photoFile = File_utility.createImageFile(picfolder);
            mCurrentPhotoPath = photoFile.getAbsolutePath();
        } catch (IOException ex) {
            Toast.makeText(mContext, "Impossibile preparare l'immagine, controlla i permessi! O riavvia!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent chooserIntent;
        List<Intent> intentList = new ArrayList<>();

        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoURI = FileProvider.getUriForFile(mContext,
                mContext.getPackageName() + ".provider", photoFile);
        camera.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoURI);
        intentList.add(camera);

        intentList.add(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI));

        chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1),
                    "Scatta una foto o prendila da quelle già salvate");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));

        startActivityForResult(chooserIntent,FOTO_REQUEST);
    }

    //--------------------------------------------

    //chiamato dall'asynctask che si occupa di caricare la bitmap
    /*@Override
    public void getNewFilePath(String path) {
        if (bitmap != null){

        }
    }*/
}
