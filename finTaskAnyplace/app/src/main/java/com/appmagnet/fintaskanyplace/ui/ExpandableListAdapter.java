package com.appmagnet.fintaskanyplace.ui;

/**
 * Created by anmolgupta on 11/13/15.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.appmagnet.fintaskanyplace.R;
import com.appmagnet.fintaskanyplace.activity.MainActivity;
import com.appmagnet.fintaskanyplace.activity.NotificationReciever;
import com.appmagnet.fintaskanyplace.dataobjects.NoteObject;
import com.appmagnet.fintaskanyplace.ui.DeleteButtonLisener;
import com.appmagnet.fintaskanyplace.util.Util;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<NoteObject>> _listDataChild;
    private Map<String,ArrayList> _mapOfBusinessess;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<NoteObject>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        this._mapOfBusinessess = null;
    }

    public ExpandableListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<NoteObject>> listDataChild, HashMap<String, ArrayList> mapOfBusiness) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listDataChild;
        this._mapOfBusinessess = mapOfBusiness;

    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        NoteObject noteObj = (NoteObject)getChild(groupPosition, childPosition);

        final String childText = (String) noteObj.getFormattedString();

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);

        convertView.setOnLongClickListener(new DeleteButtonLisener(this, _context, noteObj));
        convertView.setOnClickListener(new SearchNearbyBtnListener(_context,noteObj,_mapOfBusinessess));
        txtListChild.setText(Html.fromHtml(childText));
        convertView.setBackgroundColor(Color.WHITE);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }
        ImageButton searchNearbyBtn = (ImageButton)convertView.findViewById(R.id.search_button);
        searchNearbyBtn.setFocusable(false);
        searchNearbyBtn.setOnClickListener(new SearchNearbyBtnListener(_context,headerTitle,_mapOfBusinessess));

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(Util.getHeaderDisplayName(headerTitle));
        convertView.setBackgroundColor(Util.getHeaderBackgroundColor(convertView,headerTitle));

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void removeChild(NoteObject obj){
        List noteList = _listDataChild.get(obj.getNoteType());
        noteList.remove(obj);
        if(noteList.size()==0){
            _listDataChild.remove(obj.getNoteType());
            _listDataHeader.remove(obj.getNoteType());
        }else{
            _listDataChild.put(obj.getNoteType(),noteList);
        }
        ((MainActivity)_context).createUI();
    }


}
