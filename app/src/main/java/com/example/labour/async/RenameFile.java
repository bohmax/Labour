package com.example.labour.async;

import android.os.AsyncTask;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.labour.utility.File_utility;

import java.io.File;

public class RenameFile extends AsyncTask<String, Void, File> {

    private DialogFragment df;

    public RenameFile(DialogFragment df) throws NullPointerException{
        if (df == null) throw new NullPointerException();
        this.df = df;
    }

    @Override
    protected File doInBackground(String... strings) {
        return File_utility.handlePic(strings[0], new File(strings[1]));
    }

    @Override
    protected void onPostExecute(File file) {
        if (df != null) {
            if (file != null)
                df.dismiss();
            else
                Toast.makeText(df.getActivity(), "Impossibile salvare il file, riprovare!", Toast.LENGTH_LONG).show();
        }
    }
}
