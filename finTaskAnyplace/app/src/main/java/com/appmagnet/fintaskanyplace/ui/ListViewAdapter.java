package com.appmagnet.fintaskanyplace.ui;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.appmagnet.fintaskanyplace.R;
import com.appmagnet.fintaskanyplace.dataobjects.BusinessObject;

import java.util.ArrayList;

/**
 * Created by satyajeet on 11/22/2015.
 */
public class ListViewAdapter extends ArrayAdapter {

    private ArrayList<BusinessObject> listOfBusinessObjects;
    private Context context;

    public ListViewAdapter(Context context, ArrayList<BusinessObject> businessObjects) {
        super(context,-1,businessObjects);
        this.context = context;
        this.listOfBusinessObjects = businessObjects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_map, parent, false);
        TextView businessView = (TextView) rowView.findViewById(R.id.business_text);
        ImageButton button = (ImageButton) rowView.findViewById(R.id.open_in_maps);
        button.setOnClickListener(new OpenInMapListener(context,listOfBusinessObjects.get(position)));
        String text = "<html><b>"+ listOfBusinessObjects.get(position).getBusinessName()+"</b><br>"+
                listOfBusinessObjects.get(position).getBusinessRating()+" Stars<br>"+
                listOfBusinessObjects.get(position).getBusinessDistance()+"</html>";
        businessView.setText(Html.fromHtml(text));



        return rowView;
    }
}
