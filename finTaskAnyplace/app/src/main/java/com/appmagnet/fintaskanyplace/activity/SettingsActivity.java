package com.appmagnet.fintaskanyplace.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.appmagnet.fintaskanyplace.R;
import com.appmagnet.fintaskanyplace.util.Util;
import com.evernote.client.android.EvernoteSession;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Created by satyajeet on 11/6/2015.
 */
public class SettingsActivity extends AppCompatActivity {
    private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX;
    private static final boolean SUPPORT_APP_LINKED_NOTEBOOKS = true;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Switch evernoteSwitch = (Switch) findViewById(R.id.switch_evernote);
        evernoteSwitch.setChecked(false);
        if(EvernoteSession.getInstance()!=null)
            //evernoteSwitch.setChecked(EvernoteSession.getInstance().isLoggedIn());
            //if(!EvernoteSession.getInstance().isLoggedIn() )
        evernoteSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setSetting(Util.EVERNOTE_USER_PREF, isChecked);
                if(isChecked && !EvernoteSession.getInstance().isLoggedIn())
                    evernoteLogin();
            }
        });

        Switch googleCalendarSwitch = (Switch) findViewById(R.id.switch_google_calendar);
        googleCalendarSwitch.setChecked(isGooglePlayServicesAvailable());
        googleCalendarSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setSetting(Util.GOOGLE_CALENDAR_PREF, isChecked);
            }
        });

        Switch yelpApiSwitch = (Switch) findViewById(R.id.switch_yelp);
        yelpApiSwitch.setChecked(Boolean.parseBoolean(Util.getSettings(this, Util.YELP_PREF)));
        yelpApiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setSetting(Util.YELP_PREF,isChecked);
            }
        });

        Switch googlePlacesSwitch = (Switch) findViewById(R.id.switch_google_places);
        googlePlacesSwitch.setChecked(Boolean.parseBoolean(Util.getSettings(this, Util.GOOGLE_PLACES_PREF)));
        googlePlacesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setSetting(Util.GOOGLE_PLACES_PREF,isChecked);
            }
        });
    }

    private void evernoteLogin() {
        EvernoteSession.getInstance().authenticate(this);
    }

    private void setSetting(String key,boolean isChecked) {
        Util.setUserSetting(this,key,String.valueOf(isChecked));
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        return true;
    }


    private boolean isGooglePlayServicesAvailable() {
        final int connectionStatusCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            return false;
        } else if (connectionStatusCode != ConnectionResult.SUCCESS ) {
            return false;
        }
        return true;
    }
}
