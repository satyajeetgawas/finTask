package com.appmagnet.fintaskanyplace.initializer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by satyajeet on 10/21/2015.
 */
public class LocationHandler implements LocationListener{

    private static LocationHandler instance = null;
    public static final String LATITUDE = "LATITUDE";
    public static final String LONGITUDE = "LONGITUDE";
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    private boolean isLocationEnabled;
    private Map<String,Double> locMap;

    public LocationHandler(final Activity mainActivity){
        locationManager = (LocationManager) mainActivity.getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
            isLocationEnabled = true;
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        else{
            isLocationEnabled = false;
        }
        instance = this;
    }

    public static LocationHandler getInstance(){
        return instance;
    }

    public boolean isLocationEnabled(){
        return isLocationEnabled;
    }

    public Map<String,Double> getLocationMap(){
        locMap = new HashMap<String,Double>();
        Location l = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(null == l){
            l = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
       if(l!=null){
           locMap.put(LATITUDE, l.getLatitude());
           locMap.put(LONGITUDE, l.getLongitude());
       }
      return locMap;
    }



    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onLocationChanged(Location location) {
        locMap.put(LATITUDE,location.getLatitude());
        locMap.put(LONGITUDE,location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if(LocationManager.GPS_PROVIDER.equals(provider)){
            isLocationEnabled = true;
        }
    }
}
