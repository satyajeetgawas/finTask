package com.appmagnet.fintaskanyplace.dataobjects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by satyajeet and anmol on 11/6/2015.
 */
public class BusinessObject implements Parcelable ,Comparable<BusinessObject> {

    public static final String NAME = "NAME";
    public static final String RATING = "RATING";
    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "lng";
    public static final String CATEGORY = "cat";
    public static final String DISTANCE = "dist";
    //public static final String ITEMS = "items";

   private Map<String,String> businessProperties;

    public BusinessObject(Map<String, String> businessProperties) {
        this.businessProperties = businessProperties;
    }


    public static final Creator<BusinessObject> CREATOR = new Creator<BusinessObject>() {
        @Override
        public BusinessObject createFromParcel(Parcel in) {
            Map prop = new HashMap();
            prop.put(NAME,in.readString());
            prop.put(RATING,in.readString());
            prop.put(LATITUDE,in.readString());
            prop.put(LONGITUDE,in.readString());
            prop.put(CATEGORY,in.readString());
            prop.put(DISTANCE,in.readString());
            //prop.put(ITEMS,in.readString());
            return new BusinessObject(prop);
        }

        @Override
        public BusinessObject[] newArray(int size) {
            return new BusinessObject[size];
        }
    };

    public String getBusinessName(){
        return businessProperties.get(NAME);
    }

    public String getBusinessRating(){
        return businessProperties.get(RATING);
    }

    public String getBusinessLatitude(){
        return businessProperties.get(LATITUDE);
    }

    public String getBusinessLongitude(){
        return businessProperties.get(LONGITUDE);
    }

    public String getBusinessCategory(){
        return businessProperties.get(CATEGORY);
    }

    public String getBusinessDistance(){
        return businessProperties.get(DISTANCE);
    }

    //public String getBusinessItems() { return businessProperties.get(ITEMS);}

    public Map<String,String> getBusinessProperties(){
        return businessProperties;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(getBusinessName());
        parcel.writeString(getBusinessRating());
        parcel.writeString(getBusinessLatitude());
        parcel.writeString(getBusinessLongitude());
        parcel.writeString(getBusinessCategory());
        parcel.writeString(getBusinessDistance());
        //parcel.writeString(getBusinessItems());

    }

    @Override
    public int compareTo(BusinessObject o) {
        return (int)(Float.parseFloat(o.getBusinessRating())*10 - Float.parseFloat(getBusinessRating())*10);
    }
}
