package com.example.labour;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Permission_utility {

    private static final int NFC_PERMISSION = 1;
    private static final int CAMERA_PERMISSION = 2;
    private static final int WRITE_PERMISSION = 3;
    private static final int ACCELERATOR_PERMISSION = 4;

    static int getNfcPermission() {
        return NFC_PERMISSION;
    }

    static int getCameraPermission() {
        return CAMERA_PERMISSION;
    }

    static int getWritePermission() {
        return WRITE_PERMISSION;
    }

    static int getAcceleratorPermission() {
        return ACCELERATOR_PERMISSION;
    }

    static boolean requestPermission(Activity activity, String permission, int requestCode, String explenation){
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

    //static boolean permissionCallback(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    //}
}
