package com.appmagnet.fintaskanyplace.notes;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.appmagnet.fintaskanyplace.core.ContentClassifier;
import com.appmagnet.fintaskanyplace.core.TitleClassifier;
import com.appmagnet.fintaskanyplace.db.DBContract;
import com.appmagnet.fintaskanyplace.util.Util;
import com.appmagnet.fintaskanyplace.wunderlist.WunderlistClient;
import com.appmagnet.fintaskanyplace.wunderlist.WunderlistException;
import com.appmagnet.fintaskanyplace.wunderlist.WunderlistNote;
import com.appmagnet.fintaskanyplace.wunderlist.WunderlistSession;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by satyajeet on 11/22/2015.
 */
public class WunderlistReadUserNotes {

    private List<String> listIds;
    private List<WunderlistNote> listOfNotes;
    private Map<String,WunderlistNote> mNotesMap;
    private WunderlistClient mWunderlistCLient;

    public WunderlistReadUserNotes() {
        listIds = new ArrayList<>();
        mNotesMap = new HashMap<>();
        mWunderlistCLient = WunderlistSession.getInstance().getWunderlistClient();

    }

    private void queryListOfIds() throws WunderlistException {
        listOfNotes = mWunderlistCLient.getListOfUserNotes();
        for(WunderlistNote note:listOfNotes){
            listIds.add(note.getId());
            mNotesMap.put(note.getId(), note);
        }


    }
    public void writeNotesToDB(SQLiteDatabase db, Activity activity) throws Exception{

        try {
            queryListOfIds();
            for (String id : listIds) {
                String noteTitle = mNotesMap.get(id).getTitle();

                if (TitleClassifier.isTitleGrocery(noteTitle)) {
                    if(Util.shouldSkipEntry(activity, id, noteTitle, (String) mNotesMap.get(id).getContent()))
                        continue;
                    String contents = (String)mNotesMap.get(id).getContent();
                    if(contents.endsWith(","))
                        contents = contents.substring(0,contents.length()-1);
                    contents = contents.replace(",",", ");
                    writeToDB(db,noteTitle,id,"grocery_or_supermarket",contents);

                } else if (TitleClassifier.isNoteRequired(noteTitle)) {
                    String content = (String) mNotesMap.get(id).getContent();
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
                        if(mappedItems.endsWith(","))
                            mappedItems = mappedItems.substring(0,mappedItems.length()-1);
                        if(Util.shouldSkipEntry(activity, id, noteTitle, mappedItems))
                            continue;
                        writeToDB(db, noteTitle, id, category, mappedItems);
                    }
                }
            }
        } catch (Exception e) {
            db.close();
            throw new Exception("Error while retrieving data from wunderlist");

        }
        db.close();
    }



    private void writeToDB(SQLiteDatabase db, String note_title, String guid, String category, String mappedItems) {
        ContentValues values = new ContentValues();
        values.put(DBContract.NotesEntry.NOTE_ID,  guid);
        values.put(DBContract.NotesEntry.COLUMN_SOURCE,"Wunderlist");
        values.put(DBContract.NotesEntry.COLUMN_NAME_TITLE, note_title);
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(((WunderlistNote) mNotesMap.get(guid)).getCreated());
        values.put(DBContract.NotesEntry.COLUMN_NOTE_DATE, formattedDate);
        values.put(DBContract.NotesEntry.COLUMN_CATEGORY, category);
        values.put(DBContract.NotesEntry.COLUMN_CONTENT, mappedItems);
        db.insert(
                DBContract.NotesEntry.TABLE_NAME,
                null,
                values);
    }


}
