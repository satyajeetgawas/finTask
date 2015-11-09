package com.appmagnet.fintaskanyplace.core;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.appmagnet.fintaskanyplace.activity.MainActivity;
import com.appmagnet.fintaskanyplace.db.DBContract;
import com.appmagnet.fintaskanyplace.db.NotesDBHelper;
import com.appmagnet.fintaskanyplace.evernote.ReadUserNotes;
import com.appmagnet.fintaskanyplace.googleservices.GoogleGalendarApiTask;
import com.appmagnet.fintaskanyplace.util.Constants;
import com.appmagnet.fintaskanyplace.util.Util;
import com.evernote.client.android.EvernoteSession;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by satyajeet on 11/9/2015.
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
            if (!"0".equals(Util.getSettings(activity, Constants.PREF_ACCOUNT_NAME)))
                refreshCalendarData();
        }catch (IOException e){
            cancel(true);
        }
        return null;
    }

    private void refreshCalendarData() throws IOException {

        GoogleAccountCredential mCredential = GoogleAccountCredential.usingOAuth2(
                activity.getApplicationContext(), Arrays.asList(Constants.SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(Util.getSettings(activity, Constants.PREF_ACCOUNT_NAME));
        String account  = Util.getSettings(activity, Constants.PREF_ACCOUNT_NAME);
        if("0".equals(account))
            return;
        GoogleGalendarApiTask syncCalendarTask = new GoogleGalendarApiTask(mCredential);
        List<String> eventList = syncCalendarTask.getDataFromApi();
        for(String event:eventList){
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DBContract.NotesEntry.COLUMN_NAME_TITLE, "google calendar");
            values.put(DBContract.NotesEntry.COLUMN_CONTENT, event);
            long newRowId;
            newRowId = db.insert(
                    DBContract.NotesEntry.TABLE_NAME,
                    null,
                    values);

        }
    }

    private void refreshEvernoteData() {
        ReadUserNotes userNotes = new ReadUserNotes();


    }

    @Override
    protected void onPostExecute(Void result) {
        if(activity instanceof MainActivity)
            ((MainActivity)activity).showPostDBWrite();
    }
}
