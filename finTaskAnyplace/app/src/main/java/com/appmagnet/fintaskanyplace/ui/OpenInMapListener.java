package com.appmagnet.fintaskanyplace.ui;

import android.content.Context;
import android.view.View;

import com.appmagnet.fintaskanyplace.activity.searchedResults;
import com.appmagnet.fintaskanyplace.dataobjects.BusinessObject;

/**
 * Created by satyajeet on 11/22/2015.
 */
public class OpenInMapListener implements View.OnClickListener {
    private BusinessObject busObj;
    private Context context;
    public OpenInMapListener(Context context, BusinessObject busObj) {
        this.context = context;
        this.busObj = busObj;
    }

    @Override
    public void onClick(View v) {
        ((searchedResults)context).startMaps(busObj);

    }
}
