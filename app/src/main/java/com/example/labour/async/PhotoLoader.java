package com.example.labour.async;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;
import com.example.labour.interfacce.BitmapReadyListener;
import com.example.labour.interfacce.FileInterfaceListener;
import com.example.labour.utility.File_utility;
import java.lang.ref.WeakReference;

public class PhotoLoader extends AsyncTask<Uri, Void, Bitmap> {

    //private WeakReference<CircularImageView> civ;
    private WeakReference<Context> context;
    private FileInterfaceListener listener;
    private BitmapReadyListener brl;
    private int width, height, errcode, index;//index viene usato quando utilizzo brl per aggiornare l'adapter
    private String path;

    /*//viene chiamata se l'utente ha selezionato la galleria
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

    //Istanziato quando si deve aggiornare un Package_item
    public PhotoLoader(Context context, BitmapReadyListener brl, int index, int widthdp, int heightdp) throws NullPointerException{
        if (context == null) throw new NullPointerException();
        this.context = new WeakReference<>(context);
        this.brl = brl;
        this.index = index;
        width = widthdp;
        height = heightdp;
    }*/

    /**
     * Da utilizzare quando si vuole caricare una foto non legata a un adapter
     * @param context che verrà utilizzato per caricare la bitmap
     * @param listener callback di ritorno
     * @param path null se non si vuole salvare la bitmap in un path specifico
     * @param widthdp la lunghezza richiesta per la bitmap
     * @param heightdp l'altezza richiesta per la bitmap
     * @throws NullPointerException
     */
    public PhotoLoader(Context context, FileInterfaceListener listener, String path,
                       int widthdp, int heightdp) throws NullPointerException{
        if (context == null || listener == null) throw new NullPointerException();
        this.context = new WeakReference<>(context);
        this.listener = listener;
        this.path = path;
        width = widthdp;
        height = heightdp;
    }

    /**
     * Per riempire
     * @param context che verrà utilizzato per caricare la bitmap
     * @param brl callback chimare quando finisce
     * @param index indice da inviare se si deve riferire a una posizione in un array, che verrà restituito
     * @param widthdp la lunghezza richiesta per la bitmap
     * @param heightdp l'altezza richiesta per la bitmap
     * @throws NullPointerException
     */
    public PhotoLoader(Context context, BitmapReadyListener brl, int index,
                       int widthdp, int heightdp) throws NullPointerException{
        if (context == null || brl == null) throw new NullPointerException();
        this.context = new WeakReference<>(context);
        this.brl = brl;
        this.index = index;
        width = widthdp;
        height = heightdp;
    }


    @Override
    protected Bitmap doInBackground(Uri... uris) {
        Bitmap bitmap = null;
        Context contesto = context.get();
        if (contesto != null) {
            bitmap = File_utility.getBitMap(contesto, uris[0], width, height);
            if (brl != null)
                return bitmap;
            //richiesta da un listener
            if (path != null) { //l'utente ha selezionato la galleria
                if (bitmap != null)
                    if (!File_utility.fromBitmapToFile(bitmap, path))
                        errcode = 1;
            }
            if (bitmap != null && uris.length == 2 && uris[1] != null) //elimina il vecchio file se tutto ha avuto successo
                File_utility.destroyTemp(uris[1].getPath());
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        Context contesto = context.get();
        if (contesto != null) {
            if (brl != null) {
                if (bitmap != null)
                    brl.loadedBitmap(bitmap, index);
            }
            if (listener != null) {
                if (bitmap != null)
                    if (errcode == 0) { //nessun errore
                        listener.saveResult(bitmap, true);
                        return;
                    }
                    else
                        Toast.makeText(contesto, "Operazione fallita, riprovare", Toast.LENGTH_SHORT).show(); //bitmap caricata, ma salvataggio fallito
                else
                    Toast.makeText(contesto, "Impossibile caricare l'immagine, riprovare", Toast.LENGTH_SHORT).show();
                listener.saveResult(bitmap, false);
            }
        }

    }
}
