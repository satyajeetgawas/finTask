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
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

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
    int radius, refresh_rate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        TextView radius_text1 = (TextView) findViewById(R.id.radiusCurrentValueID);
        String a = Util.getSettings(this, Util.PROXIMITY_RADIUS);
        if(a == "") {
            a = "0";
        }
        radius_text1.setText(a + " miles");
        SeekBar seek_rad = (SeekBar) findViewById(R.id.RadiusSeekBarID);
        seek_rad.setProgress(new Integer(a));

        TextView refresh_text1 = (TextView) findViewById(R.id.refreshCurrentValueID);
        String b = Util.getSettings(this, Util.REFRESH_RATE);
        if(b == "") {
            b = "0";
        }
        refresh_text1.setText(b + " min");
        SeekBar seek_ref = (SeekBar) findViewById(R.id.refreshSeekBarID);
        seek_ref.setProgress(new Integer(b));

        Switch evernoteSwitch = (Switch) findViewById(R.id.switch_evernote);
        evernoteSwitch.setChecked(false);
        evernoteSwitch.setChecked(EvernoteSession.getInstance() != null && EvernoteSession.getInstance().isLoggedIn());
        evernoteSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setSetting(Util.EVERNOTE_USER_PREF, String.valueOf(isChecked));
                evernoteLogin(isChecked);
            }
        });

        Switch googleCalendarSwitch = (Switch) findViewById(R.id.switch_google_calendar);
        googleCalendarSwitch.setChecked(isGooglePlayServicesAvailable());
        googleCalendarSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setSetting(Util.GOOGLE_CALENDAR_PREF, String.valueOf(isChecked));
            }
        });

        Switch yelpApiSwitch = (Switch) findViewById(R.id.switch_yelp);
        yelpApiSwitch.setChecked(Boolean.parseBoolean(Util.getSettings(this, Util.YELP_PREF)));
        yelpApiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setSetting(Util.YELP_PREF, String.valueOf(isChecked));
            }
        });

        Switch googlePlacesSwitch = (Switch) findViewById(R.id.switch_google_places);
        googlePlacesSwitch.setChecked(Boolean.parseBoolean(Util.getSettings(this, Util.GOOGLE_PLACES_PREF)));
        googlePlacesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setSetting(Util.GOOGLE_PLACES_PREF, String.valueOf(isChecked));
            }
        });

        final SeekBar seek = (SeekBar) findViewById(R.id.RadiusSeekBarID);
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                radius = progress;
                setSetting(Util.PROXIMITY_RADIUS, String.valueOf(radius));
                TextView radius_text = (TextView) findViewById(R.id.radiusCurrentValueID);
                radius_text.setText(String.valueOf(radius) + " miles");
            }
        });

        final SeekBar seek2 = (SeekBar) findViewById(R.id.refreshSeekBarID);
        seek2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                refresh_rate = progress;
                setSetting(Util.REFRESH_RATE, String.valueOf(refresh_rate));
                TextView refresh_text = (TextView) findViewById(R.id.refreshCurrentValueID);
                refresh_text.setText(String.valueOf(refresh_rate) + " min");
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

    private void setSetting(String key,String value) {
        Util.setUserSetting(this, key, value);
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

    // Anmol not needed
    public void logoutEvernote() {
        if(EvernoteSession.getInstance()!=null) {
                if(EvernoteSession.getInstance().isLoggedIn()) {
                 //   Button evernoteButton = (Button) findViewById(R.id.logout_evernote);
                 //   evernoteButton.setEnabled(false);
                  //  evernoteButton.setAlpha(.5f);
                  //  evernoteButton.setClickable(false);
                  //  evernoteButton.setBackgroundColor(Color.DKGRAY);
                    // logout from evernote
                    // disable the switch
                }
        }
    }

    // Anmol Not Needed
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
