package com.appmagnet.fintaskanyplace.backgroundtasks;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.widget.Toast;

import com.appmagnet.fintaskanyplace.yelp.YelpAPI;

import java.text.Format;
import java.text.SimpleDateFormat;
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
                yelpQuery[0] = yelpApi.searchForBusinessesByLocation("groceries", "Atlanta,GA");
            }
        });
        thread.start();
        while(thread.isAlive());

        Toast.makeText(context, yelpQuery[0], Toast.LENGTH_LONG).show();

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
