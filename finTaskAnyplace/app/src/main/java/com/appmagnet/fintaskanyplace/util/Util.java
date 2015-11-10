package com.appmagnet.fintaskanyplace.util;

import android.app.Activity;
import android.content.SharedPreferences;

import com.appmagnet.fintaskanyplace.activity.MainActivity;
import com.evernote.client.android.EvernoteSession;

/**
 * created by satyajeet and anmol
 */
public final class Util {


    private static String PROXIMITY_RADIUS_VAL = "2000";
    private static String REFRESH_RATE_VAL = "2000";

    private Util() {
        // no op
    }

    public static int getSearchLimit() {
        return Constants.SEARCH_LIMIT;
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
        rate = rate * 1000 * 60;
        //REFRESH_RATE = 1000*30;
        //return REFRESH_RATE;
        return rate;
    }

    public static String getSettings(Activity activity,String key){
        SharedPreferences settings = activity.getSharedPreferences(Constants.USER_SETTING, 0);
        return settings.getString(key,"0");
    }

    public static void setUserSetting(Activity activity,String key,String value){
        SharedPreferences settings = activity.getSharedPreferences(Constants.USER_SETTING, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key,value);
        editor.commit();
    }

    public static void initializeUserSettings(MainActivity mainActivity) {
        SharedPreferences settings = mainActivity.getSharedPreferences(Constants.USER_SETTING, 0);
        PROXIMITY_RADIUS_VAL = settings.getString(Constants.PROXIMITY_RADIUS, "2000");
        REFRESH_RATE_VAL = settings.getString(Constants.REFRESH_RATE, "2000");
    }
}
