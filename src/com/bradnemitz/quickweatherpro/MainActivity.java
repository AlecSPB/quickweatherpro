package com.bradnemitz.quickweatherpro;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arcusweather.forecastio.ForecastIO;
import com.arcusweather.forecastio.ForecastIODataPoint;
import com.arcusweather.forecastio.ForecastIOResponse;
import com.bradnemitz.quickweatherpro.locationutils.*;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

public class MainActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, LocationListener  {
   
	LocationClient mLocationClient;
	Location mCurrentLocation;
	Location mFusedLocation;
	private GPSTracker gps;
	
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mNavTitles;
    

    private double lng;
    private double lat;
    private static String unitPref;
    
    private static String uiPref;
    private static boolean userOrGPS;
    static String userSetLocation;
   
    
static Context mContext;
	
	static ForecastIOResponse FIOR; 

	static int temp_int;
    static String
    current_summary,
    current_icon,
    current_time, 
    current_precipIntensity,
    current_precipProbability,
    current_precipType,
    current_temperature,
    current_apparentTemperature,
    current_dewPoint,
    current_humidity,
    current_windSpeed,
    current_windBearing,
    current_visibility,
    current_cloudCover,
    current_pressure,
    current_ozone;
    
    static String minutely_summary,
    minutely_icon;
    
    static String summary_hourly;
    
    static ForecastIODataPoint[] minutelyPoints, hourlyPoints, dailyPoints;
    
    static String[] 
    		hourly_times,
    		hourly_temps;
    		
    
    
    static String summary_daily;
    
    static String timeZone;
    static String current_temp;
    static String windBearing; 
    static String current_city = "Quick Weather Pro";
    static float current_humidity_f;
    static float current_humidity_percent;
    static int current_humidity_percent_int;
    static float current_cloudcover_f;
    static float current_cloudcover_percent;
    static int current_cloudcover_percent_int;
    static int current_dewPoint_int;
    Date lastUpdateTime;
    long lastUpdateTime_l;
    
    static String percent;
    
    static int i = 0;
    
    static View rootView = null;

    static boolean notUpdated = true;

    
    private static final String TAG_TIMEZONE = "timezone";
    private static final String TAG_CURRENTLY = "currently";
    private static final String TAG_CURRENTLY_SUMMARY = "summary";
    private static final String TAG_CURRENTLY_ICON = "icon";
    private static final String TAG_CURRENTLY_PRECIPINTENSITY = "precipIntensity";
    private static final String TAG_CURRENTLY_TEMP = "temperature";
    private static final String TAG_CURRENTLY_DEWPOINT = "dewPoint";
    private static final String TAG_CURRENTLY_WINDSPEED = "windSpeed";
    private static final String TAG_CURRENTLY_WINDBEARING = "windBearing";
    private static final String TAG_CURRENTLY_CLOUDCOVER = "cloudCover";
    private static final String TAG_CURRENTLY_HUMIDITY = "humidity";
    private static final String TAG_CURRENTLY_PRESSURE = "pressure";
    private static final String TAG_CURRENTLY_VISIBILITY = "visibility";
    private static final String TAG_CURRENTLY_OZONE = "ozone";
    private static final String TAG_MINUTELY = "minutely";
    private static final String TAG_MINUTELY_SUMMARY = "summary";
    private static final String TAG_HOURLY = "hourly";
    private static final String TAG_HOURLY_DATA = "data";
    private static final String TAG_HOURLY_SUMMARY = "summary";
    private static final String TAG_HOURLY_TIME = "time";
    private static final String TAG_HOURLY_ICON = "icon";
    private static final String TAG_HOURLY_PRECIPINTENSITY = "precipIntensity";
    private static final String TAG_HOURLY_PRECIPTYPE = "precipType";
    private static final String TAG_HOURLY_TEMP = "temperature";
//    private static final String TAG_HOURLY_VISIBILITY = "visibility";
//    private static final String TAG_HOURLY_DEWPOINT = "dewPoint";
//    private static final String TAG_HOURLY_CLOUDCOVER = "cloudCover";
//    private static final String TAG_HOURLY_HUMIDITY = "humidity";
//    private static final String TAG_HOURLY_PRESSURE = "pressure";
//    private static final String TAG_HOURLY_OZONE = "ozone";
    private static final String TAG_HOURLY_PRECIPPROB = "precipProbability";
    private static final String TAG_DAILY = "daily";
    private static final String TAG_DAILY_SUMMARY = "summary";
    private static final String TAG_DAILY_DATA = "data";
    private static final String TAG_DAILY_TIME = "time";
    private static final String TAG_DAILY_ICON = "icon";
//    private static final String TAG_DAILY_SUNRISE = "sunriseTime";
//    private static final String TAG_DAILY_SUNSET = "sunsetTime";
    private static final String TAG_DAILY_PRECIPINTENSITY = "precipIntensity";
    private static final String TAG_DAILY_PRECIPINTENSITYMAX = "precipIntensityMax";
    private static final String TAG_DAILY_PRECIPTYPE = "precipType";
    private static final String TAG_DAILY_TEMPMIN = "temperatureMin";
    private static final String TAG_DAILY_TEMPMAX = "temperatureMax";
//    private static final String TAG_DAILY_TEMPMINTIME = "temperatureMinTime";
//    private static final String TAG_DAILY_TEMPMAXTIME = "temperatureMaxTime";
//    private static final String TAG_DAILY_DEWPOINT = "dewPoint";
//    private static final String TAG_DAILY_WINDSPEED = "windSpeed";
//    private static final String TAG_DAILY_WINDBEARING = "windBearing";
//    private static final String TAG_DAILY_CLOUDCOVER = "cloudCover";
//    private static final String TAG_DAILY_HUMIDITY = "humidity";
//    private static final String TAG_DAILY_PRESSURE = "pressure";
//    private static final String TAG_DAILY_OZONE = "ozone";

    
    // JSONArray
    static JSONArray hourly_array = null;
    static JSONArray daily_array = null;
    
    
    
    
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mLocationClient = new LocationClient(this, this, this);
        mLocationClient.connect();

        mTitle = mDrawerTitle = getTitle();
        mNavTitles = getResources().getStringArray(R.array.nav_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mNavTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
            	if(uiPref.equals("New")){
            		getActionBar().setTitle(current_city);
            	} else {
            		getActionBar().setTitle(mTitle);
            	}
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
            	if(uiPref.equals("New")){
            		getActionBar().setTitle(current_city);
            	} else {
            		getActionBar().setTitle(mDrawerTitle);
            	}
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        
        
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        uiPref = sharedPref.getString("ui_selection", "New");
        unitPref = sharedPref.getString("unitPref", "us");
        userOrGPS = sharedPref.getBoolean("userLocation", false);
        

        if (savedInstanceState == null) {
            selectItem(0);
        }
        if (CheckForDataConnection(getBaseContext()) == false){
			Toast.makeText(getBaseContext(), getResources().getString(R.string.no_data_toast_title), Toast.LENGTH_LONG).show();
		}
        
    }
    
    class PostTask extends AsyncTask<String, Integer, String> {
 	   
 	   int myProgress;
 	   private ProgressDialog pd;
 	   

 		   @Override
 		   protected void onPreExecute() {
 		      super.onPreExecute();
 		      	  pd = new ProgressDialog(MainActivity.this);
	              pd.setTitle("Updating Weather Information...");
	              pd.setMessage("Please wait...");
	              pd.setCancelable(false);
	              pd.setIndeterminate(true);
	              pd.show();
 		    	   myProgress = 0;
 		   }
 		
 		   @Override
 		   protected String doInBackground(String... params) {
 			  
 			  SharedPreferences vars = getSharedPreferences("Variables", 0);  
 			  updateFIO(getBaseContext());
 			  storeInfo(vars, getBaseContext());
 			  retrieveInfo(vars);
 			  
 			  runOnUiThread(new Runnable(){
 				  @Override
 				  public void run(){
 		 			  getActionBar().setTitle(current_city);

 				  }
 			  });

 		      updateUI();
 		       		      
 		      notUpdated = false;
 		      
 			return null;
 		      
 		   }
 		
 		   @Override
 		   protected void onProgressUpdate(Integer... values) {
 		      super.onProgressUpdate(values);
 		
 		   }
 		
 		   @Override
 		  protected void onPostExecute(String result) {
 		      super.onPostExecute(result);
 		      pd.dismiss();
 		    
 		   }
  }			
   
      
    /* Request updates at startup */ 
    @Override
    protected void onResume() {
    	
    	if(gps == null){
    		gps = new GPSTracker(this);
    	}
    	LocationMethods.setupLocationUpdates(gps, this);
        super.onResume();
        
    	}
    	
    /* Remove the location listener updates when Activity is paused */
    @Override
    protected void onPause() {
    	gps.stopUsingGPS();
    	super.onPause();
	}
	    
	    @Override
		public void onConnectionFailed(ConnectionResult arg0) {
	    		//do stuff
        	notUpdated = true;
        	// Notify of no location service
        	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
       	 
			// set title
			alertDialogBuilder.setTitle(getResources().getString(R.string.no_gps_toast_title));
			alertDialogBuilder.setMessage("Error: \n" + arg0);
			// set dialog message
			alertDialogBuilder
				.setCancelable(false)
				.setNeutralButton("Okay",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
					}
				});
 
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
 
				// show it
				alertDialog.show();
		}

		@Override
		public void onConnected(Bundle arg0) {
			new PostTask().execute();
		}

		@Override
		public void onDisconnected() {
	       //do stuff here too
		}
    
    public void updateUI(){
    	   	    	
        Fragment fragment = new FragmentMaker();
        Bundle args = new Bundle();
        args.putInt(FragmentMaker.NAV_INDEX, i);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
    }
    
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
    	
    		HashMap<String, String> requestParams = new HashMap<String, String>();
    	    requestParams.put("units", unitPref);
    	    FIO.setRequestParams(requestParams);
    	    FIO.makeRequest();
    		System.out.println("Making request FIO...");
    	
    	    String responseString = FIO.getResponseString();  
    	    FIOR = new ForecastIOResponse(responseString);
    		System.out.println("Creating FIO ResponseString...");
    		
    		current_icon = FIOR.getCurrently().getValue("icon");
    		current_temperature = FIOR.getCurrently().getValue("temperature");
    	    current_apparentTemperature = FIOR.getCurrently().getValue("apparentTemperature");
    			
    		if(current_temperature != null){
    				//getValue("temperature") returns numbers like 71.23, so this:
    				//Converts the String to Float, then truncates it to an Int.
    				//Then you'll convert it back to a String.
    				//That's probably not a good way to do things.
    				//Oh well.
    				temp_int = (int)Float.parseFloat(current_temperature);
    				current_temperature = String.valueOf(temp_int);
    		}
    		if(current_apparentTemperature != null){
    			temp_int = (int)Float.parseFloat(current_apparentTemperature);
    			current_apparentTemperature = String.valueOf(temp_int);
    	}
    		
    	    current_summary = FIOR.getCurrently().getValue("summary");
    	    minutely_summary = FIOR.getMinutely().getValue("summary");
    	    summary_hourly = FIOR.getHourly().getValue("summary");
    	    summary_daily = FIOR.getDaily().getValue("summary");
    	    current_time = FIOR.getCurrently().getValue("time"); 
    	    current_precipIntensity = FIOR.getCurrently().getValue("precipIntensity");
    	    current_precipProbability = FIOR.getCurrently().getValue("precipProbability");
    	    current_precipType = FIOR.getCurrently().getValue("precipType");
    	    current_dewPoint = FIOR.getCurrently().getValue("dewPoint");
    	    current_humidity = FIOR.getCurrently().getValue("humidity");
    	    current_windSpeed = FIOR.getCurrently().getValue("windSpeed");
    	    current_windBearing = FIOR.getCurrently().getValue("windBearing");
    	    current_visibility = FIOR.getCurrently().getValue("visibility");
    	    current_cloudCover = FIOR.getCurrently().getValue("cloudCover");
    	    current_pressure = FIOR.getCurrently().getValue("pressure");
    	    current_ozone = FIOR.getCurrently().getValue("ozone");
    	    
    	    timeZone = FIOR.getValue("timezone"); ///THIS IS SO PASSING IT TO DAY FROM UNIX THING DOESN"T CRASH>!.
    	    
    	    /*
    	     * Add ALL the other strings you want to grab here.
    	     */
    	    
    	    minutelyPoints = FIOR.getDataPoints("minutely");
    	    hourlyPoints = FIOR.getDataPoints("hourly");
    	    dailyPoints = FIOR.getDataPoints("daily");
    	   	    
    	    
    	    
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


    public static void storeInfo(SharedPreferences vars, Context mContext) {
        SharedPreferences.Editor prefEditor = vars.edit();
        prefEditor.putString("current_city", current_city);
    	prefEditor.putString("current_icon", current_icon);    
    	prefEditor.putString("current_summary", current_summary);
    	prefEditor.putString("current_temperature", current_temperature);
    	prefEditor.putString("current_apparentTemperature", current_apparentTemperature);
    	prefEditor.putString("minutely_summary", minutely_summary);
    	prefEditor.putString("summary_hourly", summary_hourly);
    	prefEditor.putString("summary_daily", summary_daily);
    	prefEditor.putString("current_time", current_time);
    	prefEditor.putString("current_precipIntensity", current_precipIntensity);
    	prefEditor.putString("current_precipProbability", current_precipProbability);
    	prefEditor.putString("current_precipType", current_precipType);
    	prefEditor.putString("current_dewPoint", current_dewPoint);
    	prefEditor.putString("current_humidity", current_humidity);
        prefEditor.putString("current_windSpeed", current_windSpeed);
        prefEditor.putString("current_windBearing", current_windBearing);
        prefEditor.putString("current_visibility", current_visibility);
        prefEditor.putString("current_cloudCover", current_cloudCover);
        prefEditor.putString("current_pressure", current_pressure);
        prefEditor.putString("current_ozone", current_ozone);
    	/*
    	 * Store ALL the other strings you grabbed here.
    	 */
        prefEditor.commit();
        
    }
    
    
    public void retrieveInfo(SharedPreferences vars){
    	current_city = vars.getString("current_city", "Unknown");
		current_icon = vars.getString("current_icon", "Unknown");
		current_summary = vars.getString("current_summary", "No data.");
		current_temperature = vars.getString("current_temperature", "??");
		minutely_summary = vars.getString("minutely_summary", "No data.");
		summary_hourly = vars.getString("summary_hourly", "No data.");
		summary_daily = vars.getString("summary_daily", "No data.");
		
			    
	    current_time = vars.getString("current_time", "Unknown"); 
	    current_precipIntensity = vars.getString("current_precipIntensity", "No data.");
	    current_precipProbability = vars.getString("current_precipProbability", "No data.");
	    current_precipType = vars.getString("current_precipType", "No data.");
	    current_dewPoint = vars.getString("current_dewPoint", "No data.");
	    current_humidity = vars.getString("current_humidity", "No data.");
	    current_windSpeed = vars.getString("current_windSpeed", "No data.");
	    current_windBearing = vars.getString("current_windBearing", "No data.");
	    current_visibility = vars.getString("current_visibility", "No data.");
	    current_cloudCover = vars.getString("current_cloudCover", "No data.");
	    current_pressure = vars.getString("current_pressure", "No data.");
	    current_ozone = vars.getString("current_ozone", "No data.");
		
		
		/*
		 * Here is where you'd retrieve all the strings you stored in InfoMethods.
		 */
	    
    }
    
	@Override
	public void onLocationChanged(Location location) {
    	//empty
	}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    	//empty
    }

    @Override
    public void onProviderEnabled(String provider) {
    	//empty
    }

    @Override
    public void onProviderDisabled(String provider) {
    	//empty
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       MenuInflater inflater = getMenuInflater();
       inflater.inflate(R.menu.main, menu);
       return super.onCreateOptionsMenu(menu);  
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
        
        case R.id.action_about:
        	
        	startActivity(new Intent(this, About.class));
        	
        	return true;                
        case R.id.action_settings:
        	
        	startActivity(new Intent(this, Settings.class));
        	
        	return true;        
        case R.id.action_refresh:
        	
        	if (CheckForDataConnection(getBaseContext()) == false){
        		
            	// Notify of no location service
            	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
           	 
    			// set title
    			alertDialogBuilder.setTitle(getResources().getString(R.string.no_data_toast_title));
    			alertDialogBuilder.setMessage(getResources().getString(R.string.no_data_toast_message));
    			// set dialog message
    			alertDialogBuilder
    				.setCancelable(false)
    				.setNeutralButton("Okay",new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog,int id) {
    						dialog.cancel();
    					}
    				});
     
    				// create alert dialog
    				AlertDialog alertDialog = alertDialogBuilder.create();
     
    				// show it
    				alertDialog.show();
        		
        	} else {
        		/*
        		 * This should be the part that keeps you from updating too quickly.
        		 * Right now it doesn't work.
        		 * Fix it somehow?
        		 * 
        		 */
        		    Calendar cal = Calendar.getInstance();
        		    if(timeZone != null) {
	                    TimeZone tz = TimeZone.getTimeZone(timeZone);
	                    cal.setTimeZone(tz);
        		    }
        		    
                    cal.setTimeInMillis(System.currentTimeMillis());
                    Date timeNow = cal.getTime();
                    long timeNow_l = timeNow.getTime();
                	
                	if(timeNow_l < (lastUpdateTime_l + 300000)){
            			           			
            			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            	 
            				// set title
            				alertDialogBuilder.setTitle("Weather is already up to date!");
            	 
            				// set dialog message
            				alertDialogBuilder
            					.setCancelable(false)
            					.setNeutralButton("Okay",new DialogInterface.OnClickListener() {
            						public void onClick(DialogInterface dialog,int id) {
            							dialog.cancel();
            						}
            					});
            	 
            					// create alert dialog
            					AlertDialog alertDialog = alertDialogBuilder.create();
            	 
            					// show it
            					alertDialog.show();
            			
                    } else { 
                		new PostTask().execute();
                    }
    		}
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    void widgetUpdate(){
    	if (CheckForDataConnection(getBaseContext()) == false){
			Toast.makeText(getBaseContext(), "You do not currently have data connection.", Toast.LENGTH_LONG).show();
		} else {
			new PostTask().execute();
		}
    }
    

    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position); 
                       
            //HOW THE DRAWER SWITCHER IS WORKING
            ////////////////////////////////////
            //onItemClick calls selectItem, passing the variable 'position'
            //selectItem then creates a new fragment - calling FragmentMaker() - and also passes it 'position'
            //(Though, passing position to PlanetFragemnt() is done via a bundle)
            //FragmentMaker() then inflates the standard 'fragment_planet.xml' view 
            //It takes the position,  uses it to find the correct string in nav_array, and update the title (maybe? not sure why we have setTitle then
            //Back to selectItem, we .commit replacing the frame
            //Then update current item in the drawer, set the title, and close the drawer
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
	    	
        Fragment fragment = new FragmentMaker();
        Bundle args = new Bundle();
        args.putInt(FragmentMaker.NAV_INDEX, position);
        fragment.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
    
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        userOrGPS = sharedPref.getBoolean("userLocation", false);

        /* PRETTY SURE I DON'T NEED TO SET BACKGROUND COLOR NOW
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(Color.parseColor("#222222"));
        getActionBar().setBackgroundDrawable(colorDrawable); */
        getActionBar().setTitle(current_city);
 

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        //YOU DON'T SET THE TITLE TO THE NAME ANYMORE.
        //setTitle(mNavTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /*
     * This is a MASSIVE and unwieldy section of code for updating the UI fragments.
     * Make it better.
     */
    public static class FragmentMaker extends Fragment {
        public static final String NAV_INDEX = "index_number";

        public FragmentMaker() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            i = getArguments().getInt(NAV_INDEX);
            
            if(notUpdated == false){            
            	///UPDATE NEW UI
            	if (i == 0){          		
	        	rootView = inflater.inflate(R.layout.fragment_summary_new, container, false);        		
	        		switch (current_icon){
	        			case "clear-day":
		        			((ImageView) rootView.findViewById(R.id.icon)).setImageResource(R.drawable.clearday);
	        				break;
	        			case "clear-night":
		        			((ImageView) rootView.findViewById(R.id.icon)).setImageResource(R.drawable.clearnight);
	        				break;
	        			case "rain":
		        			((ImageView) rootView.findViewById(R.id.icon)).setImageResource(R.drawable.rain);
		        			break;
	        			case "snow":
		        			((ImageView) rootView.findViewById(R.id.icon)).setImageResource(R.drawable.snow);
		        			break;
	        			case "sleet":
		        			((ImageView) rootView.findViewById(R.id.icon)).setImageResource(R.drawable.sleet);
		        			break;
	        			case "wind":
	        				((ImageView) rootView.findViewById(R.id.icon)).setImageResource(R.drawable.windy);
	        				break;
	        			case "fog":
	        				((ImageView) rootView.findViewById(R.id.icon)).setImageResource(R.drawable.fog);
	        				break;
	        			case "cloudy":
	        				((ImageView) rootView.findViewById(R.id.icon)).setImageResource(R.drawable.cloudy);
	        				break;
	        			case "partly-cloudy-day":
	        				((ImageView) rootView.findViewById(R.id.icon)).setImageResource(R.drawable.partlycloudyday);
	        				break;
	        			case "partly-cloudy-night":
	        				((ImageView) rootView.findViewById(R.id.icon)).setImageResource(R.drawable.partlycloudynight);
	        				break;
	        			default:
	        				((ImageView) rootView.findViewById(R.id.icon)).setImageResource(R.drawable.unknown);
	        				break;
	        		}

		       
		            ((TextView) rootView.findViewById(R.id.text_now_temp)).setText(current_temperature + "°");
		            ((TextView) rootView.findViewById(R.id.text_summary_now)).setText(current_summary);
		            ((TextView) rootView.findViewById(R.id.text_summary_minutely)).setText(minutely_summary);
		            ((TextView) rootView.findViewById(R.id.text_summary_hourly)).setText(summary_hourly);
		            ((TextView) rootView.findViewById(R.id.text_summary_daily)).setText(summary_daily);
		            
		            TextView t2 = (TextView) rootView.findViewById(R.id.text_summary_forecastio);
		            t2.setMovementMethod(LinkMovementMethod.getInstance());
		            
		            
		            
            	} else if(i == 1){
		        	rootView = inflater.inflate(R.layout.fragment_current_new, container, false);
		        	
		        	            	
		            ((TextView) rootView.findViewById(R.id.text_current_summary)).setText(current_summary);
		            ((TextView) rootView.findViewById(R.id.text_current_precip)).setText(current_precipIntensity);
		            ((TextView) rootView.findViewById(R.id.text_current_temp)).setText(current_temperature + "°");
		            ((TextView) rootView.findViewById(R.id.text_current_dewpoint)).setText(current_dewPoint_int + "°");
		            if(unitPref.equals("us")){
		            	((TextView) rootView.findViewById(R.id.text_current_windspeed)).setText(current_windSpeed + " mph");
		                ((TextView) rootView.findViewById(R.id.text_current_visibility)).setText(current_visibility + " mi");
		            } else {
		                ((TextView) rootView.findViewById(R.id.text_current_windspeed)).setText(current_windSpeed + " kmph");
		                ((TextView) rootView.findViewById(R.id.text_current_visibility)).setText(current_visibility + " km");
		            }
		            ((TextView) rootView.findViewById(R.id.text_current_windbearing)).setText(current_windBearing + "°");
		            ((TextView) rootView.findViewById(R.id.text_current_cloudcover)).setText(current_cloudcover_percent_int + "%");
		            ((TextView) rootView.findViewById(R.id.text_current_humidity)).setText(current_humidity_percent_int + "%");
		            ((TextView) rootView.findViewById(R.id.text_current_pressure)).setText(current_pressure + " mb");
		            ((TextView) rootView.findViewById(R.id.text_current_ozone)).setText(current_ozone);
		            
		            TextView t2 = (TextView) rootView.findViewById(R.id.text_current_forecastio);
		            t2.setMovementMethod(LinkMovementMethod.getInstance());
		            
		            
		            
            	} else if(i == 2) {
            		
            		
            		rootView = inflater.inflate(R.layout.fragment_hourly_new, container, false);
		        	String day1 = "notADaySoItAlwaysIsn'tEqualTheFirstTime";
		        	String oldDay;
		        	String forecast;
		        	
	 	    	    for(i=0;i<hourlyPoints.length;i++){
	 	    	    	System.out.println(hourlyPoints[i].getValue("summary"));
	 	    	    	String time = hourlyPoints[i].getValue("time"); //formerly time1
	 	    	    	int temp = hourlyPoints[i].getValueAsDouble("temperature").intValue(); //formerly temp1
	 	    	    	double precipIntensity = hourlyPoints[i].getValueAsDouble("precipIntensity"); //formerly chance1
	 	    	    	int icon = 0;
	 	    	    	int precipProb; //rainChance_f
	 	    	    	Double precipProbD;
	 	    	    	String precipType; //precipType
	 	    	    	if(precipIntensity != 0){
	 	    	    		precipProbD  = 100 * hourlyPoints[i].getValueAsDouble("precipProbability");
	 	    	    		precipProb = precipProbD.intValue();
	 	    	    		precipType = hourlyPoints[i].getValue("precipType");
	 	    	    	} else {
	 	    	    		precipProb = 0;
	 	    	    		precipType = null;
	 	    	    	}
	 	    	    	
	 	    	    	String rain = null;
	 	    	    	
	    	    		if(precipIntensity < 0.002){
	    	    			//nothing
	    	    		} else if (precipIntensity >= 0.002 && precipIntensity < 0.015){
	    	    			rain = "light " + precipType;
	    	    			icon = R.drawable.lightrain;
	    	    		} else if (precipIntensity >= 0.015 && precipIntensity < 0.08){
	    	    			rain = "moderate " + precipType;
	    	    			icon = R.drawable.modrain;
	    	    		} else if (precipIntensity >= 0.08){
	    	    			rain = "heavy " + precipType;
	    	    			icon = R.drawable.heavyrain;
	    	    		}
	    	            forecast =  temp + "°, " + precipProb + "% chance of " + rain;
	    	            String hourly_temperature = temp + "°";
	    	            percent = precipProb + "%";
	 	    	    	
	 	    	    /*
		        	
		        	//OLD WAY BELOW HERE FOR i = 2
			       for(int i = 0; i < hourly_array.length(); i++){
		
		    	        try {
		    	    		Object hm1 = hourly_array.get(i);
		    	   // 		String time1 = ((JSONObject) hm1).get(TAG_HOURLY_TIME).toString();
		    	   // 		String temp1 = ((JSONObject) hm1).get(TAG_HOURLY_TEMP).toString();
		    	   // 		int temp_int = (int)Float.parseFloat(temp1);
		    	   // 		String chance1 = ((JSONObject) hm1).get(TAG_HOURLY_PRECIPINTENSITY).toString();
		    	   // 		Float chance1_f = Float.parseFloat(chance1);
		    	   // 		int rainChance_f;
		    	   // 		String precipType;
		    	  //  		int icon = 0;
		    	   // 		if(chance1_f != 0){
			    	//    		String rainChance = ((JSONObject) hm1).getString(TAG_HOURLY_PRECIPPROB).toString();
			    	//    		rainChance_f = (int)(Float.parseFloat(rainChance) * 100);
			    	//    		precipType = ((JSONObject) hm1).get(TAG_HOURLY_PRECIPTYPE).toString();
		    	    //		} else {
		    	    //			rainChance_f = 0;
		    	   // 			precipType = null;
		    	   // 		}
		    	    		
		    	    		String rain = null;
		    	    		
		    	    		
		    	    		
			    	    		///CHANGE 'rain' in below things to precipType value gotten from the array
			    	    		if(chance1_f < 0.002){
			    	    			//nothing
			    	    		} else if (chance1_f >= 0.002 && chance1_f < 0.015){
			    	    			rain = "light " + precipType;
			    	    			icon = R.drawable.lightrain;
			    	    		} else if (chance1_f >= 0.015 && chance1_f < 0.08){
			    	    			rain = "moderate " + precipType;
			    	    			icon = R.drawable.modrain;
			    	    		} else if (chance1_f >= 0.08){
			    	    			rain = "heavy " + precipType;
			    	    			icon = R.drawable.heavyrain;
			    	    		}
			    	            forecast =  temp_int + "°, " + rainChance_f + "% chance of " + rain;
			    	            current_temperature = temp_int + "°";
			    	            percent = rainChance_f + "%";
		    	    		
			    	    		/*
			    	    		 * A very rough guide is that a value of 0 corresponds to no precipitation, 0.002 corresponds to very light sprinkling, 
			    	    		 * 0.017 corresponds to light precipitation, 0.1 corresponds to moderate precipitation, and 0.4 corresponds to very heavy precipitation.
			    	    		 */
			    	    		
			    	            oldDay = day1;
			    	            day1 = ExtractWeekdayFromUNIX(time, timeZone).toUpperCase();
			    	            int dayToCheck = ExtractWeekdayFromUNIX_Int(time, timeZone);
			    	            int dayToCheck2 = (dayToCheck - 1)%7;
			    	            String hour1 = ExtractHourFromUNIX(time, timeZone);
			    	            Calendar cal = Calendar.getInstance();
			    	            TimeZone tz = TimeZone.getTimeZone(timeZone);
			    	            cal.setTimeZone(tz);
			    	            cal.setTimeInMillis(System.currentTimeMillis());
			    	            int current_day = cal.get(Calendar.DAY_OF_WEEK);
			    	            
			    	            if (dayToCheck2 == 0){
			    	            	dayToCheck2 = 7;
			    	            }
		
			    	            String dayToPrint = null;
			    	        
			    	        if(current_day == dayToCheck){
			    	        	dayToPrint = "TODAY";
			    	        } else if (current_day == dayToCheck2){
			    	        	dayToPrint = "TOMORROW";
			    	        } else {
			    	        	dayToPrint = day1;
			    	        }
		    	            
		    	           // if(oldDay.equals(day1)){
		    	            	//don't do anything
		    	          //  } else {
		    	            	//Print the day
			    	        boolean wroteTheDay = false;
			    	        int d = 2000;
			    	        
			    	        if (hour1.equals("12 am") || i == 0){
			    	        	d++;

			    	        	wroteTheDay = true;

			    	    		RelativeLayout relLayout = (RelativeLayout) rootView.findViewById(R.id.hourly_layout);
			    	    				TextView txt2 = new TextView(getActivity());
			    	    				txt2.setText(dayToPrint);
			    	    				txt2.setSingleLine(false);
			    	    				txt2.setId(d);
			    	    				Typeface myNewFace = Typeface.createFromAsset(getActivity().getAssets(),"fonts/RobotoCondensed-Regular.ttf"); 
			    	    				txt2.setTypeface(myNewFace);
			    	    				txt2.setGravity(Gravity.LEFT);
		    	    					RelativeLayout.LayoutParams tv1p = new RelativeLayout.LayoutParams(		    	    				        
			    	    						RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		    	    					if (i != 0){
		    	    						tv1p.addRule(RelativeLayout.BELOW, i);
				    	    				txt2.setPadding(15, 50, 10, 7);

		    	    					} else {
		    	    						tv1p.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				    	    				txt2.setPadding(15, 30, 10, 7);

		    	    					}
			    	    				txt2.setTextSize(18);
			    	    				txt2.setTextColor(Color.parseColor("#DDDDDD"));
			    	    				relLayout.addView(txt2, tv1p);
		    	            }
		    	    		
		    	    		
		    	    		
		    	    		RelativeLayout relLayout = (RelativeLayout) rootView.findViewById(R.id.hourly_layout);
		    	    				TextView txt1 = new TextView(getActivity());
		    	    				if(hour1.length() == 4){
		    	    					txt1.setText("  " + hour1 + "  -  " + hourly_temperature);
		    	    				} else {
		    	    					txt1.setText( hour1 + "  -  " + hourly_temperature);
		    	    				}
		    	    				txt1.setSingleLine(false);
		    	    				txt1.setId(i+1);

	    	    					RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(		    	    				        
		    	    						RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
	    	    						if (i == 0){
				    	    				lp.addRule(RelativeLayout.BELOW, i);
				    	    				txt1.setPadding(40, 100, 10, 4);
		    	        				} else if(i != 0 && wroteTheDay == true){
				    	    				lp.addRule(RelativeLayout.BELOW, i);
				    	    				txt1.setPadding(40, 120, 10, 4);
			    	    				} else {
				    	    				lp.addRule(RelativeLayout.BELOW, i);
				    	    				txt1.setPadding(40, 4, 10, 4);
			    	    				}


		    	    				Typeface myNewFace = Typeface.createFromAsset(getActivity().getAssets(),"fonts/RobotoCondensed-Regular.ttf"); 
		    	    				txt1.setTypeface(myNewFace);
		    	    				txt1.setTextSize(24);
		    	    				txt1.setTextColor(Color.parseColor("#FFFFFF"));
		    	    				relLayout.addView(txt1, lp);
		    	    				
		    	    				ImageView iv = new ImageView(getActivity());
		    	    				RelativeLayout.LayoutParams vp = 
		    	    					    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 
		    	    					                    RelativeLayout.LayoutParams.WRAP_CONTENT);
		    	    						
		    	    						vp.addRule(RelativeLayout.ALIGN_BOTTOM, i+1);
		    	    						vp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		    	    				iv.setLayoutParams(vp); 
		    	    				iv.setId(i+2);
		    	    				iv.setImageResource(icon);
		    	    				relLayout.addView(iv, vp);
		    	    				
		    	    				
		    	    				if(icon != 0){
		    	    					System.out.println("Icon value was " + icon);
		    	    					System.out.println("Adding new textview...");
			    	    				TextView tv2 = new TextView(getActivity());
			    	    				tv2.setText(percent);
			    	    				System.out.println("Set text to " + percent);
			    	    				RelativeLayout.LayoutParams tv2p = new RelativeLayout.LayoutParams(
			    	    						RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			    	    				tv2p.addRule(RelativeLayout.ALIGN_BOTTOM, i+1);
		    	    					System.out.println("Aligning to bottom of " + i+1);
			    	    				tv2p.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			    	    				tv2.setTextSize(24);
			    	    				tv2.setPadding(0, 0, 75, 0);
			    	    				tv2.setTextColor(Color.parseColor("#FFFFFF"));
			    	    				tv2.setTypeface(myNewFace);
			    	    				tv2.setLayoutParams(tv2p);
			    	    				relLayout.addView(tv2, tv2p);
			    	    				
		    	    				}
		
		    	    		
			        }
		            TextView t2 = (TextView) rootView.findViewById(R.id.text_hourly_forecastio);
		            t2.setMovementMethod(LinkMovementMethod.getInstance());            	
		            
            	} else if (i == 3){
            		
		        	rootView = inflater.inflate(R.layout.fragment_daily_new, container, false);
		        	
		        	if(daily_array != null){
			        for(int i = 0; i < daily_array.length(); i++){
		
		    	        try {
		    	    		Object hm1 = daily_array.get(i);
		    	    		String time1 = ((JSONObject) hm1).get(TAG_DAILY_TIME).toString();
		    	    		String tempMax = ((JSONObject) hm1).get(TAG_DAILY_TEMPMAX).toString();
		    	    		String tempMin = ((JSONObject) hm1).get(TAG_DAILY_TEMPMIN).toString();
		    	    		int tempMax_int = (int)Float.parseFloat(tempMax);
		    	    		int tempMin_int = (int)Float.parseFloat(tempMin);
		    	    		String daySummary = ((JSONObject) hm1).get(TAG_DAILY_SUMMARY).toString();	    	    		
		    	    		
		    	            String day1 = ExtractWeekdayFromUNIX(time1, timeZone).toUpperCase();
		    	            
				    		LinearLayout relLayout = (LinearLayout) rootView.findViewById(R.id.daily_layout);
		    				TextView txt2 = new TextView(getActivity());
		    				txt2.setText(day1);
		    				txt2.setId(i+1);
		    				Typeface myNewFace = Typeface.createFromAsset(getActivity().getAssets(),"fonts/RobotoCondensed-Regular.ttf"); 
		    				txt2.setTypeface(myNewFace);
		    				txt2.setGravity(Gravity.LEFT);
	    					txt2.setPadding(25, 30, 0, 8);		    				
		    				txt2.setTextSize(18);
		    				txt2.setTextColor(Color.parseColor("#DDDDDD"));
		    				relLayout.addView(txt2);
		    	            
				    		LinearLayout relLayout2 = (LinearLayout) rootView.findViewById(R.id.daily_layout);
		    				TextView txt1 = new TextView(getActivity());
		    				txt1.setText(Html.fromHtml(tempMax_int + "° / " + tempMin_int +  "°<br>" + daySummary));
		    				txt1.setSingleLine(false);
		    				txt1.setGravity(Gravity.LEFT);
		    				
		    				Typeface myNewFace2 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/RobotoCondensed-Regular.ttf"); 
		    				txt1.setTypeface(myNewFace2);
		    				txt1.setTextSize(24);
	    					txt1.setPadding(50, 8, 0, 5);		    				
		    				txt1.setTextColor(Color.parseColor("#FFFFFF"));
		    				relLayout2.addView(txt1); 
		
		    			} catch (JSONException e) {
		    				e.printStackTrace();
		    			}
			        }
		        	} else {
			    		LinearLayout relLayout = (LinearLayout) rootView.findViewById(R.id.daily_layout);
						TextView txt2 = new TextView(getActivity());
						txt2.setText("Update Failed");
						txt2.setId(1);
						Typeface myNewFace = Typeface.createFromAsset(getActivity().getAssets(),"fonts/RobotoCondensed-Bold.ttf"); 
						txt2.setTypeface(myNewFace);
						txt2.setGravity(Gravity.LEFT);
						txt2.setTextSize(32);
						txt2.setPadding(10, 18, 10, 5);
						txt2.setTextColor(Color.parseColor("#FFFFFF"));
						RelativeLayout.LayoutParams tv2p = new RelativeLayout.LayoutParams(
	    						RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
	    				tv2p.addRule(RelativeLayout.ALIGN_PARENT_TOP);
						relLayout.addView(txt2, tv2p);
						
			    		LinearLayout relLayout2 = (LinearLayout) rootView.findViewById(R.id.daily_layout);
						TextView txt1 = new TextView(getActivity());
						txt1.setText("NullPointerException caused by daily_array.");
						txt1.setSingleLine(false);
						txt1.setId(i);
						txt1.setGravity(Gravity.LEFT);
						Typeface myNewFace2 = Typeface.createFromAsset(getActivity().getAssets(),"fonts/RobotoCondensed-Regular.ttf"); 
						txt1.setTypeface(myNewFace2);
						txt1.setTextSize(20);
						txt1.setPadding(10, 5, 10, 10);
						txt1.setTextColor(Color.parseColor("#FFFFFF"));
						RelativeLayout.LayoutParams tv1p = new RelativeLayout.LayoutParams(
	    						RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
	    				tv1p.addRule(RelativeLayout.ALIGN_BOTTOM, 1);
						relLayout2.addView(txt1, tv1p);
		        	}
		            TextView t2 = (TextView) rootView.findViewById(R.id.text_daily_forecastio);
		            t2.setMovementMethod(LinkMovementMethod.getInstance());            	}
            	
            	return rootView;            	
        } else {
        	if (i == 0){
            	
        		
	        	rootView = inflater.inflate(R.layout.fragment_summary_new, container, false);
	        		((ImageView) rootView.findViewById(R.id.icon)).setImageResource(R.drawable.unknown);
	        		
		       
		            ((TextView) rootView.findViewById(R.id.text_now_temp)).setText("??°");
		            ((TextView) rootView.findViewById(R.id.text_summary_now)).setText("No data.");
		            ((TextView) rootView.findViewById(R.id.text_summary_minutely)).setText(" ");
		            ((TextView) rootView.findViewById(R.id.text_summary_hourly)).setText(" ");
		            ((TextView) rootView.findViewById(R.id.text_summary_daily)).setText(" ");
		            
		            TextView t2 = (TextView) rootView.findViewById(R.id.text_summary_forecastio);
		            t2.setMovementMethod(LinkMovementMethod.getInstance());
		            
		            
		            
            	} else if(i == 1){
		        	rootView = inflater.inflate(R.layout.fragment_current_new, container, false);
		        	
		        	            	
		            ((TextView) rootView.findViewById(R.id.text_current_summary)).setText("No data.");
		            ((TextView) rootView.findViewById(R.id.text_current_precip)).setText(" ");
		            ((TextView) rootView.findViewById(R.id.text_current_temp)).setText(" ");
		            ((TextView) rootView.findViewById(R.id.text_current_dewpoint)).setText(" ");
		            ((TextView) rootView.findViewById(R.id.text_current_windspeed)).setText(" ");
		            ((TextView) rootView.findViewById(R.id.text_current_visibility)).setText(" ");
		            ((TextView) rootView.findViewById(R.id.text_current_windbearing)).setText(" ");
		            ((TextView) rootView.findViewById(R.id.text_current_cloudcover)).setText(" ");
		            ((TextView) rootView.findViewById(R.id.text_current_humidity)).setText(" ");
		            ((TextView) rootView.findViewById(R.id.text_current_pressure)).setText(" ");
		            ((TextView) rootView.findViewById(R.id.text_current_ozone)).setText(" ");
		            
		            TextView t2 = (TextView) rootView.findViewById(R.id.text_current_forecastio);
		            t2.setMovementMethod(LinkMovementMethod.getInstance());
		            
		            
		            
            	} else if(i == 2) {
            		
            		
            		rootView = inflater.inflate(R.layout.fragment_hourly_new, container, false);

		    		RelativeLayout relLayout = (RelativeLayout) rootView.findViewById(R.id.hourly_layout);
    				TextView txt2 = new TextView(getActivity());
    				txt2.setText("No data.");
    				txt2.setSingleLine(false);
    				Typeface myNewFace = Typeface.createFromAsset(getActivity().getAssets(),"fonts/RobotoCondensed-Regular.ttf"); 
    				txt2.setTypeface(myNewFace);
    				txt2.setGravity(Gravity.LEFT);
					RelativeLayout.LayoutParams tv1p = new RelativeLayout.LayoutParams(		    	    				        
    				RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
					tv1p.addRule(RelativeLayout.ALIGN_PARENT_TOP);
					txt2.setPadding(15, 30, 10, 7);
    				txt2.setTextSize(18);
    				txt2.setTextColor(Color.parseColor("#DDDDDD"));
    				relLayout.addView(txt2, tv1p);
		    	    		
		            TextView t2 = (TextView) rootView.findViewById(R.id.text_hourly_forecastio);
		            t2.setMovementMethod(LinkMovementMethod.getInstance());            	
		            
            	} else if (i == 3){
            		
		        	rootView = inflater.inflate(R.layout.fragment_daily_new, container, false);
		        	
		    		LinearLayout relLayout = (LinearLayout) rootView.findViewById(R.id.daily_layout);
    				TextView txt2 = new TextView(getActivity());
    				txt2.setText("No data.");
    				Typeface myNewFace = Typeface.createFromAsset(getActivity().getAssets(),"fonts/RobotoCondensed-Regular.ttf"); 
    				txt2.setTypeface(myNewFace);
    				txt2.setGravity(Gravity.LEFT);
					txt2.setPadding(25, 30, 0, 8);		    				
    				txt2.setTextSize(18);
    				txt2.setTextColor(Color.parseColor("#DDDDDD"));
    				relLayout.addView(txt2);
		    	            
		            TextView t2 = (TextView) rootView.findViewById(R.id.text_daily_forecastio);
		            t2.setMovementMethod(LinkMovementMethod.getInstance());            	}
            	
            	return rootView;
        	
        } 
        	
        }
    }
    
    
      
    public static boolean CheckForDataConnection(Context currentContext) {
        ConnectivityManager conMgr = (ConnectivityManager) currentContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = conMgr.getActiveNetworkInfo();
        if (i == null){
            return false;
        }else if (!i.isConnected()){
            return false;
        }else if (!i.isAvailable()){
            return false;
        } else {        
        	return true;
        }
    }
    
    public static String ExtractWeekdayFromUNIX(String time, String timeZone){
    	long ts = Long.parseLong(time);
    	String date = null;

        Calendar calendar = Calendar.getInstance();       
        TimeZone tz = TimeZone.getTimeZone(timeZone);
        calendar.setTimeZone(tz);
        calendar.setTimeInMillis(ts*1000);
        int day_of_week = calendar.get(Calendar.DAY_OF_WEEK);
        
        if(day_of_week == 1){
        	date = "Sunday";
        } else if(day_of_week == 2){
        	date = "Monday";
        } else if(day_of_week == 3){
        	date = "Tuesday";
        } else if(day_of_week == 4){
        	date = "Wednesday";
        } else if(day_of_week == 5){
        	date = "Thursday";
        } else if(day_of_week == 6){
        	date = "Friday";
        } else if(day_of_week == 7){
        	date = "Saturday";
        } else {
        	date = "Failed to obtain day";
        }
        
        return date;

    }
    
    public static int ExtractWeekdayFromUNIX_Int(String time, String timeZone){
    	long ts = Long.parseLong(time);
    	Calendar calendar = Calendar.getInstance();       
        TimeZone tz = TimeZone.getTimeZone(timeZone);
        calendar.setTimeZone(tz);
        calendar.setTimeInMillis(ts*1000);
        int day_of_week = calendar.get(Calendar.DAY_OF_WEEK);
              
        return day_of_week;

    }
    
    public static String ExtractHourFromUNIX(String time, String timeZone){
    	long ts = Long.parseLong(time);
    	String twelveHour_s;
    	
        Calendar calendar = Calendar.getInstance();       
        TimeZone tz = TimeZone.getTimeZone(timeZone);
        calendar.setTimeZone(tz);
        calendar.setTimeInMillis(ts*1000);  
        int twelveHour = calendar.get(Calendar.HOUR_OF_DAY);
        
        if(twelveHour == 0){
        	twelveHour_s = "12 am";
        } else if(twelveHour == 1){
        	twelveHour_s = "1 am";
        }else if(twelveHour == 2){
        	twelveHour_s = "2 am";
        }else if(twelveHour == 3){
        	twelveHour_s = "3 am";
        }else if(twelveHour == 4){
        	twelveHour_s = "4 am";
        }else if(twelveHour == 5){
        	twelveHour_s = "5 am";
        }else if(twelveHour == 6){
        	twelveHour_s = "6 am";
        }else if(twelveHour == 7){
        	twelveHour_s = "7 am";
        }else if(twelveHour == 8){
        	twelveHour_s = "8 am";
        }else if(twelveHour == 9){
        	twelveHour_s = "9 am";
        }else if(twelveHour == 10){
        	twelveHour_s = "10 am";
        }else if(twelveHour == 11){
        	twelveHour_s = "11 am";
        }else if(twelveHour == 12){
        	twelveHour_s = "12 pm";
        }else if(twelveHour == 13){
        	twelveHour_s = "1 pm";
        }else if(twelveHour == 14){
        	twelveHour_s = "2 pm";
        }else if(twelveHour == 15){
        	twelveHour_s = "3 pm";
        }else if(twelveHour == 16){
        	twelveHour_s = "4 pm";
        }else if(twelveHour == 17){
        	twelveHour_s = "5 pm";
        }else if(twelveHour == 18){
        	twelveHour_s = "6 pm";
        }else if(twelveHour == 19){
        	twelveHour_s = "7 pm";
        }else if(twelveHour == 20){
        	twelveHour_s = "8 pm";
        }else if(twelveHour == 21){
        	twelveHour_s = "9 pm";
        }else if(twelveHour == 22){
        	twelveHour_s = "10 pm";
        }else if(twelveHour == 23){
        	twelveHour_s = "11 pm";
        }else{
        	twelveHour_s = "Failed to obtain hour";
        }

        return twelveHour_s;

    }
    
    //ParseJSON is Dead.   
    /*
    public void ParseJSON(String url){
    	System.out.println("Starting to parse JSON");
    	// Creating JSON Parser instance
    	JSONParser jParser = new JSONParser();
    	System.out.println("Created a new JSONParser");
    	// getting JSON string from URL
    	
        
		SharedPreferences prefs = getSharedPreferences("MyWigdetPreferences", 0);  

        SharedPreferences.Editor prefEditor = prefs.edit();  
        		prefEditor.putString("TEST", "SUCCESS!?!");    
        		prefEditor.commit(); 
    	
    			JSONObject c = jParser.getJSONFromUrl(url);

    	try {    
    			System.out.println("Created JSONObject");
    			timeZone = c.getString(TAG_TIMEZONE);
    	        JSONObject currently = c.getJSONObject(TAG_CURRENTLY);
    	        current_summary = currently.getString(TAG_CURRENTLY_SUMMARY);
        		prefEditor.putString("current_summary", current_summary);    
        		prefEditor.commit();     	        
    	        current_icon = currently.getString(TAG_CURRENTLY_ICON);
    	        current_precipIntensity = currently.getString(TAG_CURRENTLY_PRECIPINTENSITY);
    	        current_temp = currently.getString(TAG_CURRENTLY_TEMP);
    	        temp_int = (int)Float.parseFloat(current_temp);
        		prefEditor.putInt("current_temp", temp_int);    
        		prefEditor.commit(); 
    	        current_dewPoint = currently.getString(TAG_CURRENTLY_DEWPOINT);
    	        current_dewPoint_int = (int)Float.parseFloat(current_dewPoint);
    	        current_windSpeed = currently.getString(TAG_CURRENTLY_WINDSPEED);
    	        if(Float.parseFloat(current_windSpeed) != 0){
    	        	current_windBearing = currently.getString(TAG_CURRENTLY_WINDBEARING);
    	        } else {
    	        	current_windBearing = "0";
    	        }
    	        current_cloudCover = currently.getString(TAG_CURRENTLY_CLOUDCOVER);
    	        current_cloudcover_f = Float.parseFloat(current_cloudCover);
    	        current_cloudcover_percent = current_cloudcover_f * 100;
    	        current_cloudcover_percent_int = (int)current_cloudcover_percent;
    	        current_humidity = currently.getString(TAG_CURRENTLY_HUMIDITY);
    	        current_humidity_f = Float.parseFloat(current_humidity);
    	        current_humidity_percent = current_humidity_f * 100;
    	        current_humidity_percent_int = (int)current_humidity_percent;
    	        current_pressure = currently.getString(TAG_CURRENTLY_PRESSURE);
    	        current_visibility = currently.getString(TAG_CURRENTLY_VISIBILITY);
    	        current_ozone = currently.getString(TAG_CURRENTLY_OZONE);
    	        
    	} catch (JSONException e) {
    		e.printStackTrace();
    	}  
    	
    	try {
    	            	        
    	        JSONObject minutely = c.getJSONObject(TAG_MINUTELY);
    	        summary_minutely = minutely.getString(TAG_MINUTELY_SUMMARY);
        		prefEditor.putString("minute_summary", summary_minutely);    
        		prefEditor.commit(); 
    	        JSONObject hourly = c.getJSONObject(TAG_HOURLY);
    	        summary_hourly = hourly.getString(TAG_HOURLY_SUMMARY);
        		prefEditor.putString("hour_summary", summary_hourly);    
        		prefEditor.commit(); 
    	        JSONObject daily = c.getJSONObject(TAG_DAILY);
    	        summary_daily = daily.getString(TAG_DAILY_SUMMARY);

        		
    	        //setup a hashmap
    	        ArrayList<HashMap<String, String>> hourlyList = new ArrayList<HashMap<String, String>>();
    	        
    	        // Getting Array of hourly forecast
    	        JSONObject hourly_tag = c.getJSONObject(TAG_HOURLY);
    	        hourly_array = hourly_tag.getJSONArray(TAG_HOURLY_DATA);

    	        // looping through All 
    	        for(int i = 0; i < hourly_array.length(); i++){
    	            JSONObject h = hourly_array.getJSONObject(i);
    	            
    	            // Storing each json item in variable
    	            String time = h.getString(TAG_HOURLY_TIME);
    	            //HERE, convert the time into readable "DAY, HOUR" format
    	            //Example:  "Today, 5pm" or "Monday, 2am"
    	            String summary = h.getString(TAG_HOURLY_SUMMARY);
    	            String icon = h.getString(TAG_HOURLY_ICON);
    	            String precipIntensity = h.getString(TAG_HOURLY_PRECIPINTENSITY);
    	            String precipProb = null;
    	            String precipType = null;
        	        if(Float.parseFloat(precipIntensity) != 0){
        	        	precipProb = h.getString(TAG_HOURLY_PRECIPPROB);
            	        precipType = h.getString(TAG_DAILY_PRECIPTYPE);
        	        } else {
        	        	precipProb = "0";
        	        	precipType = null;
        	        } 
    	            String temperature = h.getString(TAG_HOURLY_TEMP);
    	         //   String dewPoint = h.getString(TAG_HOURLY_DEWPOINT);
    	            // String windSpeed = h.getString(TAG_HOURLY_WINDSPEED);
    	            //String windBearing = h.getString(TAG_HOURLY_WINDBEARING);
    	         //   String cloudCover = h.getString(TAG_HOURLY_CLOUDCOVER);
    	         //   String humidity = h.getString(TAG_HOURLY_HUMIDITY);
    	         //   String pressure = h.getString(TAG_HOURLY_PRESSURE);
    	          //  Let's be real: Are you going to use visibility in your hourly section? No.s 
    	          //  String visibility = h.getString(TAG_HOURLY_VISIBILITY);
    	            //String ozone = h.getString(TAG_HOURLY_OZONE);
    	            
    	            //new hashmap
    	            HashMap<String, String> map = new HashMap<String, String>();
    	            
    	            //add each child node to hashmap
    	            map.put(TAG_HOURLY_TIME, time);
    	           // map.put(TAG_HOURLY_WEEKDAY, weekDay);
    	          //  map.put(TAG_HOURLY_HOUR, hour);
    	            map.put(TAG_HOURLY_SUMMARY, summary);
    	            map.put(TAG_HOURLY_ICON, icon);
    	            map.put(TAG_HOURLY_PRECIPINTENSITY, precipIntensity);
    	            map.put(TAG_HOURLY_PRECIPPROB, precipProb);
    	            map.put(TAG_HOURLY_PRECIPTYPE, precipType);
    	            map.put(TAG_HOURLY_TEMP, temperature);
    	         //   map.put(TAG_HOURLY_DEWPOINT, dewPoint);
    	           // map.put(TAG_HOURLY_WINDSPEED, windSpeed);
    	           // map.put(TAG_HOURLY_WINDBEARING, windBearing);
    	         //   map.put(TAG_HOURLY_CLOUDCOVER, cloudCover);
    	         //   map.put(TAG_HOURLY_HUMIDITY, humidity);
    	         //   map.put(TAG_HOURLY_PRESSURE, pressure);
    	         //     map.put(TAG_HOURLY_VISIBILITY, visibility);
    	         //   map.put(TAG_HOURLY_OZONE, ozone);
    	            
    	            //add hashlist to arraylist
    	            hourlyList.add(map);
    	            
    	            System.out.println("Made hourly_array");
    	            
    	        }
    	        
    	} catch (JSONException e) {
    		e.printStackTrace();
    	}  
    	
    	try {

    	        //setup a hashmap
    	        ArrayList<HashMap<String, String>> dailyList = new ArrayList<HashMap<String, String>>();
    	        
    	        // Getting Array of hourly forecast
    	        JSONObject daily_tag = c.getJSONObject(TAG_DAILY);
    	        daily_array = daily_tag.getJSONArray(TAG_DAILY_DATA);
    	        
    	        System.out.println("made daily_array");

    	        // looping through All 
    	        for(int i = 0; i < daily_array.length(); i++){
    	            JSONObject d = daily_array.getJSONObject(i);
    	            
    	            // Storing each json item in variable
    	            String time = d.getString(TAG_DAILY_TIME);
    	            String summary = d.getString(TAG_DAILY_SUMMARY);
    	            String icon = d.getString(TAG_DAILY_ICON);
    	         //   String sunrise = d.getString(TAG_DAILY_SUNRISE);
    	         //   String sunset = d.getString(TAG_DAILY_SUNSET);
    	            String precipIntensity = d.getString(TAG_DAILY_PRECIPINTENSITY);
    	            String precipIntensityMax = d.getString(TAG_DAILY_PRECIPINTENSITYMAX);
    	           // String precipType = d.getString(TAG_DAILY_PRECIPTYPE);
    	            String temperatureMax = d.getString(TAG_DAILY_TEMPMAX);
    	            String temperatureMin = d.getString(TAG_DAILY_TEMPMIN);
    	       //     String temperatureMaxTime = d.getString(TAG_DAILY_TEMPMAXTIME);
    	       //     String temperatureMinTime = d.getString(TAG_DAILY_TEMPMINTIME);
    	       //     String dewPoint = d.getString(TAG_DAILY_DEWPOINT);
    	       //     String windSpeed = d.getString(TAG_DAILY_WINDSPEED);
    	       //     String windBearing = d.getString(TAG_DAILY_WINDBEARING);
    	       //     String cloudCover = d.getString(TAG_DAILY_CLOUDCOVER);
    	       //     String humidity = d.getString(TAG_DAILY_HUMIDITY);
    	       //     String pressure = d.getString(TAG_DAILY_PRESSURE);
    	            //Let's be real: Are you going to use visibility in your hourly section? No.s
    	          //  String visibility = h.getString(TAG_DAILY_VISIBILITY);
    	          //  String ozone = d.getString(TAG_DAILY_OZONE);
    	            
    	         

    	            
    	            //new hashmap
    	            HashMap<String, String> map = new HashMap<String, String>();
    	            
    	            //add each child node to hashmap
    	            map.put(TAG_DAILY_TIME, time);
    	           // map.put(TAG_HOURLY_WEEKDAY, weekDay);
    	          //  map.put(TAG_HOURLY_HOUR, hour);
    	            map.put(TAG_DAILY_SUMMARY, summary);
    	            map.put(TAG_DAILY_ICON, icon);
    	         //   map.put(TAG_DAILY_SUNRISE, sunrise);
    	         //   map.put(TAG_DAILY_SUNSET, sunset);
    	            map.put(TAG_DAILY_PRECIPINTENSITY, precipIntensity);
    	            map.put(TAG_DAILY_PRECIPINTENSITYMAX, precipIntensityMax);
    	            //map.put(TAG_DAILY_PRECIPTYPE, precipType);
    	            map.put(TAG_DAILY_TEMPMAX, temperatureMax);
    	            map.put(TAG_DAILY_TEMPMIN, temperatureMin);
    	         //   map.put(TAG_DAILY_TEMPMAXTIME, temperatureMaxTime);
    	         //   map.put(TAG_DAILY_TEMPMINTIME, temperatureMinTime);
    	        //    map.put(TAG_DAILY_DEWPOINT, dewPoint);
    	        //    map.put(TAG_DAILY_WINDSPEED, windSpeed);
    	        //    map.put(TAG_DAILY_WINDBEARING, windBearing);
    	        //    map.put(TAG_DAILY_CLOUDCOVER, cloudCover);
    	        //    map.put(TAG_DAILY_HUMIDITY, humidity);
    	        //    map.put(TAG_DAILY_PRESSURE, pressure);
    	        //      map.put(TAG_DAILY_VISIBILITY, visibility);
    	        //    map.put(TAG_DAILY_OZONE, ozone);
    	            
    	            //add hashlist to arraylist
    	            dailyList.add(map);
    	        }
    	       	   

    	
    	} catch (JSONException e) {
    		e.printStackTrace();
    	}  
    	
    	
		    Calendar cal = Calendar.getInstance();
		    TimeZone tz = TimeZone.getTimeZone(timeZone);
            cal.setTimeZone(tz);
            cal.setTimeInMillis(System.currentTimeMillis());
            lastUpdateTime = cal.getTime();
            lastUpdateTime_l = lastUpdateTime.getTime();
            System.out.println(lastUpdateTime_l);   	

    	
    }

    */
    
	public static class SettingsFragment extends PreferenceFragment {
	    @Override
		    public void onCreate(Bundle savedInstanceState) {
		        super.onCreate(savedInstanceState);
		        // Load the preferences from an XML resource
		        addPreferencesFromResource(R.xml.fragment_settings);
		    }
		}
}