package com.bradnemitz.quickweatherpro;

import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.RemoteViews;


public class Widget extends AppWidgetProvider {
	
	
	 //Strings set from JSON
    static String timeZone;
    static String current_summary;
    static String current_icon;
    static String current_precipIntensity;
    static String current_temp;
    static String current_dewPoint;
    static String current_windSpeed;
    static String current_windBearing;
    static String current_cloudCover;
    static String current_humidity;
    static String current_pressure;
    static String current_visibility;
    static String current_ozone;
    static String windBearing; 
    static String summary_minutely;
    static String summary_hourly;
    static String summary_daily;
    static String current_city = "current location";
    static int temp_int;
    static float current_humidity_f;
    static float current_humidity_percent;
    static int current_humidity_percent_int;
    static float current_cloudcover_f;
    static float current_cloudcover_percent;
    static int current_cloudcover_percent_int;
    static int current_dewPoint_int;
    Date lastUpdateTime;
    long lastUpdateTime_l;
    
    private static final String TAG_TIMEZONE = "timezone";
    private static final String TAG_CURRENTLY = "currently";
    private static final String TAG_CURRENTLY_SUMMARY = "summary";
    private static final String TAG_CURRENTLY_ICON = "icon";
    private static final String TAG_CURRENTLY_TEMP = "temperature";
    private static final String TAG_MINUTELY = "minutely";
    private static final String TAG_MINUTELY_SUMMARY = "summary";
    private static final String TAG_HOURLY = "hourly";
    private static final String TAG_HOURLY_SUMMARY = "summary";
    private static final String TAG_DAILY = "daily";
    private static final String TAG_DAILY_SUMMARY = "summary";
    // JSONArray
    static JSONArray hourly_array = null;
    static JSONArray daily_array = null;
	
	
	
	@Override
    public void onUpdate( Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds )
    {
		
		RemoteViews remoteViews;
        ComponentName widget;
        
        widget = new ComponentName( context, Widget.class );
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
        
        String url = "https://api.forecast.io/forecast/317e87f882c890e30f4a5e9b3c05c7c0/35.90247097425163,-79.06580186448991";
        
    	System.out.println("Checking data from WIDGET");
        MainActivity.CheckForDataConnection(context);
    	System.out.println("Calling PostTask from WIDGET");
    	
        new PostTask(remoteViews, widget, appWidgetManager).execute(url);
                
    }
	
	void updateWidgetUI(RemoteViews rv){
		
		System.out.println("Updating text to:" + current_summary);

        rv.setTextViewText(R.id.widget_now_summary, current_summary);
        rv.setTextViewText(R.id.widget_next_hour_summary, summary_minutely);
        rv.setTextViewText(R.id.widget_next_24_summary, summary_hourly);

	}
	
	
    class PostTask extends AsyncTask<String, Integer, String> {
    	private RemoteViews remoteViews;
		private ComponentName widget;
		private AppWidgetManager WidgetManager;
    	
  		   public PostTask(RemoteViews remoteViews, ComponentName widget, AppWidgetManager awm) {
			this.remoteViews = remoteViews;
			this.widget = widget;
			this.WidgetManager = awm;
		}

		@Override
  		   protected void onPreExecute() {
  		      super.onPreExecute();
  		   }
  		
  		   @Override
  		   protected String doInBackground(String... params) {
  			             

  			   
  		      String url=params[0];
  		      
  		      System.out.println("WIDGET GOT IT: " + url);
  		      
  		      ParseJSON2(url);
 
  			return null;
  		      
  		   }
  		
  		   @Override
  		   protected void onProgressUpdate(Integer... values) {
  		      super.onProgressUpdate(values);
  		
  		   }
  		
  		   @Override
  		  protected void onPostExecute(String result) {
  		      super.onPostExecute(result);
  	    	System.out.println("Okay - let's update!!");
  	        updateWidgetUI(remoteViews);
  	        WidgetManager.updateAppWidget(widget, remoteViews);
  		    
  		   }
   }			
	
public static void ParseJSON2(String url){
    	
    	System.out.println("Starting to parse JSON from WIDGET");
    	// Creating JSON Parser instance
    	JSONParser jParser = new JSONParser();
    	System.out.println("Created a new JSONParser from WIDGET");


    	
        	 
    	try {    
    			JSONObject c = jParser.getJSONFromUrl(url);
    			System.out.println("Created JSONObject IN WIDGET");
    			timeZone = c.getString(TAG_TIMEZONE);
    			
    			System.out.println("Doing current weathing *Widget*");
    	        JSONObject currently = c.getJSONObject(TAG_CURRENTLY);
    	        current_summary = currently.getString(TAG_CURRENTLY_SUMMARY);
    	        current_icon = currently.getString(TAG_CURRENTLY_ICON);
    	        current_temp = currently.getString(TAG_CURRENTLY_TEMP);
    	        temp_int = (int)Float.parseFloat(current_temp);
    	        
    	        JSONObject minutely = c.getJSONObject(TAG_MINUTELY);
    	        summary_minutely = minutely.getString(TAG_MINUTELY_SUMMARY);
    	        JSONObject hourly = c.getJSONObject(TAG_HOURLY);
    	        summary_hourly = hourly.getString(TAG_HOURLY_SUMMARY);
    	        JSONObject daily = c.getJSONObject(TAG_DAILY);
    	        summary_daily = daily.getString(TAG_DAILY_SUMMARY);
    	       	
    			System.out.println("Done with JSON! IN WIDGET");

    	
    	} catch (JSONException e) {
    	    e.printStackTrace();
    	}  
    	
    	
    }
    
	
	
}
