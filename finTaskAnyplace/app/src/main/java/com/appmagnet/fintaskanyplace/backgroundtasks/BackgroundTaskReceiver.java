package com.appmagnet.fintaskanyplace.backgroundtasks;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.appmagnet.fintaskanyplace.core.RunBusinessQuery;
import com.appmagnet.fintaskanyplace.util.Util;

/**
 * Created by satyajeet and anmol on 10/21/2015.
 */
public class BackgroundTaskReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(final Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
        //Acquire the lock
        wl.acquire();
        new RunBusinessQuery().execute(context);
        //Release the lock
        wl.release();
    }



    public void doInBackground(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, BackgroundTaskReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), Util.getRefreshRate(context), pi);
    }


}
