package com.example.labour;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class Database extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "LabourDB";

    private static final int DATABASE_VERSION = 13;

    private static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS Operai (_ID TEXT PRIMARY KEY not null, nome TEXT not null, cognome TEXT not null, sesso TEXT not null, eta INT not null);";
    private static final String PACCHI_COMPL = "CREATE TABLE IF NOT EXISTS Pacchi (_ID INTEGER PRIMARY KEY AUTOINCREMENT not null, titolo TEXT not null, descr TEXT not null, url TEXT, Operai_ID TEXT, FOREIGN KEY (Operai_ID) REFERENCES Operai(_ID));";

    private static Database Istance;

    static Database getInstance(Context context){
        if(Istance == null)
            Istance = new Database(context.getApplicationContext());
        return Istance;
    }

    private Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(DATABASE_CREATE);
            db.execSQL("PRAGMA foreign_keys=ON");
            db.execSQL(PACCHI_COMPL);
        } catch (SQLException e)
        {
            e.printStackTrace();
            throw e;
        }
        ContentValues values = new ContentValues();
        values.put("_ID", "pippotest");
        values.put("nome", "");
        values.put("cognome", "");
        values.put("sesso", "Uomo");
        values.put("eta", 0);
        if((db.insert("Operai",null,values))!=-1)
            Log.i("inserito","wut");
        ContentValues values1 = new ContentValues();
        values1.put("titolo", "Prova");
        values1.put("descr", "BELLA PROVA");
        values1.put("Operai_ID", "pippotest");
        if((db.insert("Pacchi",null,values1))!=-1)
            Log.i("inserito pacchi","wut");
        //db.insertOrThrow("Pacchi",null,values1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(Database.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS Operai");
        db.execSQL("DROP TABLE IF EXISTS Pacchi");
        onCreate(db);
    }


}
