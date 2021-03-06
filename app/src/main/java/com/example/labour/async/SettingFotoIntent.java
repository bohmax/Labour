package com.example.labour.async;
import android.os.AsyncTask;
import androidx.fragment.app.DialogFragment;
import com.example.labour.interfacce.FileInterfaceListener;
import com.example.labour.utility.File_utility;

import java.io.File;
import java.io.IOException;

public class SettingFotoIntent extends AsyncTask<String, Void, File> {

    private FileInterfaceListener df;

    public SettingFotoIntent(DialogFragment df) throws NullPointerException, ClassCastException{
        if (df == null) throw new NullPointerException();
        if (!(df instanceof FileInterfaceListener)) throw new ClassCastException();
        this.df =(FileInterfaceListener) df;
    }

    @Override
    protected File doInBackground(String... strings) {
        //cancella un vecchio file creato da un esecuzione precedente del programma, che magari non era stato pulito
        if (strings.length == 2 && strings[1] == null) //prima esecuzione del service di questa istanza del programma
            File_utility.destroyAllTemp(strings[0]);
        File photoFile = null;
        try {
            photoFile = File_utility.createImageFile(strings[0]);
        } catch (IOException ignored){}

        return photoFile;
    }

    @Override
    protected void onPostExecute(File file) {
        df.getTempPath(file);
    }
}
