package com.appmagnet.fintaskanyplace.dataobjects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by satyajeet and anmol on 11/6/2015.
 */
public class BusinessObject implements Parcelable {

    public static final String NAME = "NAME";
    public static final String RATING = "RATING";
    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "lng";

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

    }
}
