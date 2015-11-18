package com.appmagnet.fintaskanyplace.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by satyajeet and anmol on 11/9/2015.
 */
public class NotesDBHelper extends SQLiteOpenHelper {

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DBContract.NotesEntry.TABLE_NAME + " (" +
                    DBContract.NotesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1 ," +
                    DBContract.NotesEntry.NOTE_ID + TEXT_TYPE + COMMA_SEP +
                    DBContract.NotesEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    DBContract.NotesEntry.COLUMN_CONTENT + TEXT_TYPE + COMMA_SEP +
                    DBContract.NotesEntry.COLUMN_CATEGORY + TEXT_TYPE + COMMA_SEP +
                    DBContract.NotesEntry.COLUMN_NOTE_DATE + TEXT_TYPE +

    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DBContract.NotesEntry.TABLE_NAME;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Notes.db";

    public NotesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
