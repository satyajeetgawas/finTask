package com.appmagnet.fintaskanyplace.util;

import android.app.Activity;
import android.content.SharedPreferences;

import com.appmagnet.fintaskanyplace.activity.LoginActivity;
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
    private static String PROXIMITY_RADIUS = "5000";
    private static long REFRESH_RATE;
    public static String EVERNOTE_USER_PREF = "EVERNOTE_USER_PREF";
    private Util() {
        // no op
    }

    public static int getSearchLimit() {
        return SEARCH_LIMIT;
    }

    public static void logout(Activity activity) {
        EvernoteSession.getInstance().logOut();
        LoginActivity.launch(activity);
        activity.finish();
    }

    public static String getRadius(){
        return PROXIMITY_RADIUS;
    }


    public static long getRefreshRate() {
        REFRESH_RATE = 1000*30;
        return REFRESH_RATE;
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
}
