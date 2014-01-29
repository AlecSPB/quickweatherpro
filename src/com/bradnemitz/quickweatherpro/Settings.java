package com.bradnemitz.quickweatherpro;

import android.os.Bundle;
import android.preference.PreferenceActivity;

	public class Settings extends PreferenceActivity {
	    @SuppressWarnings("deprecation")
		@Override
		    public void onCreate(Bundle savedInstanceState) {
		        super.onCreate(savedInstanceState);
		        // Load the preferences from an XML resource
		        addPreferencesFromResource(R.xml.fragment_settings);
		    }
	   
		}