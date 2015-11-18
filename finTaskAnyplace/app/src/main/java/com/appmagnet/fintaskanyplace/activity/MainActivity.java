package com.appmagnet.fintaskanyplace.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.appmagnet.fintaskanyplace.R;
import com.appmagnet.fintaskanyplace.backgroundtasks.BackgroundTaskReceiver;
import com.appmagnet.fintaskanyplace.core.RefreshCachedNotesDB;
import com.appmagnet.fintaskanyplace.dataobjects.NoteObject;
import com.appmagnet.fintaskanyplace.db.DBContract;
import com.appmagnet.fintaskanyplace.db.NotesDBHelper;
import com.appmagnet.fintaskanyplace.initializer.LocationHandler;
import com.appmagnet.fintaskanyplace.ui.ExpandableListAdapter;
import com.appmagnet.fintaskanyplace.util.Constants;
import com.appmagnet.fintaskanyplace.util.Util;
import com.evernote.client.android.EvernoteSession;

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
    HashMap<String, List<NoteObject>> listDataChild;
    private static boolean showDialog = true;

    public BackgroundTaskReceiver backgroundTaskReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);


        backgroundTaskReceiver = new BackgroundTaskReceiver();
        Context context = this.getApplicationContext();
        backgroundTaskReceiver.doInBackground(context);







        //String loc = "Latitude " + locHandle.getLocationMap().get(LocationHandler.LATITUDE) + " Longitude " +
        //        locHandle.getLocationMap().get(LocationHandler.LONGITUDE);
        //Toast.makeText(getApplicationContext(), loc, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onResume(){
        super.onResume();
        checkAndInitializePrerequisites();
        showPostDBWrite();
        RefreshCachedNotesDB dbTask = new RefreshCachedNotesDB(this);
        dbTask.execute();

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
        boolean is = EvernoteSession.getInstance()==null || !EvernoteSession.getInstance().isLoggedIn();
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
        for(int i=0;i<expListView.getHeaderViewsCount();i++){
            expListView.expandGroup(i);
        }




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
        listDataChild = new HashMap<String, List<NoteObject>>();

        if (c.moveToFirst()) {
            do {
                NoteObject obj = new NoteObject(c);
                if(listDataChild.get(obj.getNoteType()) == null){
                    ArrayList listOfNoteObj = new ArrayList();
                    listOfNoteObj.add(obj);
                    listDataHeader.add(obj.getNoteType());
                    listDataChild.put(obj.getNoteType(),listOfNoteObj);
                }else
                {
                    ArrayList list = (ArrayList) listDataChild.get(obj.getNoteType());
                    list.add(obj);
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
