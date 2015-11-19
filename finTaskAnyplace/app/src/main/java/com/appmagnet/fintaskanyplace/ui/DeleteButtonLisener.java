package com.appmagnet.fintaskanyplace.ui;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.Toast;

import com.appmagnet.fintaskanyplace.dataobjects.NoteObject;
import com.appmagnet.fintaskanyplace.db.DBContract;
import com.appmagnet.fintaskanyplace.db.DeleteEntriesDBHelper;

/**
 * Created by satyajeet on 11/18/2015.
 */
public class DeleteButtonLisener implements View.OnClickListener {

   private NoteObject noteObject;
    private Context context;
    private ExpandableListAdapter adpater;
    public DeleteButtonLisener(ExpandableListAdapter expandableListAdapter, Context context, NoteObject noteObj) {
        this.noteObject = noteObj;
        this.context = context;
        this.adpater = expandableListAdapter;
    }

    @Override
    public void onClick(View v) {

        String noteGuid = noteObject.getGuid();
        DeleteEntriesDBHelper dbHelper = new DeleteEntriesDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBContract.DeletedEntries.COLUMN_NOTE_ID,  noteGuid);
        values.put(DBContract.DeletedEntries.COLUMN_NOTE_NAME, noteObject.getNoteTitle());
        db.insert(
                DBContract.NotesEntry.TABLE_NAME,
                null,
                values);
        if(db != null && db.isOpen())
            db.close();

        adpater.removeChild(noteObject);
    }
}
