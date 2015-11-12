package com.appmagnet.fintaskanyplace.core;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.appmagnet.fintaskanyplace.db.CategoryDBHelper;
import com.appmagnet.fintaskanyplace.db.DBContract;
import com.appmagnet.fintaskanyplace.db.NotesDBHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by satyajeet on 11/11/2015.
 */
public class ContentClassifier {

  private static Map categoryMap;
    public static String getCategory(String cont) {
//        CategoryDBHelper mDbHelper = new CategoryDBHelper(getApplicationContext());
//        SQLiteDatabase db = mDbHelper.getWritableDatabase();
//        Cursor c = db.query(
//                DBContract.NotesEntry.TABLE_NAME,  // The table to query
//                null,                               // The columns to return
//                null,                                // The columns for the WHERE clause
//                null,                            // The values for the WHERE clause
//                null,                                     // don't group the rows
//                null,                                     // don't filter by row groups
//                null                                 // The sort order
//        );
        return null;
    }

    private static Map getCategoryMap(){
        if(categoryMap == null)
            categoryMap = new HashMap();
        categoryMap.put("fish","grocey_or_supermarket");

        return categoryMap;
    }
}
