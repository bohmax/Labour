package com.example.labour;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

//#TO DO: CREARE ASYNC TASK PER GESTIRE AL MEGLIO QUESTI TASK
class File_utility {
    /**
     * Crea un file temporaneo
     * @param path in cui creare il file temporaneo
     * @return un File temporaneo creato nel path selezionato
     * @throws IOException
     */
    static File createImageFile(String path) throws IOException {
        File storageDir = new File(path);
        Log.i("Path", storageDir.getAbsolutePath());
        return File.createTempFile(
                "photofile_temp",  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    //rinomina il vacchio file, andando a eliminarlo solo dopo che il nuovo file viene rinominato correttamente

    /**
     * rinomina il vecchio file in modo da renderlo usabile come immagine profilo
     * @param path percorso del file profilo
     * @param newpic file da modificare
     * @return Un File che rappresenta la nuova foto profilo, null altrimenti
     */
    static File handlePic(String path,File newpic){
        File old = new File(path);
        File temp = new File(path + "_temp.jpg");

        if(old.exists()) {
            if (!old.renameTo(temp)) {
                return null;
            }
        }
        if(newpic.renameTo(old)){ //rimette in path originale
            boolean junk = temp.delete();
            return old;
        }
        else{ //se fallisce rimettiti se possibile nelle cond di partenza
            boolean b = old.renameTo(new File(path));
        }
        return null;
    }

    /**
     * memorizza una bitmap su un file
     * @param bitmap la bitmap da
     * @param path il percorso del file su cui mettere la bitmap
     * @return true se la bitmap è stata correttamente copiata
     */
    static boolean fromBitmapToFile(Bitmap bitmap, String path){
        OutputStream out;
        try {
            out = new FileOutputStream(path);
        } catch (FileNotFoundException e) {
            return false;
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        try {
            out.close();
        } catch (IOException ignore){}
        return true;
    }

    /**
     * ritorna la bitmap ricavata da un URI
     * @param context il context nel quale ricavare l'input stream
     * @param uri dal quale prendere il file
     * @param widthdp larghezza della bitmap in dp
     * @param heightdp altezza della bitmap in dp
     * @return la bitmap risultante, false se accade un errore
     */
    static Bitmap getBitMap(Context context, Uri uri,int widthdp, int heightdp){
        InputStream inputStream;
        try {
            inputStream = context.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            return null;
        }
        Bitmap bit = decodeSampledBitmapFromResource(inputStream, context, uri, dpitopx(context, widthdp),  dpitopx(context, heightdp));
        try {
            if (inputStream != null)
                inputStream.close();
        } catch (IOException ignore){}
        return bit;
    }

    /**
     * eliminare un specifico file
     * @param path del file da cancellare
     * @return true se è stato eliminato, falso altrimenti
     */
    static boolean destroyTemp(String path){
        File todestroy= new File(path);
        return todestroy.delete();
    }

    //per rispettare i limiti del render da https://developer.android.com/topic/performance/graphics/load-bitmap
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private static Bitmap decodeSampledBitmapFromResource(InputStream is, Context context, Uri uri, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, options);

        //dovuto a causa di un bruco
        try {
            is.close();
        } catch (IOException e) {
            return null;
        }
        InputStream is1;
        try {
            is1 = context.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            return null;
        }
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bit = BitmapFactory.decodeStream(is1, null, options);
        try {
            if (is1 != null)
                is1.close();
        } catch (IOException ignore){}
        return bit;
    }

    private static int dpitopx(Context context,int i) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, i,
                r.getDisplayMetrics());
    }
}