package com.example.labour;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    NfcAdapter nfc;
    PendingIntent pendingIntent = null;
    AlertDialog alertDialog;
    TextView text;
    ProgressBar progress;
    Snackbar sb;
    boolean nfc_no_choise = false; //se l'utente preme no nell'alert dialog questo non verrà mostrato nuovamente
    final int NFC_PERMISSION = 1;

    Handler handler = new Handler();
    Runnable task = new Runnable() {
        @Override
        public void run() {
            endKeyboard();
        }
    };

    //controlla che un determinato permesso sia stato dato
    public boolean requestPermission(String permission, int requestCode){
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
        if(nfc != null && nfc.isEnabled())
            nfc.disableForegroundDispatch(this);
        if(alertDialog.isShowing())
            alertDialog.dismiss();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("NFC_CHOISE", nfc_no_choise);
    }

    @Override
    protected void onNewIntent(Intent intent) { //viene chiamata quando lancia la pending intent perchè il flag nell intent è FLAG_ACTIVITY_SINGLE_TOP
        super.onNewIntent(intent);

        NdefMessage[] messages = getNdefMessages(intent);
        if(messages != null){
            System.out.println(messages[0]);
            text.setText(displayNFCArray(messages[0]));
        } else
            text.setText("Tag vuoto!");
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



    private NdefMessage[] getNdefMessages(Intent intent) {
        Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawMessages != null) {
            NdefMessage[] messages = new NdefMessage[rawMessages.length];
            for (int i = 0; i < messages.length; i++) {
                messages[i] = (NdefMessage) rawMessages[i];
            }
            return messages;
        } else {
            return null;
        }
    }

    static String displayNFCArray(NdefMessage messag) {
        StringBuilder builder = new StringBuilder();
        for (NdefRecord r: messag.getRecords()) {
            if (r.getTnf() == NdefRecord.TNF_WELL_KNOWN) {
                if (Arrays.equals(r.getType(), NdefRecord.RTD_TEXT)) {
                    //builder.append((char) aByte);
                    byte[] payloadBytes = r.getPayload();

                    //-----------------

                    //bisogna leggere l'header del pacchetto, altrimenti apparirà sempre un en davanti o quasi
                    //questa operazione si poteva anche fare bit a bit con payloadBytes[0] & 0x080: nota 0x080 equivale a 1000000 infatti si deve fare un & bit a bit fino al settimo e valutare questo
                    Charset charset = ((payloadBytes[0] & 0x080) == 0) ? StandardCharsets.UTF_8 : StandardCharsets.UTF_16; //status byte: bit 7 indicates encoding (0 = UTF-8, 1 = UTF-16)
                    //0x03F i primi 5 bit che contengono la lunghezza infatti sono a 1
                    int languageLength = payloadBytes[0] & 0x03F - 1; //status byte: bits 5..0 indicate length of language code

                    //-----------------
                    builder.append(new String(payloadBytes, languageLength + 1, payloadBytes.length - 1 - languageLength,charset)).append(" \n");
                    Log.d("READING", new String(payloadBytes, StandardCharsets.UTF_8));
                }
            }
        }
        builder.deleteCharAt(builder.length()-1);
        Log.d("TAG", builder.toString());
        return builder.toString();
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

        // show it
        //Toast.makeText(this, "Abilita NFC", Toast.LENGTH_SHORT).show();
    }

    private void endKeyboard(){
        text.setText("");
        progress.setVisibility(View.GONE);
        handler.removeCallbacks(task);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
