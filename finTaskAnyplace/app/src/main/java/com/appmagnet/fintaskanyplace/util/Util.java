package com.appmagnet.fintaskanyplace.util;

import android.app.Activity;
import android.content.SharedPreferences;

import com.appmagnet.fintaskanyplace.activity.MainActivity;
import com.evernote.client.android.EvernoteSession;

/**
 * @author rwondratschek
 */
public final class Util {



    public static final int SEARCH_LIMIT = 10;
    private static final String USER_SETTING = "UserSetting" ;
    public static final String GOOGLE_CALENDAR_PREF = "GOOGLE_CALENDAR_PREF";
    public static final String YELP_PREF = "YELP_PREF";
    public static final String GOOGLE_PLACES_PREF = "GOOGLE_PLACES_PREF";
    public static final String PROXIMITY_RADIUS = "PROXIMITY_RADIUS";
    private static String PROXIMITY_RADIUS_VAL = "2000";
    public static final String REFRESH_RATE = "REFRESH_RATE";
    private static String REFRESH_RATE_VAL = "2000";
    //public static long REFRESH_RATE = 30000;
    public static String EVERNOTE_USER_PREF = "EVERNOTE_USER_PREF";
    private Util() {
        // no op
    }

    public static int getSearchLimit() {
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
        rate = rate * 1000 * 60;
        //REFRESH_RATE = 1000*30;
        //return REFRESH_RATE;
        return rate;
    }

    public static String getSettings(Activity activity,String key){
        SharedPreferences settings = activity.getSharedPreferences(USER_SETTING, 0);
        return settings.getString(key,"0");
    }

    public static void setUserSetting(Activity activity,String key,String value){
        SharedPreferences settings = activity.getSharedPreferences(USER_SETTING, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key,value);
        editor.commit();
    }

    public static void initializeUserSettings(MainActivity mainActivity) {
        SharedPreferences settings = mainActivity.getSharedPreferences(USER_SETTING, 0);
        PROXIMITY_RADIUS_VAL = settings.getString(PROXIMITY_RADIUS, "2000");
        REFRESH_RATE_VAL = settings.getString(REFRESH_RATE, "2000");
    }
}
