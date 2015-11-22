package com.appmagnet.fintaskanyplace.googleservices;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.appmagnet.fintaskanyplace.db.DBContract;
import com.appmagnet.fintaskanyplace.util.Constants;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by satyajeet and anmol on 11/9/2015.
 */
public class GoogleGalendarApiTask  {
    private com.google.api.services.calendar.Calendar mService = null;


    public GoogleGalendarApiTask(GoogleAccountCredential credential) {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("finTaskAnyplace")
                .build();
    }


    /**
     * Fetch a list of the next 10 events from the primary calendar.
     * @return List of Strings describing returned events.
     * @throws IOException
     */
    public List<Event> getDataFromApi() throws IOException {
        // List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        List<String> eventStrings = new ArrayList<String>();
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, 100);
        DateTime maxDate = new DateTime(cal.getTimeInMillis());
        Events events = mService.events().list("#contacts@group.v.calendar.google.com")
                .setMaxResults(10)
                .setTimeMin(now)
                .setTimeMax(maxDate)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();
        return items;
    }


    public void writeToDB(SQLiteDatabase db) throws IOException {

        List<Event> eventList = getDataFromApi();
        for(Event event:eventList){

            ContentValues values = new ContentValues();
            values.put(DBContract.NotesEntry.COLUMN_NAME_TITLE, Constants.GOOGLE_CALENDAR);
            values.put(DBContract.NotesEntry.COLUMN_CONTENT, event.getSummary());
            values.put(DBContract.NotesEntry.COLUMN_NOTE_DATE,event.getStart().getDate().toString());
            values.put(DBContract.NotesEntry.COLUMN_CATEGORY,Constants.GOOGLE_CALENDAR_CATEGORY);
            values.put(DBContract.NotesEntry.COLUMN_SOURCE, Constants.GOOGLE_CALENDAR);
            db.insert(
                    DBContract.NotesEntry.TABLE_NAME,
                    null,
                    values);

        }
    }




}
