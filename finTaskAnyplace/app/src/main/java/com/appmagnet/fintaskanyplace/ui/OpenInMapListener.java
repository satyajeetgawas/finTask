package com.appmagnet.fintaskanyplace.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.appmagnet.fintaskanyplace.activity.SearchedResults;
import com.appmagnet.fintaskanyplace.dataobjects.BusinessObject;
import com.appmagnet.fintaskanyplace.util.Util;

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
        ((SearchedResults)context).startMaps(busObj);

    }
}
