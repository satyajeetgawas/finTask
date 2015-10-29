package com.appmagnet.fintaskanyplace.backgroundtasks;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.appmagnet.fintaskanyplace.initializer.LocationHandler;
import com.appmagnet.fintaskanyplace.yelp.YelpAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by satyajeet on 10/21/2015.
 */
public class BackgroundTaskReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
        //Acquire the lock
        wl.acquire();

        //You can do the processing here update the widget/remote views.
        Bundle extras = intent.getExtras();
        StringBuilder msgStr = new StringBuilder();



        final String[] yelpQuery = {""};
        Thread thread = new Thread(new Runnable(){
            @Override
                         public void run() {
                YelpAPI yelpApi = new YelpAPI();
                LocationHandler lh = LocationHandler.getInstance();
                if(!lh.isLocationEnabled()){
                    return;
                }
                String location = lh.getLocationMap().get(LocationHandler.LATITUDE).toString();
                location +=",";
                location += lh.getLocationMap().get(LocationHandler.LONGITUDE).toString();
                yelpQuery[0] = yelpApi.searchForBusinessesByLocation("groceries", location);
            }
        });
        thread.start();
        while(thread.isAlive());
        ArrayList<String> businessNames=null;
        try{
            JSONObject json = new JSONObject(yelpQuery[0]);
            JSONArray businesses = json.getJSONArray("businesses");
            businessNames = new ArrayList<String>(businesses.length());
            for (int i = 0; i < businesses.length(); i++) {
                JSONObject business = businesses.getJSONObject(i);
                Double rating  = business.getDouble("rating");
                if(rating>3.5)
                    businessNames.add(business.getString("name")+", "+rating.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    if(businessNames!=null){
        String businessesCon =  TextUtils.join("\n", businessNames);
        Toast.makeText(context, businessesCon, Toast.LENGTH_LONG).show();
    }




        //Release the lock
        wl.release();
    }

    public void doInBackground(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, BackgroundTaskReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        //After after 30 seconds
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 10, pi);
    }
}
