package com.example.labour.interfacce;

import android.graphics.Bitmap;

import com.example.labour.Package_item;

public interface BitmapReadyListener {

    void startImageRequest(Package_item item, int index);

    void loadedBitmap(Bitmap bitmap, int index);
}
