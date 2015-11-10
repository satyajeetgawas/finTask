package com.appmagnet.fintaskanyplace.activity;

import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.appmagnet.fintaskanyplace.R;
import com.appmagnet.fintaskanyplace.util.Constants;
import com.appmagnet.fintaskanyplace.util.Util;
import com.evernote.client.android.EvernoteSession;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;

import java.util.Arrays;

/**
 * Created by satyajeet and anmol on 10/21/2015.
 */
public class SettingsActivity extends AppCompatActivity {

    GoogleAccountCredential mCredential;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    int radius, refresh_rate;
    private Switch googleCalendarSwitch;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    protected void onResume() {
        super.onResume();
       resetSettings();
    }

    private void resetSettings(){
        TextView radius_text1 = (TextView) findViewById(R.id.radiusCurrentValueID);
        String a = Util.getSettings(this, Constants.PROXIMITY_RADIUS);
        if(a == "") {
            a = "0";
        }
        radius_text1.setText(a + " miles");
        SeekBar seek_rad = (SeekBar) findViewById(R.id.RadiusSeekBarID);
        seek_rad.setProgress(new Integer(a));

        TextView refresh_text1 = (TextView) findViewById(R.id.refreshCurrentValueID);
        String b = Util.getSettings(this, Constants.REFRESH_RATE);
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
                evernoteLogin(isChecked);
            }
        });

        googleCalendarSwitch = (Switch) findViewById(R.id.switch_google_calendar);
        googleCalendarSwitch.setChecked(!"0".equals(Util.getSettings(this, Constants.PREF_ACCOUNT_NAME)));
        googleCalendarSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                googleCalendarLogin(isChecked);
            }
        });

        Switch yelpApiSwitch = (Switch) findViewById(R.id.switch_yelp);
        yelpApiSwitch.setChecked(Boolean.parseBoolean(Util.getSettings(this, Constants.YELP_PREF)));
        yelpApiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setSetting(Constants.YELP_PREF, String.valueOf(isChecked));
            }
        });

        Switch googlePlacesSwitch = (Switch) findViewById(R.id.switch_google_places);
        googlePlacesSwitch.setChecked(Boolean.parseBoolean(Util.getSettings(this, Constants.GOOGLE_PLACES_PREF)));
        googlePlacesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setSetting(Constants.GOOGLE_PLACES_PREF, String.valueOf(isChecked));
            }
        });

        final SeekBar seek = (SeekBar) findViewById(R.id.RadiusSeekBarID);
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                radius = progress;
                setSetting(Constants.PROXIMITY_RADIUS, String.valueOf(radius));
                TextView radius_text = (TextView) findViewById(R.id.radiusCurrentValueID);
                radius_text.setText(String.valueOf(radius) + " miles");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final SeekBar seek2 = (SeekBar) findViewById(R.id.refreshSeekBarID);
        seek2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                refresh_rate = progress;
                setSetting(Constants.REFRESH_RATE, String.valueOf(refresh_rate));
                TextView refresh_text = (TextView) findViewById(R.id.refreshCurrentValueID);
                refresh_text.setText(String.valueOf(refresh_rate) + " min");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    private void googleCalendarLogin(boolean isChecked) {
        if(isChecked) {
            SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
            mCredential = GoogleAccountCredential.usingOAuth2(
                    getApplicationContext(), Arrays.asList(Constants.SCOPES))
                    .setBackOff(new ExponentialBackOff())
                    .setSelectedAccountName(settings.getString(Constants.PREF_ACCOUNT_NAME, null));
            if (mCredential.getSelectedAccountName() == null) {
               chooseAccount();
            }
        }else{
           Util.setUserSetting(this,Constants.PREF_ACCOUNT_NAME,null);
        }

    }

    private void evernoteLogin(boolean isChecked) {
        if(isChecked)
            EvernoteSession.getInstance().authenticate(this);
        else
            Util.logout(this);
    }



    private void setSetting(String key,String value) {
        Util.setUserSetting(this, key, value);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        return true;
    }


    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    isGooglePlayServicesAvailable();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        mCredential.setSelectedAccountName(accountName);
                        Util.setUserSetting(this, Constants.PREF_ACCOUNT_NAME, accountName);
                    }
                    else{
                        googleCalendarSwitch.setChecked(false);
                    }
                }else
                    googleCalendarSwitch.setChecked(false);
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode != RESULT_OK) {
                    chooseAccount();
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void chooseAccount() {
       startActivityForResult(
                mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }

    private boolean isGooglePlayServicesAvailable() {
        final int connectionStatusCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        } else if (connectionStatusCode != ConnectionResult.SUCCESS ) {
            return false;
        }
        return true;
    }

    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                connectionStatusCode,
                SettingsActivity.this,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }




}
