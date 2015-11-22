package com.appmagnet.fintaskanyplace.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import com.appmagnet.fintaskanyplace.R;
import com.appmagnet.fintaskanyplace.dataobjects.BusinessObject;
import com.appmagnet.fintaskanyplace.dataobjects.NoteObject;
import com.appmagnet.fintaskanyplace.db.DBContract;
import com.appmagnet.fintaskanyplace.db.NotesDBHelper;
import com.appmagnet.fintaskanyplace.ui.ExpandableListAdapter;
import com.appmagnet.fintaskanyplace.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by satyajeet and anmol on 11/10/15.
 */
public class NotificationReciever extends AppCompatActivity {

    private HashMap<String, ArrayList> mapOfBusiness;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<NoteObject>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);

        mapOfBusiness = (HashMap<String, ArrayList>)getIntent().getSerializableExtra(Constants.LIST_OF_PLACES);
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
        return super.onOptionsItemSelected(item);
    }


    private void startSettingsActivity(){
        Intent intent = new Intent(this,SettingsActivity.class);
        startActivity(intent);
    }

    public void createUI() {
        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild,mapOfBusiness);

        // setting list adapter
        expListView.setAdapter(listAdapter);
        for(int i=0;i<expListView.getHeaderViewsCount();i++){
            expListView.expandGroup(i);
        }
//        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//            @Override
//            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//                String header = (String) parent.getExpandableListAdapter().getGroup(groupPosition);
//                NoteObject noteObj = (NoteObject)parent.getExpandableListAdapter().getChild(groupPosition,childPosition);
//                makeToast(header,noteObj);
//                return false;
//            }
//        });


    }

    private void makeToast(String header, NoteObject noteObj) {
        ArrayList<BusinessObject> businessesList = new ArrayList<>(mapOfBusiness.get(header));

        if(Constants.UNCATEGORIZED.equals(header)){
            ArrayList addList = new ArrayList();
            for(BusinessObject busObj:businessesList)
            {

               if(busObj.getBusinessCategory().equals(noteObj.getContents()))
                   addList.add(busObj);
            }
            businessesList.clear();
            businessesList.addAll(addList);

        }

        String toast = "";
        for(BusinessObject busObj:businessesList)
        {
            toast +="\n "+busObj.getBusinessName();
        }

        Intent intent = new Intent(getApplicationContext(), searchedResults.class);
        intent.putExtra(Constants.AFTER_NOTIFICATION_LIST, businessesList);
        intent.putExtra(Constants.CONTENTS_STRING, noteObj.getContents());
        //PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), intent, 0);
        startActivity(intent);
        //Toast.makeText(getApplicationContext(),toast,Toast.LENGTH_LONG).show();
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

        ArrayList listOfCategories = new ArrayList<>();
        ArrayList listOfBusiness = new ArrayList<>();
        for (HashMap.Entry<String, ArrayList> entry : mapOfBusiness.entrySet()) {
            listOfBusiness = entry.getValue();

            listOfCategories.add(entry.getKey());
            //for(Object businessObject : listOfBusiness){
            //    String temp = ((BusinessObject)businessObject).getBusinessName()+" "+((BusinessObject)businessObject).getBusinessRating();
            //    listOfPlaces.add(temp);

            //}
        }

        if (c.moveToFirst()) {
            do {
                NoteObject obj = new NoteObject(c);

                if( listOfCategories.contains(obj.getNoteType()) ) {
                    if (listDataChild.get(obj.getNoteType()) == null) {
                        ArrayList listOfNoteObj = new ArrayList();
                        listOfNoteObj.add(obj);
                        listDataHeader.add(obj.getNoteType());
                        listDataChild.put(obj.getNoteType(), listOfNoteObj);
                    } else {
                        ArrayList list = (ArrayList) listDataChild.get(obj.getNoteType());
                        list.add(obj);
                        listDataChild.put(obj.getNoteType(), list);
                    }
                }
            }
            while (c.moveToNext());
        }
        if (c != null && !c.isClosed())
            c.close();

        createUI();

    }


}
