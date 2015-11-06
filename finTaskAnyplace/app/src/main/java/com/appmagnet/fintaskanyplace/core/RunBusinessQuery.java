package com.appmagnet.fintaskanyplace.core;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

import com.appmagnet.fintaskanyplace.dataobjects.BusinessObject;
import com.appmagnet.fintaskanyplace.googleservices.GooglePlacesApi;
import com.appmagnet.fintaskanyplace.initializer.LocationHandler;
import com.appmagnet.fintaskanyplace.yelp.YelpAPI;

import java.util.ArrayList;

/**
 * Created by satyajeet on 11/6/2015.
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
            String businessesCon = TextUtils.join("\n", businessNames);
            Toast.makeText(context, businessesCon, Toast.LENGTH_LONG).show();
        }
    }

    private ArrayList<BusinessObject> runTheSearchQuery() {
        YelpAPI yelpApi = new YelpAPI();
        GooglePlacesApi googlePlaces = new GooglePlacesApi();
        LocationHandler lh = LocationHandler.getInstance();
        ArrayList<BusinessObject> listOfBusinesses = new ArrayList<BusinessObject>();

        if (lh != null && lh.isLocationEnabled()) {
            if (lh.getLocationMap().get(LocationHandler.LATITUDE) != null) {
                String location;
                location = lh.getLocationMap().get(LocationHandler.LATITUDE).toString();
                location += ",";
                location += lh.getLocationMap().get(LocationHandler.LONGITUDE).toString();

                 /*
            read the cached notes list and generate search terms, run the query  for individual
            terms and identify which task can be completed based on the user settings like radius
            and minimum rating
             */
                String term = "grocery_or_supermarket"; //this term will be obtained from the saved(cached) file which is
                //generated or refreshed once daily
                listOfBusinesses.addAll(yelpApi.searchForBusinessesByLocation(term, location));
                listOfBusinesses.addAll(googlePlaces.searchForBusinessesByLocation(term, location));
            }
        }
        return listOfBusinesses;
    }
}
