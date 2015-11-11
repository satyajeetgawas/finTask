package com.appmagnet.fintaskanyplace.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.appmagnet.fintaskanyplace.activity.MainActivity;
import com.evernote.client.android.EvernoteSession;

/**
 * created by satyajeet and anmol
 */
public final class Util {


    private static String PROXIMITY_RADIUS_VAL = "2000";
    private static String REFRESH_RATE_VAL = "2000";
    private static String SEARCH_LIMIT = "10";

    private Util() {
        // no op
    }

    public static String getSearchLimit() {
        return SEARCH_LIMIT;
    }

    public static void logout(Activity activity) {
        EvernoteSession.getInstance().logOut();
        activity.finish();
    }

    public static String getRadius(){
        return PROXIMITY_RADIUS_VAL;
    }


    public static long getRefreshRate() {
        long rate = new Long(REFRESH_RATE_VAL);
        rate = rate *  10;
        //REFRESH_RATE = 1000*30;
        //return REFRESH_RATE;
        return rate;
    }

    public static String getSettings(Context context,String key){
        SharedPreferences settings = context.getSharedPreferences(Constants.USER_SETTING, 0);
        return settings.getString(key,"0");
    }

    public static void setUserSetting(Activity activity,String key,String value){
        SharedPreferences settings = activity.getSharedPreferences(Constants.USER_SETTING, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key,value);
        editor.commit();
    }


}
