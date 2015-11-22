package com.appmagnet.fintaskanyplace.db;

import android.provider.BaseColumns;

/**
 * Created by satyajeet and anmol on 11/9/2015.
 */
public final class DBContract {

    public DBContract(){}

    public static abstract class NotesEntry implements BaseColumns {
        public static final String TABLE_NAME = "Notes";
        public static final String NOTE_ID = "entryid";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_NOTE_DATE = "note_date";
        public static final String COLUMN_SOURCE = "source";
    }

    public static abstract class CategoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "Category";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_CONTENTS = "content";
    }

    public static abstract class DeletedEntries implements BaseColumns {
        public static final String TABLE_NAME = "Deleted_Entries";
        public static final String COLUMN_NOTE_ID = "note_id";
        public static final String COLUMN_NOTE_NAME = "note_name";
        public static final String COLUMN_NOTE_CONTENT = "note_content";
    }

}
