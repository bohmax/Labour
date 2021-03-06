package com.example.labour.utility;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class File_utility {
    /**
     * Crea un file temporaneo
     * @param path in cui creare il file temporaneo
     * @return un File temporaneo creato nel path selezionato
     * @throws IOException
     */
    public static File createImageFile(String path) throws IOException {
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
    public static File handlePic(String path,File newpic){
        File old = new File(path);
        File temp = new File(path + "_temp.jpg");

        if(old.exists()) {
            if (!old.renameTo(temp)) {
                return null;
            }
        }
        if(newpic.renameTo(old)){ //rimette in path originale
            temp.delete();
            return old;
        }
        else{ //se fallisce rimettiti se possibile nelle cond di partenza
            old.renameTo(new File(path));
        }
        return null;
    }

    /**
     * memorizza una bitmap su un file
     * @param bitmap la bitmap da
     * @param path il percorso del file su cui mettere la bitmap
     * @throws NullPointerException se bitmap o path sono null
     * @return true se la bitmap è stata correttamente copiata
     */
    public static boolean fromBitmapToFile(Bitmap bitmap, String path) throws NullPointerException{
        if (bitmap == null || path == null) throw new NullPointerException();
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
    public static Bitmap getBitMap(Context context, Uri uri,int widthdp, int heightdp){
        return decodeSampledBitmapFromResource(context, uri, dpitopx(context, widthdp),  dpitopx(context, heightdp));

    }

    /**
     * eliminare uno specifico file
     * @param path del file da cancellare
     * @return true se è stato eliminato, falso altrimenti
     */
    public static boolean destroyTemp(String path){
        if(path!=null) {
            File todestroy = new File(path);
            if (todestroy.exists())
                return todestroy.delete();
            return true;
        } return true;
    }

    /**
     * elimina tutti i file che contengono la parola temp
     * @param path la path del folder in cui eliminare i file
     */
    public static void destroyAllTemp(String path){
        File folder = new File(path);

        File[] filenamestemp = folder.listFiles();

        for (File file : filenamestemp) {
            if (file.getAbsolutePath().contains("temp"))
                file.delete();
        }
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

    private static Bitmap decodeSampledBitmapFromResource(Context context, Uri uri, int reqWidth, int reqHeight) {

        InputStream inputStream = createInput(context, uri);

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, options); //injustbound = true => ritorna una bitmap null ma con option con i bound dell immagine
        CloseStream(inputStream);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        inputStream = createInput(context, uri);
        options.inJustDecodeBounds = false;
        Bitmap bit = BitmapFactory.decodeStream(inputStream, null, options);
        CloseStream(inputStream);

        try {
            return rotateImageIfRequired(bit, uri);
        } catch (IOException e) {
            return bit;
        }
    }

    private static InputStream createInput(Context context, Uri uri){
        InputStream is = null;
        try {
            is = context.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException ignored) { }
        return is;
    }

    private static void CloseStream(InputStream is){
        try {
            if (is != null)
                is.close();
        } catch (IOException ignore){}
    }

    private static int dpitopx(Context context,int i) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, i,
                r.getDisplayMetrics());
    }

    /**
     * Rotate an image if required.
     * https://www.samieltamawy.com/how-to-fix-the-camera-intent-rotated-image-in-android/
     * @param img           The image bitmap
     * @param selectedImage Image URI
     * @return The resulted Bitmap after manipulation
     */
    private static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {

        if (selectedImage.getPath() == null) return null;
        ExifInterface ei = new ExifInterface(selectedImage.getPath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

}
