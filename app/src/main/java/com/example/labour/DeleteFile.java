package com.example.labour;

import android.os.AsyncTask;
import android.util.Log;

import androidx.fragment.app.DialogFragment;

public class DeleteFile extends AsyncTask<String, Void, Boolean> {

    private DialogFragment df;

    DeleteFile(){ }

    DeleteFile(DialogFragment df){
        this.df = df;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        return File_utility.destroyTemp(strings[0]);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) Log.i("Distrutto", "hurra");
        else Log.i("Stack", "overflow");
        if (df != null)
            df.dismiss();
    }
}
