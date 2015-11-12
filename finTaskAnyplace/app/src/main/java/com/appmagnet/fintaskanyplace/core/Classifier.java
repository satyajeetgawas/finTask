package com.appmagnet.fintaskanyplace.core;

import android.content.ContentValues;

import com.appmagnet.fintaskanyplace.db.DBContract;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.Attributes;

/**
 * Created by satyajeet and anmol on 11/11/15.
 */


public class Classifier {

    private String title, content;

    private ArrayList categoryList, contentList;

    public Classifier(String title, String content) {
        this.title = title;
        this.content = content;
        this.categoryList = new ArrayList();
        this.contentList = new ArrayList();
        processTitleAndContent();
    }

    private void processTitleAndContent() {
        if(TitleClassifier.isTitleGrocery(title))
            categoryList.add("grocery_or_supermarket");
        else if(TitleClassifier.isNoteRequired(title));
            parserContent();
    }

    private void parserContent() {
        ArrayList<String> listofContents = new ArrayList<>();
        listofContents.addAll(Arrays.asList(content.split(",")));
        for(String cont : listofContents) {
            if(cont == null || cont.isEmpty()){
                continue;
            }
            cont = cont.trim();
            contentList.add(cont);
            categoryList.add(getCategory(cont));
        }
    }

    private String getCategory(String cont) {
        return ContentClassifier.getCategory(cont.toLowerCase());
    }

    public List<ContentValues> getContentValues() {
        List<ContentValues> cvList = new ArrayList<>();
        for (int i=0;i<categoryList.size();i++) {
            ContentValues values = new ContentValues();
            values.put(DBContract.NotesEntry.COLUMN_NAME_TITLE, title);
            values.put(DBContract.NotesEntry.COLUMN_CONTENT, (String) contentList.get(i));
            values.put(DBContract.NotesEntry.COLUMN_CATEGORY, (String) categoryList.get(i));
            cvList.add(values);
        }
        return cvList;
    }
}
