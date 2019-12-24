package com.example.labour;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import com.mikhaellopez.circularimageview.CircularImageView;
import java.lang.ref.WeakReference;

public class PhotoLoader extends AsyncTask<Uri, Void, Bitmap> {

    private WeakReference<CircularImageView> civ;
    private FileInterface fragment;
    private int width, height, errcode;
    private String path;

    //viene chiamata se l'utente ha selezionato la galleria
    PhotoLoader(DialogFragment fragment, WeakReference<CircularImageView> civ, int widthdp, int heightdp, String path) throws NullPointerException, ClassCastException{
        if(civ == null || fragment == null) throw new NullPointerException();
        if (!(fragment instanceof FileInterface)) throw new ClassCastException();
        this.civ = civ;
        this.fragment =(FileInterface) fragment;
        this.path = path;
        width = widthdp;
        height = heightdp;
    }

    //Istanziato da SubscribeFragment se deve essere caricato dalla fotocamera
    PhotoLoader(DialogFragment fragment, WeakReference<CircularImageView> civ, int widthdp, int heightdp) throws NullPointerException, ClassCastException{
        if(civ == null) throw new NullPointerException();
        if (!(fragment instanceof FileInterface)) throw new ClassCastException();
        this.civ = civ;
        this.fragment =(FileInterface) fragment;
        width = widthdp;
        height = heightdp;
    }

    //Istanziato quando si deve caricare la foto attuale dell'utente
    PhotoLoader(WeakReference<CircularImageView> civ, int widthdp, int heightdp) throws NullPointerException{
        if(civ == null) throw new NullPointerException();
        this.civ = civ;
        width = widthdp;
        height = heightdp;
    }

    @Override
    protected Bitmap doInBackground(Uri... uris) {
        Bitmap bitmap = null;
        if(civ!=null)
            bitmap = File_utility.getBitMap(civ.get().getContext(), uris[0], width, height);
        if(path != null) {// l'utente ha selezionato la galleria
            if (bitmap != null)
                if (!File_utility.fromBitmapToFile(bitmap, path))
                    errcode = 1;
        }
        if (fragment != null && bitmap != null && errcode == 0 && uris[1] != null) //elimina il vecchio file se tutto ha avuto successo
            File_utility.destroyTemp(uris[1].getPath());
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (civ != null) {
            boolean result = false;
            if (bitmap != null)
                if (errcode == 0) {
                    civ.get().setImageBitmap(bitmap);
                    result = true;
                }
                else Toast.makeText(civ.get().getContext(), "Operazione fallita, riprovare", Toast.LENGTH_SHORT).show(); //bitmap caricata, ma salvataggio fallito
            else Toast.makeText(civ.get().getContext(), "Impossibile caricare l'immagine, riprovare", Toast.LENGTH_SHORT).show();
            if (fragment != null)
                fragment.saveResult(result);
        }

    }
}
