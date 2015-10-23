package com.appmagnet.fintaskanyplace.evernote;

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
import java.util.List;

/**
 * Created by satyajeet on 10/21/2015.
 */
public class ReadUserNotes {

    private List<String> guidList;
    private List<String> notesRawData;
    private List<String> notesData;
    private EvernoteNoteStoreClient noteStoreClient;
    final static String  NOTE_START         = "<en-note>";
    final static String NOTE_CLOSE = "</en-note>";
    final static String TODO_TAG_START = "<en-todo>";
    final static String TODO_TAG_CLOSE = "</en-todo>";
    final static String DIV = "<div>";
    final static String DIV_END = "</div>";

    public ReadUserNotes() {
        guidList = new ArrayList<String>();
        notesRawData = new ArrayList<String>();
        notesData = new ArrayList<String>();
        noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();

    }

    public boolean queryNotesGuid() {
        try {
            NoteFilter filter = new NoteFilter();
            NoteList noteList = noteStoreClient.findNotes(filter, 0, 10);
            if (noteList != null) {
                List<Note> listOfNotes = noteList.getNotes();

                for (Note note : listOfNotes) {
                    guidList.add(note.getGuid());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean queryNotesRawData() {
        try {
            for (String guid : guidList) {
                notesRawData.add(noteStoreClient.getNoteContent(guid));
            }

        } catch (EDAMNotFoundException | EDAMSystemException | EDAMUserException | TException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public List<String> processRawNoteData(){
        for(String noteRaw : notesRawData){
            String noteData = noteRaw.substring(noteRaw.indexOf(NOTE_START));
            noteData = noteData.replaceAll(NOTE_START,"");
            noteData = noteData.replaceAll(NOTE_CLOSE,"");
            noteData = noteData.replaceAll(TODO_TAG_START,"");
            noteData = noteData.replaceAll(TODO_TAG_CLOSE,"");
            noteData = noteData.replaceAll(DIV,"");
            noteData = noteData.replaceAll(DIV_END,"");
            notesData.add(noteData);

        }
        return notesData;

    }

    public List<String> allNotesData(){
        return notesData;
    }


}
