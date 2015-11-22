package com.appmagnet.fintaskanyplace.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
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
import com.appmagnet.fintaskanyplace.dataobjects.BusinessObject;
import com.appmagnet.fintaskanyplace.dataobjects.NoteObject;
import com.appmagnet.fintaskanyplace.db.DBContract;
import com.appmagnet.fintaskanyplace.db.NotesDBHelper;
import com.appmagnet.fintaskanyplace.initializer.LocationHandler;
import com.appmagnet.fintaskanyplace.ui.ExpandableListAdapter;
import com.appmagnet.fintaskanyplace.util.Constants;
import com.appmagnet.fintaskanyplace.util.Util;
import com.appmagnet.fintaskanyplace.wunderlist.WunderlistSession;
import com.appmagnet.fintaskanyplace.wunderlist.WunderlistUser;
import com.evernote.client.android.EvernoteSession;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.thrift.TException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Created by satyajeet and anmol on 10/12/2015.
 */
public class MainActivity extends AppCompatActivity {


    private LocationHandler locHandle;
    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private ProgressDialog progress;
    private int noOfItems;

    public BackgroundTaskReceiver backgroundTaskReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        backgroundTaskReceiver = new BackgroundTaskReceiver();
        Context context = this.getApplicationContext();
        backgroundTaskReceiver.doInBackground(context);
    }

    @Override
    public void onResume(){
        super.onResume();
        checkAndInitializePrerequisites();
        showPostDBWrite();

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
        if(id==R.id.action_refresh){
            progress = ProgressDialog.show(this, getString(R.string.loading_title),
                    getString(R.string.loading_content), true);
            RefreshCachedNotesDB dbTask = new RefreshCachedNotesDB(this);
            dbTask.execute();
        }
        return super.onOptionsItemSelected(item);
    }


    private void checkAndInitializePrerequisites() {
        locHandle= new LocationHandler(this);
        boolean is = EvernoteSession.getInstance()==null || !EvernoteSession.getInstance().isLoggedIn();
        if ((EvernoteSession.getInstance()==null || !EvernoteSession.getInstance().isLoggedIn()) &&
                "0".equals(Util.getSettings(this, Constants.PREF_ACCOUNT_NAME))
                && (WunderlistSession.getInstance()==null || !WunderlistSession.getInstance().isLoggedIn()))
        {
           showEnableAtleastOneAccount();
        }

        else if (!locHandle.isLocationEnabled()) {
           showLocationEnableDialog();
        }
    }




    private void showEnableAtleastOneAccount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Services Not Active");
        builder.setMessage("Please enable atleast one Service");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                startSettingsActivity();
            }
        });
        Dialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }


    private void showLocationEnableDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Location Services Not Active");
        builder.setMessage("Please enable Location Services and GPS");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
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

        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        listAdapter = new ExpandableListAdapter(this, Util.listDataHeader, Util.listDataChild);
        expListView.setAdapter(listAdapter);
        for(int i=0;i<Util.listDataHeader.size();i++){
            expListView.expandGroup(i);
        }
    }

    public void showPostDBWrite() {

        if(progress!=null){
            progress.dismiss();
        }
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

        Util.listDataHeader = new ArrayList<String>();
        Util.listDataChild = new HashMap<String, List<NoteObject>>();
        noOfItems = c.getCount();
        if (c.moveToFirst()) {
            do {
                NoteObject obj = new NoteObject(c);

                if (Util.listDataChild.get(obj.getNoteType()) == null) {
                    ArrayList listOfNoteObj = new ArrayList();
                    listOfNoteObj.add(obj);
                    Util.listDataHeader.add(obj.getNoteType());
                    Util.listDataChild.put(obj.getNoteType(), listOfNoteObj);
                } else {
                    ArrayList list = (ArrayList) Util.listDataChild.get(obj.getNoteType());
                    list.add(obj);
                    Util.listDataChild.put(obj.getNoteType(), list);
                }
            }
            while (c.moveToNext());
        }
        if (c != null && !c.isClosed())
            c.close();
        createUI();
    }
}
