package com.appmagnet.fintaskanyplace.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.appmagnet.fintaskanyplace.R;
import com.appmagnet.fintaskanyplace.backgroundtasks.BackgroundTaskReceiver;
import com.appmagnet.fintaskanyplace.evernote.ReadUserNotes;
import com.appmagnet.fintaskanyplace.initializer.LocationHandler;
import com.appmagnet.fintaskanyplace.util.Util;
import com.evernote.client.android.EvernoteSession;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by satyajeet on 10/12/2015.
 */
public class MainActivity extends AppCompatActivity {

    private LocationHandler locHandle;


    public BackgroundTaskReceiver backgroundTaskReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);


        backgroundTaskReceiver = new BackgroundTaskReceiver();
        Context context = this.getApplicationContext();
        backgroundTaskReceiver.doInBackground(context);





        // Intent intent = new Intent(getBaseContext(), CalendarLogin.class);
        //startActivity(intent);

        //String loc = "Latitude " + locHandle.getLocationMap().get(LocationHandler.LATITUDE) + " Longitude " +
        //        locHandle.getLocationMap().get(LocationHandler.LONGITUDE);
        //Toast.makeText(getApplicationContext(), loc, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onResume(){
        super.onResume();
        checkAndInitializePrerequisites();
        Util.initializeUserSettings(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_fin_task_anyplace, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id==R.id.action_settings){
            startSettingsActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void checkAndInitializePrerequisites() {
        locHandle= new LocationHandler(this);

        if (EvernoteSession.getInstance()==null || !EvernoteSession.getInstance().isLoggedIn())
        {
           showEnableAtleastOneAccount();
        }

        else if (!locHandle.isLocationEnabled()) {
           showLocationEnableDialog();
        }
        else
            showTaskList();


    }


    private void showTaskList() {

        final List<String> namesList = new ArrayList<>();
        //--Set guid of notebook to only retrieve notes for the current notebok
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ReadUserNotes userNotes = new ReadUserNotes();
                if (userNotes.queryNotesGuid()) {
                    if (userNotes.queryNotesRawData()) {
                        namesList.addAll(userNotes.processRawNoteData());
                    }
                }

            }
        });

        thread.start();

        while (thread.isAlive()) ;
        if (namesList.size() > 0) {
            String noteNames = TextUtils.join(", ", namesList);
            Toast.makeText(getApplicationContext(), "" + noteNames + " notes have been retrieved", Toast.LENGTH_LONG).show();
        }
    }



    private void showEnableAtleastOneAccount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Services Not Active");
        builder.setMessage("Please enable atleast one Service");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                // Show location settings when the user acknowledges the alert dialog
                startSettingsActivity();
            }
        });
        Dialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }


    private void showLocationEnableDialog(){
        // Build the alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Location Services Not Active");
        builder.setMessage("Please enable Location Services and GPS");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                // Show location settings when the user acknowledges the alert dialog
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        Dialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

    }


    private void startSettingsActivity(){
        Intent intent = new Intent(this,SettingsActivity.class);
        startActivity(intent);
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
