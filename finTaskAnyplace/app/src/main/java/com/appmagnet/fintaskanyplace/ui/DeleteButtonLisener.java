package com.appmagnet.fintaskanyplace.ui;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.appmagnet.fintaskanyplace.dataobjects.NoteObject;

/**
 * Created by satyajeet on 11/18/2015.
 */
public class DeleteButtonLisener implements View.OnClickListener {

   private NoteObject noteObject;
    private Context context;
    private ExpandableListAdapter adpater;
    public DeleteButtonLisener(ExpandableListAdapter expandableListAdapter, Context context, NoteObject noteObj) {
        this.noteObject = noteObj;
        this.context = context;
        this.adpater = expandableListAdapter;
    }

    @Override
    public void onClick(View v) {

        String noteGuid = noteObject.getGuid();
        adpater.removeChild(noteObject);


    }
}
