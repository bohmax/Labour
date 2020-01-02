package com.example.labour;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class MyDatabase {

    private static SQLiteDatabase database;
    private final static String OP_TABLE="Operai"; // name of table
    private final static String OP_PACCHI="Pacchi"; // name of table

    //campi della tabella operai
    private final static String OP_ID="_ID";
    private final static String OP_NOME="nome";
    private final static String OP_COGNOME="cognome";
    private final static String OP_SESSO="sesso";
    private final static String OP_ETA="eta";

    //campi della tabella pacchi
    private final static String OP_TITOLO="titolo";
    private final static String OP_DESCR="descr";
    private final static String OP_EXT="Operai_ID";

    public MyDatabase(Context context){
        if (database == null) {
            Database dbHelper = Database.getInstance(context);
            database = dbHelper.getWritableDatabase();
        }
    }



    public long createRecordsOperai(String id, String name, String cognome, String gender, int eta) {
        ContentValues values = new ContentValues();
        values.put(OP_ID, id);
        values.put(OP_NOME, name);
        values.put(OP_COGNOME, cognome);
        values.put(OP_SESSO, gender);
        values.put(OP_ETA, eta);
        return database.insert(OP_TABLE, null, values);
    }

    public long createRecordsPacchi(String titolo, String descrizione, String operai_id) {
        ContentValues values = new ContentValues();
        values.put(OP_TITOLO, titolo);
        values.put(OP_DESCR, descrizione);
        values.put(OP_EXT, operai_id);
        return database.insert(OP_PACCHI, null, values);
    }

    public void updateRecordsOperai(String id, String name, String cognome, String gender, int eta) {
        ContentValues values = new ContentValues();
        values.put(OP_NOME, name);
        values.put(OP_COGNOME, cognome);
        values.put(OP_SESSO, gender);
        values.put(OP_ETA, eta);
        database.update(OP_TABLE, values, OP_ID+"=?", new String[]{id});
    }

    public String[] searchByIdOperai(String id) {
        Cursor cur = database.rawQuery("SELECT * FROM " + OP_TABLE + " WHERE " + OP_ID + "=?", new String[]{id});
        cur.moveToFirst();
        String[] elem ={
            cur.getString(cur.getColumnIndex(OP_NOME)),
                cur.getString(cur.getColumnIndex(OP_COGNOME)),
                        cur.getString(cur.getColumnIndex(OP_ETA)),
                                cur.getString(cur.getColumnIndex(OP_SESSO))
        };
        cur.close();
        return elem;
    }

    public ArrayList<Package_item> searchByIdPacchi(String id) {
        Cursor cur = database.rawQuery("SELECT * FROM " + OP_PACCHI + " WHERE " + OP_EXT + "=?", new String[]{id});
        ArrayList<Package_item> list= new ArrayList<>();
        while (cur.moveToNext()) {
            list.add( new Package_item(
                    cur.getString(cur.getColumnIndex(OP_TITOLO)),
                    cur.getString(cur.getColumnIndex(OP_DESCR)),
                    R.drawable.package_default));
        }
        cur.close();
        return list;
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
