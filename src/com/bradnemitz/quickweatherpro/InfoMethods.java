package com.bradnemitz.quickweatherpro;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.preference.PreferenceManager;

import com.arcusweather.forecastio.ForecastIO;
import com.arcusweather.forecastio.ForecastIOResponse;
import com.bradnemitz.quickweatherpro.locationutils.LocationMethods;

public class InfoMethods {

	Context mContext;
	
	static String current_city;	
	static String current_icon;
	static String temp;
	static int temp_int;
    static String current_summary;
    static String summary_minutely;
    static String summary_hourly;
    static String summary_daily;

public static void updateFIO(Context mContext){
	
	System.out.println("Using arcus weather lib from InfoMethods to update");
	
	LocationMethods.updateCurrentLocation(mContext);
	Location location = LocationMethods.getCurrentLocation();
	
	double lat = 0;
	double lng = 0;
	
	if(location != null){
	    lat = (double) (location.getLatitude());
	    lng = (double) (location.getLongitude());
	    
	    getCityName(mContext, lat, lng);
		
	    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
	    String unitPref = sharedPref.getString("units_selection", "us");
		
	    ForecastIO FIO = new ForecastIO("317e87f882c890e30f4a5e9b3c05c7c0", lat, lng);
		System.out.println("Creating FIO...");
	
	
	    //ability to set the units, exclude blocks, extend options and user agent for the request. This is not required.
	    HashMap<String, String> requestParams = new HashMap<String, String>();
	    requestParams.put("units", unitPref);
	    FIO.setRequestParams(requestParams);
	    FIO.makeRequest();
		System.out.println("Making request FIO...");
	
	    String responseString = FIO.getResponseString();  
	    ForecastIOResponse FIOR = new ForecastIOResponse(responseString);
		System.out.println("Creating FIO ResponseString...");
		
		current_icon = FIOR.getCurrently().getValue("icon");
		temp = FIOR.getCurrently().getValue("temperature");
			
		if(temp != null){
				//getValue("temperature") returns numbers like 71.23, so this:
				//Converts the String to Float, then truncates it to an Int.
				//Then you'll convert it back to a String.
				//That's probably not a good way to do things.
				//Oh well.
				temp_int = (int)Float.parseFloat(temp);
				temp = String.valueOf(temp_int);
		}
		
	    current_summary = FIOR.getCurrently().getValue("summary");
	    summary_minutely = FIOR.getMinutely().getValue("summary");
	    summary_hourly = FIOR.getHourly().getValue("summary");
	    summary_daily = FIOR.getDaily().getValue("summary");
	    /*
	     * Add ALL the other strings you want to grab here.
	     */
	    
	} else {
		//Location was null. Do something about it.
	}
}

public static void getCityName(Context mContext, double lat, double lng){
	//get current city
    Geocoder gcd = new Geocoder(mContext, Locale.getDefault());
    List<Address> addresses = null;
    try {
		addresses = gcd.getFromLocation(lat, lng, 1);
    } catch (IOException e) {
		
		e.printStackTrace();
    }
    if (addresses != null) {
  	  try{
        current_city = addresses.get(0).getLocality();
        System.out.println(current_city);
  	  } catch (NullPointerException e) {
  		  //There's an error! Do something
  	  } catch (IndexOutOfBoundsException i) {
  		  //Different error! Oh no!
    	  }
    }
}


public static void storeInfo(SharedPreferences vars) {
    SharedPreferences.Editor prefEditor = vars.edit();
    prefEditor.putString("current_city", current_city);
	prefEditor.putString("current_icon", current_icon);    
	prefEditor.putString("current_summary", current_summary);
	prefEditor.putString("temp", temp);
	prefEditor.putString("summary_minutely", summary_minutely);
	prefEditor.putString("summary_hourly", summary_hourly);
	prefEditor.putString("summary_daily", summary_daily);
	/*
	 * Store ALL the other strings you grabbed here.
	 */
    prefEditor.commit(); 
}


}