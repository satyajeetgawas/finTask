package com.appmagnet.fintaskanyplace.core;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

import com.appmagnet.fintaskanyplace.R;
import com.appmagnet.fintaskanyplace.activity.NotificationReciever;
import com.appmagnet.fintaskanyplace.activity.searchedResults;
import com.appmagnet.fintaskanyplace.dataobjects.BusinessObject;
import com.appmagnet.fintaskanyplace.db.DBContract;
import com.appmagnet.fintaskanyplace.db.NotesDBHelper;
import com.appmagnet.fintaskanyplace.googleservices.GooglePlacesApi;
import com.appmagnet.fintaskanyplace.initializer.LocationHandler;
import com.appmagnet.fintaskanyplace.ui.SearchNearbyBtnListener;
import com.appmagnet.fintaskanyplace.util.Constants;
import com.appmagnet.fintaskanyplace.util.Util;
import com.appmagnet.fintaskanyplace.yelp.YelpAPI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by satyajeet and anmol on 11/6/2015.
 */
public class RunBusinessQuery extends AsyncTask<Object, Void, HashMap<String, ArrayList>> {
    Context context;
    String category;
    boolean isForNotification = true;
    boolean skipGoogle = false;
    private SearchNearbyBtnListener listener;


    @Override
    protected HashMap<String , ArrayList> doInBackground(Object[] params) {
        context = (Context)params[0];
        isForNotification = true;
        if(params.length>1){
            isForNotification = false;
            category = (String)params[1];
            skipGoogle = (boolean)params[2];
            listener = (SearchNearbyBtnListener)params[3];
        }


        return runTheSearchQuery();
    }

    @Override
    protected void onPostExecute(HashMap<String,ArrayList> mapOfBusinesses) {
         //for debuging later create a notification

        if ( mapOfBusinesses.size()>0 && isForNotification) {
            createNotification(mapOfBusinesses);
        }else if(!isForNotification){
            if(listener!=null)
                listener.dismissProgress();
            if(mapOfBusinesses.get(category)!=null && context !=null){
                Intent intent = new Intent(context.getApplicationContext(), searchedResults.class);
                intent.putParcelableArrayListExtra(Constants.AFTER_NOTIFICATION_LIST, mapOfBusinesses.get(category));
                intent.putExtra(Constants.CONTENTS_STRING, category);
                context.startActivity(intent);
            }

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


                if(!isForNotification){
                    ArrayList<BusinessObject> listOfBusinesses = new ArrayList<BusinessObject>();
                    if (Boolean.parseBoolean(Util.getSettings(context, Constants.YELP_PREF))) {
                        YelpAPI yelpApi = new YelpAPI();
                        listOfBusinesses.addAll(yelpApi.searchForBusinessesByLocation(category, location));
                    }
                    if (Boolean.parseBoolean(Util.getSettings(context, Constants.GOOGLE_PLACES_PREF)) && !skipGoogle) {
                        GooglePlacesApi googlePlaces = new GooglePlacesApi();
                        listOfBusinesses.addAll(googlePlaces.searchForBusinessesByLocation(category, location));
                    }

                    if (listOfBusinesses.size() > 0) {
                        filterBusiness(listOfBusinesses);
                        categoryBusMap.put(category, listOfBusinesses);
                    }
                    return categoryBusMap;
                }


                NotesDBHelper mDbHelper = new NotesDBHelper(context);
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                Cursor c = db.rawQuery("SELECT DISTINCT category FROM Notes", null);
                //String term = "grocery_or_supermarket"; //this term will be obtained from the saved(cached) file which is
                //generated or refreshed once daily

                if (c.moveToFirst()) {
                    do {
                        ArrayList<BusinessObject> listOfBusinesses = new ArrayList<BusinessObject>();
                        String term = c.getString(c.getColumnIndex(DBContract.NotesEntry.COLUMN_CATEGORY));
                        //String items = c.getString(c.getColumnIndex(DBContract.NotesEntry.COLUMN_CONTENT));

                        if (Constants.UNCATEGORIZED.equals(term))
                            continue;
                        if (Boolean.parseBoolean(Util.getSettings(context, Constants.YELP_PREF))) {
                            YelpAPI yelpApi = new YelpAPI();
                            listOfBusinesses.addAll(yelpApi.searchForBusinessesByLocation(term, location));
                        }
                        if (Boolean.parseBoolean(Util.getSettings(context, Constants.GOOGLE_PLACES_PREF))) {
                            GooglePlacesApi googlePlaces = new GooglePlacesApi();
                            listOfBusinesses.addAll(googlePlaces.searchForBusinessesByLocation(term, location));
                        }

                        if (listOfBusinesses.size() > 0) {
                            filterBusiness(listOfBusinesses);
                            categoryBusMap.put(term, listOfBusinesses);
                        }

                    } while (c.moveToNext());

                }

                c.close();
                c = db.rawQuery("SELECT * FROM " + DBContract.NotesEntry.TABLE_NAME
                        + " WHERE " + DBContract.NotesEntry.COLUMN_CATEGORY
                        + " = '" + Constants.UNCATEGORIZED + "'", null);
                ArrayList<BusinessObject> listOfBusinesses = new ArrayList<BusinessObject>();
                if (c.moveToFirst()) {
                    do {

                        String ItemList = c.getString(c.getColumnIndex(DBContract.NotesEntry.COLUMN_CONTENT));
                        if (ItemList.endsWith(",")) {
                            int n = ItemList.lastIndexOf(",");
                            ItemList = ItemList.substring(0, n);
                        }
                        ArrayList<String> uncategorizedItems = new ArrayList<String>(Arrays.asList(ItemList.split(",")));
                        if (uncategorizedItems.size() > 0) {
                            for (String s : uncategorizedItems) {
                                if (s != null) {
                                    if (Boolean.parseBoolean(Util.getSettings(context, Constants.YELP_PREF))) {
                                        YelpAPI yelpApi = new YelpAPI();
                                        listOfBusinesses.addAll(yelpApi.searchForBusinessesByLocation(s, location));
                                    }
                                }
                            }
                        }
                    } while (c.moveToNext());
                    if (listOfBusinesses.size() > 0) {
                        filterBusiness(listOfBusinesses);
                        categoryBusMap.put(Constants.UNCATEGORIZED, listOfBusinesses);
                    }
                }
            }
        }

        return categoryBusMap;
    }

    private void filterBusiness(ArrayList<BusinessObject> listOfBusinesses) {
        for (int a = 0; a < listOfBusinesses.size(); a++) {
            for (int b = a + 1; b < listOfBusinesses.size(); b++) {
                if (listOfBusinesses.get(a).getBusinessName().equalsIgnoreCase(listOfBusinesses.get(b).getBusinessName())) {
                    Double longa = new Double(listOfBusinesses.get(a).getBusinessLongitude());
                    Double longb = new Double(listOfBusinesses.get(b).getBusinessLongitude());
                    Double lata = new Double(listOfBusinesses.get(a).getBusinessLatitude());
                    Double latb = new Double(listOfBusinesses.get(b).getBusinessLatitude());
                    if (Math.abs(longa - longb) < 1 && Math.abs(lata - latb) < 1) {
                        listOfBusinesses.remove(b);
                        b--;
                    }
                }
            }
        }
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
