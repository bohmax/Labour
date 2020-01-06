package com.example.labour.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.labour.async.ServerRequest;
import com.example.labour.interfacce.TaskListener;
import com.example.labour.fragment.MenuFragment;
import com.example.labour.MyDatabase;
import com.example.labour.R;
import com.example.labour.utility.getID_NFC;
import com.google.android.material.snackbar.Snackbar;

import java.lang.ref.WeakReference;

public class LoginActivity extends AppCompatActivity implements TaskListener {

    private NfcAdapter nfc;
    private PendingIntent pendingIntent = null;
    private AlertDialog alertDialog; //richiede l'attivazione del nfc non i permessi
    private MenuFragment menuf;
    private ProgressBar progress;
    private Snackbar sb;
    EditText test;

    boolean serverrequest = false; //per sapere se l'utente vuole interagire con il server
    boolean nfc_not_choosed = false; //se l'utente preme no nell'alert dialog questo non verrà mostrato nuovamente

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        test = findViewById(R.id.test);
        progress = findViewById(R.id.scroll);
        test.setOnClickListener(v -> richiediAccesso(test.getText().toString()));
        if(savedInstanceState != null) {
            menuf =(MenuFragment) getSupportFragmentManager().getFragment(savedInstanceState, "MenuFragmente");
            nfc_not_choosed = savedInstanceState.getBoolean("NFC_CHOISE");
        }
        else {
            menuf = new MenuFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_menu, menuf).commit();
        }


        nfc = NfcAdapter.getDefaultAdapter(this);
        if (nfc != null ) {
            //Toast.makeText(this, "NFC AVAIBLE", Toast.LENGTH_LONG).show();
            pendingIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            alertDialog = createWirlessAlert();

            sb = Snackbar.make(findViewById(R.id.linear), R.string.ATTIVA_NFC, Snackbar.LENGTH_INDEFINITE);
            sb.setAction("Mostra", v -> {
                Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                startActivity(intent);
            });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(nfc != null) {
            boolean pref = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("NFC", true);
            serverrequest = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("CONNECT", false);
            Log.i("preso", String.valueOf(pref));
            if (nfc.isEnabled()) nfc.enableForegroundDispatch(this, pendingIntent, null, null);
            else if(!pref) sb.dismiss();
            else if (!nfc_not_choosed) alertDialog.show();
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
        getSupportFragmentManager().putFragment(savedInstanceState, "MenuFragmente", menuf);
        savedInstanceState.putBoolean("NFC_CHOISE", nfc_not_choosed);
    }

    @Override
    protected void onNewIntent(Intent intent) { //viene chiamata quando lancia la pending intent perchè il flag nell intent è FLAG_ACTIVITY_SINGLE_TOP
        super.onNewIntent(intent);

        NdefMessage[] messages = getID_NFC.getNdefMessages(intent);
        if(messages != null){
            richiediAccesso(getID_NFC.getNFCPayload(messages[0]));
        } else
            Log.i("Empy", "empty text!");
    }

    //per la gestione della tastiera
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_ENTER: {
                richiediAccesso(getID_NFC.get_ID());
                break;
            }
            case (KeyEvent.KEYCODE_BACK): {
                moveTaskToBack(true);
                break;
            }
            default: { //aggiungi il valore
                getID_NFC.concatToString(event.getUnicodeChar());
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void tryInsertDB(String id){
        new RequestUser(this).execute(id);
    }

    private void richiediAccesso(String id){
        if(id.length()>0) {
            if (serverrequest) {
                progress.setVisibility(View.VISIBLE);
                new ServerRequest(this).execute(id);
            } else tryInsertDB(id);
        }
    }

    private AlertDialog createWirlessAlert(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle("NFC Settings");

        // set dialog message
        alertDialogBuilder
                .setMessage("Do you want to enable NFC ?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    //enable wifi
                    Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                    startActivity(intent);
                })
                .setNegativeButton("No", (dialog, which) -> {
                    nfc_not_choosed = true;
                    sb.show();
                });

        // create alert dialog
        return alertDialogBuilder.create();
    }

    //chiamata dall'asynctask quando finisce
    @Override
    public void serverTask(boolean answer, String id) {
        progress.setVisibility(View.GONE);
        if(answer)
            tryInsertDB(id);
        else
            Toast.makeText(this, "Impossibile contattare il server, riprovare.", Toast.LENGTH_LONG).show();
    }

    private static class RequestUser extends AsyncTask<String, Void, Long> {

        private WeakReference<LoginActivity> activityReference;
        private String id;

        RequestUser(LoginActivity act) {
            activityReference = new WeakReference<>(act);
        }

        @Override
        protected Long doInBackground(String... ids) {
            id = ids[0];
            LoginActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return null;

            MyDatabase db = new MyDatabase(activity);
            return db.createRecordsOperai(id, "Mario", "Rossi", "Uomo", 22);
        }

        @Override
        protected void onPostExecute(Long result) {

            LoginActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            Intent i = new Intent(activity, MainActivity.class);
            if(result != -1)
                i.putExtra("Exist", false);
            else
                i.putExtra("Exist", true);
            i.putExtra("ID", id);
            activity.startActivity(i);
        }
    }
}
