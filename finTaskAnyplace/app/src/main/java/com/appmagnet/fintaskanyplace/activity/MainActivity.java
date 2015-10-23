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
import android.widget.TextView;
import android.widget.Toast;

import com.appmagnet.fintaskanyplace.R;
import com.appmagnet.fintaskanyplace.backgroundtasks.BackgroundTaskReceiver;
import com.appmagnet.fintaskanyplace.evernote.ReadUserNotes;
import com.appmagnet.fintaskanyplace.initializer.LocationHandler;
import com.evernote.client.android.EvernoteSession;
import com.evernote.edam.type.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by satyajeet on 10/12/2015.
 */
public class MainActivity extends AppCompatActivity {

    LocationHandler locHandle;
    public BackgroundTaskReceiver backgroundTaskReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        if (!EvernoteSession.getInstance().isLoggedIn()) {
            // LoginChecker will call finish
            return;
        }
        backgroundTaskReceiver = new BackgroundTaskReceiver();
        Context context = this.getApplicationContext();
        backgroundTaskReceiver.doInBackground(context);

        final List<String> namesList = new ArrayList<>();
        //--Set guid of notebook to only retrieve notes for the current notebok
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                ReadUserNotes userNotes = new ReadUserNotes();
                if(userNotes.queryNotesGuid()){
                    if(userNotes.queryNotesRawData()){
                        namesList.addAll(userNotes.processRawNoteData());
                    }
                }

            }
        });

        thread.start();
        boolean isLocationDisabled = true;
        locHandle= new LocationHandler(this);
        if (!locHandle.isLocationEnabled()) {
            showLocationEnableDialog();
        }

        String loc = "Latitude " + locHandle.getLocationMap().get(LocationHandler.LATITUDE) + " Longitude " +
                locHandle.getLocationMap().get(LocationHandler.LONGITUDE);
        Toast.makeText(getApplicationContext(), loc, Toast.LENGTH_LONG).show();

        while (thread.isAlive()) ;
        if (namesList.size() > 0) {
            String noteNames = TextUtils.join(", ", namesList);
            Toast.makeText(getApplicationContext(), "" + noteNames + " notes have been retrieved", Toast.LENGTH_LONG).show();
        }

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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume(){
        super.onResume();
    }


}
