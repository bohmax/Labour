package com.example.labour.interfacce;

import android.graphics.Bitmap;

import com.example.labour.Package_item;

public interface BitmapReadyListener {

    /**
     * Chiamata per far partire la richiesta
     * @param item item in cui si vuole inserire l'immagine
     * @param index index dell'item all'interno dell'adapter
     */
    void startImageRequest(Package_item item, int index);

    /**
     * Richiesta finita
     * @param bitmap la bitmap ottenuta
     * @param index l'indirizzo richiesto in start image
     */
    void loadedBitmap(Bitmap bitmap, int index);
}
