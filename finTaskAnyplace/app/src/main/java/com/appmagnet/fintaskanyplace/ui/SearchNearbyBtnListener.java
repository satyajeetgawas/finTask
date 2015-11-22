package com.appmagnet.fintaskanyplace.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.appmagnet.fintaskanyplace.R;
import com.appmagnet.fintaskanyplace.activity.searchedResults;
import com.appmagnet.fintaskanyplace.core.RunBusinessQuery;
import com.appmagnet.fintaskanyplace.dataobjects.BusinessObject;
import com.appmagnet.fintaskanyplace.dataobjects.NoteObject;
import com.appmagnet.fintaskanyplace.util.Constants;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by satyajeet on 11/18/2015.
 */
public class SearchNearbyBtnListener implements View.OnClickListener {
    private final Context context;
    private String category;
    private NoteObject noteObj;
    private Map<String,ArrayList> mapOfBusinessess;
    private ProgressDialog progress;

    public SearchNearbyBtnListener(Context context, String category, Map<String, ArrayList> mapOfBusinessess) {
        this.context = context;
        this.category = category;
        this.mapOfBusinessess = mapOfBusinessess;
    }
    public SearchNearbyBtnListener(Context context, NoteObject noteObject, Map<String, ArrayList> mapOfBusinessess) {
        this.context = context;
        this.category = noteObject.getNoteType();
        this.mapOfBusinessess = mapOfBusinessess;
        this.noteObj = noteObject;
    }

    @Override
    public void onClick(View v) {
        ArrayList<BusinessObject> businessesList = null;
        if(mapOfBusinessess!=null) {
            businessesList = new ArrayList<>(mapOfBusinessess.get(category));
            if (Constants.UNCATEGORIZED.equals(category)) {
                ArrayList addList = new ArrayList();
                for (BusinessObject busObj : businessesList) {

                    if (busObj.getBusinessCategory().equals(category))
                        addList.add(busObj);
                }
                businessesList.clear();
                businessesList.addAll(addList);

            }
            Intent intent = new Intent(context.getApplicationContext(), searchedResults.class);
            intent.putParcelableArrayListExtra(Constants.AFTER_NOTIFICATION_LIST, businessesList);
            intent.putExtra(Constants.CONTENTS_STRING, category);
            context.startActivity(intent);

        }else{
            progress = ProgressDialog.show(context, context.getString(R.string.loading_title),
                    context.getString(R.string.search_nearby_msg), true);
            boolean skipGoogle = false;
            if(noteObj!=null && Constants.UNCATEGORIZED.equals(category)){
                category= noteObj.getContents();
                skipGoogle = true;
            }


            Object[] params = {context,category,skipGoogle,this};
           new RunBusinessQuery().execute(params);
        }


    }

    public void dismissProgress(){
        if(progress!=null)
            progress.dismiss();
    }
}
