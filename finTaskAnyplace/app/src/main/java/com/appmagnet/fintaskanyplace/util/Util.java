package com.appmagnet.fintaskanyplace.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.View;

import com.appmagnet.fintaskanyplace.R;
import com.appmagnet.fintaskanyplace.activity.MainActivity;
import com.appmagnet.fintaskanyplace.dataobjects.NoteObject;
import com.appmagnet.fintaskanyplace.db.DBContract;
import com.appmagnet.fintaskanyplace.db.DeleteEntriesDBHelper;
import com.evernote.client.android.EvernoteSession;

import java.util.HashMap;
import java.util.List;

/**
 * created by satyajeet and anmol
 */
public final class Util {


    private static int PROXIMITY_RADIUS_VAL = 2;
    private static int REFRESH_RATE_HOURS = 4;
    private static String SEARCH_LIMIT = "10";
    public static List<String> listDataHeader;
    public static HashMap<String, List<NoteObject>> listDataChild;
    private static HashMap<String,Integer> colorMap;
    private static HashMap<String,String> displayName;
    private static Application applicationContext;


    public static String getSearchLimit() {
        return SEARCH_LIMIT;
    }

    public static String getRadius(Context context){
        String radius_string = getSettings(context,Constants.PROXIMITY_RADIUS);
        int radiusInMiles = PROXIMITY_RADIUS_VAL;
        long radius;
        if(radius_string != null)
            radiusInMiles = Integer.parseInt(radius_string);
        radius = radiusInMiles*1609;
        String radi = String.valueOf(radius);
        //REFRESH_RATE = 1000*30;
        //return REFRESH_RATE;
        return radi;
        //return PROXIMITY_RADIUS_VAL;
    }


    public static long getRefreshRate(Context context) {
        String rate = getSettings(context,Constants.REFRESH_RATE);
        int timeInHour = REFRESH_RATE_HOURS ;
        long refreshRate;
        if(rate!=null)
            timeInHour = Integer.parseInt(rate);
        refreshRate = timeInHour*1000*60*60*60;
        //REFRESH_RATE = 1000*30;
        //return REFRESH_RATE;
        return refreshRate;
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

    public static boolean shouldSkipEntry(Activity activity, String guid, String note_title,String content) {
        boolean shouldSkip = false;
        DeleteEntriesDBHelper dbHelper = new DeleteEntriesDBHelper(activity);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DBContract.DeletedEntries.TABLE_NAME + " WHERE " +
                DBContract.DeletedEntries.COLUMN_NOTE_ID + "= '" + guid + "' AND " +
                DBContract.DeletedEntries.COLUMN_NOTE_NAME + " = '" +
                note_title + "'"+" AND "+ DBContract.DeletedEntries.COLUMN_NOTE_CONTENT +"= '"+
                content+"'";
        Cursor c = db.rawQuery(query, null);

        if(c.getCount()>0)
            shouldSkip = true;
        c.close();
        db.close();
        return shouldSkip;
    }

    public static int getHeaderBackgroundColor(View convertView, String header){
        if(colorMap==null){
            colorMap  = new HashMap<>();
//            colorMap.put("grocery_or_supermarket", convertView.getResources().getColor(R.color.grocery_or_supermarket));
//            colorMap.put("hardware_store",convertView.getResources().getColor(R.color.hardware_store));
//            colorMap.put("clothing_store",convertView.getResources().getColor(R.color.clothing_store));
//            colorMap.put(Constants.UNCATEGORIZED,convertView.getResources().getColor(R.color.uncategorized));
//            colorMap.put(Constants.GOOGLE_CALENDAR_CATEGORY,convertView.getResources().getColor(R.color.google_calendar));

        }
        if(colorMap.get(header) == null)
            return Color.WHITE;
        return colorMap.get(header);
    }

    public static Application getApplicationContext() {
        return applicationContext;
    }

    public static void setApplicationContext(Application applicationContext) {
        Util.applicationContext = applicationContext;
    }

    public static String getHeaderDisplayName(String headerTitle) {
        if(displayName==null) {
            displayName = new HashMap<>();
            displayName.put("grocery_or_supermarket", "Grocery");
            displayName.put("clothing_store", "Clothing");
            displayName.put("electronics_store", "Electronic");
            displayName.put("hardware_store", "Hardware");
            displayName.put("laundry", "Laundry");
        }
        if(displayName.get(headerTitle)==null)
            return headerTitle;
        return displayName.get(headerTitle);
        }

}
