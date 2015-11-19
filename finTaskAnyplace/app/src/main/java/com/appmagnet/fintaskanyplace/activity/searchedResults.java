package com.appmagnet.fintaskanyplace.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.appmagnet.fintaskanyplace.R;
import com.appmagnet.fintaskanyplace.dataobjects.BusinessObject;
import com.appmagnet.fintaskanyplace.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by anmolgupta on 11/19/15.
 */
public class searchedResults extends AppCompatActivity {

    private ArrayList businessList;
    private String items;
    private String[] businesses;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_results);
        businessList = (ArrayList)getIntent().getParcelableExtra(Constants.AFTER_NOTIFICATION_LIST);
        items = (String)getIntent().getStringExtra(Constants.CONTENTS_STRING);
        //String temp;
        int i = 0;
        for(Object businessObject : businessList) {
            String temp = ((BusinessObject) businessObject).getBusinessName() + "\n" + ((BusinessObject) businessObject).getBusinessRating();
            businesses[i] = temp;
            i++;
        }
        createUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_fin_task_anyplace, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id==R.id.action_settings){
            startSettingsActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void startSettingsActivity(){
        Intent intent = new Intent(this,SettingsActivity.class);
        startActivity(intent);
    }

    public void createUI() {

        if (items.endsWith(",")) {
            int n = items.lastIndexOf(",");
            items = items.substring(0, n);
        }
        //LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll_example);

        // Add textview 1
        TextView textView1 = (TextView) findViewById(R.id.text_search);
        textView1.setText(items);

        ListView ListView1 = (ListView) findViewById(R.id.list_search);
        ArrayAdapter<String> businessAdapter = new ArrayAdapter<String>(this, R.layout.abc_list_menu_item_layout, businesses);
        ListView1.setAdapter(businessAdapter);

        ListView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //The position where the list item is clicked is obtained from the
                //the parameter position of the android listview
                int itemPosition = position;

                //Get the String value of the item where the user clicked
                //String itemValue = (String) mListView.getItemAtPosition(position);

                //In order to start displaying new activity we need an intent
                //Intent intent = new Intent(getApplicationContext(), CountryActivity.class);

                //Putting the Id of image as an extra in intent
                //intent.putExtra("flag", FlagId[position]);

                //Here we will pass the previously created intent as parameter
                //startActivity(intent);

            }


        });


    }
}

