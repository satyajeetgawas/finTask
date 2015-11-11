package com.appmagnet.fintaskanyplace.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appmagnet.fintaskanyplace.R;
import com.appmagnet.fintaskanyplace.dataobjects.BusinessObject;
import com.appmagnet.fintaskanyplace.util.Constants;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by satyajeet and anmol on 11/10/15.
 */
public class NotificationReciever extends AppCompatActivity {

    private TextView mOutputText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        ArrayList listOfBusiness = getIntent().getParcelableArrayListExtra(Constants.LIST_OF_PLACES);
        LinearLayout activityLayout = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        activityLayout.setLayoutParams(lp);
        activityLayout.setOrientation(LinearLayout.VERTICAL);
        activityLayout.setPadding(16, 16, 16, 16);

        ViewGroup.LayoutParams tlp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        mOutputText = new TextView(this);
        mOutputText.setLayoutParams(tlp);
        mOutputText.setPadding(16, 16, 16, 16);
        mOutputText.setVerticalScrollBarEnabled(true);
        mOutputText.setMovementMethod(new ScrollingMovementMethod());
        activityLayout.addView(mOutputText);

        setContentView(activityLayout);
        ArrayList listOfPlaces = new ArrayList<>();
        for(Object businessObject : listOfBusiness){
            String temp = ((BusinessObject)businessObject).getBusinessName()+" "+((BusinessObject)businessObject).getBusinessRating();
            listOfPlaces.add(temp);

        }
        mOutputText.setText(TextUtils.join("\n", listOfPlaces));
    }


}
