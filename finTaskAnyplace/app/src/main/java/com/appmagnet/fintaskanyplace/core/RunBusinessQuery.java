package com.appmagnet.fintaskanyplace.core;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.appmagnet.fintaskanyplace.R;
import com.appmagnet.fintaskanyplace.activity.MainActivity;
import com.appmagnet.fintaskanyplace.activity.NotificationReciever;
import com.appmagnet.fintaskanyplace.dataobjects.BusinessObject;
import com.appmagnet.fintaskanyplace.db.DBContract;
import com.appmagnet.fintaskanyplace.db.NotesDBHelper;
import com.appmagnet.fintaskanyplace.googleservices.GooglePlacesApi;
import com.appmagnet.fintaskanyplace.initializer.LocationHandler;
import com.appmagnet.fintaskanyplace.util.Constants;
import com.appmagnet.fintaskanyplace.util.Util;
import com.appmagnet.fintaskanyplace.yelp.YelpAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by satyajeet and anmol on 11/6/2015.
 */
public class RunBusinessQuery extends AsyncTask<Context, Void, HashMap<String, ArrayList>> {
    Context context;

    @Override
    protected HashMap<String , ArrayList> doInBackground(Context[] params) {
        context = params[0];
        return runTheSearchQuery();
    }

    @Override
    protected void onPostExecute(HashMap<String, ArrayList> mapOfBusinesses) {
         //for debuging later create a notification

        if ( mapOfBusinesses.size()>0) {
//           String businessesCon = TextUtils.join("\n", businessNames);
//           Toast.makeText(context, businessesCon, Toast.LENGTH_LONG).show();
            createNotification(mapOfBusinesses);
        }
    }

    private HashMap runTheSearchQuery() {

        LocationHandler lh = LocationHandler.getInstance();
        HashMap categoryBusMap = new HashMap<String, ArrayList>();

        if (lh != null && lh.isLocationEnabled()) {
            if (lh.getLocationMap().get(LocationHandler.LATITUDE) != null) {
                String location;
                location = lh.getLocationMap().get(LocationHandler.LATITUDE).toString();
                location += ",";
                location += lh.getLocationMap().get(LocationHandler.LONGITUDE).toString();
 //               location = "33.75,-84.39";
                 /*
            read the cached notes list and generate search terms, run the query  for individual
            terms and identify which task can be completed based on the user settings like radius
            and minimum rating
             */
                NotesDBHelper mDbHelper = new NotesDBHelper(context);
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                Cursor c = db.rawQuery("SELECT DISTINCT category FROM Notes", null);
                //String term = "grocery_or_supermarket"; //this term will be obtained from the saved(cached) file which is
                //generated or refreshed once daily

                if(c.moveToFirst()) {
                    do {
                        ArrayList<BusinessObject> listOfBusinesses = new ArrayList<BusinessObject>();
                        String term = c.getString(c.getColumnIndex(DBContract.NotesEntry.COLUMN_CATEGORY));
                        //if(Constants.UNCATEGORIZED.equals(term)) {
                        if (Boolean.parseBoolean(Util.getSettings(context, Constants.YELP_PREF))) {
                            YelpAPI yelpApi = new YelpAPI();
                            listOfBusinesses.addAll(yelpApi.searchForBusinessesByLocation(term, location));
                        }
                        if (Boolean.parseBoolean(Util.getSettings(context, Constants.GOOGLE_PLACES_PREF))) {
                            GooglePlacesApi googlePlaces = new GooglePlacesApi();
                            listOfBusinesses.addAll(googlePlaces.searchForBusinessesByLocation(term, location));
                        }
                        if(listOfBusinesses.size() > 0)
                            categoryBusMap.put(term, listOfBusinesses);
                    }   while(c.moveToNext());
                }
                }
            }
        return categoryBusMap;
    }

    private void createNotification(HashMap mapOfBusinessPlaces) {
        // Prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent(context, NotificationReciever.class);
        intent.putExtra(Constants.LIST_OF_PLACES,mapOfBusinessPlaces);
        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);

        // Build notification
        // Actions are just fake
        NotificationCompat.Builder noti = new NotificationCompat.Builder(context)
                .setContentTitle("finTaskAnyplace")
                .setContentText(context.getString(R.string.notification_msg)).setSmallIcon(R.drawable.evernote)
                .setContentIntent(pIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.setAutoCancel(true);
        notificationManager.notify(0, noti.build());
    }

}
