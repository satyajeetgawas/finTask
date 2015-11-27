package com.appmagnet.fintaskanyplace.yelp;

import android.content.Context;

import com.appmagnet.fintaskanyplace.dataobjects.BusinessObject;
import com.appmagnet.fintaskanyplace.googleservices.GoogleMapsApi;
import com.appmagnet.fintaskanyplace.util.ApiKeys;
import com.appmagnet.fintaskanyplace.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Code sample for accessing the Yelp API V2.
 * <p/>
 * This program demonstrates the capability of the Yelp API version 2.0 by using the Search API to
 * query for businesses by a search term and location, and the Business API to query additional
 * information about the top result from the search query.
 * <p/>
 * <p/>
 * See <a href="http://www.yelp.com/developers/documentation">Yelp Documentation</a> for more info.
 */
public class YelpAPI {

    private static final String API_HOST = "api.yelp.com";
    private static final String SEARCH_PATH = "/v2/search";
    private static final String BUSINESS_PATH = "/v2/business";

    OAuthService service;
    Token accessToken;

    /**
     * Setup the Yelp API OAuth credentials.
     */
    public YelpAPI() {
        this.service =
                new ServiceBuilder().provider(TwoStepOAuth.class).apiKey(ApiKeys.CONSUMER_KEY)
                        .apiSecret(ApiKeys.CONSUMER_SECRET).build();
        this.accessToken = new Token(ApiKeys.TOKEN, ApiKeys.TOKEN_SECRET);
    }

    /**
     * Creates and sends a request to the Search API by term and location.
     * <p/>
     * See <a href="http://www.yelp.com/developers/documentation/v2/search_api">Yelp Search API V2</a>
     * for more info.
     *
     * @param term     <tt>String</tt> of the search term to be queried
     * @param location <tt>String</tt> of the location
     * @return <tt>String</tt> JSON Response
     */
    public ArrayList<BusinessObject> searchForBusinessesByLocation(String term, String location, Context context) {
        OAuthRequest request = createOAuthRequest(SEARCH_PATH);
        request.addQuerystringParameter("term", term);
        request.addQuerystringParameter("ll", location);
        request.addQuerystringParameter("limit", Util.getSearchLimit());
        request.addQuerystringParameter("radius_limit", Util.getRadius(context));
        String responseJsonString = sendRequestAndGetResponse(request);
        return getBusinessObjectsFromJson(responseJsonString,term, location);
    }

    private ArrayList<BusinessObject> getBusinessObjectsFromJson(String responseJsonString, String cat, String user_location ) {
        ArrayList<BusinessObject> listPlaces = null;
        JSONObject json = null;
        JSONArray businesses = null;
       try{
           json = new JSONObject(responseJsonString);
           businesses = json.getJSONArray("businesses");
          }catch(JSONException e) {
           e.printStackTrace();
       }
        if(businesses !=null){
            listPlaces = new ArrayList<BusinessObject>(businesses.length());
            for (int i = 0; i < businesses.length(); i++) {
                try {
                    JSONObject business = businesses.getJSONObject(i);
                    Map<String,String> busProp = new HashMap<String,String>();
                    busProp.put( BusinessObject.NAME , business.getString("name"));
                    busProp.put( BusinessObject.RATING , business.getString("rating"));
                    busProp.put( BusinessObject.LATITUDE , business.getJSONObject("location").getJSONObject("coordinate").getString("latitude"));
                    busProp.put( BusinessObject.LONGITUDE, business.getJSONObject("location").getJSONObject("coordinate").getString("longitude"));
                    busProp.put(BusinessObject.CATEGORY,cat);
                    //busProp.put(BusinessObject.ITEMS,items);
           //         business.getString("location.coordinate.longitude"));
                    String dest_location = business.getJSONObject("location").getJSONObject("coordinate").getString("latitude") + ","+business.getJSONObject("location").getJSONObject("coordinate").getString("longitude");
                    GoogleMapsApi googleMaps = new GoogleMapsApi();
                    String distance = googleMaps.getDistanceFromUser(user_location, dest_location);
                    busProp.put(BusinessObject.DISTANCE, distance);
                    listPlaces.add(new BusinessObject(busProp));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        if(listPlaces==null)
            listPlaces = new ArrayList<BusinessObject>();
        return listPlaces;
    }



    /**
     * Creates and returns an {@link OAuthRequest} based on the API endpoint specified.
     *
     * @param path API endpoint to be queried
     * @return <tt>OAuthRequest</tt>
     */
    private OAuthRequest createOAuthRequest(String path) {
        OAuthRequest request = new OAuthRequest(Verb.GET, "https://" + API_HOST + path);
        return request;
    }

    /**
     * Sends an {@link OAuthRequest} and returns the {@link Response} body.
     *
     * @param request {@link OAuthRequest} corresponding to the API request
     * @return <tt>String</tt> body of API response
     */
    private String sendRequestAndGetResponse(OAuthRequest request) {
        this.service.signRequest(this.accessToken, request);
        Response response = request.send();
        return response.getBody();
    }
}
