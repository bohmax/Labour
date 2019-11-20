package com.example.labour;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class Database extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "DBName";

    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS Operai ( _ID TEXT PRIMARY KEY, nome TEXT not null, cognome TEXT not null, sesso TEXT not null,età INT not null);";

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
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(Database.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS Dipartimento");
        onCreate(db);
    }


}
