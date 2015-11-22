package com.appmagnet.fintaskanyplace.core;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.appmagnet.fintaskanyplace.activity.MainActivity;
import com.appmagnet.fintaskanyplace.db.NotesDBHelper;
import com.appmagnet.fintaskanyplace.notes.EvernoteReadUserNotes;
import com.appmagnet.fintaskanyplace.googleservices.GoogleGalendarApiTask;
import com.appmagnet.fintaskanyplace.notes.WunderlistReadUserNotes;
import com.appmagnet.fintaskanyplace.util.Constants;
import com.appmagnet.fintaskanyplace.util.Util;
import com.appmagnet.fintaskanyplace.wunderlist.WunderlistSession;
import com.evernote.client.android.EvernoteSession;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by satyajeet and anmol on 11/9/2015.
 */
public class RefreshCachedNotesDB extends AsyncTask<Void,Void,Void> {

    private Activity activity;
    private NotesDBHelper mDbHelper;
    public RefreshCachedNotesDB(Activity a){
        this.activity = a;
        mDbHelper = new NotesDBHelper(activity.getApplicationContext());
        mDbHelper.onUpgrade(mDbHelper.getWritableDatabase(), NotesDBHelper.DATABASE_VERSION,NotesDBHelper.DATABASE_VERSION);
    }

    @Override
    protected Void doInBackground(Void... params) {
       try {
            if (EvernoteSession.getInstance()!=null && EvernoteSession.getInstance().isLoggedIn())
                refreshEvernoteData();
           if(WunderlistSession.getInstance()
                   !=null && WunderlistSession.getInstance().isLoggedIn())
               refreshWunderlistData();
            if (!"0".equals(Util.getSettings(activity, Constants.PREF_ACCOUNT_NAME)))
                refreshCalendarData();
        }catch (Exception e){
           cancel(true);
        }
        return null;
    }

    private void refreshWunderlistData() throws Exception {
        WunderlistReadUserNotes wunderlistNotes = new WunderlistReadUserNotes();
        SQLiteDatabase db  = mDbHelper.getWritableDatabase();
        wunderlistNotes.writeNotesToDB(db,activity);
        db.close();

    }

    private void refreshCalendarData() throws IOException {

        GoogleAccountCredential mCredential = GoogleAccountCredential.usingOAuth2(
                activity.getApplicationContext(), Arrays.asList(Constants.SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(Util.getSettings(activity, Constants.PREF_ACCOUNT_NAME));
        GoogleGalendarApiTask syncCalendarTask = new GoogleGalendarApiTask(mCredential);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        syncCalendarTask.writeToDB(db);
        db.close();

    }

    private void refreshEvernoteData() throws Exception {
      EvernoteReadUserNotes userNotes = new EvernoteReadUserNotes();
        SQLiteDatabase db  = mDbHelper.getWritableDatabase();
        userNotes.writeNotesToDB(db,activity);
        db.close();
    }

    @Override
    protected void onPostExecute(Void result) {
        if(activity instanceof MainActivity)
            ((MainActivity)activity).showPostDBWrite();
    }
}
