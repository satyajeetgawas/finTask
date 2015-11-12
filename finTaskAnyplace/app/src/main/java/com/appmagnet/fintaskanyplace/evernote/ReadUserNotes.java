package com.appmagnet.fintaskanyplace.evernote;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.appmagnet.fintaskanyplace.db.DBContract;
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
    final static String  NOTE_START         = "<en-note>";
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

    private void  queryNotesGuid() throws EDAMUserException, EDAMSystemException, TException, EDAMNotFoundException {

            NoteFilter filter = new NoteFilter();
            NoteList noteList = noteStoreClient.findNotes(filter, 0, 10);
            if (noteList != null) {
                List<Note> listOfNotes = noteList.getNotes();

                for (Note note : listOfNotes) {
                    guidList.add(note.getGuid());
                    notesTitle.put(note.getGuid(),note.getTitle());

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
            noteData = noteData.replaceAll(NEW_LINE,"");
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
                ContentValues values = new ContentValues();
                values.put(DBContract.NotesEntry.COLUMN_NAME_TITLE, (String) notesTitle.get(guid));
                values.put(DBContract.NotesEntry.COLUMN_CONTENT, (String) notesData.get(guid));
                db.insert(
                        DBContract.NotesEntry.TABLE_NAME,
                        null,
                        values);

            }
        } catch (Exception e) {
            throw new Exception("Error while retrieving data from evernote");

        }
    }

}
