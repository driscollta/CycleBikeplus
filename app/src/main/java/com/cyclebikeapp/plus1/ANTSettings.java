package com.cyclebikeapp.plus1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import static com.cyclebikeapp.plus1.Constants.ANTSETTINGS_TYPE_CAL;
import static com.cyclebikeapp.plus1.Constants.ANTSETTINGS_TYPE_SEARCH_PAIR;
import static com.cyclebikeapp.plus1.Constants.HAS_ANT;
import static com.cyclebikeapp.plus1.Constants.KEY_CAL_CHANNEL;
import static com.cyclebikeapp.plus1.Constants.KEY_CHOOSER_CODE;
import static com.cyclebikeapp.plus1.Constants.KEY_PAIR_CHANNEL;
import static com.cyclebikeapp.plus1.Constants.KEY_PLUG_IN_VERSION;
import static com.cyclebikeapp.plus1.Constants.POWER_WHEEL_CIRCUM;
import static com.cyclebikeapp.plus1.Constants.POWER_WHEEL_IS_CAL;
import static com.cyclebikeapp.plus1.Constants.PREFS_NAME;
import static com.cyclebikeapp.plus1.Constants.SHOW_ANT;
import static com.cyclebikeapp.plus1.Constants.USE_ANT;
import static com.cyclebikeapp.plus1.Constants.WHEEL_CIRCUM;
import static com.cyclebikeapp.plus1.Constants.WHEEL_IS_CAL;

public class ANTSettings extends AppCompatActivity {
	
	/** smallest wheel circumference */
	private static final double LOWER_WHEEL_CIRCUM = 1.075;
	/** largest wheel circumference */
	private static final double UPPER_WHEEL_CIRCUM = 2.51;
	/** default wheel circumference */
	private static final double DEFAULT_WHEEL_CIRCUM = 2.142;	
	private static final String FORMAT_4_3F = "%4.3f";
    // (arbitrary) request code for the purchase flow
    static final int RC_IAB_HELPER = 10001;
	private static final int RC_ANT_DEVICE_EDIT = 42;
	private static final String KEY_AUTO_CONNECT_ALL = "autoconnect_all";

	private TextView antAvail;
	private TextView pluginVersion;
	private EditText wheelEdit;
	private CheckBox useAntCheck;
	private CheckBox showAntCheck;
	private CheckBox autoConnectCheck;
	private TextView autoConnectText;
	private Double wheelCirc;
	private boolean useAnt;
	private boolean showAnt;
	private AntPlusManager mAntManager;
	private boolean autoConnectANTAll = true;
	private static boolean debugAppState = MainActivity.debugAppState;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (debugAppState) {Log.i(this.getClass().getName(), "onCreate()");}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ant_settings_view);
		setResult(Activity.RESULT_CANCELED);
		mAntManager = new AntPlusManager(getApplicationContext());
		setupActionBar();
		getWidgetIDs();
		loadPreferences();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
			saveState();
	        NavUtils.navigateUpFromSameTask(this);
			this.overridePendingTransition(0, 0);
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	private void setupActionBar() {
		android.support.v7.app.ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			// Show the Up button in the action bar.
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setTitle(getString(R.string.menu_ant));
			actionBar.show();
		}
	}
	private void getWidgetIDs() {
		Button aboutANTButton = (Button) findViewById(R.id.ant_help_btn);
		aboutANTButton.setOnClickListener(aboutANTButtonClickListener);
		Button antManagerButton = (Button) findViewById(R.id.ant_manager_btn);
		antManagerButton.setOnClickListener(antManagerButtonClickListener);
		Button trainerModeButton = (Button) findViewById(R.id.trainer_mode_btn);
		trainerModeButton.setOnClickListener(trainerModeButtonClickListener);
		antAvail = (TextView) findViewById(R.id.ant_avail);
		pluginVersion = (TextView) findViewById(R.id.plugin_ver);
		wheelEdit = (EditText)findViewById(R.id.wheel_edit);
		useAntCheck = (CheckBox) findViewById(R.id.use_ant_checkbox);
		showAntCheck = (CheckBox) findViewById(R.id.show_ant_checkbox);
		autoConnectCheck = (CheckBox) findViewById(R.id.autoConnect_ant_checkbox);
		autoConnectCheck.setOnClickListener(autoConnectCheckOnClick);
		autoConnectText = (TextView) findViewById(R.id.autoconnect_text);
		useAntCheck.setOnClickListener(useAntOnClick);
		showAntCheck.setOnClickListener(showAntOnClick);
	}

	@SuppressLint("DefaultLocale")
	private void loadPreferences() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		String pluginVersionStr = settings.getString(KEY_PLUG_IN_VERSION, getString(R.string.na));
		boolean hasAnt = settings.getBoolean(HAS_ANT, false);
		antAvail.setText((hasAnt ? "<y>" : "<n>"));
		pluginVersion.setText(pluginVersionStr);
		useAnt = settings.getBoolean(USE_ANT, true);
		showAnt = settings.getBoolean(SHOW_ANT, true);
		autoConnectANTAll = settings.getBoolean(KEY_AUTO_CONNECT_ALL, false);
		autoConnectText.setText(getAutoConnectCheckText(autoConnectANTAll));
		wheelCirc = Double.valueOf(settings.getString(WHEEL_CIRCUM, "2.142"));
		wheelEdit.setText(String.format(FORMAT_4_3F, wheelCirc));
		useAntCheck.setChecked(useAnt);
		showAntCheck.setChecked(showAnt);
		if (!useAnt){
			showAntCheck.setChecked(false);
			showAnt = useAnt;
			showAntCheck.setEnabled(false);
		}
		autoConnectCheck.setChecked(autoConnectANTAll);
		loadAntConfiguration();
		if (mAntManager.wheelCnts.isCalibrated) {
			wheelCirc = mAntManager.wheelCnts.wheelCircumference;
		} else if (mAntManager.powerWheelCnts.isCalibrated) {
			wheelCirc = mAntManager.powerWheelCnts.wheelCircumference;
		}
	}
	
	private CharSequence getAutoConnectCheckText(boolean autoConnectANTAll2) {
		String connectAny = getString(R.string.ant_autoconnect_any);
		String connectLast = getString(R.string.ant_autoconnect_last);
		if (autoConnectANTAll2) {
			return connectAny;
		} else {
			return connectLast;
		}
	}

	private void loadAntConfiguration() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		mAntManager.wheelCnts.wheelCircumference = Double.valueOf(settings
				.getString(WHEEL_CIRCUM, "2.142"));
		mAntManager.powerWheelCnts.wheelCircumference = Double.valueOf(settings
				.getString(POWER_WHEEL_CIRCUM, "2.142"));
		mAntManager.wheelCnts.isCalibrated = settings.getBoolean(WHEEL_IS_CAL, false);
		mAntManager.powerWheelCnts.isCalibrated = settings.getBoolean(POWER_WHEEL_IS_CAL, false);		
	}

	@Override
	protected void onPause() {
		saveState();
		super.onPause();
	}
	@Override
	protected void onStop() {
		if (debugAppState) Log.i(this.getClass().getName(), "onStop()");
		super.onStop();
	}

	// We're being destroyed. It's important to dispose of the helper here!
	@Override
	public void onDestroy() {
		super.onDestroy();

	}
    
	private void saveState() {
		//only save the values that might change in this activity
		String wheelCircumference = wheelEdit.getText().toString();
		useAnt = useAntCheck.isChecked();
		showAnt = showAntCheck.isChecked();
		// We need an Editor object to make preference changes.
		// All objects are from android.context.Context
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		double newWheelCirc;
	    try {
			newWheelCirc = Double.valueOf(wheelCircumference.replaceAll(",", "."));
	    } catch (NumberFormatException e) {
	        newWheelCirc = DEFAULT_WHEEL_CIRCUM;
	    }
		if ((newWheelCirc > UPPER_WHEEL_CIRCUM) 
				|| (newWheelCirc < LOWER_WHEEL_CIRCUM)){
			newWheelCirc = DEFAULT_WHEEL_CIRCUM;
		}
		wheelCircumference = String.valueOf(newWheelCirc);
		boolean wheelCircChanged = Math.abs(newWheelCirc - wheelCirc) > .01;
		if (wheelCircChanged) {
			editor.putBoolean(WHEEL_IS_CAL, false);
			editor.putString(WHEEL_CIRCUM, wheelCircumference);
			editor.putBoolean(POWER_WHEEL_IS_CAL, false);
			editor.putString(POWER_WHEEL_CIRCUM, wheelCircumference);
		}
		editor.putBoolean(USE_ANT, useAnt);
		editor.putBoolean(SHOW_ANT, showAnt);
		editor.putBoolean(KEY_AUTO_CONNECT_ALL, autoConnectANTAll);
		editor.apply();
	}
	
	private OnClickListener aboutANTButtonClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent i11 = new Intent(ANTSettings.this, AboutANTScroller.class);
			startActivity(i11);			
		}
	};
	private OnClickListener antManagerButtonClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent i111 = new Intent(ANTSettings.this, ANTDeviceEditor.class);
			startActivityForResult(i111, RC_ANT_DEVICE_EDIT);			
			// show expanded list of ANT devices with search... button
		}
	};		
	
	private OnClickListener autoConnectCheckOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			autoConnectANTAll = autoConnectCheck.isChecked();
			autoConnectText.setText(getAutoConnectCheckText(autoConnectANTAll));
		}
	};		

	private OnClickListener useAntOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			useAnt = useAntCheck.isChecked();
			if (!useAnt){
				showAntCheck.setChecked(false);
				showAnt = useAnt;
				showAntCheck.setEnabled(false);
			} else {
				showAntCheck.setEnabled(true);
			}
		}
	};		

	private OnClickListener showAntOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
		}
	};		
	private OnClickListener trainerModeButtonClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent i111 = new Intent(ANTSettings.this, TrainerModeSettings.class);
			startActivity(i111);			
			// show trainer mode layout with about textbox, check box, spoof location spinner
		}
	};		

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case RC_ANT_DEVICE_EDIT:
			int chooserCode = data.getIntExtra(KEY_CHOOSER_CODE, 0);
			if ((chooserCode == ANTSETTINGS_TYPE_SEARCH_PAIR
					|| chooserCode == ANTSETTINGS_TYPE_CAL) 
					&& resultCode == RESULT_OK) {
				SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, 0).edit();
				editor.putInt(KEY_CHOOSER_CODE, chooserCode);
				editor.putInt(KEY_PAIR_CHANNEL, data.getIntExtra(KEY_PAIR_CHANNEL, 0));
				editor.putInt(KEY_CAL_CHANNEL, data.getIntExtra(KEY_CAL_CHANNEL, 0)).apply();
				Intent antSettingsIntent = new Intent();
				antSettingsIntent.putExtra(KEY_CHOOSER_CODE, chooserCode);
				antSettingsIntent.putExtra(KEY_PAIR_CHANNEL, data.getIntExtra(KEY_PAIR_CHANNEL, 0));
				antSettingsIntent.putExtra(KEY_CAL_CHANNEL, data.getIntExtra(KEY_CAL_CHANNEL, 0));
				setResult(RESULT_OK, antSettingsIntent);
				finish();
			}
			break;
		case RC_IAB_HELPER: 
		}
	}

}
