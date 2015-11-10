package com.appmagnet.fintaskanyplace.dataobjects;

import java.util.Map;

/**
 * Created by satyajeet and anmol on 11/6/2015.
 */
public class BusinessObject {

    public static final String NAME = "NAME";
    public static final String RATING = "RATING";
    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "lng";

   private Map<String,String> businessProperties;

    public BusinessObject(Map<String, String> businessProperties) {
        this.businessProperties = businessProperties;
    }

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



}
