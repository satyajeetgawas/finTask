package com.appmagnet.fintaskanyplace.core;

import android.app.Activity;
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



    public static String getCategory(String cont, Activity activity) {
        CategoryDBHelper mDbHelper = new CategoryDBHelper(activity.getApplicationContext());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] returnCol = {DBContract.CategoryEntry.COLUMN_CATEGORY};
       cont = cont.toLowerCase().trim();
//        Cursor c = db.query(DBContract.CategoryEntry.TABLE_NAME,
//               returnCol,
//                DBContract.CategoryEntry.COLUMN_CONTENTS+"='"+cont+"'",
//                null,
//                null,
//                null,
//                null,
//                null
//        );
       //cont = "egg";
        Cursor c = db.rawQuery("SELECT category FROM Category WHERE content = '"+cont+"'",null);
        c.moveToFirst();
        int couunt = c.getCount();
        String category =  c.getString(0);
        if(c!=null && !c.isClosed())
            c.close();
        return category;
    }

}
