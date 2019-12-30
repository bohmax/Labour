package com.example.labour.utility;

import android.hardware.SensorManager;

import org.apache.commons.collections4.queue.CircularFifoQueue;

public class Orientation_utility {

    private float[] mGravity; //per gestione accelerometro e magnetometro
    private float[] mGeomagnetic;
    private final int bufsize = 30; //dimensione della coda
    private CircularFifoQueue<Float> buf = new CircularFifoQueue<>(bufsize); //cerco una media per l'azimuth in modo da non avere un risultato ballerino
    private float somma = 0;//per avitae di sommare ogni volta tutti gli elementi dell array
    private int last_inserted = 0; //indice dell'elemento da rimuovere dalla coda

    public void setmGravity(float[] mGravity) {
        this.mGravity = mGravity;
    }

    public void setmGeomagnetic(float[] mGeomagnetic) {
        this.mGeomagnetic = mGeomagnetic;
    }

    public float getRotatioMedia(){
        if (mGravity != null && mGeomagnetic != null) {
            float[] R = new float[9];
            float[] I = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float[] orientation = new float[3];
                SensorManager.getOrientation(R, orientation);
                float azimut = orientation[0]; // orientation contains: azimut, pitch and roll, azimut è in radianti
                float rotation = (float)(Math.toDegrees(azimut)+360)%360;
                if (buf.size()==bufsize) { //se la coda è piena rimuovi il primo elemento da somma e aggiungi il nuovo elemento
                    float value = buf.get(last_inserted);
                    somma -= value;
                    somma += rotation;
                    buf.add(rotation);
                    last_inserted = (last_inserted+1)%bufsize;
                    return somma /(float) buf.size();
                } else{
                    somma += rotation;
                    buf.add(rotation);
                }
            }
        } return 0;
    }

    public String getDirection(float absolute){
        int range = (int) (absolute/16);
        switch (range) {
            case 1:
            case 2:
                return "NE";
            case 3:
            case 4:
                return "E";
            case 5:
            case 6:
                return "SE";
            case 7:
            case 8:
                return "S";
            case 9:
            case 10:
                return "SW";
            case 11:
            case 12:
                return "W";
            case 13:
            case 14:
                return "NW";
            case 15:
            case 0:
            default: return "N";
        }
    }

}
