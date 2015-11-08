package com.appmagnet.fintaskanyplace.activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
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
    private static float dX, dY;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        Switch evernoteSwitch = (Switch) findViewById(R.id.switch_evernote);
        evernoteSwitch.setChecked(false);
        evernoteSwitch.setChecked(EvernoteSession.getInstance()!=null && EvernoteSession.getInstance().isLoggedIn() );
        evernoteSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setSetting(Util.EVERNOTE_USER_PREF, isChecked);
                evernoteLogin(isChecked);
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

    private void evernoteLogin(boolean isChecked) {
        if(isChecked)
            EvernoteSession.getInstance().authenticate(this);
        else
            Util.logout(this);
//        Button evernoteButton = (Button) findViewById(R.id.logout_evernote);
//        evernoteButton.setEnabled(true);
//        evernoteButton.setClickable(true);
//        evernoteButton.setAlpha(1f);
//        evernoteButton.setBackgroundColor(Color.GRAY);
//        // Enable the Logout from Evernote Button

    }

    private void setSetting(String key,boolean isChecked) {
        Util.setUserSetting(this, key, String.valueOf(isChecked));
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

    public void logoutEvernote() {
        if(EvernoteSession.getInstance()!=null) {
                if(EvernoteSession.getInstance().isLoggedIn()) {
                    Button evernoteButton = (Button) findViewById(R.id.logout_evernote);
                    evernoteButton.setEnabled(false);
                    evernoteButton.setAlpha(.5f);
                    evernoteButton.setClickable(false);
                    evernoteButton.setBackgroundColor(Color.DKGRAY);
                    // logout from evernote
                    // disable the switch
                }
        }
    }


    public boolean onTouch(View view, MotionEvent event) {

        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:

                dX = view.getX() - event.getRawX();
                dY = view.getY() - event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:

                view.animate()
                        .x(event.getRawX() + dX)
                        .y(event.getRawY() + dY)
                        .setDuration(0)
                        .start();
                break;
            default:
                return false;
        }
        return true;
    }
}
