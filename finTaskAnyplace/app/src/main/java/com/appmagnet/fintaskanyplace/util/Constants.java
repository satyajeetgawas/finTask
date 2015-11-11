package com.appmagnet.fintaskanyplace.util;

import com.google.api.services.calendar.CalendarScopes;

/**
 * Created by satyajeet and anmol on 11/9/2015.
 */
public interface Constants {


    String[] SCOPES = { CalendarScopes.CALENDAR_READONLY };
    String PREF_ACCOUNT_NAME = "accountName";
    String USER_SETTING = "UserSetting" ;
    String YELP_PREF = "YELP_PREF";
    String GOOGLE_PLACES_PREF = "GOOGLE_PLACES_PREF";
    String PROXIMITY_RADIUS = "PROXIMITY_RADIUS";
    String REFRESH_RATE = "REFRESH_RATE";
    String LIST_OF_PLACES = "LIST_OF_PLACES" ;
    //public static long REFRESH_RATE = 30000;
}
