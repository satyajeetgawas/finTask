package com.appmagnet.fintaskanyplace.initializer;


import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;

import com.appmagnet.fintaskanyplace.R;
import com.appmagnet.fintaskanyplace.db.CategoryDBHelper;
import com.appmagnet.fintaskanyplace.db.DBContract;
import com.appmagnet.fintaskanyplace.util.ApiKeys;
import com.evernote.client.android.EvernoteSession;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
created by satyajeet and anmol
 */

public class Initializer extends Application {

    private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX;
    private static final boolean SUPPORT_APP_LINKED_NOTEBOOKS = true;
    @Override
    public void onCreate() {
    super.onCreate();
        //Set up the Evernote singleton session, use EvernoteSession.getInstance() later
        new EvernoteSession.Builder(this)
                .setEvernoteService(EVERNOTE_SERVICE)
                .setSupportAppLinkedNotebooks(SUPPORT_APP_LINKED_NOTEBOOKS)
                .setForceAuthenticationInThirdPartyApp(true)
//                .setLocale(Locale.SIMPLIFIED_CHINESE)
                .build(ApiKeys.EVERNOTE_CONSUMER_KEY, ApiKeys.EVERNOTE_CONSUMER_SECRET)
                .asSingleton();


        CategoryDBHelper dbHelper = new CategoryDBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query(DBContract.CategoryEntry.TABLE_NAME,
                null,null,null,null,null,null );
        if (c.getCount() == 0) {
            Toast.makeText(getApplicationContext(), "No Bloat", Toast.LENGTH_LONG).show();
            Map<String, ArrayList> categoryMap = read();
            for (Map.Entry<String, ArrayList> entry : categoryMap.entrySet()) {
                String key = entry.getKey();
                ArrayList<String> mappedItems = entry.getValue();
                for (String item : mappedItems) {
                    ContentValues values = new ContentValues();
                    values.put(DBContract.CategoryEntry.COLUMN_CATEGORY, key.trim());
                    values.put(DBContract.CategoryEntry.COLUMN_CONTENTS, item.trim());
                    db.insert(DBContract.CategoryEntry.TABLE_NAME, null, values);
                }
            }

        }
        if(c!=null && !c.isClosed())
        {
            c.close();
            db.close();
        }
    }




    public Map read(){
        InputStream inputStream = getResources().openRawResource(R.raw.category);
        Map categoryMap = new HashMap();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] row = line.split("\t");
                ArrayList mappedItems = new ArrayList();
                for(int i=1;i<row.length;++i){
                    mappedItems.add(row[i]);
                }
                categoryMap.put(row[0],mappedItems);
               }
        }
        catch (IOException ex) {
            throw new RuntimeException("Error in reading file: "+ex);
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: "+e);
            }
        }
        return categoryMap;
    }
}
