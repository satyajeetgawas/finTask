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
    private String noteGuid;
    private String source;

    public NoteObject(Cursor c) {
        noteTitle = c.getString(c.getColumnIndex(DBContract.NotesEntry.COLUMN_NAME_TITLE));
        noteType = c.getString(c.getColumnIndex(DBContract.NotesEntry.COLUMN_CATEGORY ));
        noteDate = c.getString(c.getColumnIndex(DBContract.NotesEntry.COLUMN_NOTE_DATE));
        noteContent = c.getString(c.getColumnIndex(DBContract.NotesEntry.COLUMN_CONTENT));
        noteGuid = c.getString(c.getColumnIndex(DBContract.NotesEntry.NOTE_ID));
        source = c.getString(c.getColumnIndex(DBContract.NotesEntry.COLUMN_SOURCE));

    }

    public String getFormattedString() {
        String temp = "<html><b>"+source+" : "+noteTitle + "</b><br>" + noteDate + "<br>" + noteContent+"</html>";
        return temp;
    }


    public String getNoteType() {
        return noteType;
    }


    public String getGuid() {
        return noteGuid;
    }

    public String getContents() {
        return noteContent;
    }


    public String getNoteTitle() {
        return noteTitle;
    }
}
