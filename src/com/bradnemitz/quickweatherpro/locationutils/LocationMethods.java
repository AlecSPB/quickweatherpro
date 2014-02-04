package com.bradnemitz.quickweatherpro.locationutils;

import java.util.Date;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;



public class LocationMethods {
	
	static Location currentLocation = null;
	Context mContext;

	public static void updateCurrentLocation(Context mContext) {

	        LocationManager mlocManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE); 
	        final Criteria criteria = new Criteria();
	        criteria.setAccuracy(Criteria.NO_REQUIREMENT);  
	        String provider = mlocManager.getBestProvider(criteria, true);
	        System.out.println("best provider is " + provider);
	        String gpsprovider = "gps";
	        if(provider == null)
	        {
	        	currentLocation = null;
	        }
	        
	        if(provider.equals(gpsprovider))
	        {
	        	boolean hasgpsloc = true;
		        Date now = new Date();
		        Long now_ms = now.getTime();
		        Long last_ms;
		        Long diff_ms = null;
	        	try
	        	{
			        currentLocation = mlocManager.getLastKnownLocation(provider);     
			        last_ms = currentLocation.getTime();
			        diff_ms = now_ms - last_ms;
	        	}
	        	catch(Exception e)
	        	{
	        		hasgpsloc = false;
	        	}
	        
	        	if(hasgpsloc == false)
	        	{
			        if(mlocManager.isProviderEnabled("network"))
		        	{
			        	currentLocation = mlocManager.getLastKnownLocation("network");     
		        	}
			        else
			        {
			        	currentLocation = mlocManager.getLastKnownLocation("passive");     
			        }
	        	}
	        	else if(diff_ms >= 60000)
		        {
			        if(mlocManager.isProviderEnabled("network"))
		        	{
			        	System.out.println("more than 1 minutes, going to network");
			        	currentLocation = mlocManager.getLastKnownLocation("network");     
		        	}
			        else
			        {
			        	System.out.println("more than 1 minutes, network, not enabled");
				        currentLocation = mlocManager.getLastKnownLocation(provider);     
			        }
		        }
	        }
	        else
	        {
	        	System.out.println("not gps");
		        currentLocation = mlocManager.getLastKnownLocation(provider);     
	        }  
	}
	

	public static Location getCurrentLocation() {
		return currentLocation;	
	}
	
	public static void setupLocationUpdates(GPSTracker gps, Context mContext)
	    {
	        LocationManager mlocManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);  
	        if(gps.isGPSEnabled)
	        {
		        mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, gps);  
	        }
	        else if(gps.isNetworkEnabled)
	        {
		        mlocManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, 0, 0, gps);  
	        }
	        else
	        {
		        mlocManager.requestLocationUpdates( LocationManager.PASSIVE_PROVIDER, 0, 0, gps);  
	        }
	    }
	 
	 public static void stopLocationUpdates(Context mContext)
	    {
	        GPSTracker gps = new GPSTracker(mContext);
	        gps.stopUsingGPS();
	    }
	
	
	
	
}