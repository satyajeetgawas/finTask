package com.appmagnet.fintaskanyplace.core;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.appmagnet.fintaskanyplace.R;
import com.appmagnet.fintaskanyplace.activity.NotificationReciever;
import com.appmagnet.fintaskanyplace.dataobjects.BusinessObject;
import com.appmagnet.fintaskanyplace.googleservices.GooglePlacesApi;
import com.appmagnet.fintaskanyplace.initializer.LocationHandler;
import com.appmagnet.fintaskanyplace.util.Constants;
import com.appmagnet.fintaskanyplace.util.Util;
import com.appmagnet.fintaskanyplace.yelp.YelpAPI;

import java.util.ArrayList;

/**
 * Created by satyajeet and anmol on 11/6/2015.
 */
public class RunBusinessQuery extends AsyncTask<Context, Void, ArrayList<BusinessObject>> {
    Context context;

    @Override
    protected ArrayList<BusinessObject> doInBackground(Context[] params) {
        context = params[0];
        return runTheSearchQuery();
    }

    @Override
    protected void onPostExecute(ArrayList<BusinessObject> listOfBusinesses) {
        ArrayList<String> businessNames = new ArrayList<>();  //for debuging later create a notification
        for (BusinessObject busObj : listOfBusinesses) {
            businessNames.add(busObj.getBusinessName() + ", " + busObj.getBusinessRating());
        }
        if (businessNames != null) {
//           String businessesCon = TextUtils.join("\n", businessNames);
//           Toast.makeText(context, businessesCon, Toast.LENGTH_LONG).show();
            createNotification(listOfBusinesses);
        }
    }

    private ArrayList<BusinessObject> runTheSearchQuery() {

        LocationHandler lh = LocationHandler.getInstance();
        ArrayList<BusinessObject> listOfBusinesses = new ArrayList<BusinessObject>();

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
                String term = "grocery_or_supermarket"; //this term will be obtained from the saved(cached) file which is
                //generated or refreshed once daily
                if(Boolean.parseBoolean(Util.getSettings(context, Constants.YELP_PREF))){
                    YelpAPI yelpApi = new YelpAPI();
                    listOfBusinesses.addAll(yelpApi.searchForBusinessesByLocation(term, location));
                }
                if(Boolean.parseBoolean(Util.getSettings(context, Constants.GOOGLE_PLACES_PREF))){
                    GooglePlacesApi googlePlaces = new GooglePlacesApi();
                    listOfBusinesses.addAll(googlePlaces.searchForBusinessesByLocation(term, location));
                }
            }
        }
        return listOfBusinesses;
    }

    private void createNotification(ArrayList listOfBusinessPlaces) {
        // Prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent(context, NotificationReciever.class);
        intent.putExtra(Constants.LIST_OF_PLACES,listOfBusinessPlaces);
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
