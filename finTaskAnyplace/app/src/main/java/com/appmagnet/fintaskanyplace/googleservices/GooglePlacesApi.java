package com.appmagnet.fintaskanyplace.googleservices;


import android.content.Context;

import com.appmagnet.fintaskanyplace.dataobjects.BusinessObject;
import com.appmagnet.fintaskanyplace.util.ApiKeys;
import com.appmagnet.fintaskanyplace.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by satyajeet and anmol on 11/5/2015.
 */
public class GooglePlacesApi {

    public ArrayList<BusinessObject> searchForBusinessesByLocation(String term, String location, Context context) {

        StringBuilder urlString = new StringBuilder(
                "https://maps.googleapis.com/maps/api/place/search/json?");
        urlString.append("&types=" + term);
        urlString.append("&location=" + location);
        urlString.append("&radius=" + Util.getRadius(context));
      //  urlString.append("&opennow");
        urlString.append("&sensor=false");
        urlString.append("&key=" + ApiKeys.GOOGLE_API_KEY);

        InputStream stream = null;
        try {
            URL url = new URL(urlString.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            stream = conn.getInputStream();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String jsonResponse = "";
        try {
            java.util.Scanner s = new java.util.Scanner(stream).useDelimiter("\\A");
           if(s.hasNext() )
               jsonResponse = s.next();
        }catch (NullPointerException e){

        }

        return getBusinessObjectsFromJson(jsonResponse,term, location);
    }

    private ArrayList<BusinessObject> getBusinessObjectsFromJson(String jsonResponse, String term, String user_location) {
        ArrayList<BusinessObject> listPlaces = null;
        JSONObject json = null;
        JSONArray results = new JSONArray();
        try {
            json = new JSONObject(jsonResponse);
            results = json.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        listPlaces = new ArrayList<BusinessObject>(results.length());
        for (int i = 0; i < results.length(); i++) {
            try {
                JSONObject business = results.getJSONObject(i);
                Map<String, String> busProp = new HashMap<String, String>();
                busProp.put(BusinessObject.NAME, business.getString("name"));
                busProp.put(BusinessObject.RATING, business.getString("rating"));
                busProp.put(BusinessObject.LATITUDE, business.getJSONObject("geometry").getJSONObject("location").getString("lat"));
                busProp.put(BusinessObject.LONGITUDE, business.getJSONObject("geometry").getJSONObject("location").getString("lng"));
                busProp.put(BusinessObject.CATEGORY,term);
                listPlaces.add(new BusinessObject(busProp));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (listPlaces == null)
            listPlaces = new ArrayList<BusinessObject>();
        return listPlaces;
    }
}
