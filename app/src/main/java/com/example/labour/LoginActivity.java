package com.example.labour;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class LoginActivity extends AppCompatActivity {

    NfcAdapter nfc;
    PendingIntent pendingIntent = null;
    AlertDialog alertDialog;
    TextView text;
    ProgressBar progress;
    Snackbar sb;
    boolean nfc_no_choise = false; //se l'utente preme no nell'alert dialog questo non verrà mostrato nuovamente
    final int NFC_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = findViewById(R.id.text);

        if(savedInstanceState != null)
            nfc_no_choise = savedInstanceState.getBoolean("NFC_CHOISE");
        else
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_menu, new MenuFragment()).commit();
        if(requestPermission(Manifest.permission.NFC, NFC_PERMISSION))
            //Toast.makeText(this,"funziona", Toast.LENGTH_LONG);
            Log.i("Funziona", "GOOD");

        //progress = findViewById(R.id.progressBar_cyclic);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        nfc = NfcAdapter.getDefaultAdapter(this);
        if (nfc != null ) {
            //Toast.makeText(this, "NFC AVAIBLE", Toast.LENGTH_LONG).show();
            pendingIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            alertDialog = createWirlessAlert();

            sb = Snackbar.make(findViewById(R.id.linear), R.string.ATTIVA_NFC, Snackbar.LENGTH_INDEFINITE);
            sb.setAction("Mostra", new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                    startActivity(intent);
                }
            });
        }
        //toolbar.setFocusable(false);
        //toolbar.setFocusableInTouchMode(false);
        //progress.setVisibility(View.GONE);
    }

    //nfc in giù
    @Override
    protected void onResume() {
        super.onResume();
        if(nfc != null) {
            boolean pref = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("NFC", true);
            Log.i("preso", String.valueOf(pref));
            if (nfc.isEnabled()) nfc.enableForegroundDispatch(this, pendingIntent, null, null);
            else if(!pref) sb.dismiss();
            else if (!nfc_no_choise) alertDialog.show();
            else sb.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(nfc != null && nfc.isEnabled()) {
            nfc.disableForegroundDispatch(this);
            if (alertDialog.isShowing())
                alertDialog.dismiss();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("NFC_CHOISE", nfc_no_choise);
    }

    @Override
    protected void onNewIntent(Intent intent) { //viene chiamata quando lancia la pending intent perchè il flag nell intent è FLAG_ACTIVITY_SINGLE_TOP
        super.onNewIntent(intent);

        NdefMessage[] messages = GetLogin_ID.getNdefMessages(intent);
        if(messages != null){
            System.out.println(messages[0]);
            text.setText(GetLogin_ID.getNFCPayload(messages[0]));
        } else
            Log.i("Empy", "empty text!");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case NFC_PERMISSION:{
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
            default:
                throw new IllegalStateException("Unexpected value: " + requestCode);
        }
    }

    //per la gestione della tastiera
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_ENTER: {
                String _id = GetLogin_ID.get_ID();
                break;
            }
            case (KeyEvent.KEYCODE_BACK): {
                moveTaskToBack(true);
                break;
            }
            default: { //aggiungi il valore
                GetLogin_ID.concatToString(event.getUnicodeChar());
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    //controlla che un determinato permesso sia stato dato
    private boolean requestPermission(String permission, int requestCode){
        //check permission
        if(ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED){
            //permssi non ancora dati, mostra una spiegazione
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    permission)) {
                //mostra una spiegazione all utente
                Toast.makeText(this,"Pemessi non dati", Toast.LENGTH_SHORT).show();
            } else {
                // richiedi permesso
                ActivityCompat.requestPermissions(this,
                        new String[]{permission},
                        requestCode); //requestcode specifica il numero di richiesta
            }
        } else return true;
        return false;
    }

    private AlertDialog createWirlessAlert(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle("Wifi Settings");

        // set dialog message
        alertDialogBuilder
                .setMessage("Do you want to enable WIFI ?")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        //enable wifi
                        Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        nfc_no_choise = true;
                        sb.show();
                    }
                });

        // create alert dialog
        return alertDialogBuilder.create();
    }

}
