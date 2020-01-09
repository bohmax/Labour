package com.example.labour.async;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.labour.interfacce.BitmapReadyListener;
import com.example.labour.interfacce.FileInterfaceListener;
import com.example.labour.utility.File_utility;
import com.mikhaellopez.circularimageview.CircularImageView;
import java.lang.ref.WeakReference;

public class PhotoLoader extends AsyncTask<Uri, Void, Bitmap> {

    private WeakReference<CircularImageView> civ;
    private WeakReference<Context> context;
    private FileInterfaceListener fragment;
    private BitmapReadyListener brl;
    private int width, height, errcode, index;//index viene usato quando utilizzo brl per aggiornare l'adapter
    private String path;

    //viene chiamata se l'utente ha selezionato la galleria
    public PhotoLoader(DialogFragment fragment, WeakReference<CircularImageView> civ, int widthdp, int heightdp, String path) throws NullPointerException, ClassCastException{
        if(civ == null || fragment == null) throw new NullPointerException();
        if (!(fragment instanceof FileInterfaceListener)) throw new ClassCastException();
        this.civ = civ;
        this.fragment =(FileInterfaceListener) fragment;
        this.path = path;
        width = widthdp;
        height = heightdp;
    }

    //Istanziato da SubscribeFragment se deve essere caricato dalla fotocamera
    public PhotoLoader(FileInterfaceListener fragment, WeakReference<CircularImageView> civ, int widthdp, int heightdp) throws NullPointerException, ClassCastException{
        if(civ == null) throw new NullPointerException();
        this.civ = civ;
        this.fragment = fragment;
        width = widthdp;
        height = heightdp;
    }

    /*//Istanziato quando si deve caricare la foto attuale dell'utente
    public PhotoLoader(FileInterfaceListener fragment, WeakReference<CircularImageView> civ, int widthdp, int heightdp) throws NullPointerException, ClassCastException{
        if(civ == null || fragment == null) throw new NullPointerException();
        this.civ = civ;
        this.fragment =fragment;
        width = widthdp;
        height = heightdp;
    }*/

    //Istanziato quando si deve aggiornare un Package_item
    public PhotoLoader(Context context, BitmapReadyListener brl, int index, int widthdp, int heightdp) throws NullPointerException{
        if (context == null) throw new NullPointerException();
        this.context = new WeakReference<>(context);
        this.brl = brl;
        this.index = index;
        width = widthdp;
        height = heightdp;
    }

    @Override
    protected Bitmap doInBackground(Uri... uris) {
        Bitmap bitmap = null;
        if (brl != null && context.get()!= null)
            return File_utility.getBitMap(context.get(), uris[0], width, height);
        if(civ!=null)
            bitmap = File_utility.getBitMap(civ.get().getContext(), uris[0], width, height);
        if(path != null) {// l'utente ha selezionato la galleria
            if (bitmap != null)
                if (!File_utility.fromBitmapToFile(bitmap, path))
                    errcode = 1;
        }
        if (bitmap != null && uris.length == 2 && uris[1] != null) //elimina il vecchio file se tutto ha avuto successo
            File_utility.destroyTemp(uris[1].getPath());
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (brl != null){
            if (bitmap != null)
            brl.loadedBitmap(bitmap, index);
        }
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
