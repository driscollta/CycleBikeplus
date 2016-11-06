package com.cyclebikeapp.plus1;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

import java.lang.reflect.Method;
import java.util.List;
/*
 * Copyright 2013 cyclebikeapp. All Rights Reserved.
*/

public class SettingsActivity extends AppCompatPreferenceActivity{
    protected Method mLoadHeaders = null;
    protected Method mHasHeaders = null;

    private class PreferenceChangedListener implements
			OnSharedPreferenceChangeListener {

		@SuppressWarnings("deprecation")
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			updatePrefSummary(findPreference(key));
		}
	}

	/**
     * Checks to see if using new v11+ way of handling PrefFragments.
     * @return Returns false pre-v11, else checks to see if using headers.
     */
    public boolean isNewV11Prefs() {
//        if (mHasHeaders!=null && mLoadHeaders!=null) {
//            try {
//                return (Boolean)mHasHeaders.invoke(this);
//            } catch (IllegalArgumentException e) {
//            } catch (IllegalAccessException e) {
//            } catch (InvocationTargetException e) {
//            }
//        }
        return false;
    }

    private void initSummary(Preference p) {
        if (p instanceof PreferenceGroup) {
            PreferenceGroup pGrp = (PreferenceGroup) p;
            for (int i = 0; i < pGrp.getPreferenceCount(); i++) {
                initSummary(pGrp.getPreference(i));
            }
        } else {
            updatePrefSummary(p);
        }
    }

    private void updatePrefSummary(Preference p) {
        if (p instanceof ListPreference) {
            ListPreference listPref = (ListPreference) p;
            p.setSummary(listPref.getEntry());
        }
        if (p instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) p;
            if (p.getTitle().toString().contains("assword"))
            {
                p.setSummary("******");
            } else {
                p.setSummary(editTextPref.getText());
            }
        }
        if (p instanceof MultiSelectListPreference) {
            EditTextPreference editTextPref = (EditTextPreference) p;
            p.setSummary(editTextPref.getText());
        }
        if (p instanceof CheckBoxPreference) {
        	CheckBoxPreference checkBoxPref = (CheckBoxPreference) p;
        	if (checkBoxPref.isChecked()){
        	p.setSummary(checkBoxPref.getSummaryOn());
        	} else {
            	p.setSummary(checkBoxPref.getSummaryOff());       		
        	}
        }
    }
	@SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        PreferenceChangedListener prefsChangedListener = new PreferenceChangedListener();
		sPrefs.registerOnSharedPreferenceChangeListener(prefsChangedListener); 
        addPreferencesFromResource(R.xml.preferences);
		PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.preferences, false);
        initSummary(getPreferenceScreen());
        setupActionBar();
    }
    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        String title = "CycleBike+ Settings";
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(title);
            actionBar.show();
        }
    }
    @Override
    public void onBuildHeaders(List<Header> aTarget) {
//        try {
//            mLoadHeaders.invoke(this,new Object[]{R.xml.preferences_headers,aTarget});
//        } catch (IllegalArgumentException e) {
//        } catch (IllegalAccessException e) {
//        } catch (InvocationTargetException e) {
//        }   
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	        NavUtils.navigateUpFromSameTask(this);
			this.overridePendingTransition(0, 0);
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
}
