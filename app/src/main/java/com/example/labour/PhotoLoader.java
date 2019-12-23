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
    //private FileInterface fragment;
    private int width, height, errcode;
    private String path;

    //viene chiamata se l'utente ha selezionato la galleria
    PhotoLoader(DialogFragment fragment, WeakReference<CircularImageView> civ, int widthdp, int heightdp, String path) throws NullPointerException{
        if(civ == null || fragment == null) throw new NullPointerException();
        this.civ = civ;
        //if (fragment instanceof FileInterface)
        //    this.fragment =(FileInterface) fragment;
        this.path = path;
        width = widthdp;
        height = heightdp;
    }

    //Istanziato da SubscribeFragment se deve essere caricato dalla fotocamera
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
        if(path != null) {
            if (bitmap == null)
                errcode = 1;
            else if (!File_utility.fromBitmapToFile(bitmap, path))
                errcode = 2;
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null && civ != null) {
            civ.get().setImageBitmap(bitmap);
            if (path != null && errcode > 0)
                if (errcode == 1) Toast.makeText(civ.get().getContext(), "Impossibile caricare l'immagine, riprovare", Toast.LENGTH_SHORT).show();
                else Toast.makeText(civ.get().getContext(), "Operazione fallita, riprovare", Toast.LENGTH_SHORT).show();

        }
    }
}
