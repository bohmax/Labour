package com.example.labour.utility;

import android.hardware.SensorManager;
import com.example.labour.Direction;

public class Orientation_utility {

    private static float[] R = new float[9];
    private static float[] I = new float[9];
    private static float[] orientation = new float[3];
    private static final int bufsize = 10; //dimensione della coda
    private static float[] buf = new float[bufsize]; //cerco una media per l'azimuth in modo da non avere un risultato ballerino
    private static float somma = 0;//per avitae di sommare ogni volta tutti gli elementi dell array
    private static int last_inserted = 0; //indice dell'elemento da rimuovere dalla coda
    private static final float alpha = 0.8f;

    //reason https://stackoverflow.com/questions/19473819/removing-gravity-from-accelerometer-documentation-code
    public static void remove_gravity(float[] array, float[] values){
        for (int i = 0; i < array.length; i++)
            array[i] = alpha * array[i] + (1 - alpha) * values[i];
    }

    public static float getRotatioMedia(float[] mGravity, float[] mGeomagnetic){
        if (mGravity != null && mGeomagnetic != null) {
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_X, //se lo schermo ruota risistema le coordinate
                        SensorManager.AXIS_Z, I);
                SensorManager.getOrientation(R, orientation);
                float azimuth = orientation[0]; // orientation contains: azimut, pitch and roll, azimut è in radianti
                //mi occupo del buffer
                float value = buf[last_inserted];
                somma -= value;
                somma += azimuth;
                buf[last_inserted] = azimuth;
                last_inserted = (last_inserted+1)%bufsize;
                float media = somma/buf.length;
                return  ((float) (Math.toDegrees(media) + 360)) % 360;
            }
        } return 0;
    }

    public static Direction getDirection(float absolute){
        int range = (int) (absolute/22.5);
        switch (range) {
            case 1:
            case 2:
                return Direction.Nord_Est;
            case 3:
            case 4:
                return Direction.Est;
            case 5:
            case 6:
                return Direction.Sud_Est;
            case 7:
            case 8:
                return Direction.Sud;
            case 9:
            case 10:
                return Direction.Sud_Ovest;
            case 11:
            case 12:
                return Direction.Ovest;
            case 13:
            case 14:
                return Direction.Nord_Ovest;
            case 15:
            case 0:
            default: return Direction.Nord;
        }
    }

    /**
     * confronta due direzioni, ritornando la prima se le 2 sono riconosciute come vicine se aggiungendo un offset
     * ottendo la stessa direzione di dir1
     * @param dir1 prima direzione
     * @param dir2 seconda direzione in float
     * @return 1 se le direzioni sono vicine, 2 se sono opposte 0 altrimenti
     */
    public static int closeDirection(Direction dir1, float dir2){
        final int offset = 10;
        int dir1value = dir1.ordinal();
        int dir2sup = (int) (dir2 + 360 + offset) % 360;
        int dir2inf = (int) (dir2 + 360 - offset) % 360;
        int dir2supvalue = getDirection(dir2sup).ordinal();
        int dir2infvalue = getDirection(dir2inf).ordinal();
        if (dir1value == dir2supvalue || dir2infvalue == dir1value) return 1; //se era vicino è entrato in uno di quei due range
        int dir2opp = (int) (dir2 + 360 - 180) % 360;
        if (dir1value == dir2opp) return 2;
        return 0;
    }

    public static float forzaMAgnetica(float[] mGeomagnetic){
        return (float) Math.sqrt(mGeomagnetic[0] * mGeomagnetic[0] +
                mGeomagnetic[1] * mGeomagnetic[1] + mGeomagnetic[2] * mGeomagnetic[2]);
    }
}
