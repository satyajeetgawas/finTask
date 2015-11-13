package com.appmagnet.fintaskanyplace.activity;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.appmagnet.fintaskanyplace.R;
import com.appmagnet.fintaskanyplace.backgroundtasks.BackgroundTaskReceiver;
import com.appmagnet.fintaskanyplace.core.RefreshCachedNotesDB;
import com.appmagnet.fintaskanyplace.dataobjects.NoteObject;
import com.appmagnet.fintaskanyplace.db.DBContract;
import com.appmagnet.fintaskanyplace.db.NotesDBHelper;
import com.appmagnet.fintaskanyplace.evernote.ReadUserNotes;
import com.appmagnet.fintaskanyplace.initializer.LocationHandler;
import com.appmagnet.fintaskanyplace.util.Constants;
import com.appmagnet.fintaskanyplace.util.Util;
import com.evernote.client.android.EvernoteSession;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by satyajeet and anmol on 10/12/2015.
 */
public class MainActivity extends AppCompatActivity {

    private LocationHandler locHandle;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    public BackgroundTaskReceiver backgroundTaskReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);


        backgroundTaskReceiver = new BackgroundTaskReceiver();
        Context context = this.getApplicationContext();
        backgroundTaskReceiver.doInBackground(context);

        RefreshCachedNotesDB dbTask = new RefreshCachedNotesDB(this);
        dbTask.execute();
        Toast.makeText(getApplicationContext(), "DB writing started", Toast.LENGTH_SHORT).show();




        //String loc = "Latitude " + locHandle.getLocationMap().get(LocationHandler.LATITUDE) + " Longitude " +
        //        locHandle.getLocationMap().get(LocationHandler.LONGITUDE);
        //Toast.makeText(getApplicationContext(), loc, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onResume(){
        super.onResume();
        // this is not needed in onCreate() as both of these are called in beginning as well.
        checkAndInitializePrerequisites();
      //  Util.initializeUserSettings(this);
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

        if ((EvernoteSession.getInstance()==null || !EvernoteSession.getInstance().isLoggedIn()) &&
                "0".equals(Util.getSettings(this, Constants.PREF_ACCOUNT_NAME)))
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

    public void createUI() {
        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        // preparing list data
        //prepareListData()


        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(
                        getApplicationContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });
    }

    public void showPostDBWrite() {

        NotesDBHelper mDbHelper = new NotesDBHelper(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Cursor c = db.query(
                DBContract.NotesEntry.TABLE_NAME,  // The table to query
                null,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        if (c.moveToFirst()) {
            do {
                NoteObject obj = new NoteObject(c);
                if(listDataChild.get(obj.getNoteType()) == null){
                    ArrayList listOfNotes = new ArrayList();
                    listOfNotes.add(obj.getFormattedString());
                    listDataHeader.add(obj.getNoteType());
                    listDataChild.put(obj.getNoteType(),listOfNotes);
                }else
                {
                    ArrayList list = (ArrayList) listDataChild.get(obj.getNoteType());
                    list.add(obj.getFormattedString());
                    listDataChild.put(obj.getNoteType(),list);
                }

            }
            while (c.moveToNext());
        }
        if (c != null && !c.isClosed())
            c.close();

        createUI();

    }
}
