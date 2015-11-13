package com.appmagnet.fintaskanyplace.dataobjects;

import android.database.Cursor;

import com.appmagnet.fintaskanyplace.db.DBContract;

/**
 * Created by anmolgupta on 11/13/15.
 */
public class NoteObject {

    private String noteType;
    private String noteTitle;
    private String noteDate;
    private String noteContent;

    public NoteObject(Cursor c) {
        noteTitle = c.getString(c.getColumnIndex(DBContract.NotesEntry.COLUMN_NAME_TITLE));
        noteType = c.getString(c.getColumnIndex(DBContract.NotesEntry.COLUMN_CATEGORY ));
        noteDate = "Nov 13, 2015";
        noteContent = c.getString(c.getColumnIndex(DBContract.NotesEntry.COLUMN_CONTENT));
    }

    public String getFormattedString() {
        String temp = noteTitle + "\n" + noteDate + "\n" + noteContent;
        return temp;
    }


    public String getNoteType() {
        return noteType;
    }


}
