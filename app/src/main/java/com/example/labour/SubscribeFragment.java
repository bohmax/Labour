package com.example.labour;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getElement();
        if(getArguments()!=null)
            ID = getArguments().getString("ID");
        if (savedInstanceState!=null)
            mCurrentPhotoPath = savedInstanceState.getString("path");
        mydb = new MyDatabase(getContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.set_data);
        builder.setIcon(R.drawable.ic_account_circle_black_24dp);
        builder.setMessage("Altrimenti si può continuare a lavorare in maniera quasi anonima");
        builder.setView(v);

        return builder.create();
    }

    @Override
    public void onClick(View v) {
        if (v == accept) {
            String sesso,eta="0";
            if(button.getText().toString().equals("SESSO"))
                sesso = "Uomo";
            else sesso = button.getText().toString();
            if(age.getText().toString().length()!=0)
                eta =age.getText().toString();
            mydb.updateRecords(ID,nome.getText().toString(),cognome.getText().toString(),sesso, Integer.parseInt(eta));
        } else getDialog().dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                int width = ViewGroup.LayoutParams.MATCH_PARENT;
                int height = ViewGroup.LayoutParams.MATCH_PARENT;
                dialog.getWindow().setLayout(width, height);
            }
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

                Uri uri;
                if(data==null || (uri=data.getData())==null){// l'utente ha selezionato la camera
                    Log.i("suces", "well");
                    File f = new File(mCurrentPhotoPath);
                    //scanPic(f);
                    if ((f = handlePic(f)) != null){
                        Log.i("new path?", f.getAbsolutePath());
                        Bitmap bitmap = getBitMap(Uri.fromFile(f));
                        if(bitmap!=null)
                            civ.setImageBitmap(bitmap);
                    }
                } else {
                    //l'utente ha selezionato una foto dalla galleria
                    Log.i("imm", uri.toString());
                    Bitmap bitmap = getBitMap(data.getData());
                    if(bitmap != null){
                        civ.setImageBitmap(bitmap);
                        fromBitmapToFile(bitmap);
                    }
                }
            }
            else {
                Log.i("Fail", "prova elimina");
                File todestroy= new File(mCurrentPhotoPath);
                if(todestroy.delete())
                    Log.i("Distrutto", "hurra");
                else Log.i("STack", "overflow");
            }

        }
    }

    /*@Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("nome", nome.getText().toString());
        outState.putString("cognome", cognome.getText().toString());
        outState.putString("sesso", button.getText().toString());
        outState.putString("eta", age.getText().toString());
        //outState.putString("nome", nome.getText().toString());
    }*/

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

    void onImageClick(View v) {
        Intent chooserIntent;
        List<Intent> intentList = new ArrayList<>();
        File photoFile = null;

        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
                photoFile = createImageFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(mContext,
                    mContext.getPackageName() + ".provider",
                    photoFile);
            Log.i("Pavido", photoURI.toString());
            camera.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoURI);
            intentList.add(camera);
        }

        intentList.add(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI));

        chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1),
                    "Scatta una foto o prendila da quelle già salvate");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));

        startActivityForResult(chooserIntent,FOTO_REQUEST);
    }

    //--------------------------------------------

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

    private File createImageFile() throws IOException {
        File storageDir = new File(mContext.getApplicationInfo().dataDir+"/files");
        Log.i("Path", storageDir.getAbsolutePath());
        File image = File.createTempFile(
                "ptofile_temp",  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.i("Path", mCurrentPhotoPath);
        return image;
    }

    //#TO DO: CREARE ASYNC TASK PER GESTIRE AL MEGLIO QUESTI TASK
    private void scanPic(File newpick) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(newpick);
        mediaScanIntent.setData(contentUri);
        mContext.sendBroadcast(mediaScanIntent);
    }

    //rinomina il vacchio file, andando a eliminarlo solo dopo che il nuovo file viene rinominato correttamente
    private File handlePic(File newpic){
        String picpath = mContext.getApplicationInfo().dataDir+"/files/profile.jpg";
        File old = new File(picpath);
        File temp = new File(picpath + "_temp.jpg");

        if(old.exists()) {
            if (!old.renameTo(temp)) {
                Toast.makeText(mContext, "Operazione fallita, riprovare", Toast.LENGTH_SHORT).show();
                return null;
            }
        }
        if(newpic.renameTo(old)){
            boolean junk = temp.delete();
            return old;
        }
        else{ //se fallisce rimettiti se possibile nelle cond di partenza
            boolean b = old.renameTo(new File(picpath));
            Toast.makeText(mContext, "Operazione fallita, riprovare", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    private boolean fromBitmapToFile(Bitmap bitmap){
        OutputStream out;
        try {
            out = new FileOutputStream(mContext.getApplicationInfo().dataDir+"/files/profile.jpg");
        } catch (FileNotFoundException e) {
            Toast.makeText(mContext, "Operazione fallita, riprovare", Toast.LENGTH_SHORT).show();
            return false;
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        return true;
    }

    private Bitmap getBitMap(Uri uri){
        InputStream inputStream;
        try {
            inputStream = mContext.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            Toast.makeText(mContext, "File non trovato", Toast.LENGTH_LONG).show();
            return null;
        }
        return BitmapFactory.decodeStream(inputStream);
    }
}
