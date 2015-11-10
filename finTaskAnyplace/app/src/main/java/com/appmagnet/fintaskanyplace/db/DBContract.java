package com.appmagnet.fintaskanyplace.db;

import android.provider.BaseColumns;

/**
 * Created by satyajeet and anmol on 11/9/2015.
 */
public final class DBContract {

    public DBContract(){}

    public static abstract class NotesEntry implements BaseColumns {
        public static final String TABLE_NAME = "Notes";
        public static final String COLUMN_NAME_ENTRY_ID = "entryid";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_CATEGORY = "category";
    }
}
