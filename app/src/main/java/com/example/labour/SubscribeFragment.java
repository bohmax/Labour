package com.example.labour;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;

import de.hdodenhof.circleimageview.CircleImageView;

public class SubscribeFragment extends DialogFragment implements PopupMenu.OnMenuItemClickListener, View.OnClickListener {

    protected static final int CAMERA_REQUEST = 0;
    protected static final int GALLERY_PICTURE = 1;
    private String ID;
    private MyDatabase mydb;
    private Button button, accept, disable;
    private TextInputEditText nome,cognome, age;
    private CircleImageView civ;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getElement();
        if(getArguments()!=null)
            ID = getArguments().getString("ID");

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
            Log.i("cognome", cognome.getText().toString());
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

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        button.setText(item.getTitle());
        return true;
    }

    void onImageClick(View v) {
        //Create an Intent with action as ACTION_PICK
        Intent intent=new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        // Launching the Intent
        startActivityForResult(intent,CAMERA_REQUEST);
    }
    //--------------------------------------------

    private View getElement(){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.subscribe_fragment, null);
        button = view.findViewById(R.id.buttonsex);
        accept = view.findViewById(R.id.imposta);
        disable = view.findViewById(R.id.annulla);
        age = view.findViewById(R.id.TextEta);
        nome = view.findViewById(R.id.TextNome);
        cognome = view.findViewById(R.id.TextCognome);
        civ = view.findViewById(R.id.image);
        civ.setColorFilter(getResources().getColor(R.color.grey) , PorterDuff.Mode.DARKEN);
        accept.setOnClickListener(this);
        disable.setOnClickListener(this);
        return view;
    }
}
