package com.example.labour.utility;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.labour.R;
import com.google.android.material.snackbar.Snackbar;

public class Permission_utility {

    public static final int EXTERNAL_PERMISSION = 1;
    public static final int FOTO_PERMISSION = 2;

    @SuppressWarnings("unused")
    public static boolean requestPermission(Activity activity, String permission, int requestCode, String explenation){
        //check permission
        if(ContextCompat.checkSelfPermission(activity, permission)
                != PackageManager.PERMISSION_GRANTED){
            //permssi non ancora dati, mostra una spiegazione
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                //mostra una spiegazione all utente
                Toast.makeText(activity,explenation, Toast.LENGTH_SHORT).show();
            } else {
                // richiedi permesso
                ActivityCompat.requestPermissions(activity, new String[]{permission},
                        requestCode); //requestcode specifica il numero di richiesta
            }
            return false;
        } else return true;
    }

    public static boolean requestPermission(Fragment fragment, Activity act, String[] permissions, int requestCode, String explenation){
        boolean granted = true;
        int i = 0;
        while (i < permissions.length && granted) {
            granted = ContextCompat.checkSelfPermission(act, permissions[i]) == PackageManager.PERMISSION_GRANTED;
            i++;
        }
        if (granted) return true;
        i = 0;
        for (String permission: permissions)
            if (fragment.shouldShowRequestPermissionRationale(permission)) {//ritorna false se l'utente ha premuto non mostrare più
                i++;
            }
        if (i == 0)
            Snackbar.make(act.findViewById(R.id.frame), explenation, Snackbar.LENGTH_SHORT).show();
        fragment.requestPermissions(permissions, requestCode);
        return false;
    }
}
