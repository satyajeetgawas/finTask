package com.appmagnet.fintaskanyplace.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.appmagnet.fintaskanyplace.R;
import com.appmagnet.fintaskanyplace.dataobjects.BusinessObject;
import com.appmagnet.fintaskanyplace.initializer.LocationHandler;
import com.appmagnet.fintaskanyplace.ui.ListViewAdapter;
import com.appmagnet.fintaskanyplace.util.Constants;
import com.appmagnet.fintaskanyplace.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by anmolgupta on 11/19/15.
 */
public class searchedResults extends AppCompatActivity {

    private ArrayList<BusinessObject> businessList;
    private String items;
    private  ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_results);
        businessList = (ArrayList)getIntent().getParcelableArrayListExtra(Constants.AFTER_NOTIFICATION_LIST);

        items = (String)getIntent().getStringExtra(Constants.CONTENTS_STRING);
        list = (ListView)findViewById(R.id.list_search);
        ArrayList<BusinessObject> addPlaces = new ArrayList<>();
        for(int i=0;i<businessList.size();i++){
            String distStr =  businessList.get(i).getBusinessDistance().replace("miles","");
            if(!distStr.equals("") && !distStr.isEmpty()){
                Double  distance =new Double(distStr.trim());
                if(distance < new Double((Util.getSettings(this, Constants.PROXIMITY_RADIUS))))
                    addPlaces.add(businessList.get(i));
            }

        }
        list.setAdapter(new ListViewAdapter(this,addPlaces));
    }

    public void startMaps(BusinessObject businessObject){
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr=" + LocationHandler.getInstance().getLocationMap().get(LocationHandler.LATITUDE)
                                + "," + LocationHandler.getInstance().getLocationMap().get(LocationHandler.LONGITUDE) +"&daddr="+
                                businessObject.getBusinessLatitude()+","+businessObject.getBusinessLongitude()));
        startActivity(intent);
    }

}

