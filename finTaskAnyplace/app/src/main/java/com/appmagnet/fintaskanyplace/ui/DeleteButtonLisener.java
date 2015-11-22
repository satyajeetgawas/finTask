package com.appmagnet.fintaskanyplace.ui;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.appmagnet.fintaskanyplace.R;
import com.appmagnet.fintaskanyplace.dataobjects.NoteObject;
import com.appmagnet.fintaskanyplace.db.DBContract;
import com.appmagnet.fintaskanyplace.db.DeleteEntriesDBHelper;

/**
 * Created by satyajeet on 11/18/2015.
 */
public class DeleteButtonLisener implements View.OnLongClickListener {

   private NoteObject noteObject;
    private Context context;
    private ExpandableListAdapter adpater;
    public DeleteButtonLisener(ExpandableListAdapter expandableListAdapter, Context context, NoteObject noteObj) {
        this.noteObject = noteObj;
        this.context = context;
        this.adpater = expandableListAdapter;
    }



    @Override
    public boolean onLongClick(View v) {
        new AlertDialog.Builder(context)
                .setIcon(null)
                .setTitle(R.string.delete)
                .setMessage(R.string.really_delete)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String noteGuid = noteObject.getGuid();
                        DeleteEntriesDBHelper dbHelper = new DeleteEntriesDBHelper(context);
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put(DBContract.DeletedEntries.COLUMN_NOTE_ID,  noteGuid);
                        values.put(DBContract.DeletedEntries.COLUMN_NOTE_NAME, noteObject.getNoteTitle());
                        values.put(DBContract.DeletedEntries.COLUMN_NOTE_CONTENT,noteObject.getContents());
                        db.insert(
                                DBContract.DeletedEntries.TABLE_NAME,
                                null,
                                values);
                        if(db != null && db.isOpen())
                            db.close();

                        adpater.removeChild(noteObject);
                    }

                })
                .setNegativeButton(R.string.cancel, null)
                .show();

        return true;
    }
}
