package com.appmagnet.fintaskanyplace.evernote;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.appmagnet.fintaskanyplace.core.ContentClassifier;
import com.appmagnet.fintaskanyplace.core.TitleClassifier;
import com.appmagnet.fintaskanyplace.db.DBContract;
import com.appmagnet.fintaskanyplace.db.DeleteEntriesDBHelper;
import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteList;
import com.evernote.edam.type.Note;
import com.evernote.thrift.TException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by satyajeet and anmol on 10/21/2015.
 */
public class ReadUserNotes {

    private List<String> guidList;
    private Map notesData;
    private Map notesTitle;
    private EvernoteNoteStoreClient noteStoreClient;
    final static String NOTE_START = "<en-note>";
    final static String NOTE_CLOSE = "</en-note>";
    final static String TODO_TAG_START = "<en-todo>";
    final static String TODO_TAG_CLOSE = "</en-todo>";
    final static String DIV = "<div>";
    final static String DIV_END = "</div>";
    final static String NEW_LINE = "<div><br clear=\"none\"/></div>";

    public ReadUserNotes() {
        guidList = new ArrayList();
        notesTitle = new HashMap();
        notesData = new HashMap();
        noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();

    }

    private void queryNotesGuid() throws EDAMUserException, EDAMSystemException, TException, EDAMNotFoundException {

        NoteFilter filter = new NoteFilter();
        NoteList noteList = noteStoreClient.findNotes(filter, 0, 10);
        if (noteList != null) {
            List<Note> listOfNotes = noteList.getNotes();

            for (Note note : listOfNotes) {
                guidList.add(note.getGuid());
                notesTitle.put(note.getGuid(), note.getTitle());

            }
        }
    }

    private void queryAndProcessNotesRawData() throws EDAMUserException, EDAMSystemException, TException, EDAMNotFoundException {

        for (String guid : guidList) {
            String noteRawData = noteStoreClient.getNoteContent(guid);
            String noteData = noteRawData.substring(noteRawData.indexOf(NOTE_START));
            noteData = noteData.replaceAll(NOTE_START, "");
            noteData = noteData.replaceAll(NOTE_CLOSE, "");
            noteData = noteData.replaceAll(TODO_TAG_START, "");
            noteData = noteData.replaceAll(TODO_TAG_CLOSE, "");
            noteData = noteData.replaceAll(NEW_LINE, "");
            noteData = noteData.replaceAll(DIV, "");
            noteData = noteData.replaceAll(DIV_END, ",");
            notesData.put(guid, noteData);

        }
    }

    public void writeNotesToDB(SQLiteDatabase db, Activity activity) throws Exception {

        try {
            queryNotesGuid();
            queryAndProcessNotesRawData();
            for (String guid : guidList) {
                String note_title = (String) notesTitle.get(guid);
                if(shouldSkipEntry(activity,guid,note_title))
                    continue;
                if (TitleClassifier.isTitleGrocery(note_title)) {
                   writeToDB(db,note_title,guid,"grocery_or_supermarket",(String)notesData.get(guid));
                } else if (TitleClassifier.isNoteRequired(note_title)) {
                    String content = (String) notesData.get(guid);
                    String[] items = content.split(",");
                    Map<String, String> itemMap = new HashMap();
                    for (String item : items) {
                        String category = ContentClassifier.getCategory(item, activity);
                        String val = itemMap.get(category);
                        if (val == null) {
                            itemMap.put(category, item);
                        } else {
                            val += ", " + item;
                            itemMap.put(category, val);
                        }
                    }
                    for (Map.Entry<String, String> entry : itemMap.entrySet()) {
                        String category = entry.getKey();
                        String mappedItems = entry.getValue();
                        writeToDB(db, note_title, guid, category, mappedItems);
                    }
                }
            }
        } catch (Exception e) {
            db.close();
            throw new Exception("Error while retrieving data from evernote");

        }
        db.close();
    }



    private void writeToDB(SQLiteDatabase db, String note_title, String guid, String category, String mappedItems) {
        ContentValues values = new ContentValues();
        values.put(DBContract.NotesEntry.NOTE_ID,  guid);
        values.put(DBContract.NotesEntry.COLUMN_NAME_TITLE, "Evernote: " + note_title);
        values.put(DBContract.NotesEntry.COLUMN_NOTE_DATE, (String) notesTitle.get(guid));
        values.put(DBContract.NotesEntry.COLUMN_CATEGORY, category);
        values.put(DBContract.NotesEntry.COLUMN_CONTENT, mappedItems);
        db.insert(
                DBContract.NotesEntry.TABLE_NAME,
                null,
                values);
    }
    private boolean shouldSkipEntry(Activity activity, String guid, String note_title) {
        boolean shouldSkip = false;
        DeleteEntriesDBHelper dbHelper = new DeleteEntriesDBHelper(activity);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM "+ DBContract.DeletedEntries.TABLE_NAME+" WHERE "+
                DBContract.DeletedEntries.COLUMN_NOTE_ID + "= '" + guid + "' AND "+
                DBContract.DeletedEntries.COLUMN_NOTE_NAME +" = '"+
                note_title+"'", null);

        if(c.getCount()>0)
            shouldSkip = true;
        c.close();
        db.close();
        return shouldSkip;
    }
}
