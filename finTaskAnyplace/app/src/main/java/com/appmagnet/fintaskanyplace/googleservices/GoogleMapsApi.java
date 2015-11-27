package com.appmagnet.fintaskanyplace.googleservices;

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


/**
 * Created by anmolgupta on 11/27/15.
 */
public class GoogleMapsApi {
    public String getDistanceFromUser(String user_loc, String dest_loc) {
        StringBuilder urlString = new StringBuilder(
                "https://maps.googleapis.com/maps/api/distancematrix/json?");
        urlString.append("&origin=" + user_loc);
        urlString.append("&destination=" + dest_loc);
        urlString.append("&units=imperial");
        urlString.append("&sensor=false");
        //  urlString.append("&opennow");
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

        return getDistanceFromJson(jsonResponse);
    }

    private String getDistanceFromJson(String jsonResponse) {
        String Distance = "";
        ArrayList<String> listDistances;
        JSONObject json = null;
        JSONArray results = new JSONArray();
        try {
            json = new JSONObject(jsonResponse);
            results = json.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
                JSONObject distance = results.getJSONObject(0);
                Distance = distance.getJSONObject("distance").getString("text");
                //busProp.put(BusinessObject.ITEMS,items)
            } catch (JSONException e) {
                e.printStackTrace();
            }

        return Distance;
    }
}
