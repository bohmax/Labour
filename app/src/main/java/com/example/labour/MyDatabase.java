package com.example.labour;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

class MyDatabase {

    private static SQLiteDatabase database;
    private final static String OP_TABLE="Operai"; // name of table

    //campi della tabella operai
    private final static String OP_ID="_ID";
    private final static String OP_NOME="nome";
    private final static String OP_COGNOME="cognome";
    private final static String OP_SESSO="sesso";
    private final static String OP_ETA="età";

    MyDatabase(Context context){
        if (database == null) {
            Database dbHelper = Database.getInstance(context);
            database = dbHelper.getWritableDatabase();
        }
    }



    long createRecords(String id, String name, String cognome, String gender, int eta) {
        ContentValues values = new ContentValues();
        values.put(OP_ID, id);
        values.put(OP_NOME, name);
        values.put(OP_COGNOME, cognome);
        values.put(OP_SESSO, gender);
        values.put(OP_ETA, eta);
        return database.insert(OP_TABLE, null, values);
    }

    long updateRecords(String id, String name, String cognome, String gender, int eta) throws SQLiteConstraintException {
        ContentValues values = new ContentValues();
        values.put(OP_ID, id);
        values.put(OP_NOME, name);
        values.put(OP_COGNOME, cognome);
        values.put(OP_SESSO, gender);
        values.put(OP_ETA, eta);
        return database.update(OP_TABLE, values, "_id="+id, null);
    }

    /*public boolean delete(String key, String elem, SimpleCursorAdapter spc){
        boolean ris = false;
        String whereclause;
        String[] cols = new String[] {OP_ID, OP_NOME,OP_COGNOME,OP_SESSO,OP_ETA};
        if (key!=null) whereclause = DIP_ID+"="+key;
        else whereclause = DIP_NAME+"="+elem;
        ris = database.delete("Dipartimento", whereclause,null) > 0;
        if (ris)
            spc.changeCursor(database.query(true, DIP_TABLE,cols,null, null, null, null, null, null));//refresha il cursore
        return ris;
    }*/

    /*public SimpleCursorAdapter selectRecords(Context context) {
        String[] cols = new String[] {OP_ID, OP_NOME,OP_COGNOME,OP_SESSO,OP_ETA};
        return new SimpleCursorAdapter(context, R.layout.linear,
                database.query(true, DIP_TABLE,cols,null
                        , null, null, null, null, null), new String[]{"_ID","nome"},new int[]{R.id.number_entry,R.id.name_entry}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
    }*/
}
