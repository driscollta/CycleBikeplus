package com.cyclebikeapp.plus1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dsi.ant.AntSupportChecker;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeCadencePcc;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeCadencePcc.ICalculatedCadenceReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikePowerPcc;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikePowerPcc.CalculatedWheelSpeedReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikePowerPcc.CalibrationMessage;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikePowerPcc.DataSource;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikePowerPcc.ICalculatedCrankCadenceReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikePowerPcc.ICalculatedPowerReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikePowerPcc.ICalibrationMessageReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikePowerPcc.IInstantaneousCadenceReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikePowerPcc.IRawCrankTorqueDataReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikePowerPcc.IRawPowerOnlyDataReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikePowerPcc.IRawWheelTorqueDataReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc.CalculatedSpeedReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc.IRawSpeedAndDistanceDataReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc;
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc.DataState;
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc.IHeartRateDataReceiver;
import com.dsi.ant.plugins.antplus.pcc.MultiDeviceSearch;
import com.dsi.ant.plugins.antplus.pcc.defines.BatteryStatus;
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceState;
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceType;
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult;
import com.dsi.ant.plugins.antplus.pcc.defines.RequestStatus;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IDeviceStateChangeReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IPluginAccessResultReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusBikeSpdCadCommonPcc;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusCommonPcc.IBatteryStatusReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusCommonPcc.IManufacturerIdentificationReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusCommonPcc.IProductInformationReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusCommonPcc.IRequestFinishedReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusLegacyCommonPcc.ICumulativeOperatingTimeReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusLegacyCommonPcc.IManufacturerAndSerialReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusLegacyCommonPcc.IVersionAndModelReceiver;
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.cyclebikeapp.plus1.Constants.*;
import static com.cyclebikeapp.plus1.Utilities.isGPSLocationEnabled;

/**
 * Copyright 2013 cyclebikeapp. All Rights Reserved.
 */

@SuppressLint("DefaultLocale")
public class MainActivity extends AppCompatActivity {

	// organized static strings into a private class called .Constants
	// http://stackoverflow.com/questions/320588/interfaces-with-static-fields-in-java-for-sharing-constants
	private int res_white;
	/**
	 * load this from string resource to get the "TM"
	 */
	public String ANTplus = "";
	/**
	 * this HashMap contains the data in the turn list. It is updated in the
	 * refreshHashMap method and passed to CrazyAdapter to display on the
	 * screen. Also use this HashMap when long-pressing item to extract the
	 * distance
	 */
	private ArrayList<HashMap<String, String>> routeHashMap = new ArrayList<>();
	/**
	 * indicator of which Alert dialog is calling from the menu
	 */
	private int dialogType = 0;
	private LocationManager locationManager;

	// display Views
	private View mLayout;
	private TextView cadenceLabel;
	private TextView heartLabel;
	private TextView powerLabel;
	private RelativeLayout cadHRPowerLayout;
	private TextView tripDistLabel, tripDistTitle;
	private TextView avgSpeedLabel, maxSpeedLabel, gpsSpeedLabel;
	private TextView avgSpeedTitle, maxSpeedTitle, gpsSpeedTitle;
	private TextView tripTimeLabel, tripTimeTitle;
	private TextView appMessage;
	private TextView trainerModeComment;
	private Button exitTMBtn;
	private Context context;
	private View speedCell;
	private View powerCell;
	private View hrCell;
	private View cadCell;
	private View antToastAnchor;
	/**
	 * this is used in location spoofer to marquee-scroll the title
	 */
	private TextView actionbar_title_text;
	private PowerManager pm = null;
	/**
	 * name of the route file being followed
	 */
	private String chosenGPXFile = "";
	/**
	 * name of the previous route file being followed need this in case the
	 * chosen file doesn't load, and we have to revert
	 */
	private String prevChosenFile = "";
	/**
	 * name of the tcx file to upload
	 */
	private String chosenTCXFile = "";

	private BikeStat myBikeStat;

	private NavRoute myNavRoute;
	/**
	 * the current location as received from the GPS sensor
	 */
	private Location myPlace = new Location(LocationManager.GPS_PROVIDER);
	/**
	 * temporary Location for distance/bearing calculations
	 */
	private Location there = new Location(LocationManager.GPS_PROVIDER);
	/**
	 * generate fake locations in trainer mode
	 */
	private LocationSpoofer spoofer;
	/**
	 * should the app ask the user to turn-on gps in onStart()?
	 */
	private boolean gpsTurnOn = true;
	boolean unitPref;
	/**
	 * tag the first attempt at acquiring gps locations. If it takes too long,
	 * try resetting almanac
	 */
	private long gpsAcqSysTimeStamp;
	/**
	 * use System time to indicate loss of GPS after 3 seconds
	 */
	private long newLocSysTimeStamp;
	/**
	 * if this is the first location received, do something different in
	 * BikeStat to calculate distance
	 */
	private boolean gpsFirstLocation = true;
	/**
	 * last time that the GPS ephemeris data was reset by the program. Used if
	 * GPS acquisition takes too long
	 */
	private long almanacResetTime;
	/**
	 * the scrolling turn-by-turn list
	 */
	private ListView turnByturnList;
	/**
	 * a means of assembling icons and text in each row of the turn list
	 */
	private TurnByTurnListAdapter turnByturnAdapter;
	/**
	 * is the list still scrolling?
	 */
	protected boolean scrolling = false;
	/**
	 * have the unit preferences changed?
	 */
	private boolean prefChanged = true;
	/**
	 * if last location is older than 3 seconds, this will be false and //
	 * speedo display will show xx.x; miles to turns will show ??
	 */
	private boolean gpsLocationCurrent;
	/**
	 * alternate satAcq message with this switch
	 */
	private boolean satAcqMess = true;
	/**
	 * only open new tcx file in Location Listener; use this tag to force a new
	 * tcx rather than re-open the old one; for example in reset()
	 */
	private boolean forceNewTCX_FIT = false;
	/**
	 * if we're resuming the route and loading a file, need a switch to open a
	 * new or re-open the old tcx file
	 */
	public boolean resumingRoute = false;
	// all the Location functions
	private LocationHelper mLocationHelper;
	/**
	 * Class to manage all the ANT messaging and setup
	 */
	private AntPlusManager mAntManager;
	/**
	 * switch in Settings check box to show/hide cad HR power data
	 */
	private boolean showANTData;
	/**
	 * switch in Settings check box to use ANT sensors and initialize ANT
	 */
	private boolean useANTData;
	private boolean hasANT = false;
	private boolean autoConnectANTAll = false;
	/**
	 * when returning from ANTSettings with calibration requested code
	 */
	AntPlusHeartRatePcc hrPcc = null;
	AntPlusBikeCadencePcc cadPcc = null;
	AntPlusBikeSpeedDistancePcc speedPcc = null;
	AntPlusBikePowerPcc powerPcc = null;
	MultiDeviceSearch mSearch;
	protected PccReleaseHandle<AntPlusHeartRatePcc> hrmReleaseHandle = null;
	protected PccReleaseHandle<AntPlusBikeCadencePcc> cadReleaseHandle;
	protected PccReleaseHandle<AntPlusBikeSpeedDistancePcc> speedReleaseHandle;
	protected PccReleaseHandle<AntPlusBikePowerPcc> powerReleaseHandle;
	ANTDBAdapter dataBaseAdapter = null;
	/**
	 * switch to prevent reading and writing from BikeStat at the same time
	 */
	private boolean writingTrackRecord = false;
	/**
	 * use trainer mode to spoof locations
	 */
	private boolean trainerMode = false;

	private boolean firstSpoofLocation = true;
	static final boolean debugOldTCXFile = false;
	static boolean debugCrankCadence = false;
	static boolean debugAppState = true;
	static boolean debugWheelCal = false;
	static boolean debugPowerWheelCal = false;
	static boolean debugGPSAlmanac = true;
	static boolean debugAntDeviceState = false;
	static boolean debugMDS = true;
	static boolean debugRefreshTiming = false;
	static boolean debugLocation = true;
    static boolean debugMessageBox = true;

    @Override
	public void onCreate(Bundle savedInstanceState) {
		if (debugAppState) Log.d(this.getClass().getName(), "onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scroller);
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		turnByturnList = (ListView) findViewById(R.id.list);
		/* should the app keep the screen on - not if we're paused */
		turnByturnList.setKeepScreenOn(true);
		/* handles the scrolling action */
		turnByturnList.setOnScrollListener(scrollListener);
		/* responds to long-press in turn list */
		turnByturnList.setOnItemLongClickListener(longClickListener);
		context = getApplicationContext();
		res_white = ContextCompat.getColor(context, R.color.white);
		ANTplus = getResources().getString(R.string.ANTplus);
		myBikeStat = new BikeStat(context);
		myNavRoute = new NavRoute(context);
		spoofer = new LocationSpoofer(context);
		initializeScreen();
        myPlace = getLocFromSharedPrefs();
        mLocationHelper = new LocationHelper(getApplicationContext());
        mLocationHelper.mResolvingError = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mAntManager = new AntPlusManager(context);
		// show screen early
		refreshScreen();
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		autoResumeRoute();
		dataBaseAdapter = new ANTDBAdapter(context);
		resetPcc();
		startAutoConnectANT();
	}// onCreate

	/**
	 * if tcx file is not old, resume previous route
	 */
	private void autoResumeRoute() {
		if (debugOldTCXFile) {Log.d(this.getClass().getName(), "autoResumeRoute()");}
		// called from onCreate()
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		boolean old = myBikeStat.tcxLog.readTCXFileLastModTime(
				settings.getString(TCX_LOG_FILE_NAME, ""), getTCXFileAutoReset());
		// read .lastModifiedTime from SharedPreferences rather than from File
		if (!old) {
			restoreSharedPrefs();
			myNavRoute.mChosenFile = new File(chosenGPXFile);
			refreshScreen();
			resumingRoute = true;
			// load file in async task with progress bar in case file is big
			// it would generate ANR error
			LoadData task = new LoadData();
			task.execute();
			prefChanged = true;
		} else {// output file is old
			// TODO see if RestoreRoute() does restore all the old data
			mAntManager.restartHR(myBikeStat.getAvgHeartRate(), myBikeStat.getAvgHeartRate());
			myBikeStat.setAvgHeartRate(0);
			myBikeStat.setMaxHeartRate(0);
			mAntManager.restartCadence();
			mAntManager.restartPower();
			mAntManager.restartWheelCal(myBikeStat.getWheelTripDistance());
			mAntManager.restartPowerWheelCal(myBikeStat.getWheelTripDistance());
			deleteAllTmpRouteFiles();
			// have to put this in shared prefs, or the old file name is loaded in onResume
			chosenGPXFile = "";
			settings.edit().putString(KEY_CHOSEN_GPXFILE, chosenGPXFile).apply();
		}
	}

	/**
	 * Retrieve application persistent data.
	 */
	private void loadANTConfiguration() {
		// if (debugAppState) Log.d(this.getClass().getName(), "loadANTConfiguration()");
		// Restore static preferences; is called from initializeANT()
		// note that all sensor data from a "live" session are loaded from
		// restoreSharedPrefs()
		//restart the MDS search cycle # of start-pause-stop cycles
		mAntManager.setNumMDSSearchCycles(0);
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		hasANT = settings.getBoolean(HAS_ANT, false);
		hasANT = AntSupportChecker.hasAntFeature(context);
		// these could have changed in ANTSettings by entering a number in wheel circumference
		mAntManager.wheelCnts.isCalibrated = settings.getBoolean(WHEEL_IS_CAL, false);
		mAntManager.powerWheelCnts.isCalibrated = settings.getBoolean( POWER_WHEEL_IS_CAL, false);
		// trainer mode parameters
		trainerMode = settings.getBoolean(KEY_TRAINER_MODE, false);
		autoConnectANTAll = settings.getBoolean(KEY_AUTO_CONNECT_ALL, false);
		new loadDBDeviceListBackground().execute();
	}

	private void initializeANT() {
		// called from onResume() -> startSensors() and in refreshScreen if pref
		// changed
		if (debugAppState) Log.d(this.getClass().getName(), "initializeANT()");
		loadANTConfiguration();
		if (hrPcc != null) {
			myBikeStat.hasHR = (hrPcc.getCurrentDeviceState() == DeviceState.TRACKING);
		}
		if (cadPcc != null) {
			myBikeStat.hasCadence = (cadPcc.getCurrentDeviceState() == DeviceState.TRACKING);
		}
		if (speedPcc != null) {
			myBikeStat.hasSpeed = (speedPcc.getCurrentDeviceState() == DeviceState.TRACKING)
					&& mAntManager.wheelCnts.isCalibrated;
			if ((speedPcc.getCurrentDeviceState() == DeviceState.TRACKING)
					&& !mAntManager.wheelCnts.isCalibrated) {
				// make sure we're using the current value of wheel
				// circumference in calibrated speed
				speedPcc.subscribeCalculatedSpeedEvent(null);
				subscribeCalibratedSpeed();
			}
		}
		if (powerPcc != null) {
			myBikeStat.hasPower = (powerPcc.getCurrentDeviceState() == DeviceState.TRACKING);
			myBikeStat.hasPowerSpeed = (powerPcc.getCurrentDeviceState() == DeviceState.TRACKING)
					&& mAntManager.powerWheelCnts.isCalibrated;
			if ((powerPcc.getCurrentDeviceState() == DeviceState.TRACKING)
					&& !mAntManager.powerWheelCnts.isCalibrated) {
				// make sure we're using the current value of wheel
				// circumference in calibrated speed
				powerPcc.subscribeCalculatedWheelSpeedEvent(null);
				subscribeCalibratedPowerSpeed();
			}
		}
		// if the speed sensor is not calibrated, read shared prefs to use
		// entered value, or default
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		if (!myBikeStat.hasSpeed) {
			mAntManager.wheelCnts.wheelCircumference = Double.valueOf(settings
					.getString(WHEEL_CIRCUM, DOUBLE_ZERO));
			// set wheel circumference to a default value if stored value out of range
			if ((mAntManager.wheelCnts.wheelCircumference > UPPER_WHEEL_CIRCUM)
					|| (mAntManager.wheelCnts.wheelCircumference < LOWER_WHEEL_CIRCUM)) {
				mAntManager.wheelCnts.wheelCircumference = DEFAULT_WHEEL_CIRCUM;
			}
		}
		// if the power speed sensor is not calibrated, read shared prefs to use same default value
		if (!myBikeStat.hasPowerSpeed) {
			mAntManager.powerWheelCnts.wheelCircumference = Double
					.valueOf(settings.getString(POWER_WHEEL_CIRCUM, DOUBLE_ZERO));
			// set wheel circumference to a default value if stored value out of range
			if ((mAntManager.powerWheelCnts.wheelCircumference > UPPER_WHEEL_CIRCUM)
					|| (mAntManager.powerWheelCnts.wheelCircumference < LOWER_WHEEL_CIRCUM)) {
				mAntManager.powerWheelCnts.wheelCircumference = DEFAULT_WHEEL_CIRCUM;
			}
		}
		// restore the channel state .isSubscribed
		mAntManager.setChannelSubscribed(
				settings.getBoolean(DEVICE_SUBSCRIBED_HRM, false),
				DeviceType.HEARTRATE);
		mAntManager.setChannelSubscribed(
				settings.getBoolean(DEVICE_SUBSCRIBED_CADENCE, false),
				DeviceType.BIKE_CADENCE);
		mAntManager.setChannelSubscribed(
				settings.getBoolean(DEVICE_SUBSCRIBED_SPEED, false),
				DeviceType.BIKE_SPD);
		mAntManager.setChannelSubscribed(
				settings.getBoolean(DEVICE_SUBSCRIBED_POWER, false),
				DeviceType.BIKE_POWER);
	}

	private void initializeMergedRouteTurnList() {
		// called from changeTrackDensityBackground and LoadData
		// don't calculate the route distances for the whole list here
		// maybe do that in a background task, but it has to be re-done
		// for refresh hashmap anyway
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);
		boolean unitPref = (sharedPref.getString(
				getResources().getString(R.string.pref_unit_key), ZERO)
				.equals(ZERO));
		boolean hiViz = sharedPref.getBoolean(HI_VIZ, false);
		String unit;
		int dimLevel = res_white;
		if (unitPref) {
			unit = MILE;
		} else {
			unit = KM;
		}
		// this is the TrackPoint density-reduce array that matches the index of
		// the routeHashMap
		// this makes it easy to match index in refreshHashMap
		myNavRoute.mergedRoute_HashMap.clear();
		routeHashMap.clear();// clears the HashMap
		// go thru the mergedRoute and convert to HashMap
		// called from dealWithGoodData after LoadData
		if (myNavRoute.mergedRoute != null) {
			for (int i = 0; i < myNavRoute.mergedRoute.size(); i++) {
				if (!myNavRoute.mergedRoute.get(i).delete) {
					GPXRoutePoint tempRP;
					tempRP = myNavRoute.mergedRoute.get(i);
					// copying to new GPXRoutePoint ArrayList
					myNavRoute.mergedRoute_HashMap.add(tempRP);
				}
			}
		} else {// mergedRoute == null
			return;
		}
		for (int i = 0; i < myNavRoute.mergedRoute_HashMap.size(); i++) {
			HashMap<String, String> hmItem = new HashMap<>();
			int turnDirIcon = myNavRoute.mergedRoute_HashMap.get(i).turnIconIndex;
			if (hiViz) {
				dimLevel = ContextCompat.getColor(context, R.color.texthiviz);
				// don't put hiViz "X" icon; other hiViz incons have iconLevel
				// +18 for high-viz color
				if (turnDirIcon != 99) {
					turnDirIcon += 18;
				}
			}
			String streetName = myNavRoute.mergedRoute_HashMap.get(i)
					.getStreetName();
			String distanceString = "??";
			if ((i > turnByturnList.getFirstVisiblePosition() - 1)
					&& (i < turnByturnList.getLastVisiblePosition() + 1)) {
				float result[];
				result = distFromMyPlace2WPMR(i);
				// results returns in miles; convert to meters, if needed
				double distMultiplier = mile_per_meter;
				if (unitPref) {
					unit = MILE;
				} else {
					distMultiplier = km_per_meter;
					unit = KM;
				}
				double distance = result[0] * distMultiplier;
				if (gpsLocationCurrent) {
					distanceString = String.format(FORMAT_1F, distance);
				}
				if (distance < 0.1) {// switch to display in feet / m
					int dist;
					// increment in multiples of 20', a likely resolution limit
					if (unitPref) {
						dist = (int) Math.floor(distance * 264) * 20;
						unit = FOOT;
					} else {
						dist = (int) Math.floor(distance * 100) * 10;
						unit = METER;
					}
					if (gpsLocationCurrent) {
						distanceString = String.format(FORMAT_3D, dist);
					}
				}// if dist<0.1
			}// only calculate distance for visible turns; this will be re-done
			// in refreshHashMap, but it may take a while in large lists
			int bearingIcon = myNavRoute.mergedRoute_HashMap.get(i).relBearIconIndex;
			// creating new HashMap
			hmItem.put(KEY_TURN, Integer.toString(turnDirIcon));
			hmItem.put(KEY_STREET, streetName);
			hmItem.put(KEY_DISTANCE, distanceString);
			hmItem.put(KEY_UNIT, unit);
			hmItem.put(KEY_BEARING, Integer.toString(bearingIcon));
			hmItem.put(KEY_DIM, Integer.toString(dimLevel));
			routeHashMap.add(hmItem);

		}// for loop
		// add a blank item to the bottom of the list
		HashMap<String, String> hmItem = new HashMap<>();
		hmItem.put(KEY_TURN, Integer.toString(99));
		hmItem.put(KEY_STREET, "");
		hmItem.put(KEY_DISTANCE, "");
		hmItem.put(KEY_UNIT, "");
		hmItem.put(KEY_BEARING, Integer.toString(0));
		hmItem.put(KEY_DIM, ZERO);
		routeHashMap.add(hmItem);
		// Getting adapter by passing files data ArrayList
		turnByturnAdapter = new TurnByTurnListAdapter(this, routeHashMap);
		turnByturnList.setAdapter(turnByturnAdapter);
		turnByturnList.setSelectionFromTop(myNavRoute.firstListElem, 0);
	}

	private void initializeScreen() {
		mLayout = findViewById(R.id.RelativeLayout101);

		mLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.bkgnd_black));
		initHashMap();
		// the area of the screen displaying power, heart-rate or cadence
		// need the view to set on-click listener
		speedCell = findViewById(R.id.speedcell);
		powerCell = findViewById(R.id.powercell);
		powerCell.setOnClickListener(powerCellClickListener);
		hrCell = findViewById(R.id.hrcell);
		hrCell.setOnClickListener(hrCellClickListener);
		cadCell = findViewById(R.id.cadcell);
		cadCell.setOnClickListener(cadCellClickListener);
		// need to be able to view or hide Cad-HR-Power layout
		cadHRPowerLayout = (RelativeLayout) findViewById(R.id.include1);
		// this is a place to display ant sensor data messages
		antToastAnchor = findViewById(R.id.include2);
		cadenceLabel = (TextView) findViewById(R.id.textView621);
		heartLabel = (TextView) findViewById(R.id.textView623);
		powerLabel = (TextView) findViewById(R.id.textView625);
		powerLabel.setTextColor(ContextCompat.getColor(context, R.color.poweryellow));
		tripDistTitle = (TextView) findViewById(R.id.textView24);
		tripDistLabel = (TextView) findViewById(R.id.textView25);
		tripTimeTitle = (TextView) findViewById(R.id.textView38);
		tripTimeLabel = (TextView) findViewById(R.id.textView39);
		avgSpeedTitle = (TextView) findViewById(R.id.textView26);
		avgSpeedLabel = (TextView) findViewById(R.id.textView27);
		maxSpeedTitle = (TextView) findViewById(R.id.textView28);
		maxSpeedLabel = (TextView) findViewById(R.id.textView29);
		appMessage = (TextView) findViewById(R.id.textView40);
		gpsSpeedTitle = (TextView) findViewById(R.id.textView32);
		gpsSpeedLabel = (TextView) findViewById(R.id.textView33);
		trainerModeComment = (TextView) findViewById(R.id.trainer_mode_comment);
		exitTMBtn = (Button) findViewById(R.id.exit_trainer_mode_btn);
		exitTMBtn.setOnClickListener(exitTrainerModeButtonClickListener);
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		useANTData = settings.getBoolean(USE_ANT, true);
		showANTData = settings.getBoolean(SHOW_ANT, true);
		prefChanged = true;
	}// initializeScreen()

	/**
	 * the HashMap is the turn-by-turn rows in the scrolling list
	 */
	private void initHashMap() {
		routeHashMap.clear();
		HashMap<String, String> hmItem = new HashMap<>();
		for (int j = 0; j < 7; j++) {
			// just put an X for turn and north direction arrow in the list
			hmItem.put(KEY_TURN, Integer.toString(99));
			hmItem.put(KEY_STREET, "");
			hmItem.put(KEY_DISTANCE, "");
			hmItem.put(KEY_UNIT, "");
			hmItem.put(KEY_BEARING, ZERO);
			hmItem.put(KEY_DIM, ZERO);
			routeHashMap.add(hmItem);
		}
		turnByturnAdapter = new TurnByTurnListAdapter(this, routeHashMap);
		turnByturnList.setAdapter(turnByturnAdapter);
	}

    private Location getLocFromSharedPrefs() {
        Location aLoc = new Location(LocationManager.NETWORK_PROVIDER);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        aLoc.setLongitude(Double.parseDouble(settings.getString(SAVED_LON, PREFS_DEFAULT_LONGITUDE)));
        aLoc.setLatitude(Double.parseDouble(settings.getString(SAVED_LAT, PREFS_DEFAULT_LATITUDE)));
        aLoc.setAltitude(0);
        // this is just temporary until we get a location from LocationHelper
        aLoc.setTime(settings.getLong(PREF_SAVED_LOC_TIME, PREFS_DEFAULT_TIME));
        return aLoc;
    }

	// this operates the turn-list scroller
	public OnScrollListener scrollListener = new OnScrollListener() {
		public void onScroll(AbsListView view, int firstItem,
				int visibleItemCount, int totalItemCount) {
		}

		public void onScrollStateChanged(AbsListView view, int scrollState) {
			switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:
					if (scrolling) {
						myNavRoute.firstListElem = view.getFirstVisiblePosition();
						scrolling = false;
					}
					refreshScreen();
					break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				case OnScrollListener.SCROLL_STATE_FLING:
					scrolling = true;
					break;
				default:
					break;
			}
		}
	};

	/**
	 * display max, average power and total energy when the power display area
	 * is clicked
	 */
	private android.view.View.OnClickListener powerCellClickListener = new android.view.View.OnClickListener() {

		@SuppressLint("RtlHardcoded")
		@Override
		public void onClick(final View v) {
			String toastText = "Max Power: " + myBikeStat.getMaxPower()
					+ " W\n" + "Avg Power: " + myBikeStat.getAvgPower()
					+ " W\n" + "Total Energy: "
					+ (int) (mAntManager.getCumEnergy() / 1000) + " kJ";
			viewToast(toastText, -80, Gravity.TOP | Gravity.RIGHT, v, res_white);
		}
	};

	/**
	 * display max, average heart rate when the hr display area is clicked
	 */
	private android.view.View.OnClickListener hrCellClickListener = new android.view.View.OnClickListener() {

		@Override
		public void onClick(final View v) {
			String toastText = "Max Heart Rate: "
					+ myBikeStat.getMaxHeartRate() + "\n" + "Avg Heart Rate: "
					+ myBikeStat.getAvgHeartRate();
			viewToast(toastText, -80, Gravity.TOP | Gravity.CENTER_HORIZONTAL,
					v, res_white);
		}
	};

	/**
	 * display max, average cadence when the cad display area is clicked
	 */
	private android.view.View.OnClickListener cadCellClickListener = new android.view.View.OnClickListener() {

		@SuppressLint("RtlHardcoded")
		@Override
		public void onClick(final View v) {
			String avgCadText = "0";
			String maxCadText = "0";
			// if we have a cadence sensor (max cad > 0) use that
			if (myBikeStat.getMaxCadence() > 0) {
				avgCadText = Integer.toString(myBikeStat.getAvgCadence());
				maxCadText = Integer.toString(myBikeStat.getMaxCadence());
			}
			String toastText = "Max Cadence: " + maxCadText + "\n"
					+ "Avg Cadence: " + avgCadText;
			viewToast(toastText, -80, Gravity.TOP | Gravity.LEFT, v, res_white);
		}
	};

	private android.view.View.OnClickListener exitTrainerModeButtonClickListener = new android.view.View.OnClickListener() {

		@Override
		public void onClick(View v) {
			exitTrainerMode();
		}
	};

	public OnItemLongClickListener longClickListener = new OnItemLongClickListener() {
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos,
				long id) {
			// only respond to long-click if location is current & there is a
			// route
			if ((myNavRoute.mergedRoute_HashMap.size() < 1)
					| !gpsLocationCurrent) {
				return true;
			}
			if (checkNearEnough(pos)) {
				// only if we're near to the clicked way point
				// Now do all the nitty-gritty of re-navigating from here
				// set bonus miles so logic will recognize way points
				myNavRoute.setBonusMiles(myBikeStat.getGPSTripDistance()
						- myNavRoute.mergedRoute_HashMap.get(pos)
						.getRouteMiles());
				if (debugAppState)
					Log.d(this.getClass().getName() + "LongClick", "pos: "+ pos
							+ " tripDist: "+ myBikeStat.getGPSTripDistance()
							+ " routeMiles: " + myNavRoute.mergedRoute_HashMap.get(pos).getRouteMiles());
				// set .beenThere = false for all way points
				// refreshHashMap will re-set .beenThere for all waypoints
				for (int index = 0; index < myNavRoute.mergedRoute_HashMap.size(); index++) {
					GPXRoutePoint tempRP;
					tempRP = myNavRoute.mergedRoute_HashMap.get(index);
					tempRP.setBeenThere(false);
					myNavRoute.mergedRoute_HashMap.set(index, tempRP);
				}// for all way points in the route
			}
			return true;
		}
	};

	private void saveState() {
		if (debugAppState) {
			Log.i(this.getClass().getName(), "saveState()");
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				updateDBData(" - from saveState()");
			}
		}).start();

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		// Save channel state .isSubscribed
		editor.putBoolean(DEVICE_SUBSCRIBED_HRM,
				mAntManager.isChannelSubscribed(DeviceType.HEARTRATE));
		editor.putBoolean(DEVICE_SUBSCRIBED_CADENCE,
				mAntManager.isChannelSubscribed(DeviceType.BIKE_CADENCE));
		editor.putBoolean(DEVICE_SUBSCRIBED_SPEED,
				mAntManager.isChannelSubscribed(DeviceType.BIKE_SPD));
		editor.putBoolean(DEVICE_SUBSCRIBED_POWER,
				mAntManager.isChannelSubscribed(DeviceType.BIKE_POWER));
		// remove all the paired ANT device numbers so it doesn't get re-loaded
		editor.putInt(DEVICE_NUMBER_HRM, 0);
		editor.putInt(DEVICE_NUMBER_SPEED, 0);
		editor.putInt(DEVICE_NUMBER_CADENCE, 0);
		editor.putInt(DEVICE_NUMBER_SPEEDCADENCE, 0);
		editor.putInt(DEVICE_NUMBER_POWER, 0);

		// all the cadence sensor parameters
		editor.putInt(PREV_PED_CNTS, (int) mAntManager.crankCadenceCnts.prevCount);
		editor.putBoolean(PED_CNTS_INIT, mAntManager.crankCadenceCnts.initialized);
		editor.putInt(NUM_PEDAL_CAD, (int) mAntManager.getNumPedalCad());
		editor.putInt(TOTAL_PEDAL_CAD, (int) mAntManager.getTotalPedalCad());
		editor.putInt(AVG_CADENCE, myBikeStat.getAvgCadence());
		editor.putInt(MAX_CADENCE, myBikeStat.getMaxCadence());

		// all the HR sensor parameters
		editor.putInt(TOTAL_HR_COUNTS, (int) mAntManager.getTotalHRCounts());
		editor.putInt(NUM_HR_EVENTS, (int) mAntManager.getNumHREvents());
		editor.putInt(AVG_HR, myBikeStat.getAvgHeartRate());
		editor.putInt(MAX_HR, myBikeStat.getMaxHeartRate());

		// all the speed sensor parameters
		editor.putInt(NUM_WHEEL_CNTS, (int) mAntManager.wheelCnts.calTotalCount);
		editor.putLong(WHEEL_CUMREV, mAntManager.wheelCnts.cumulativeRevolutions);
		editor.putLong(WHEEL_CUMREV_AT_START, mAntManager.wheelCnts.cumulativeRevsAtCalStart);
		editor.putBoolean(WHEEL_IS_CAL, mAntManager.wheelCnts.isCalibrated);
		editor.putString(WHEEL_CIRCUM, Double.toString(mAntManager.wheelCnts.wheelCircumference));
		editor.putString(START_DIST, Double.toString(mAntManager.wheelCnts.calGPSStartDist));
		editor.putString(MAX_SPEED, Double.toString(myBikeStat.getMaxSpeed()));
		editor.putString(WHEEL_TRIP_TIME, Double.toString(myBikeStat.getWheelRideTime()));
		editor.putString(WHEEL_TRIP_DISTANCE, Double.toString(myBikeStat.getWheelTripDistance()));
		editor.putLong(WHEEL_PREV_COUNT, mAntManager.wheelCnts.prevCount);

		// power data
		editor.putBoolean(POWER_CNTS_INIT, mAntManager.calcPowerData.initialized);
		editor.putInt(CUM_ENERGY, (int) mAntManager.getCumEnergy());
		editor.putString(CUM_POWER_TIME, Double.toString(mAntManager.getCumPowerTime()));
		editor.putInt(AVG_POWER, myBikeStat.getAvgPower());
		editor.putInt(MAX_POWER, myBikeStat.getMaxPower());

		// calculated crank cadence data from power meter
		editor.putInt(TOTAL_CALC_CAD, (int) mAntManager.getTotalCalcCrankCad());
		editor.putInt(NUM_CALC_CAD, (int) mAntManager.getNumCalcCrankCad());
		editor.putInt(AVG_CALC_CADENCE, myBikeStat.getAvgCadence());
		editor.putInt(MAX_CALC_CADENCE, myBikeStat.getMaxCadence());

		// power wheel data
		editor.putLong(POWER_WHEEL_CUMREV, mAntManager.powerWheelCnts.cumulativeRevolutions);
		editor.putLong(POWER_WHEEL_PREV_COUNT, mAntManager.powerWheelCnts.prevCount);
		editor.putLong(POWER_WHEEL_CUMREV_AT_START, mAntManager.powerWheelCnts.cumulativeRevsAtCalStart);
		editor.putInt(NUM_POWER_WHEEL_CNTS, (int) mAntManager.powerWheelCnts.calTotalCount);
		editor.putBoolean(POWER_WHEEL_IS_CAL, mAntManager.powerWheelCnts.isCalibrated);
		editor.putString(POWER_WHEEL_CIRCUM, Double.toString(mAntManager.powerWheelCnts.wheelCircumference));
		editor.putString(POWER_START_DIST, Double.toString(mAntManager.powerWheelCnts.calGPSStartDist));
		editor.putString(POWER_WHEEL_TRIP_TIME, Double.toString(myBikeStat.getPowerWheelRideTime()));
		editor.putString(POWER_WHEEL_TRIP_DISTANCE, Double.toString(myBikeStat.getPowerWheelTripDistance()));

		String pluginVersionStr = getString(R.string.na);
		int pluginVersion = AntPluginPcc.getInstalledPluginsVersionNumber(context);
		// ANT plug-ins not available
		if (pluginVersion != -1) {
			pluginVersionStr = AntPluginPcc.getInstalledPluginsVersionString(context);
		}
		editor.putString(KEY_PLUG_IN_VERSION, pluginVersionStr);
		editor.putBoolean(HAS_ANT, hasANT);
		editor.putBoolean(KEY_AUTO_CONNECT_ALL, autoConnectANTAll);

		editor.putString(SAVED_LAT, Double.toString(myPlace.getLatitude()));
		editor.putString(SAVED_LON, Double.toString(myPlace.getLongitude()));
		editor.putLong(PREF_SAVED_LOC_TIME, myPlace.getTime());
		editor.putString(TRIP_TIME, Double.toString(myBikeStat.getGPSRideTime()));
		editor.putString(TRIP_DISTANCE, Double.toString(myBikeStat.getGPSTripDistance()));
		editor.putString(WHEEL_TRIP_DISTANCE, Double.toString(myBikeStat.getWheelTripDistance()));
		editor.putString(PREV_WHEEL_TRIP_DISTANCE, Double.toString(myBikeStat.getPrevWheelTripDistance()));
		editor.putString(SPOOF_WHEEL_TRIP_DISTANCE, Double.toString(myBikeStat.getSpoofWheelTripDistance()));
		editor.putString(PREV_SPOOF_WHEEL_TRIP_DISTANCE, Double.toString(myBikeStat.getPrevSpoofWheelTripDistance()));
		editor.putBoolean(KEY_TRAINER_MODE, trainerMode);
		editor.putString(BONUS_MILES, Double.toString(myNavRoute.getBonusMiles()));
		editor.putInt(CURR_WP, myNavRoute.currWP);
		editor.putInt(FIRST_LIST_ELEM, myNavRoute.firstListElem);
		editor.putString(KEY_CHOSEN_GPXFILE, chosenGPXFile);
		editor.putString(KEY_CHOSEN_TCXFILE, chosenTCXFile);
		editor.putString(TCX_LOG_FILE_NAME, myBikeStat.tcxLog.outFileName);
		editor.putInt(TCX_LOG_FILE_FOOTER_LENGTH, myBikeStat.tcxLog.outFileFooterLength);
		editor.putBoolean(KEY_FORCE_NEW_TCX, forceNewTCX_FIT);
		editor.putLong(ALMANAC_RESET_TIME, almanacResetTime);
		editor.apply();
	}

	@Override
	protected void onResume() {
		if (debugAppState) { Log.i(this.getClass().getName(), "onResume()"); }
        Log.e(this.getClass().getName(), "connecting to GoogleApiCient");
        mLocationHelper.mGoogleApiClient.connect();
		try {
			if (dataBaseAdapter == null) {
				Log.e(this.getClass().getName(), "dataBaseAdapter is null");
			} else {
				dataBaseAdapter.close();
				dataBaseAdapter.open();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		super.onResume();
		//		Log.w(this.getClass().getName(), "Airplane mode radios list: " + Settings.Global.getString(getContentResolver(), Global.AIRPLANE_MODE_RADIOS));
		//save the name of the route file temporarily until its validated
		prevChosenFile = chosenGPXFile;
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		chosenGPXFile = settings.getString(KEY_CHOSEN_GPXFILE, "");
		chosenTCXFile = settings.getString(KEY_CHOSEN_TCXFILE, "");
		myBikeStat.tcxLog.outFileName = settings.getString(TCX_LOG_FILE_NAME, "");
		myBikeStat.tcxLog.outFileFooterLength = settings.getInt(TCX_LOG_FILE_FOOTER_LENGTH, 1);
		//if we're returning from SettingsActivity, test if Track Point density
		// has changed. If so we must re-load chosenFile
		// see if SharedPreferences value of trackDensity is different than DefaultSharedPreferences
		int trackDensity = settings.getInt(KEY_TRACK_DENSITY, 0);
		myNavRoute.defaultTrackDensity = 0;
		SharedPreferences defaultSettings = PreferenceManager.getDefaultSharedPreferences(this);
		String defTrackDensity = defaultSettings.getString(getResources()
				.getString(R.string.pref_trackpoint_density_key), ZERO);
		myNavRoute.defaultTrackDensity = Integer.valueOf(defTrackDensity);
		if (trackDensity != myNavRoute.defaultTrackDensity && myNavRoute.mergedRoute_HashMap.size() > 0) {
			// save RouteMiles @ firstListElem so we can recalculate firstListElem with new track density
			myNavRoute.routeMilesatFirstListElem = myNavRoute.mergedRoute_HashMap.get(
					myNavRoute.firstListElem).getRouteMiles();
			editor.putInt(KEY_TRACK_DENSITY, myNavRoute.defaultTrackDensity);
			editor.apply();
			new ChangeTrackDensityBackground().execute();
			// save the route here in a background task
			new SaveRouteFileBackground().execute(chosenGPXFile);
		}
		prefChanged = true;
		startSensors();
	}

	@Override
	protected void onStart() {
		if (debugAppState) Log.i(this.getClass().getName(), "onStart()");
		// This verification should be done during onStart() because the system
		// calls this method when the user returns to the activity, which
		// ensures the desired location provider is enabled each time the
		// activity resumes from the stopped state.
		/* is the GPS receiver enabled? */
		if (!isGPSLocationEnabled(getApplicationContext()) & gpsTurnOn) {
			//requestGPSOn();
		} else {
			// gps provider is on; time-tag acquisition start time
			gpsAcqSysTimeStamp = SystemClock.elapsedRealtime();
		}
		super.onStart();
	}

	@Override
	protected void onPause() {
		if (debugAppState) Log.i(this.getClass().getName(), "onPause()");
		stopMultiDeviceSearch();
		if (pm != null && pm.isScreenOn()) {
			// shouldn't stop location, sensor or autoconnect watchdogs if
			// screen off and recording data
			// timerTask can persist; make sure nothing autoConnects by clearing the device list

			stopSensorWatchdog();
			stopLocationWatchdog();
			stopSpoofingLocations();
		}
		saveState();
		super.onPause();
	}

	@Override
	protected void onStop() {
		if (debugAppState)
			Log.i(this.getClass().getName(), "onStop()");
		super.onStop();
		if (dataBaseAdapter != null) {
			dataBaseAdapter.close();
		}
	}

	@Override
	protected void onDestroy() {
		if (debugAppState) Log.d(this.getClass().getName(), "onDestroy()");
		unsubscribeCadEvents();
		unsubscribeHrEvents();
		unsubscribePowerEvents();
		unsubscribeSpeedEvents();
		resetPcc();
		mAntManager.antDBDeviceList.clear();
        mLocationHelper.stopLocationUpdates();
        mLocationHelper.mGoogleApiClient.disconnect();
		myBikeStat.tcxLog.closeTCXLogFile();
		//we're not uploading the file, so "" means just close file
		new CloseFitFileBackground().execute("");
		//powerCell.getHandler().removeCallbacksAndMessages(null);
		stopSpoofingLocations();
		stopMultiDeviceSearch();
		stopSensorWatchdog();
		stopLocationWatchdog();
		stopAutoConnectAnt();
		super.onDestroy();
	}

	private void startSensors() {
		// called from onResume()
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		useANTData = settings.getBoolean(USE_ANT, true);
		showANTData = settings.getBoolean(SHOW_ANT, true);
		if (debugAppState) Log.i(this.getClass().getName(), "startSensors()");
		gpsLocationCurrent = false;
		myBikeStat.setPaused(true);
		myBikeStat.setSpeed(trainerMode);
		// Since startSensors is called from onResume, we may already have these
		// watchdogs running. Stop them first before starting.
		stopLocationWatchdog();
		startLocationWatchdog();
		stopSensorWatchdog();
		if (useANTData) {
			initializeANT();
			startSensorWatchdog();
		} else {
			showANTData = false;
		}
		if (trainerMode) {
			refreshScreen();
			stopSpoofingLocations();
			startSpoofingLocations();
		}
	}

	private void requestGPSOn() {
		// we're only going to ask to turn-on GPS once
		gpsTurnOn = false;
		dialogType = 300;
		dealWithDialog(R.string.reqGPSOn_message, R.string.reqGPSOn_title);
	}

    private void dealWithDialog(int message, int title) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				MainActivity.this);
		// Add the buttons
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						handleDialogAction();
					}
				});
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
					}
				});
		// Set other dialog properties
		builder.setMessage(message).setTitle(title).show();
	}

	private void handleDialogAction() {// OK button pressed in Alert Dialog
		switch (dialogType) {
			case 100:
				toggleAirplaneMode();
				break;
			case 200: // menu reset type
				resetData();
				SharedPreferences settings1 = getSharedPreferences(PREFS_NAME, 0);
				SharedPreferences.Editor editor1 = settings1.edit();
				editor1.putString(BONUS_MILES,
						Double.toString(myNavRoute.getBonusMiles()));
				editor1.putString(TRIP_TIME,
						Double.toString(myBikeStat.getGPSRideTime()));
				editor1.putString(TRIP_DISTANCE,
						Double.toString(myBikeStat.getGPSTripDistance()));
				editor1.putString(MAX_SPEED,
						Double.toString(myBikeStat.getMaxSpeed()));
				// save the pedCount and wheelCount values here, too
				editor1.putInt(NUM_WHEEL_CNTS,
						(int) mAntManager.wheelCnts.calTotalCount);
				editor1.putInt(NUM_POWER_WHEEL_CNTS,
						(int) mAntManager.powerWheelCnts.calTotalCount);
				editor1.putBoolean(PED_CNTS_INIT,
						mAntManager.crankCadenceCnts.initialized);
				editor1.putString(START_DIST,
						Double.toString(mAntManager.wheelCnts.calGPSStartDist));
				editor1.apply();
				// clear the NavRoute
				myNavRoute.mergedRoute.clear();
				myNavRoute.mergedRoute_HashMap.clear();
				chosenGPXFile = "";
				createTitle(chosenGPXFile);
				// clear the turn list
				initHashMap();
				firstSpoofLocation = true;
				gpsFirstLocation = true;
				refreshScreen();
				// open a new tcx log file when we get the next location
				// the previous Log File will be closed when the new one is opened
				forceNewTCX_FIT = true;
				// reset the NavRoute filename
				break;
			case 300: // GPS enable type
				enableLocationSettings();
				break;
			case 400: // menu restore type
				restoreSharedPrefs();
				myNavRoute.mChosenFile = new File(chosenGPXFile);
				refreshScreen();
				resumingRoute = true;
				// load file in async task with progress bar in case file is big
				new LoadData().execute();
				break;
			default:
				break;
		}
	}

	/**
	 * clear all the trip data
	 */
	private void resetData() {
		if (debugAppState) Log.i(this.getClass().getName(), "resetData()");
		myBikeStat.reset();
		myNavRoute.setBonusMiles(0);
		SharedPreferences settings1 = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor1 = settings1.edit();
		editor1.putString(BONUS_MILES,
				Double.toString(myNavRoute.getBonusMiles()));
		editor1.putString(TRIP_TIME,
				Double.toString(myBikeStat.getGPSRideTime()));
		editor1.putString(TRIP_DISTANCE,
				Double.toString(myBikeStat.getGPSTripDistance()));
		editor1.putString(WHEEL_TRIP_TIME,
				Double.toString(myBikeStat.getWheelRideTime()));
		editor1.putString(WHEEL_TRIP_DISTANCE,
				Double.toString(myBikeStat.getWheelTripDistance()));
		editor1.putString(POWER_WHEEL_TRIP_TIME,
				Double.toString(myBikeStat.getPowerWheelRideTime()));
		editor1.putString(POWER_WHEEL_TRIP_DISTANCE,
				Double.toString(myBikeStat.getPowerWheelTripDistance()));
		editor1.putString(MAX_SPEED, Double.toString(myBikeStat.getMaxSpeed()));
		// save the pedCount and wheelCount values here, too
		editor1.putInt(NUM_WHEEL_CNTS,
				(int) mAntManager.wheelCnts.calTotalCount);
		editor1.putInt(NUM_POWER_WHEEL_CNTS,
				(int) mAntManager.powerWheelCnts.calTotalCount);
		editor1.putBoolean(PED_CNTS_INIT,
				mAntManager.crankCadenceCnts.initialized);
		editor1.putString(START_DIST,
				Double.toString(mAntManager.wheelCnts.calGPSStartDist));
		editor1.putBoolean(KEY_TRAINER_MODE, trainerMode);
		editor1.apply();
		mAntManager.restartHR(0, 0);
		mAntManager.restartCadence();
		mAntManager.restartPower();
		mAntManager.restartWheelCal(myBikeStat.getWheelTripDistance());
		mAntManager.restartPowerWheelCal(myBikeStat.getWheelTripDistance());
		myBikeStat.hasSpeed = false;
		myBikeStat.hasPowerSpeed = false;
		prefChanged = true;
	}

	private void toggleAirplaneMode() {
		String action = Settings.ACTION_AIRPLANE_MODE_SETTINGS;
		Intent settingsIntent = new Intent(action);
		startActivity(settingsIntent);
	}

	private void enableLocationSettings() {
		Intent settingsIntent = new Intent(
				Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(settingsIntent);
	}

	private void restoreSharedPrefs() {
		// called from autoResumeRoute() when tcx file is not old and menu:Restore
		myPlace = getLocFromSharedPrefs();
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		String s = settings.getString(TRIP_TIME, Double.toString(0.1));
		myBikeStat.setGPSTripTime(Double.valueOf(s));
		s = settings.getString(TRIP_DISTANCE, DOUBLE_ZERO);
		myBikeStat.setGPSTripDistance(Double.valueOf(s));
		s = settings.getString(WHEEL_TRIP_TIME, Double.toString(0.1));
		myBikeStat.setWheelRideTime(Double.valueOf(s));
		s = settings.getString(POWER_WHEEL_TRIP_TIME, Double.toString(0.1));
		myBikeStat.setPowerWheelRideTime(Double.valueOf(s));
		s = settings.getString(WHEEL_TRIP_DISTANCE, DOUBLE_ZERO);
		myBikeStat.setWheelTripDistance(Double.valueOf(s));
		s = settings.getString(POWER_WHEEL_TRIP_DISTANCE, DOUBLE_ZERO);
		myBikeStat.setPowerWheelTripDistance(Double.valueOf(s));
		s = settings.getString(PREV_WHEEL_TRIP_DISTANCE, DOUBLE_ZERO);
		myBikeStat.setPrevWheelTripDistance(Double.valueOf(s));
		s = settings.getString(SPOOF_WHEEL_TRIP_DISTANCE, DOUBLE_ZERO);
		myBikeStat.setSpoofWheelTripDistance(Double.valueOf(s));
		s = settings.getString(PREV_SPOOF_WHEEL_TRIP_DISTANCE, DOUBLE_ZERO);
		myBikeStat.setPrevSpoofWheelTripDistance(Double.valueOf(s));
		myNavRoute.currWP = settings.getInt(CURR_WP, WILDCARD);
		myNavRoute.firstListElem = settings.getInt(FIRST_LIST_ELEM, WILDCARD);
		s = settings.getString(BONUS_MILES, DOUBLE_ZERO);
		myNavRoute.setBonusMiles(Double.valueOf(s));
		almanacResetTime = settings.getLong(ALMANAC_RESET_TIME, 0);
		chosenGPXFile = settings.getString(KEY_CHOSEN_GPXFILE, "");
		chosenTCXFile = settings.getString(KEY_CHOSEN_TCXFILE, "");
		myBikeStat.tcxLog.outFileName = settings.getString(TCX_LOG_FILE_NAME, "");
		myBikeStat.tcxLog.outFileFooterLength = settings.getInt(TCX_LOG_FILE_FOOTER_LENGTH, 1);
		forceNewTCX_FIT = settings.getBoolean(KEY_FORCE_NEW_TCX, false);
		// all the ANT device measurements
		// cadence data
		mAntManager.crankCadenceCnts.prevCount = settings.getInt(PREV_PED_CNTS, 0);
		mAntManager.crankCadenceCnts.initialized = settings.getBoolean(PED_CNTS_INIT, false);
		mAntManager.setNumPedalCad(settings.getInt(NUM_PEDAL_CAD, 0));
		mAntManager.setTotalPedalCad(settings.getInt(TOTAL_PEDAL_CAD, 0));
		myBikeStat.setAvgCadence(settings.getInt(AVG_CADENCE, 0));
		myBikeStat.setMaxCadence(settings.getInt(MAX_CADENCE, 0));
		// HR data
		mAntManager.setTotalHRCounts(settings.getInt(TOTAL_HR_COUNTS, 0));
		mAntManager.setNumHREvents(settings.getInt(NUM_HR_EVENTS, 0));
		myBikeStat.setAvgHeartRate(settings.getInt(AVG_HR, 0));
		myBikeStat.setMaxHeartRate(settings.getInt(MAX_HR, 0));
		// wheel data
		mAntManager.wheelCnts.calTotalCount = settings.getInt(NUM_WHEEL_CNTS, 0);
		mAntManager.wheelCnts.cumulativeRevsAtCalStart = settings.getLong(WHEEL_CUMREV_AT_START, 0);
		mAntManager.wheelCnts.cumulativeRevolutions = settings.getLong(WHEEL_CUMREV, 0);
		mAntManager.wheelCnts.isCalibrated = settings.getBoolean(WHEEL_IS_CAL, false);
		mAntManager.wheelCnts.prevCount = settings.getLong(WHEEL_PREV_COUNT, 0);
		mAntManager.wheelCnts.wheelCircumference = Double.valueOf(settings.getString(WHEEL_CIRCUM, DOUBLE_ZERO));
		mAntManager.wheelCnts.calGPSStartDist = Double.valueOf(settings.getString(START_DIST, DOUBLE_ZERO));
		myBikeStat.setMaxSpeed(Double.valueOf(settings.getString(MAX_SPEED, DOUBLE_ZERO)));
		// power data
		mAntManager.setCumEnergy(settings.getInt(CUM_ENERGY, 0));
		mAntManager.setCumPowerTime(Double.valueOf(settings.getString(CUM_POWER_TIME, DOUBLE_ZERO)));
		myBikeStat.setAvgPower(settings.getInt(AVG_POWER, 0));
		myBikeStat.setMaxPower(settings.getInt(MAX_POWER, 0));
		mAntManager.powerWheelCnts.calTotalCount = settings.getInt(NUM_POWER_WHEEL_CNTS, 0);
		mAntManager.powerWheelCnts.cumulativeRevsAtCalStart = settings.getLong(POWER_WHEEL_CUMREV_AT_START, 0);
		mAntManager.powerWheelCnts.cumulativeRevolutions = settings.getLong(POWER_WHEEL_CUMREV, 0);
		mAntManager.powerWheelCnts.prevCount = settings.getLong(POWER_WHEEL_PREV_COUNT, 0);
		mAntManager.powerWheelCnts.isCalibrated = settings.getBoolean(POWER_WHEEL_IS_CAL, false);
		mAntManager.powerWheelCnts.wheelCircumference = Double.valueOf(settings
				.getString(POWER_WHEEL_CIRCUM, DOUBLE_ZERO));
		mAntManager.powerWheelCnts.calGPSStartDist = Double.valueOf(settings
				.getString(POWER_START_DIST, DOUBLE_ZERO));
		// calculated crank cadence data from power meter
		mAntManager.setTotalCalcCrankCad(settings.getInt(TOTAL_CALC_CAD, 0));
		mAntManager.setNumCalcCrankCad(settings.getInt(NUM_CALC_CAD, 0));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.activity_display, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_load:
				if (trainerMode) {
					viewToast(getString(R.string.can_t_navigate_in_trainer_mode_),
							40, ANT_TOAST_GRAVITY, antToastAnchor, res_white);
				} else {
					Intent loadFileIntent = new Intent(this, ShowFileList.class);
					//tell the ShowFileList chooser to display route files
					loadFileIntent.putExtra(CHOOSER_TYPE, ROUTE_FILE_TYPE);
					loadFileIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					startActivityForResult(loadFileIntent, RC_SHOW_FILE_LIST);
				}
				break;
			case R.id.menu_settings:
				Intent i = new Intent(this, SettingsActivity.class);
				startActivity(i);
				// use this to tell refreshScreen to re-write the titles
				prefChanged = true;
				break;
			case R.id.menu_reset:
				dialogType = 200;
				dealWithDialog(R.string.reset_message, R.string.reset_title);
				break;
			case R.id.menu_reset_strava:
				// call stravashare with empty filename to just authorize, test for empty filename before doing upload
				Intent stravaUploadIntent = new Intent(MainActivity.this, StravaShareCBPlus.class);
				stravaUploadIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				stravaUploadIntent.putExtra(UPLOAD_FILENAME, "");
				startActivityForResult(stravaUploadIntent, UPLOAD_FILE_SEND_REQUEST_CODE);
				break;
			case R.id.action_share:
				// this intent displays a list of files for the user to select
				Intent shareFileIntent = new Intent(MainActivity.this, ShowFileList.class);
				// change "type" parameter to display .tcx or .fit files in ShowFileList()
				shareFileIntent.putExtra(CHOOSER_TYPE, ACTIVITY_FILE_TYPE);
				startActivityForResult(shareFileIntent, RC_SHOW_FILE_LIST);
				break;
			case R.id.menu_restore_route:
				dialogType = 400;
				dealWithDialog(R.string.restore_message, R.string.restore_title);
				break;
			case R.id.menu_about:
				Intent i11 = new Intent(this, AboutScroller.class);
				startActivity(i11);
				break;
			case R.id.menu_ant:
				Intent ant_settings = new Intent(this, ANTSettings.class);
				ant_settings.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivityForResult(ant_settings, RC_ANT_SETTINGS);
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}// onOptionsItemSelected()

	private void refreshScreen() {
		if (mAntManager.isMDSStarting()) {
			if (debugMDS) Log.e(this.getClass().getName(), "couldn't do refreshScreen(), MDSStarting");
			return;
		}		if (prefChanged) {
			// if units changed, refresh units in titles
			// this is only the default value, wheel will be calibrated when
			// receiving GPS locations
			mLayout.invalidate();
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
			boolean hiViz = sharedPref.getBoolean(HI_VIZ, false);
			int backgroundColor = ContextCompat.getColor(context, R.color.bkgnd_gray);
			if (hiViz) {
				backgroundColor = ContextCompat.getColor(context, R.color.bkgnd_black);
			}
			mLayout.setBackgroundColor(backgroundColor);
			refreshTitles();
			// if display pref changed, update CAD, HR, Power display
			refreshCadHRPower();
			prefChanged = false;
		}
		if (trainerMode) {
			trainerModeComment.setVisibility(View.VISIBLE);
			exitTMBtn.setVisibility(View.VISIBLE);
			turnByturnList.setVisibility(View.GONE);
		} else {
			trainerModeComment.setVisibility(View.GONE);
			exitTMBtn.setVisibility(View.GONE);
			turnByturnList.setVisibility(View.VISIBLE);
		}
		setScreenDim();
		refreshMergedRouteHashMap();
		refreshBikeStatRow();
	}// refreshScreen()

	/**
	 * read the tcx file reset value from preferences
	 */
	private long getTCXFileAutoReset() {
		int[] resetTimes = {2, 4, 6, 8};
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		String theString = sharedPref.getString(
				getResources().getString(R.string.pref_tcx_idle_time_key), "3");
		Integer tcxFileResetTimeIndex = Integer.parseInt(theString);
		return resetTimes[tcxFileResetTimeIndex];
	}

	/**
	 * show or hide the Cad-HR-power display
	 */
	private void refreshCadHRPower() {
		if (mAntManager.isMDSStarting()) {
			if (debugMDS) Log.e(this.getClass().getName(), "couldn't do refreshCadHRPower(), MDSStarting");
			return;
		}		// only do this when returning from settings menu or in startSensors()
		// also change the HR Title from BPM to %max
		int visibility = View.GONE;
		if (showANTData) {
			visibility = View.VISIBLE;
			// update the values
			updateCadHrPowerLabels();
		}
		cadHRPowerLayout.setVisibility(visibility);
		cadHRPowerLayout.invalidate();
	}

	/**
	 * refresh the cadence, heart-rate and power values
	 */
	private void updateCadHrPowerLabels() {
		refreshCadence();
		refreshHR();
		refreshPower();
	}

	private void refreshPower() {
		if (mAntManager.isMDSStarting()) {
			if (debugMDS) Log.e(this.getClass().getName(), "couldn't do refreshPower(), MDSStarting");
			return;
		}
		powerCell.post(new Runnable() {
			@Override
			public void run() {
				long startTime = System.nanoTime();
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
				boolean hiViz = sharedPref.getBoolean(HI_VIZ, false);
				int textColor = ContextCompat.getColor(context, R.color.white);
				if (hiViz) {
					textColor = ContextCompat.getColor(context, R.color.texthiviz);
				}
				if ((powerPcc != null)
						&& (powerPcc.getCurrentDeviceState() == DeviceState.TRACKING)) {
					String powerText = Integer.toString((int) ((myBikeStat
							.getPower() + myBikeStat.getPrevPower()) / 2.));
					// Only display raw power if calculated power is not
					// current, and we have a new, current raw power event
					if (!mAntManager.calcPowerData.isDataCurrent) {
						powerText = Integer.toString((int) ((myBikeStat
								.getRawPower() + myBikeStat.getPrevRawPower()) / 2.));
					}
					//if (debugMDS){Log.i(this.getClass().getName(), "refreshPower() - text: " + powerText);}
					powerLabel.setText(powerText);
				} else {// null or not tracking
					powerLabel.setText(DASHES);
					powerCell.setBackgroundColor(ContextCompat.getColor(context, R.color.bkgnd_gray));
					if (hiViz) {
						powerCell.setBackgroundColor(ContextCompat.getColor(context, R.color.bkgnd_black));
					}
				}
				if (powerPcc != null) {
					if (powerPcc.getCurrentDeviceState() == DeviceState.SEARCHING) {
						powerCell.setBackgroundColor(ContextCompat.getColor(context, R.color.bkgnd_green));
					} else if (powerPcc.getCurrentDeviceState() == DeviceState.DEAD) {
						powerCell.setBackgroundColor(ContextCompat.getColor(context, R.color.bkgnd_red));
					} else if (powerPcc.getCurrentDeviceState() == DeviceState.TRACKING) {
						powerCell.setBackgroundColor(ContextCompat.getColor(context, R.color.bkgnd_gray));
						if (hiViz) {
							powerCell.setBackgroundColor(ContextCompat.getColor(context, R.color.bkgnd_black));
						}
					}
				}
				powerLabel.setTextColor(textColor);
				if (debugRefreshTiming) {
					Log.w(this.getClass().getName(), "refreshPower duration: "
							+ String.format(FORMAT_4_1F, (System.nanoTime() - startTime) / 1000000.) + " msec");
				}
			}
		});// post(Runnable)
	}

	private void refreshHR() {
		if (mAntManager.isMDSStarting()) {
			if (debugMDS) Log.e(this.getClass().getName(), "couldn't do refreshHR(), MDSStarting");
			return;
		}
		hrCell.post(new Runnable() {
			@SuppressLint("SetTextI18n")
            @Override
			public void run() {
				long startTime = System.nanoTime();
				SharedPreferences sharedPref = PreferenceManager
						.getDefaultSharedPreferences(MainActivity.this);
				boolean hiViz = sharedPref.getBoolean(HI_VIZ, false);
				int textColor = ContextCompat.getColor(context, R.color.white);
				if (hiViz) {
					textColor = ContextCompat.getColor(context, R.color.texthiviz);
				}
				if ((hrPcc == null)
						|| (hrPcc.getCurrentDeviceState() != DeviceState.TRACKING)
						|| !mAntManager.hrData.isDataCurrent) {
					heartLabel.setText(DASHES);
					hrCell.setBackgroundColor(ContextCompat.getColor(context, R.color.bkgnd_gray));
					if (hiViz) {
						hrCell.setBackgroundColor(ContextCompat.getColor(context, R.color.bkgnd_black));
					}
				} else {
					heartLabel.setText(Integer.toString(myBikeStat.getHR()));
				}
				// change hrCell bkgnd color to indicate searching status
				if (hrPcc != null) {
					if (hrPcc.getCurrentDeviceState() == DeviceState.SEARCHING) {
						hrCell.setBackgroundColor(ContextCompat.getColor(context, R.color.bkgnd_green));
					} else if (hrPcc.getCurrentDeviceState() == DeviceState.DEAD) {
						hrCell.setBackgroundColor(ContextCompat.getColor(context, R.color.bkgnd_red));
					} else if (hrPcc.getCurrentDeviceState() == DeviceState.TRACKING) {
						hrCell.setBackgroundColor(ContextCompat.getColor(context, R.color.bkgnd_gray));
						if (hiViz) {
							hrCell.setBackgroundColor(ContextCompat.getColor(context, R.color.bkgnd_black));
						}
					}
				}
				heartLabel.setTextColor(textColor);
				if (debugRefreshTiming) {
					Log.w(this.getClass().getName(), "refreshHR duration: "
							+ String.format(FORMAT_4_1F, (System.nanoTime() - startTime) / 1000000.) + " msec");
				}
			}
		});// post(Runnable)
	}

	private void refreshCadence() {
		if (mAntManager.isMDSStarting()) {
			if (debugMDS) Log.e(this.getClass().getName(), "couldn't do refreshCadence(), MDSStarting");
			return;
		}
		cadCell.post(new Runnable() {
			@Override
			public void run() {
				long startTime = System.nanoTime();
				SharedPreferences sharedPref = PreferenceManager
						.getDefaultSharedPreferences(MainActivity.this);
				boolean hiViz = sharedPref.getBoolean(HI_VIZ, false);
				int textColor;
				if (hiViz) {
					cadCell.setBackgroundColor(ContextCompat.getColor(context, R.color.bkgnd_black));
					textColor = ContextCompat.getColor(context, R.color.texthiviz);
				} else {
					cadCell.setBackgroundColor(ContextCompat.getColor(context, R.color.bkgnd_gray));
					textColor = ContextCompat.getColor(context, R.color.white);
				}
				if ((myBikeStat.hasCadence && mAntManager.pedalCadenceCnts.isDataCurrent)
						|| (myBikeStat.hasPowerCadence && mAntManager.crankCadenceCnts.isDataCurrent)) {
					//Log.i(this.getClass().getName(), "refreshCad - cadence: " + myBikeStat.getCadence());
					cadenceLabel.setText(String.format(FORMAT_3D, myBikeStat.getCadence()));
				} else {
					cadenceLabel.setText(DASHES);
				}

				// change cadCell bkgnd color to indicate searching status
				if (cadPcc != null) {
					if (cadPcc.getCurrentDeviceState() == DeviceState.SEARCHING) {
						cadCell.setBackgroundColor(ContextCompat.getColor(context, R.color.bkgnd_green));
					} else if (cadPcc.getCurrentDeviceState() == DeviceState.DEAD) {
						cadCell.setBackgroundColor(ContextCompat.getColor(context, R.color.bkgnd_red));
					} else if (cadPcc.getCurrentDeviceState() == DeviceState.TRACKING) {
						cadCell.setBackgroundColor(ContextCompat.getColor(context, R.color.bkgnd_gray));
						if (hiViz) {
							cadCell.setBackgroundColor(ContextCompat.getColor(context, R.color.bkgnd_black));
						}
					}
				}// set bkgnd color
				cadenceLabel.setTextColor(textColor);
				if (debugRefreshTiming) {
					Log.w(this.getClass().getName(), "refreshCadence duration: "
							+ String.format(FORMAT_4_1F, (System.nanoTime() - startTime) / 1000000.) + " msec");
				}
			}
		});// post(Runnable)
	}

	private void refreshMergedRouteHashMap() {
		if (mAntManager.isMDSStarting()) {
			if (debugMDS) Log.e(this.getClass().getName(), "couldn't do refreshMergedRouteHashMap(), MDSStarting");
			return;
		}
		turnByturnList.post(new Runnable() {
			@Override
			public void run() {
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(turnByturnList.getContext());
				boolean unitPref = (sharedPref.getString(getResources().getString(R.string.pref_unit_key), ZERO).equals(ZERO));
				boolean hiViz = sharedPref.getBoolean(HI_VIZ, false);
				double distMultiplier = mile_per_meter;
				String unit;
				int j = turnByturnList.getFirstVisiblePosition();
				while ((j < routeHashMap.size())
						&& (j < myNavRoute.mergedRoute_HashMap.size())
						&& (j < turnByturnList.getLastVisiblePosition() + 1)) {
					HashMap<String, String> hmItem = new HashMap<>();
					float result[];
					result = distFromMyPlace2WPMR(j);
					// results returns in miles; convert to meters, if needed
					double distance = result[0] * distMultiplier;
					int turnDirIcon = myNavRoute.mergedRoute_HashMap.get(j).turnIconIndex;
					String streetName = myNavRoute.mergedRoute_HashMap.get(j).getStreetName();
					String distanceString = "??";
					if (gpsLocationCurrent) {
						distanceString = String.format(FORMAT_1F, distance);
					}
					if (unitPref) {
						unit = MILE;
					} else {
						distMultiplier = km_per_meter;
						unit = KM;
					}
					if (distance < 0.1) {// switch to display in feet / m
						int dist;
						// increment in multiples of 20', a likely resolution limit
						if (unitPref) {
							dist = (int) Math.floor(distance * 264) * 20;
							unit = FOOT;
						} else {
							dist = (int) Math.floor(distance * 100) * 10;
							unit = METER;
						}
						if (gpsLocationCurrent) {
							distanceString = String.format(FORMAT_3D, dist);
						}
					}// if dist<0.1
					// make sure the bearing is between 0 & 360 degrees
					double bearing = (result[1] + _360) % _360;
					// we only get an accurate DOT when we're moving at a speed greater
					// than "accurateGPSSpeed"
					// this will use the last accurate DOT from GPS location
					// make sure the relative bearing is between 0 & 360 degrees
					double relBearing = (bearing - myNavRoute.accurateDOT + _360) % _360;
					// convert between relative bearing and the bearing icon (arrow)
					// want north arrow for rel bearing between
					// (360 - 11.5) to (360 + 11.5) or 348 to 11.5; north arrow icon
					// is #0, nne arrow is #1, etc
					int bearingIcon = (int) Math
							.floor((((relBearing + DEG_PER_BEARING_ICON / 2) % _360) / DEG_PER_BEARING_ICON));
					// Log.d(this.getClass().getName(), "relBearing: " + String.format(FORMAT_4_1F,
					// relBearing) + "  bearingIcon: " + bearingIcon);
					int dimLevel = res_white;
					if (hiViz) {
						dimLevel = ContextCompat.getColor(context, R.color.texthiviz);
						// don't put hiViz "X" icon; other hiViz icons have iconLevel +
						// 18 for high-viz color
						if (turnDirIcon != 99) {
							turnDirIcon = myNavRoute.mergedRoute_HashMap.get(j).turnIconIndex + 18;
						}
					}
					boolean dimmed = (myNavRoute.mergedRoute_HashMap.get(j)
							.isBeenThere());
					if (dimmed) {
						dimLevel = ContextCompat.getColor(context, R.color.textdim);
						// if turn icons are dimmed the icon level is at + 9
						turnDirIcon = myNavRoute.mergedRoute_HashMap.get(j).turnIconIndex + 9;
						// dimmed bearing icon (arrow) levels are + 16
						bearingIcon += 16;
					}
					if (myNavRoute.isProximate() & (j == myNavRoute.currWP)) {
						dimLevel = ContextCompat.getColor(context, R.color.gpsgreen);
					}
					// change distance, distance unit, bearing icon level
					// don't need to change turn, street name unless firstListElem
					// changed
					hmItem.put(KEY_TURN, Integer.toString(turnDirIcon));
					hmItem.put(KEY_STREET, streetName);
					hmItem.put(KEY_DISTANCE, distanceString);
					hmItem.put(KEY_UNIT, unit);
					hmItem.put(KEY_BEARING, Integer.toString(bearingIcon));
					hmItem.put(KEY_DIM, Integer.toString(dimLevel));
					routeHashMap.set(j, hmItem);
					j++;
				}// while visible item
				// after all edits are done
				turnByturnAdapter.notifyDataSetChanged();
				// Decide which element should be at the top of the list and how to
				// scroll there if we're close to a WayPoint, but it's not at the
				// top of the list, smooth scroll
				// to top and set 1st element to the current WayPoint. This could be
				// because we've manually scrolled the list away from currWP
				if (myNavRoute.isProximate()) {
					// convert myNavRoute.currWP to a hash map index
					myNavRoute.firstListElem = myNavRoute.currWP;
				}
				// After we've moved away from the Proximate Way Point need to bump the
				// scroll list up one row
				turnByturnList.setSelectionFromTop(myNavRoute.firstListElem, 0);
			}
		});
	}

	private void refreshTitles() {
		// only refresh titles if unit preference changed
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);
		unitPref = (sharedPref.getString(
				getResources().getString(R.string.pref_unit_key), ZERO)
				.equals(ZERO));
		String tripDistString, avgSpeedString, maxSpeedString, gpsSpeedString;
		if (unitPref) {
			tripDistString = getResources().getString(R.string.trip_dist_mi);
			avgSpeedString = getResources().getString(R.string.avg_speed_mph);
			maxSpeedString = getResources().getString(R.string.max_speed_mph);
			gpsSpeedString = getResources().getString(R.string.curr_gps_speed_mph);
		} else {
			tripDistString = getResources().getString(R.string.trip_dist_km);
			avgSpeedString = getResources().getString(R.string.avg_speed_kph);
			maxSpeedString = getResources().getString(R.string.max_speed_kph);
			gpsSpeedString = getResources().getString(
					R.string.curr_gps_speed_kph);
		}
		tripDistTitle.setText(tripDistString);
		avgSpeedTitle.setText(avgSpeedString);
		maxSpeedTitle.setText(maxSpeedString);
		String calString = "";
		if (mAntManager.wheelCnts.isCalibrated
				|| mAntManager.powerWheelCnts.isCalibrated) {
			calString = " cal";
		}
		gpsSpeedTitle.setText(gpsSpeedString + calString);
		tripTimeTitle.setText(getResources().getString(R.string.trip_time));
	}

	private void refreshTimeDistance() {
		if (mAntManager.isMDSStarting()) {
			if (debugMDS) Log.e(this.getClass().getName(), "couldn't do refreshTimeDistance(), MDSStarting");
			return;
		}
		tripTimeLabel.post(new Runnable() {
			@Override
			public void run() {
				SharedPreferences sharedPref = PreferenceManager
						.getDefaultSharedPreferences(MainActivity.this);
				unitPref = (sharedPref.getString(
						getResources().getString(R.string.pref_unit_key), ZERO)
						.equals(ZERO));
				boolean hiViz = sharedPref.getBoolean(HI_VIZ, false);
				double value;
				double distMultiplier = mile_per_meter;
				int textColor = res_white;
				if (hiViz) {
					textColor = ContextCompat.getColor(context, R.color.texthiviz);
				}
				if (!unitPref) {
					distMultiplier = km_per_meter;
				}
				// Trip Time
				String timeText = myBikeStat.getTripTimeStr(myBikeStat.getGPSRideTime());
				// use calibrated speed sensor for trip time if available
				// notice that we copy trip time for uncalibrated speed sensor
				// if gps not acquired
				if (myBikeStat.hasSpeed) {
					timeText = myBikeStat.getTripTimeStr(myBikeStat.getWheelRideTime());
					// or use calibrated Power Wheel sensor
				} else if (myBikeStat.hasPowerSpeed) {
					timeText = myBikeStat.getTripTimeStr(myBikeStat.getPowerWheelRideTime());
				}
				tripTimeLabel.setText(timeText);
				tripTimeLabel.setTextColor(textColor);
				// Trip Distance
				value = myBikeStat.getGPSTripDistance() * distMultiplier;
				// use calibrated speed sensor for distance if available
				// notice that we copy trip distance for uncalibrated speed
				// sensor if gps not acquired
				// so actually this code is not necessary
				if (myBikeStat.hasSpeed) {
					value = myBikeStat.getWheelTripDistance() * distMultiplier;
					// or use calibrated Power Wheel sensor
				} else if (myBikeStat.hasPowerSpeed) {
					value = myBikeStat.getPowerWheelTripDistance() * distMultiplier;
				}
				tripDistLabel.setText(String.format(FORMAT_4_1F, value));
				tripDistLabel.setTextColor(textColor);
			}
		});
	}

	/**
	 * called from trainer mode and refreshScreen()
	 */
	private void refreshBikeStatRow() {
		if (mAntManager.isMDSStarting()) {
			if (debugMDS) Log.e(this.getClass().getName(), "couldn't do refreshBikeStatRow(), MDSStarting");
			return;
		}
		tripTimeLabel.post(new Runnable() {
			@Override
			public void run() {
				long startTime = System.nanoTime();
				SharedPreferences sharedPref = PreferenceManager
						.getDefaultSharedPreferences(MainActivity.this);
				unitPref = (sharedPref.getString(
						getResources().getString(R.string.pref_unit_key), ZERO)
						.equals(ZERO));
				boolean hiViz = sharedPref.getBoolean(HI_VIZ, false);
				double value, avgValue, maxValue;
				double distMultiplier = mile_per_meter;
				double speedMultiplier = mph_per_mps;
				int textColor = res_white;
				if (hiViz) {
					textColor = ContextCompat.getColor(context, R.color.texthiviz);
				}
				if (!unitPref) {
					speedMultiplier = kph_per_mps;
					distMultiplier = km_per_meter;
				}

				// Trip Time
				String timeText = myBikeStat.getTripTimeStr(myBikeStat.getGPSRideTime());
				// use calibrated speed sensor for trip time if available
				// notice that we copy trip time for uncalibrated speed sensor
				// if gps not acquired
				if (myBikeStat.hasSpeed) {
					timeText = myBikeStat.getTripTimeStr(myBikeStat.getWheelRideTime());
					// or use calibrated Power Wheel sensor
				} else if (myBikeStat.hasPowerSpeed) {
					timeText = myBikeStat.getTripTimeStr(myBikeStat.getPowerWheelRideTime());
				}
				tripTimeLabel.setText(timeText);
				tripTimeLabel.setTextColor(textColor);

				// Trip Distance
				value = myBikeStat.getGPSTripDistance() * distMultiplier;
				// use calibrated speed sensor for distance if available
				// notice that we copy trip distance for uncalibrated speed
				// sensor if gps not acquired
				// so actually this code is not necessary
				if (myBikeStat.hasSpeed) {
					value = myBikeStat.getWheelTripDistance() * distMultiplier;
					// or use calibrated Power Wheel sensor
				} else if (myBikeStat.hasPowerSpeed) {
					value = myBikeStat.getPowerWheelTripDistance() * distMultiplier;
				}
				tripDistLabel.setText(String.format(FORMAT_4_1F, value));
				tripDistLabel.setTextColor(textColor);

				// Average Speed
				avgValue = myBikeStat.getAvgSpeed() * speedMultiplier;
				// if gps location not current, try speed sensor
				if (!gpsLocationCurrent && myBikeStat.hasSpeedSensor
						&& (myBikeStat.getWheelRideTime() > 0)) {
					avgValue = (myBikeStat.getWheelTripDistance() / myBikeStat
							.getWheelRideTime()) * speedMultiplier;
					// or try powertap sensor
				} else if (!gpsLocationCurrent
						&& myBikeStat.hasPowerSpeedSensor
						&& (myBikeStat.getPowerWheelRideTime() > 0)) {
					avgValue = (myBikeStat.getPowerWheelTripDistance() / myBikeStat
							.getPowerWheelRideTime()) * speedMultiplier;
				}
				// average can't be greater than maximum speed but early values can be screwy
				if (avgValue > myBikeStat.getMaxSpeed() * speedMultiplier) {
					avgValue = myBikeStat.getMaxSpeed() * speedMultiplier;
				}
				avgSpeedLabel.setText(String.format(FORMAT_3_1F, avgValue));
				avgSpeedLabel.setTextColor(textColor);

				// Max Speed
				maxValue = myBikeStat.getMaxSpeed() * speedMultiplier;
				maxSpeedLabel.setText(String.format(FORMAT_3_1F, maxValue));
				maxSpeedLabel.setTextColor(textColor);
				if (debugRefreshTiming) {
					Log.w(this.getClass().getName(), "refreshBikeStatRow duration: "
							+ String.format(FORMAT_4_1F, (System.nanoTime() - startTime) / 1000000.) + " msec");
				}
			}// run
		});// runnable
		//refreshSpeed();
	}

	private void refreshSpeed() {
		if (mAntManager.isMDSStarting()) {
			if (debugMDS) Log.e(this.getClass().getName(), "couldn't do refreshSpeed(), MDSStarting");
			return;
		}
		speedCell.post(new Runnable() {
			@Override
			public void run() {
				long startTime = System.nanoTime();
				String speedString = "XX.x";
				double speedMult = mph_per_mps;
				if (!unitPref) {
					speedMult = kph_per_mps;
				}
				double spd = myBikeStat.getSpeed();
				if (spd != -1) {
					speedString = String.format(FORMAT_3_1F, spd * speedMult);
				}
/*
				Log.d(this.getClass().getName(), "hasGPS? " + (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)?"yes":"no")
						+ " current? " + (myBikeStat.gpsSpeedCurrent?"yes":"no")
						+ " gpsSpeed: " + String.format(FORMAT_3_1F,myBikeStat.getGpsSpeed() * speedMult));
				Log.d(this.getClass().getName(), "hasSpeed? " + (myBikeStat.hasSpeed?"yes":"no")
						+ " hasSpeedSensor? " + (myBikeStat.hasSpeedSensor?"yes":"no")
						+ " sensorSpeed: " + String.format(FORMAT_3_1F,myBikeStat.getSensorSpeed() * speedMult));
				Log.d(this.getClass().getName(), "hasPowerSpeed? " + (myBikeStat.hasPowerSpeed?"yes":"no")
						+ " hasPowerSensor? " + (myBikeStat.hasPowerSpeedSensor?"yes":"no")
						+ " current? " + (myBikeStat.powerSpeedCurrent?"yes":"no")
						+ " powerSpeed: " + String.format(FORMAT_3_1F,myBikeStat.getPowerSpeed() * speedMult));
				Log.d(this.getClass().getName(), "refreshSpeed()_ paused? " + (myBikeStat.isPaused() ? "true" : "false")
						+ " speedString: " + speedString);
*/
				gpsSpeedLabel.setText(speedString);
				gpsSpeedLabel.setTextColor(myBikeStat.getSpeedColor());
				if (debugRefreshTiming) {
					Log.w(this.getClass().getName(), "refreshSpeed duration: "
							+ String.format(FORMAT_4_1F, (System.nanoTime() - startTime) / 1000000.) + " msec");
				}
			}// run
		});// runnable
	}// refreshSpeed()

	/**
	 * let the screen dim as per user settings when paused
	 */
	private void setScreenDim() {
		if (myBikeStat.isPaused()) {
			findViewById(R.id.list).setKeepScreenOn(false);
		} else {
			findViewById(R.id.list).setKeepScreenOn(true);
		}
	}

	private void writeAppMessage(String message, int color) {
    boolean shouldShowAppMessageBox;
		// write a message in the App message area
		if ((!Utilities.hasGPSPermission(getApplicationContext()) && !trainerMode) || !Utilities.hasStoragePermission(getApplicationContext())) {
            shouldShowAppMessageBox = true;
			if (debugMessageBox) Log.v(this.getClass().getName(), "appMessage: noGPSPerm || noWritePerm");
		} else if (!isGPSLocationEnabled(getApplicationContext()) && !trainerMode) {
            if (debugMessageBox) Log.v(this.getClass().getName(), "appMessage: GPS provider off");
			color = res_white;
            shouldShowAppMessageBox = true;
			message = getResources().getString(R.string.req_gps_on);
		} else if (!gpsLocationCurrent && !trainerMode) {
            if (debugMessageBox) Log.v(this.getClass().getName(), "appMessage: GPS loc old");
			color = res_white;
            shouldShowAppMessageBox = true;
			if (satAcqMess) {
				satAcqMess = false;
				message = getResources().getString(R.string.acq_satellites1);
			} else {
				satAcqMess = true;
				message = getResources().getString(R.string.acq_satellites2);
			}
		} else {if (debugMessageBox){ Log.v(this.getClass().getName(), "appMessage: trainer mode");
            			Log.v(this.getClass().getName(), "appMessage: no MessageBox");}
            shouldShowAppMessageBox = trainerMode;
        }
        showAppMessBox(shouldShowAppMessageBox);
		appMessage.setText(message);
		appMessage.setTextColor(color);
	}

	private void showAppMessBox(boolean b) {
		int visibility = android.view.View.GONE;
		if (b) {
			visibility = android.view.View.VISIBLE;
		}
		appMessage.setVisibility(visibility);
	}

	private float[] distFromMyPlace2WPMR(int index) {
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);
		// distancePref = true for Route distance display; false for direct
		// distance display
		boolean distancePref = (sharedPref.getString(
				getResources().getString(R.string.pref_distance_key), ZERO)
				.equals(ZERO));
		float[] results;
		if (distancePref) {
			results = calcMRRouteDistance(index);
		} else {
			results = calcMRDirectDistance(index);
		}
		return results;
	}// distFromMyPlace2WP()

	private float[] calcMRDirectDistance(int index) {
		float[] results = {0, 0};
		if (index > myNavRoute.mergedRoute_HashMap.size())
			return results;
		there.setLatitude(myNavRoute.mergedRoute_HashMap.get(index).lat);
		there.setLongitude(myNavRoute.mergedRoute_HashMap.get(index).lon);
		results[0] = myPlace.distanceTo(there);
		results[1] = myPlace.bearingTo(there);
		return results;
	}

	private float[] calcMRRouteDistance(int index) {
		// in meters
		double distance = 0, distCurrWP;
		float[] results = {0, 0};
		if (index > myNavRoute.mergedRoute_HashMap.size())
			return results;
		if (index < myNavRoute.currWP) {
			// Don't calculate distances where we've already been
			// Could create a formula, but why?
		} else {
			// calculate distance from myPlace to currWP
			there.setLatitude(myNavRoute.mergedRoute_HashMap
					.get(myNavRoute.currWP).lat);
			there.setLongitude(myNavRoute.mergedRoute_HashMap
					.get(myNavRoute.currWP).lon);
			distCurrWP = myPlace.distanceTo(there);
			if (index == myNavRoute.currWP) {
				distance = distCurrWP;
			} else {
				// distance to any entry in the list is difference in RouteMiles
				// between that entry and the RouteMiles at the currWP, plus the
				// distance from
				// where we are to the currWP (calculated before)
				distance += (myNavRoute.mergedRoute_HashMap.get(index)
						.getRouteMiles() - myNavRoute.mergedRoute_HashMap.get(
						myNavRoute.currWP).getRouteMiles());
				// If currWP.beenThere is false, add distCurrWP to running total
				// because we're before the currWP
				// If currWP.beenThere is true subtract distCurrWP from running
				// distance total for other WPs because we're after the currWP,
				// just not farEnough away to increment the currWP
				if (myNavRoute.mergedRoute_HashMap.get(myNavRoute.currWP).isBeenThere()) {
					distance -= distCurrWP;
				} else {
					distance += distCurrWP;
				}
			}
			results[0] = (float) distance;
			there.setLatitude(myNavRoute.mergedRoute_HashMap.get(index).lat);
			there.setLongitude(myNavRoute.mergedRoute_HashMap.get(index).lon);
			results[1] = myPlace.bearingTo(there);
		}
		return results;
	}

	/**
	 * Set up a timer to call spoofLocations every second, simulating a GPS
	 * provider The bike speed sensor will have put distance information into
	 * myBikeStat.spoofWheelTripDistance() and
	 * myBikeStat.prevSpoofWheelTripDistance()
	 * <p/>
	 * For simulation purposes, each time the Timer fires, put a distance and
	 * speed into myBikeStat, as subscribeCalibratedSpeed() would. Delete
	 * simulation mode for production version
	 */

	TimerTask spoofLocationsTimerTask;
	final Handler spoofLocationsHandler = new Handler();
	Timer spoofLocationsTimer = new Timer();
	// shorter interval times don't improve the Strava variation in apparent
	// speed around the track.
	final long spoofInterval = 1000;

	/**
	 * a watchdog timer to spoof locations in trainer mode
	 */
	private void startSpoofingLocations() {
		spoofLocationsTimerTask = new TimerTask() {
			@Override
			public void run() {
				spoofLocationsHandler.post(new Runnable() {
					@Override
					public void run() {
						if (trainerMode) {
							spoofLocations();
							MainActivity.this.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									if (!scrolling && (pm != null)
											&& pm.isScreenOn()) {
										refreshBikeStatRow();
									}
									if (myBikeStat.hasSpeedSensor
											|| myBikeStat.hasPowerSpeedSensor) {
										writeAppMessage(
												getString(R.string.trainer_mode_recording_track),
												ContextCompat.getColor(context, R.color.gpsgreen));
									} else {
										writeAppMessage(
												getString(R.string.trainer_mode_no_speed_sensor),
												ContextCompat.getColor(context, R.color.gpsred));
									}
								}// run
							});// post(Runnable)
						}// trainerMode
					}// run
				});// spoofLocationsHandler Runnable
			}
		};// TimerTask()
		spoofLocationsTimer.schedule(spoofLocationsTimerTask, 300, spoofInterval);
	}

	private void stopSpoofingLocations() {
		if (debugAppState) {
			Log.i(this.getClass().getName(), "stopSpoofingLocations()");
		}
		spoofLocationsHandler.removeCallbacksAndMessages(null);
		if (spoofLocationsTimerTask != null) {
			spoofLocationsTimerTask.cancel();
		}
	}

	/**
	 * Use bike speed-distance sensor to create an elliptical track of timed
	 * locations; called by the spoofLocationsTimer every second
	 * spoofer.spoofLocations() will write coordinates in BikeStat
	 * writeTrackRecord() will pick these up for the tcx file
	 */
	private void spoofLocations() {
		if (debugAppState) {
			Log.i(this.getClass().getName(), "spoofLocations()");
		}
		// don't display GPS speed
		gpsLocationCurrent = false;
		newLocSysTimeStamp = SystemClock.elapsedRealtime();
		SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, 0);
		// this is the last choice in the velodrome list in Settings prefs
		int veloChoice = sharedPref.getInt(KEY_VELO_CHOICE, 0);
		String[] spoofResult = spoofer.spoofLocations(firstSpoofLocation, myBikeStat, veloChoice);
		forceNewTCX_FIT = sharedPref.getBoolean(KEY_FORCE_NEW_TCX, false);
		if (forceNewTCX_FIT) {
			firstSpoofLocation = true;
			resetData();
		}
		if (firstSpoofLocation) {
			createTitle(getString(R.string._spoofing_locations_) + spoofResult[0]);
			trainerModeComment.setText(spoofResult[0] + "\n" + spoofResult[1]);
			actionbar_title_text.setText(getString(R.string._spoofing_locations_, spoofResult[0]));
			firstSpoofLocation = false;
			myBikeStat.setPaused(true);
			openReopenTCX_FIT();
			forceNewTCX_FIT = false;
			sharedPref.edit().putBoolean(KEY_FORCE_NEW_TCX, false).apply();
			// now un-pause
			myBikeStat.setPaused(false);
		}
		writeTrackRecord();
		//testZeroPaused();
	}

	class LocationHelper implements GoogleApiClient.OnConnectionFailedListener,
									GoogleApiClient.ConnectionCallbacks,
									com.google.android.gms.location.LocationListener {

        boolean mResolvingError;
        ConnectionResult connectionFailureResult;
        private Status locationSettingsResultStatus;
        int priority = LocationRequest.PRIORITY_HIGH_ACCURACY;
        //private Location myLocation;
        final GoogleApiClient mGoogleApiClient;

        LocationHelper(Context context) {
            //test if GooglePlay Services is available and up to date
            googlePlayAvailable(context);
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mResolvingError = false;
        }

        /**
         * Use Google Location API to get a user location
         *
         * @param mLocationRequest specifies update interval and accuracy
         */
        void startLocationUpdates(LocationRequest mLocationRequest) {
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } catch (SecurityException ignore) {
            }
        }

        /**
         * Disable locationListener when Display Activity is destroyed
         */
        void stopLocationUpdates() {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }
        }

        LocationRequest createLocationRequest() {
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(ONE_SEC);
            mLocationRequest.setSmallestDisplacement(0f);
            mLocationRequest.setFastestInterval(ONE_SEC);
            int priority = LocationRequest.PRIORITY_HIGH_ACCURACY;
            mLocationRequest.setPriority(priority);
            return mLocationRequest;
        }

        @Override
        public void onConnected(Bundle bundle) {
            if (MainActivity.debugLocation) {
                Log.w(this.getClass().getName(), "Connected to Google API Client");
            }
            try {
                myPlace = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            } catch (SecurityException ignore) {

            }
            if (myPlace != null) {
                saveLocSharedPrefs(myPlace);
            } else {
                myPlace = getLocFromSharedPrefs();
            }
            final LocationRequest mLocationRequest = createLocationRequest();
            final LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);
            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
            startLocationUpdates(mLocationRequest);
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                    final Status status = locationSettingsResult.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can initialize location requests here.
                            try {
                                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                                        mLocationRequest, LocationHelper.this);
                            } catch (SecurityException ignore) {
                            }
                            connectionFailureResult = null;
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            if (MainActivity.debugLocation) {
                                Log.w(this.getClass().getName(), "location settings result callback() - RESOLUTION_REQUIRED");
                            }
                            // Location settings are not satisfied, but this can be fixed
                            // by showing the user a dialog.
                            Snackbar mLocationSettingsSnackBar = Snackbar.make(
                                    antToastAnchor,
                                    getString(R.string.open_location_settings),
                                    Snackbar.LENGTH_INDEFINITE);
                            mLocationSettingsSnackBar.setAction(R.string.allow, new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        mLocationHelper.locationSettingsResultStatus.startResolutionForResult(
                                                MainActivity.this, REQUEST_CHECK_SETTINGS);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                        if (debugLocation) { e.printStackTrace(); }
                                    }
                                }
                            }).show();
                            locationSettingsResultStatus = status;
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way
                            // to fix the settings so we won't show the dialog.
                            locationSettingsResultStatus = status;
                            if (MainActivity.debugLocation) {
                                Log.w(this.getClass().getName(), "SETTINGS_CHANGE_UNAVAILABLE");
                            }
                            break;
                    }
                }
            });
        }

        private void saveLocSharedPrefs(Location location) {
            SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(SAVED_LAT, Double.toString(location.getLatitude()));
            editor.putString(SAVED_LON, Double.toString(location.getLongitude()));
            editor.putLong(PREF_SAVED_LOC_TIME, location.getTime()).apply();
        }

        @Override
        public void onConnectionSuspended(int i) {
            if (MainActivity.debugLocation) {
                Log.w(this.getClass().getName(), "ConnectionSuspended");
            }
        }

        @Override
        public void onLocationChanged(Location location) {

            if (MainActivity.debugLocation) {
                Log.w(this.getClass().getName(), "onLocationChanged(): long - " +
                        String.format("%7.4f", location.getLongitude())
                        + " lat - " + String.format("%7.4f", location.getLatitude())
                        + " alt - " + String.format("%7.1f", location.getAltitude())
                        + " accuracy - " + String.format("%7.1f",  (double) location.getAccuracy()));
            }
            // if location time-stamp is weird, skip the data
            // also demand "goodEnoughLocationAccuracy" before using the data
            long startTime = System.nanoTime();
            if (trainerMode || mAntManager.isMDSStarting()
                    || location.getTime() == 0
                    || location.getAccuracy() > goodEnoughLocationAccuracy
                    || Math.abs(System.currentTimeMillis() - location.getTime()) >  TWENTYFOUR_HOURS) {
                return;
            }
            newLocSysTimeStamp = SystemClock.elapsedRealtime();
            myPlace = location;
            myNavRoute.setPrevDOT(myNavRoute.getDOT());
            myNavRoute.setDOT(location.getBearing());
            // DOT bearing from gps locations is not too accurate if traveling slowly
            // To display relative bearing to waypoints in refreshHashMap
            // use the last accurate DOT
            if (location.getSpeed() > accurateGPSSpeed) {
                myNavRoute.accurateDOT = location.getBearing();
            }
            myNavRoute.setDeltaDOT(Math.abs(myNavRoute.getDOT() - myNavRoute.getPrevDOT()));
            // Unless we have a speed sensor, this doesn't account for time ticking while loc not current.
            // Situation where GPS is switched off, or drops-out, or rider went inside for lunch.
            // When new location is received, distance may only be 10', but elapsed time may be an hour.
            // Average speed now includes this elapsed time. When new location received,
            // use the new deltaDistance, but calculate delta time based on current average speed.
            // This only affects the display, data written to file is not changed.
            gpsLocationCurrent = true;
            myBikeStat.gpsSpeedCurrent = true;
            myBikeStat.setLastGoodWP(location, gpsFirstLocation, true);
            myBikeStat.setSpeed(trainerMode);
            // this also calculates trip distance, ride time, etc
            myNavRoute.refreshRouteWayPoints(location, myBikeStat.getGPSTripDistance());
            // If speed and power-speed sensors are not calibrated (!myBikeStat.hasSpeed), copy GPS
            // TripTime and TripDistance
            if (!myBikeStat.hasSpeed) {
                myBikeStat.setWheelTripDistance(myBikeStat.getGPSTripDistance());
                myBikeStat.setWheelRideTime(myBikeStat.getGPSRideTime());
            }
            if (!myBikeStat.hasPowerSpeed) {
                myBikeStat.setPowerWheelTripDistance(myBikeStat.getGPSTripDistance());
                myBikeStat.setPowerWheelRideTime(myBikeStat.getGPSRideTime());
            }
            // firstLocation is used to start the track record; it is true after a reset()
            if (gpsFirstLocation) {
                writeAppMessage("", res_white);
                gpsFirstLocation = false;
                // pause briefly to write the first track record
                myBikeStat.setPaused(true);
                // Re-open or create a new tcx file if it's old. Also we can force a new tcx file with the boolean
                openReopenTCX_FIT();
                // now un-pause
                myBikeStat.setPaused(false);
            }// first location
            // write to tcx file
            writeTrackRecord();
            // If location accuracy is poor and have an uncalibrated speed sensor or an
            // uncalibrated power-speed sensor, restart the calibration process
            if ((location.getAccuracy() > goodLocationAccuracy)
                    && ((myBikeStat.hasSpeedSensor && !mAntManager.wheelCnts.isCalibrated)
                    || (myBikeStat.hasPowerSpeedSensor && !mAntManager.powerWheelCnts.isCalibrated))) {
                mAntManager.restartWheelCal(myBikeStat.getWheelTripDistance());
                mAntManager.restartPowerWheelCal(myBikeStat.getWheelTripDistance());
                if (debugWheelCal || debugPowerWheelCal) {
                    Log.i(this.getClass().getName(), "poor location accuracy (m): "
                            + String.format(FORMAT_3_1F, location.getAccuracy()));
                }
            }
            if (!scrolling && (pm != null) && pm.isScreenOn()) {
                refreshScreen();
                if (!myBikeStat.hasPowerSpeedSensor && !myBikeStat.hasSpeedSensor) {
                    myBikeStat.setSpeed(trainerMode);
                    refreshSpeed();
                }
            }
            if (debugRefreshTiming) {
                Log.w(this.getClass().getName(), "LocationListener duration: "
                        + String.format(FORMAT_4_1F, (System.nanoTime() - startTime) / 1000000.) + " msec");
            }
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            if (debugLocation) {
                Log.w(this.getClass().getName(), "onConnectionFailed: (message) " + connectionResult.getErrorMessage());
                Log.w(this.getClass().getName(), "onConnectionFailed: (error) " + connectionResult.getErrorCode());
            }
            if (mResolvingError) {
                // Already attempting to resolve an error.
                return;
            } else if (connectionResult.hasResolution()) {
                try {
                    mResolvingError = true;
                    connectionResult.startResolutionForResult(MainActivity.this, REQUEST_RESOLVE_ERROR);
                } catch (IntentSender.SendIntentException e) {
                    // There was an error with the resolution intent. Try again.
                    mGoogleApiClient.connect();
                }
            } else {
                // Show dialog using GoogleApiAvailability.getErrorDialog()
                showErrorDialog(connectionResult.getErrorCode());
                mResolvingError = true;
            }

        }

        private boolean googlePlayAvailable(Context context) {
            int googlePlayAvailableResponse = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
            if (googlePlayAvailableResponse == ConnectionResult.SUCCESS) {
                return true;
            } else {
                GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, googlePlayAvailableResponse, 0).show();
            }
            return false;
        }
        /* Creates a dialog for an error message */
        private void showErrorDialog(int errorCode) {
            // Create a fragment for the error dialog
            ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
            // Pass the error that should be displayed
            Bundle args = new Bundle();
            args.putInt(DIALOG_ERROR, errorCode);
            dialogFragment.setArguments(args);
            dialogFragment.show(getSupportFragmentManager(), "errordialog");
        }

    }
    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() { }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GoogleApiAvailability.getInstance().getErrorDialog(
                    this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            if (debugLocation){Log.i(this.getClass().getName(), "onDismiss()");}
            ((MainActivity) getActivity()).onDialogDismissed();
        }
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    private void onDialogDismissed() {
        mLocationHelper.mResolvingError = false;
        mLocationHelper.connectionFailureResult = null;
    }
	/**
	 * Test for paused condition so we won't write log entries, allow screen to
	 * dim, increment ride time, etc
	 * This is called from spoofLocations in TrainerMode, LocationListener when we get a new location
	 * and from Sensor Watchdog every three seconds
	 */
	private void testZeroPaused() {

		boolean paused = false;
		// paused if we don't have speed sensor, or calibrated PowerTap and location not current
		// outside of trainer mode. PowerTap speed not reliable if its not calibrated
		boolean condition0 = !trainerMode && !myBikeStat.hasPowerSpeedSensor
				&& !myBikeStat.hasSpeedSensor && !gpsLocationCurrent;
		// before location current, outside of trainer mode
		// an uncalibrated speed sensor currently has a low speed reading
		boolean condition0a = !trainerMode && !gpsLocationCurrent
				&& myBikeStat.hasSpeedSensor
				&& mAntManager.wheelCnts.isDataCurrent
				&& (myBikeStat.getSensorSpeed() < .1);
		// before location current, outside of trainer mode, without a speed sensor
		// an uncalibrated Power Tap currently has a low speed reading
		boolean condition0b = !trainerMode && !gpsLocationCurrent && !myBikeStat.hasSpeedSensor
				&& mAntManager.powerWheelCnts.isDataCurrent && (myBikeStat.getPowerSpeed() < .1);
		// paused if the current data from calibrated speed sensor is low;
		// speed sensor battery sometimes runs low and gives faulty readings Sensor watchdog tests for this
		boolean condition1 = myBikeStat.hasSpeed
				&& mAntManager.wheelCnts.isDataCurrent
				&& (myBikeStat.getSensorSpeed() < .1);
		// paused if calibrated PowerTap speed is low
		// (when PowerTap battery gets low, it sends out 0.0 for speed.
		// Sensor watchdog detects this and un-calibrates the PowerTap)
		boolean condition3 = myBikeStat.hasPowerSpeed
				&& mAntManager.powerWheelCnts.isDataCurrent
				&& (myBikeStat.getPowerSpeed() < .1);
		// paused if don't have a calibrated speed sensor, or a calibrated power speed sensor,
		// we have a current GPS location and GPS speed and direction-of-travel are low
		// if gps signal drops-out we can get faulty pause; with a calibrated sensor we can correct this
		boolean condition2 = !trainerMode && gpsLocationCurrent
				&& !myBikeStat.hasSpeed
				&& !myBikeStat.hasPowerSpeed
				&& (myBikeStat.getGpsSpeed() < speedDOTPausedVal)
				&& (myNavRoute.getDeltaDOT() < speedDOTPausedVal);

		// paused if in TrainerMode and no speed sensors available
		boolean condition4 = trainerMode
				&& (!myBikeStat.hasSpeedSensor
				&& !myBikeStat.hasPowerSpeedSensor);

		// paused if in TrainerMode and PowerTap or speedSensor speed is low
		boolean condition5 = trainerMode
				&& ((myBikeStat.hasSpeedSensor && (myBikeStat.getSensorSpeed() < .1))
				|| (myBikeStat.hasPowerSpeedSensor && (myBikeStat.getPowerSpeed() < .1)));

		if ((condition0 || condition0a || condition0b || condition1 || condition2 || condition3
				|| condition4 || condition5)) {
			paused = true;
			// update "previous-time" so ride-time clock stops when paused
			mAntManager.wheelCnts.prevTime = SystemClock.elapsedRealtime();
			mAntManager.powerWheelCnts.prevTime = SystemClock.elapsedRealtime();
		}
		myBikeStat.setPaused(paused);
		myBikeStat.setSpeed(trainerMode);
		if (paused){
			long deltaTime = System.currentTimeMillis() - mAntManager.getForceMDSStartTime();
			if (debugMDS) { Log.i(this.getClass().getName(), "forceMDS delta: " + String.format(FORMAT_3_1F, deltaTime/1000.) + " sec"
					+ " num MDS cycles: " + mAntManager.getNumMDSSearchCycles());}
			// we've paused after not pausing for three minutes; increase the # MDS search cycles
			// we'll stop MDS searching after NUM_MDSCYCLES
			if ((deltaTime > THREE_MINUTES)){
				mAntManager.setNumMDSSearchCycles(mAntManager.getNumMDSSearchCycles()+1);
			}
			// start the MDS when paused
			mAntManager.setForceMDSStartTime(System.currentTimeMillis());
		}

/*
		Log.w(this.getClass().getName(), "condition0? " + (condition0 ? "true" : "false")
				+ " / condition0a? " + (condition0a ? "true" : "false")
				+ " / condition0b? " + (condition0b ? "true" : "false")
				+ " / condition1? " + (condition1 ? "true" : "false")
				+ " / condition2? " + (condition2 ? "true" : "false")
				+ " / condition3? " + (condition3 ? "true" : "false")
				+ " / condition4? " + (condition4 ? "true" : "false")
				+ " / condition5? " + (condition5 ? "true" : "false"));
		Log.w(this.getClass().getName(), "hasGPS? " + (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)? "yes" : "no")
				+ " current? " + (myBikeStat.gpsSpeedCurrent ? "yes" : "no")
				+ " gpsSpeed: " + String.format(FORMAT_3_1F, myBikeStat.getGpsSpeed() * mph_per_mps));
		Log.w(this.getClass().getName(), "hasSpeed? " + (myBikeStat.hasSpeed ? "yes" : "no")
				+ " hasSpeedSensor? " + (myBikeStat.hasSpeedSensor ? "yes" : "no")
				+ " sensorSpeed: " + String.format(FORMAT_3_1F, myBikeStat.getSensorSpeed() * mph_per_mps));
		Log.w(this.getClass().getName(), "hasPowerSpeed? " + (myBikeStat.hasPowerSpeed ? "yes" : "no")
				+ " hasPowerSensor? " + (myBikeStat.hasPowerSpeedSensor ? "yes" : "no")
				+ " current? " + (myBikeStat.powerSpeedCurrent ? "yes" : "no")
				+ " powerSpeed: " + String.format(FORMAT_3_1F, myBikeStat.getPowerSpeed() * mph_per_mps));
*/

		if (debugMDS) Log.w(this.getClass().getName(), "paused? " + (myBikeStat.isPaused() ? "true" : "false"));

	}

	private void writeTrackRecord() {
		if (!Utilities.hasStoragePermission(getApplicationContext())) {
			// Should we show an explanation? Check box "don't show again" override.
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
					writeAppMessage(getString(R.string.write_permission_denied),
							ContextCompat.getColor(context, R.color.gpsred));
					return;
				} else {
					// No explanation needed, we can request the permission.
					ActivityCompat.requestPermissions(MainActivity.this,
							new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
							MY_PERMISSIONS_REQUEST_WRITE);
				}
			}
			return;
		}
		//If we're rebuilding or closing the fit file from re-opening tcx, or sharing a file,
		//don't write a new tcx record, just return
		if (myBikeStat.fitLog.isFileEncoderBusy()) {
			return;
		}
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
		boolean autoPause = sharedPref.getBoolean("auto_pause", false);
		if (myBikeStat.isPaused() && autoPause) {
			// close the current track on pause()
			myNavRoute.trackClosed = true;
		} else {
			writingTrackRecord = true;
			locationWatchdogHandler.post(new Runnable() {
				@Override
				public void run() {
					long startTime = System.nanoTime();
					myBikeStat.tcxLog.writeTCXRecord(myBikeStat, myNavRoute);
					myBikeStat.fitLog.writeRecordMesg(myBikeStat);
					//Log.w(this.getClass().getName(), "writeTrackRecord() takes " + String.format(FORMAT_4_1F, (System.nanoTime() - startTime) / 1000000.) + " ms");
				}
			});
			// if file not open, try to re-open; getError() will return SD card error if !fileHasPermission
			if ((myBikeStat.tcxLog.getError().equals(""))) {
				// no error, successfully wrote record
				myNavRoute.trackClosed = false;
			} else {
				forceNewTCX_FIT = true;
				openReopenTCX_FIT();
			}
		}
		writingTrackRecord = false;
	}
	/**
	 * Read the ANT device database and add everything to the antDBDeviceList
	 * where we'll look when MDS finds a device
	 */
	public class loadDBDeviceListBackground extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			mAntManager.antDBDeviceList.clear();
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (dataBaseAdapter != null && !dataBaseAdapter.isClosed()) {
				mAntManager.antDBDeviceList.addAll(dataBaseAdapter.getAllDeviceData());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

		}
	}
	/**
	 * Since route files could be changed by user and the name could be the
	 * same, we can't keep route files in private storage for very long. We'll
	 * delete all files in private storage when autoResumeRoute() decides that
	 * .tcx file is old. Loading routes from private storage was only intended
	 * to avoid the long LoadData() process when navigating the app
	 */
	private void deleteAllTmpRouteFiles() {
		// Delete all cached route files if .tcx file is old
		String[] routeFiles = fileList();
		for (String file : routeFiles) {
			deleteFile(file);
		}
	}// deleteAllRouteFiles()

	/**
	 * Once the route file has been loaded and prepared, save mergedRoute in
	 * private storage. This could be a long task for a large route file, so put
	 * in background task
	 */
	public class SaveRouteFileBackground extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			setProgressBarIndeterminateVisibility(Boolean.TRUE);
		}

		@Override
		protected Void doInBackground(String... filename) {
			saveRouteFile(filename[0]);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			setProgressBarIndeterminateVisibility(Boolean.FALSE);
		}
	}

	/**
	 * In revertChosen file, do the restore operation in background for faster
	 * response on UI. In LoadData, want to wait for restoreFile() to finish
	 * before moving on to .loadNavRoute This could be a long task for a large
	 * route file, so put in background task
	 */
	public class RestoreRouteFileBackground extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			setProgressBarIndeterminateVisibility(Boolean.TRUE);
		}

		@Override
		protected Void doInBackground(String... filename) {
			restoreRouteFile(filename[0]);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			setProgressBarIndeterminateVisibility(Boolean.FALSE);
		}
	}

	/**
	 * Bypass the SAX parser when restoring a route or changing TrackPoint
	 * density and load the route ArrayList from private file storage. Returns
	 * an error if the route is not in private storage; then we'll have to use
	 * the SAX parser
	 *
	 * @param fileName the file to restore
	 **/
	@SuppressWarnings("unchecked")
	private String restoreRouteFile(String fileName) {
		if (debugOldTCXFile) {
			Log.d(this.getClass().getName(), RESTORE_ROUTE_FILE_GPXFILENAME + fileName);
		}
		FileInputStream fis;
		String error = "";
		// add prefix denoting track point density and removing path characters
		fileName = adjustFileName(fileName);
		try {
			fis = openFileInput(fileName);
			ObjectInputStream ois = new ObjectInputStream(fis);
			myNavRoute.mergedRoute = (ArrayList<GPXRoutePoint>) ois.readObject();
			ois.close();
			fis.close();
		} catch (FileNotFoundException e) {
			error = FILE_NOT_FOUND;
		} catch (Exception e) {
			error = EXCEPTION;
			e.printStackTrace();
		}
		return error;
	}

	/**
	 * save the route ArrayList to private storage so we can bypass the SAX
	 * parser when restoring route or changing TrackPoint density. Saves some
	 * time when using a big Trackpoint file
	 *
	 * @param fileName the route to save
	 **/
	private void saveRouteFile(String fileName) {
		if (debugOldTCXFile) {
			Log.d(this.getClass().getName(), "saveRouteFile()");
		}
		// add prefix denoting track point density and removing path characters
		fileName = adjustFileName(fileName);
		// see if the file already exists
		String[] routeFiles = fileList();
		boolean fileAlreadyExists = false;
		for (String file : routeFiles) {
			if (file.equals(fileName)) {
				fileAlreadyExists = true;
				break;
			}
		}
		// if the file doesn't exist, write it; otherwise don't write it
		if (!fileAlreadyExists) {
			try {
				FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(myNavRoute.mergedRoute);
				oos.close();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String adjustFileName(String fileName) {

		// delete path prefix; private storage names can't have path symbols
		// filename returned from ShowFileList has path characters in it
		if (fileName != null) {
			int start = fileName.lastIndexOf("/") + 1;
			int end = fileName.length();
			if ((end - start) <= 0) {
				fileName = "";
			} else {
				fileName = fileName.substring(start, end);
			}
		}
		// add prefix denoting track point density
		SharedPreferences defaultSettings = PreferenceManager.getDefaultSharedPreferences(context);
		String defTrackDensity = defaultSettings.getString(
				getResources().getString(R.string.pref_trackpoint_density_key), "0");
		return TP_DENSITY + defTrackDensity + fileName + TMP_CB_ROUTE;
	}

	/**
	 * Whenever we close the fit file, the FileEncoder writes
	 * the fit file in a background task. When finished, set a flag that allows
	 * us to accept new data.
	 */
	public class CloseFitFileBackground extends AsyncTask<String, String, String> {
		ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			if (!MainActivity.this.isFinishing()) {
				progressDialog = ProgressDialog.show(MainActivity.this,
						"Processing...", "Closing files...", false);
			}
			myBikeStat.fitLog.setFileEncoderBusy(true);
		}

		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			progressDialog.setMessage(values[0]);
		}

		@Override
		protected String doInBackground(String... params) {
			myBikeStat.tcxLog.closeTCXLogFile();
			myBikeStat.fitLog.closeFitFile();
			return params[0];
		}

		@Override
		protected void onPostExecute(String sharingFileName) {
			super.onPostExecute(sharingFileName);
			myBikeStat.fitLog.setFileEncoderBusy(false);
			if (!("").equals(sharingFileName)) {
				progressDialog.dismiss();
				uploadFileSend(sharingFileName);
			}
		}
	}

	public class OpenNewFitFileBackground extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(Void... params) {

			myBikeStat.fitLog.setFileEncoderBusy(true);
			String error = myBikeStat.fitLog.openNewFIT(myBikeStat);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// set a flag that says we've opened the fit file and we're
			// ready to write new data
			myBikeStat.fitLog.setFileEncoderBusy(false);
		}
	}

	/**
	 * Whenever we re-open the fit file, have to parse the tcx file and re-write
	 * the fit file in a background task. When finished, set a flag that allows
	 * us to accept new data.
	 */
	public class ReopenFitFileBackground extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(Void... params) {
			// encode the .fit file once we've re-opened a tcx file
			// (chosenTCXFile will always have a .tcx suffix since we only show
			// tcx file types in ShowFileList chooser
			myBikeStat.fitLog.setFileEncoderBusy(true);
			String error = myBikeStat.fitLog.reOpenFitFile(myBikeStat.tcxLog.outFileName);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// set a flag that says we've re-encoded the fit file and we're
			// ready to write new data
			myBikeStat.fitLog.setFileEncoderBusy(false);
		}
	}

	/**
	 * Change trackdensity in hash map from a prepared mergedRoute. This could
	 * be a long task for a large route file, so put in background task
	 */
	public class ChangeTrackDensityBackground extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			setProgressBarIndeterminateVisibility(Boolean.TRUE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// save RouteMiles @ firstListElem so we can recalculate
			// firstListElem with new track density
			myNavRoute.routeMilesatFirstListElem = myNavRoute.mergedRoute_HashMap
					.get(myNavRoute.firstListElem).getRouteMiles();
			// save RouteMiles @ currWP so we can recalculate currWP with new
			// track density
			myNavRoute.changeTrkPtDensity(myNavRoute.defaultTrackDensity);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// all UI altering tasks have to go in the post-execute method
			initializeMergedRouteTurnList();// set-up the HashMap for street turns
			// recalculate firstListElem using previous RouteMiles at top of list
			myNavRoute.recalcFirstListElem();
			// recalculate currWP using TripDistance minus bonus miles
			myNavRoute.recalcCurrWP(myBikeStat.getGPSTripDistance()
					- myNavRoute.getBonusMiles());
			// save the new firstListElem and currWP in shared Prefs
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt(CURR_WP, myNavRoute.currWP);
			editor.putInt(FIRST_LIST_ELEM, myNavRoute.firstListElem);
			editor.apply();
            setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
			// save the route here in a background task
			new SaveRouteFileBackground().execute(chosenGPXFile);

			refreshScreen();
		}
	}

	/**
	 * Use an asynchronous, background thread to load the file with a progress
	 * bar in case it's a big file
	 * todo Use ThreadPoolExecuter instead of AsyncTask on UI thread
	 */
	public class LoadData extends AsyncTask<Context, String, Void> {
		ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			myNavRoute.setError("");
			// display the progress dialog
			progressDialog = ProgressDialog.show(MainActivity.this,
					LOADING_FILE, LOOKING_FOR_ROUTE_DATA, false);
			progressDialog.setCancelable(true);
			progressDialog.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					progressDialog.dismiss();
					myNavRoute.setError(USER_CANCELED);
				}
			});
		}

		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			progressDialog.setMessage(values[0]);
		}

		@Override
		protected Void doInBackground(Context... params) {
			// See if we have this file cached; if so, load from cache
			if (!("").equals(restoreRouteFile(chosenGPXFile))) {
				// couldn't find the file in cache, so use the SAX parser via .loadNavRoute
				if (!chosenGPXFile.equals("")) {
					myNavRoute.loadNavRoute();
					if ((myNavRoute.handler.handlersGPXRoute.size() == 0)
							&& (myNavRoute.handler.handlersTrackPtRoute.size() == 0)) {
						if (myNavRoute.getError().equals("")) {
							// don't obscure another error
							myNavRoute.setError(NO_ROUTE_DATA_IN_FILE);
						}
					}
				}
				// if there was no SAX error, lat/long error, etc and there is
				// route data, initialize the route
				if (myNavRoute.getError().equals("") && (!chosenGPXFile.equals(""))) {
					progressDialog.setCancelable(false);
					publishProgress(INITIALIZING_ROUTE);
					myNavRoute.prepareRoute();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// all UI altering tasks have to go in the post-execute method
			progressDialog.dismiss();
			if (!chosenGPXFile.equals("")) {
				if (!myNavRoute.getError().equals("")) {
					revertChosenFile();
					// either "no route data...", or invalid lat/lon data, or user-canceled
					Toast.makeText(getApplicationContext(),
							myNavRoute.getError(), Toast.LENGTH_LONG).show();
					myNavRoute.setError("");
				} else {
					dealWithGoodData();
				}
			} else {// there was no filename specified
				myNavRoute.mergedRoute.clear();
				initHashMap();
			}
		}

		/**
		 * the file selected in the Chooser isn't valid, return the last good
		 * file to Shared Preferences and NavRoute.chosenFile
		 */
		private void revertChosenFile() {
			myNavRoute.mChosenFile = new File(prevChosenFile);
			chosenGPXFile = prevChosenFile;
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(KEY_CHOSEN_GPXFILE, prevChosenFile);
			editor.apply();
			new RestoreRouteFileBackground().execute(chosenGPXFile);
			createTitle(prevChosenFile);
		}

		private void dealWithGoodData() {
			if (debugOldTCXFile) {
				Log.d(this.getClass().getName(), "dealWithGoodData() - gpxfilename: " + chosenGPXFile);
			}
			myNavRoute.firstListElem = 0;
			myNavRoute.currWP = 0;
			myNavRoute.refreshRouteWayPoints(myPlace, myBikeStat.getGPSTripDistance());
			myNavRoute.setProximate(false);
			createTitle(chosenGPXFile);
			initializeMergedRouteTurnList();// set-up the HashMap for street turns
			forceNewTCX_FIT = false;
			if (!resumingRoute) { // loading a new route file
				// if tcx file isn't old, ask to
				// open a new tcx file for the new route and zero-out data
				boolean old = myBikeStat.tcxLog.readTCXFileLastModTime(
						myBikeStat.tcxLog.outFileName, getTCXFileAutoReset());
				if (!old) {
					doAskResetPermission();
				}
			} else {// we are resuming route via menu item or autoResuming where tcx file is not old
				// in which case we should open the current tcx file
				// LoadData sets firstListElem = 0; must restore this value
				SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
				myNavRoute.currWP = settings.getInt(CURR_WP, WILDCARD);
				myNavRoute.firstListElem = settings.getInt(FIRST_LIST_ELEM, WILDCARD);
				turnByturnList.setSelectionFromTop(myNavRoute.firstListElem, 0);
			}
			// save the route here in a background task
			new SaveRouteFileBackground().execute(chosenGPXFile);
		}// Deal with good data
	}// LoadData class

	private void doAskResetPermission() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String buttonTextID[] = {getString(R.string.ok),
				getString(R.string.no)};
		builder.setPositiveButton(buttonTextID[0],
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// user pressed the okay button
						resetData();
						gpsFirstLocation = true;
						forceNewTCX_FIT = true;
						if (debugOldTCXFile) {
							Log.d(this.getClass().getName(),
									"doAskResetPermission() Okay button");
							Log.d(this.getClass().getName(), "tcx footerLength: "
									+ myBikeStat.tcxLog.outFileFooterLength);
							Log.d(this.getClass().getName(), "tcx filename: "
									+ myBikeStat.tcxLog.outFileName);
							Log.d(this.getClass().getName(), "forceNewTCX: "
									+ (forceNewTCX_FIT ? "true" : "false"));
						}
					}
				});
		builder.setNegativeButton(buttonTextID[1],
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User pressed the no button
						forceNewTCX_FIT = false;
						if (debugOldTCXFile) {
							Log.d(this.getClass().getName(), "doAskResetPermission() No button");
							Log.d(this.getClass().getName(), "tcx footerLength: "
									+ myBikeStat.tcxLog.outFileFooterLength);
							Log.d(this.getClass().getName(), "tcx filename: "
									+ myBikeStat.tcxLog.outFileName);
							Log.d(this.getClass().getName(), "forceNewTCX: "
									+ (forceNewTCX_FIT ? "true" : "false"));
						}
					}
				});
		// Set other dialog properties
		builder.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				forceNewTCX_FIT = false;
			}
		});
		builder.setMessage(getString(R.string.okay_to_reset_trip_data_))
				.setTitle(getString(R.string.reset_data)).show();
	}

	/**
	 * Include the route name in the window title. Called from reset() and load
	 * route options menu items; and from initializeScreen(). When called from
	 * location spoofer, use scrolling title bar
	 */
	@SuppressLint("InflateParams")
	private void createTitle(String chosenFile) {
		if (trainerMode) {
			ActionBar ab = getActionBar();
			if (ab != null) {
				ab.setDisplayShowCustomEnabled(true);
				ab.setDisplayShowTitleEnabled(false);
			}
			LayoutInflater inflator = (LayoutInflater) this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = inflator.inflate(R.layout.marquee_action_bar, null);
			actionbar_title_text = ((TextView) v.findViewById(R.id.actionbar_title));
			if (chosenFile.equals("")) {
				actionbar_title_text.setText(this.getClass().getName());
			} else {
				actionbar_title_text.setText(chosenFile);
			}
			// even tho' we've set this in the XML file, have to set it again???
			actionbar_title_text.setHorizontallyScrolling(true);
			// this is the key to actually having the title scroll hahaha
			actionbar_title_text.setSelected(true);
			// assign the view to the actionbar
			if (ab != null) ab.setCustomView(v);
		} else {
			ActionBar ab = getActionBar();
			if (!chosenFile.equals("")) {
				int start = chosenFile.lastIndexOf("/") + 1;
				int end = chosenFile.length();
				if (chosenFile.endsWith(GPX)) {
					end = chosenFile.lastIndexOf(GPX);
				} else if (chosenFile.endsWith(TCX)) {
					end = chosenFile.lastIndexOf(TCX);
				} else if (chosenFile.endsWith(XML)) {
					end = chosenFile.lastIndexOf(XML);
				}
				String title = chosenFile.substring(start, end);
				if (ab != null) {
					ab.setDisplayShowCustomEnabled(false);
					ab.setDisplayShowTitleEnabled(true);
					ab.setTitle(title);
				}
			} else {
				if (ab != null) {
					ab.setDisplayShowCustomEnabled(false);
					ab.setDisplayShowTitleEnabled(true);
					ab.setTitle(this.getClass().getName());
				}
			}
		}
	}

	/**
	 * When long-clicking a way point in the list, make sure it's close enough
	 * check myPlace.distanceTo(Way point at pos-position in list) < nearEnough
	 * Set message either now navigating from..., or not close enough
	 *
	 * @param pos is the item number in the mergedRouteHashmap
	 * @return true if we are close enough to the waypoint
	 */
	private boolean checkNearEnough(int pos) {
		if (pos >= myNavRoute.mergedRoute_HashMap.size()) {
			return false;
		}
		GPXRoutePoint tempRP;
		tempRP = myNavRoute.mergedRoute_HashMap.get(pos);
		Location loc = new Location(myPlace);
		loc.setLatitude(tempRP.lat);
		loc.setLongitude(tempRP.lon);
		double dist = myPlace.distanceTo(loc);
		String streetString = tempRP.getStreetName();
		String str = getString(R.string.now_navigating_from_) + streetString;
		boolean near = (dist < nearEnough);
		if (near) {
			Toast nearToast = Toast.makeText(getApplicationContext(), str,
					Toast.LENGTH_SHORT);
			TextView v = (TextView) nearToast.getView().findViewById(android.R.id.message);
			v.setTextColor(ContextCompat.getColor(context, R.color.gpsgreen));
			v.setTextSize(16);
			nearToast.show();
		} else {
			str = streetString + getString(R.string._is_not_close_enough);
			Toast nearToast = Toast.makeText(getApplicationContext(), str,
					Toast.LENGTH_SHORT);
			TextView v = (TextView) nearToast.getView().findViewById(android.R.id.message);
			v.setTextColor(ContextCompat.getColor(context, R.color.gpsred));
			v.setTextSize(16);
			nearToast.show();
		}
		return near;
	}

	private void doCalibratePower() {
		if (debugAppState) {
			Log.i(this.getClass().getName(), "doCalibratePower()");
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String message = getString(R.string.follow_power_meter_instructions);
		CharSequence titleString = getString(R.string.calibrate_power_meter);
		builder.setMessage(message).setTitle(titleString).setCancelable(true);
		builder.setNegativeButton(getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
					}
				});
		builder.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
			}
		});
		builder.setPositiveButton(getString(R.string.calibrate),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// send manual calibration message
						boolean submitted = false;
						if (powerPcc != null) {
							if (debugAppState) {
								Log.i(this.getClass().getName(), "requesting manual calibration");
							}
							submitted = powerPcc.requestManualCalibration(requestFinishedReceiver);
						}
						if (!submitted) {
							if (debugAppState) {
								Log.i(this.getClass().getName(),
										getString(R.string.calibration_request_could_not_be_made));
							}
							viewToast(
									getString(R.string.calibration_request_could_not_be_made),
									40, ANT_TOAST_GRAVITY, antToastAnchor, res_white);
						} else {
							if (debugAppState) {
								Log.i(this.getClass().getName(),
										getString(R.string.calibration_requested));
							}
							viewToast(
									getString(R.string.calibration_requested),
									40, ANT_TOAST_GRAVITY, antToastAnchor, res_white);
						}// calibration result receiver will display confirmation message
					}
				});// positive button
		builder.show();
	}

	final IRequestFinishedReceiver requestFinishedReceiver = new IRequestFinishedReceiver() {
		@Override
		public void onNewRequestFinished(final RequestStatus requestStatus) {
			MainActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					switch (requestStatus) {
						case SUCCESS:
							break;
						case FAIL_PLUGINS_SERVICE_VERSION:
							goGetANTPlugins();
							break;
						default:
							break;
					}
				}
			});
		}
	};

	private void doSearchPair(final int deviceType) {
		if (debugMDS) {Log.i(this.getClass().getName(), "doPair()" + "device: " + deviceType);}
		// don't allow autoConnect while we're pairing. This will be set to false after user selects device, or cancels pairing dialog
		mAntManager.channelConfig[AntPlusManager.HRM_CHANNEL].pairing = true;
		final ProgressDialog pd = new ProgressDialog(MainActivity.this);
		pd.setIndeterminate(true);
		pd.setMessage("Searching for sensors");
		pd.show();

		speedCell.postDelayed(new Runnable() {
			@Override
			public void run() {
				pd.dismiss();
				showActiveDeviceDialog(deviceType);
				// show a dialog with all active devices; when user selects a device, add it to the DB if its not there
				// and set search priority to 1. Reset other priority. Ask user to enter device name and update DB.
			}
		}, 10000);

	}// do SearchPair

	private void showActiveDeviceDialog(final int deviceType) {
		//get ArrayList<ActiveANTDeviceData> for deviceType
		final ArrayList<ActiveANTDeviceData> activeDeviceData;
		activeDeviceData = mAntManager.getActiveDeviceDBDataByDeviceType(deviceType);
		if (debugMDS) mAntManager.logActiveDBDeviceListData("doSearch");
		activeDeviceData.addAll(mAntManager.getActiveDeviceOtherDataByDeviceType(deviceType));
		if (debugMDS) mAntManager.logActiveOtherDeviceListData("doSearch");
		List<String> deviceNames = new ArrayList<>();
		for (ActiveANTDeviceData data : activeDeviceData) {
			String name = data.getData().getAsString(DB_KEY_DEV_NAME);
			if (name == null || name.equals("")){
				name = "<" + data.getDeviceNum() + ">";
			}
			deviceNames.add(name);
		}
		final int deviceNameSize = deviceNames.size();
		CharSequence[] items;
		if (deviceNameSize == 0) {
			items = new CharSequence[1];
			items[0] = "no devices found";
		} else {
			items = deviceNames.toArray(new CharSequence[deviceNames.size()]);
		}
		// create dialog with builder; title = Pair device, simple adapter, onItemClick method, cancel button
		AlertDialog.Builder pairAlert = new AlertDialog.Builder(MainActivity.this);

		pairAlert.setTitle("Select device to pair");
		pairAlert.setNegativeButton("Refresh", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// restart MDS to allow autoConnect
				mAntManager.setForceMDSStartTime(System.currentTimeMillis());
				doSearchPair(deviceType);
			}
		});
		pairAlert.setPositiveButton(getString(R.string.cancel), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mAntManager.channelConfig[AntPlusManager.HRM_CHANNEL].pairing = false;
			}
		});
		pairAlert.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				mAntManager.channelConfig[AntPlusManager.HRM_CHANNEL].pairing = false;
			}
		});
		pairAlert.setItems(items, new OnClickListener() {
			/**
			 * This method will be invoked when a button in the dialog is clicked.
			 *
			 * @param dialog The dialog that received the click.
			 * @param which  The button that was clicked (e.g.
			 *               {@link DialogInterface#BUTTON1}) or the position
			 */
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//Log.i(this.getClass().getName(), "Search - onClick item: " + which);
				if (deviceNameSize == 0) {
					mAntManager.channelConfig[AntPlusManager.HRM_CHANNEL].pairing = false;
					return;
				}
				// restart MDS to allow autoConnect
				mAntManager.setForceMDSStartTime(System.currentTimeMillis());
				ActiveANTDeviceData data = activeDeviceData.get(which);
				//set data priority to 1
				ContentValues dataContent = data.getData();
				dataContent.put(DB_KEY_SEARCH_PRIORITY, 1);
				data.setData(dataContent);
				//if data is not in data base, add it
				if (!mAntManager.isDeviceInDBActiveList(data.getDeviceNum())) {
					//ask user for device name
					showDeviceNameDialog(data);
				} else {
					// device was already in database and in our active device DB list,
					// check for null or empty device name
					ContentValues content = data.getData();
					String devName = content.getAsString(DB_KEY_DEV_NAME);
					if (devName == null || devName.equals("")) {
						content.put(DB_KEY_DEV_NAME, "<" + data.getDeviceNum() + ">");
						dataBaseAdapter.updateDeviceRecord(data.getDeviceNum(), content);
					}
					mAntManager.updateActiveDBDeviceData(data.getDeviceNum(), content);
					doCleanUp(data);
				}
			}

			/**
			 * Device was not in database so give user a chance to name the device
			 * @param data ActiveANT device data to add to database
			 */
			private void showDeviceNameDialog(final ActiveANTDeviceData data) {
				final AlertDialog.Builder editAlert = new AlertDialog.Builder(MainActivity.this);
				final EditText input = new EditText(MainActivity.this);
				//input.setText(data.getData().getAsString(DB_KEY_DEV_NAME));
				editAlert.setView(input);
				editAlert.setTitle("Enter Device Name");
				editAlert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String editTextValue = input.getText().toString().trim();
						ContentValues content = data.getData();
						if (editTextValue.equals("")) {
							editTextValue = "<" + data.getDeviceNum() + ">";
						}
						content.put(DB_KEY_DEV_NAME, editTextValue);
						//add device to antDeviceDBList.
						mAntManager.addToANTDBDeviceList(data);
						// also add device to database
						dataBaseAdapter.addDeviceToDB(content);
						doCleanUp(data);
					}
				});
				// user chose not to give device a name; still add device to DB and do other clean-up
				editAlert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						ContentValues content = data.getData();
						String devName = content.getAsString(DB_KEY_DEV_NAME);
						if (devName == null || devName.equals("")) {
							content.put(DB_KEY_DEV_NAME, "<" + data.getDeviceNum() + ">");
						}
						//add device to antDeviceDBList.
						mAntManager.addToANTDBDeviceList(data);
						// also add device to database
						dataBaseAdapter.addDeviceToDB(content);
						doCleanUp(data);
						dialog.cancel();
					}
				});
				editAlert.show();
			}

			private void doCleanUp(final ActiveANTDeviceData data) {
				// Set search priority of this device to 1 and adjust other priorities
				dataBaseAdapter.resetSearchPriorityFromAcq(data.getDeviceNum(), deviceType);
				mAntManager.resetSearchPriority(data.getDeviceNum(), DeviceType.getValueFromInt(deviceType));
				//dataBaseAdapter.dumpDBToLog("searchPair()");
				switch (DeviceType.getValueFromInt(data.getDeviceType())) {
					case BIKE_CADENCE:
						unsubscribeCadEvents();
						releaseCadPcc();
						cadCell.post(new Runnable() {
							public void run() {
								if (debugMDS) Log.i(this.getClass().getName(), "autoConnectCad() - requestDeviceAccess()");
								cadReleaseHandle = AntPlusBikeCadencePcc.requestAccess(context, data.getDeviceNum(), 0, false,
										CAD_IPluginAccessResultReceiver, CAD_IDeviceStateChangeReceiver);
							}
						});
						break;
					case BIKE_SPD:
						unsubscribeSpeedEvents();
						releaseSpeedPcc();
						speedCell.post(new Runnable() {
							public void run() {
								if (debugMDS) Log.i(this.getClass().getName(), "autoConnectSpeed() - requestDeviceAccess()");
								speedReleaseHandle = AntPlusBikeSpeedDistancePcc.requestAccess(context, data.getDeviceNum(), 0, false,
										Speed_IPluginAccessResultReceiver, Speed_IDeviceStateChangeReceiver);
							}
						});
						break;
					case BIKE_POWER:
						unsubscribePowerEvents();
						releasePowerPcc();
						powerCell.post(new Runnable() {
							public void run() {
								if (debugMDS) Log.i(this.getClass().getName(), "autoConnectPower() - requestDeviceAccess()");
								powerReleaseHandle = AntPlusBikePowerPcc.requestAccess(context, data.getDeviceNum(), 0,
										Pow_IPluginAccessResultReceiver, Pow_IDeviceStateChangeReceiver);
							}
						});
						break;
					case HEARTRATE:
						unsubscribeHrEvents();
						releaseHrPcc();
						hrCell.post(new Runnable() {
							public void run() {
								if (debugMDS) Log.i(this.getClass().getName(), "autoConnectHRM() - requestDeviceAccess()");
								hrmReleaseHandle = AntPlusHeartRatePcc.requestAccess(context, data.getDeviceNum(), 0,
										HR_IPluginAccessResultReceiver, HR_IDeviceStateChangeReceiver);
							}
						});
						break;
					case BIKE_SPDCAD:
						unsubscribeCadEvents();
						releaseCadPcc();
						unsubscribeSpeedEvents();
						releaseSpeedPcc();
						cadCell.post(new Runnable() {
							public void run() {
								if (debugMDS) Log.i(this.getClass().getName(), "autoConnectCad() - requestDeviceAccess()");
								cadReleaseHandle = AntPlusBikeCadencePcc.requestAccess(context, data.getDeviceNum(), 0, true,
										CAD_IPluginAccessResultReceiver, CAD_IDeviceStateChangeReceiver);
							}
						});
						break;
				}
				//mAntManager.logActiveDBDeviceListData("doCleanUp from Search");
				// after we've manipulated the active Device list, clear it because we may have moved a device from Other to DB list
				mAntManager.antOtherDeviceList.clear();
				mAntManager.channelConfig[AntPlusManager.HRM_CHANNEL].pairing = false;
			}
		});
		pairAlert.show();
	}

	/**
	 * Receives HRM state changes and alters channel configurations
	 */
	protected IDeviceStateChangeReceiver HR_IDeviceStateChangeReceiver = new IDeviceStateChangeReceiver() {

		@Override
		public void onDeviceStateChange(final DeviceState newDeviceState) {
			if (debugAppState) {
				Log.v(this.getClass().getName(), "onDeviceStateChangeHRM() - "
						+ " newDeviceState: " + newDeviceState.name());
			}
			switch (newDeviceState) {
				case PROCESSING_REQUEST:
				case UNRECOGNIZED:
				case CLOSED:
					break;
				case TRACKING:
					// wait until hrPcc DeviceState is active
					trackingHRM();
					break;
				case DEAD:
					// start the MDS if device changed state to DEAD
					mAntManager.setForceMDSStartTime(System.currentTimeMillis());
					releaseHrPcc();
					break;
				case SEARCHING:
					notTrackingHRM();
					break;
				default:
					// must not be tracking the HR sensor anymore, or it stopped transmitting...
					notTrackingHRM();
					break;
			}
			if (showANTData) {
				// don't have access to UI
				hrCell.post(new Runnable() {
					@Override
					public void run() {
						updateCadHrPowerLabels();
					}
				});
			}// if showANTData
			//logChannelState(DeviceType.HEARTRATE, "onHRMDeviceStateChange()");
		}// onDeviceStateChange()
	};// HR_IDeviceStateChangeReceiver

	/**
	 * unsubscribes from HRM data events before calling .close
	 */
	protected void unsubscribeHrEvents() {
		if ((hrPcc == null)
				|| !mAntManager.isChannelSubscribed(DeviceType.HEARTRATE)) {
			mAntManager.setChannelSubscribed(false, DeviceType.HEARTRATE);
			return;
		}
		if (hrPcc != null) {
			try {
				hrPcc.subscribeHeartRateDataEvent(null);
				hrPcc.subscribeCumulativeOperatingTimeEvent(null);
				hrPcc.subscribeManufacturerAndSerialEvent(null);
				hrPcc.subscribeVersionAndModelEvent(null);
			}catch (RuntimeException e){
				e.printStackTrace();
			}
		}
		mAntManager.setChannelSubscribed(false, DeviceType.HEARTRATE);
	}

	/**
	 * Subscribes to some of the HRM data events
	 */
	protected void subscribeToHrEvents() {
		if ((hrPcc == null)) {
			return;
		}
		if (hrPcc.getCurrentDeviceState() != DeviceState.TRACKING) {
			return;
		}
		mAntManager.setChannelSubscribed(true, DeviceType.HEARTRATE);
		// heart rate counter starts over when we close and open hr channel
		mAntManager.hrData.prevCount = 0;
		//Log.i(this.getClass().getName(), "subscribeHeartRateDataEvent() ");
		hrPcc.subscribeHeartRateDataEvent(new IHeartRateDataReceiver() {

			private void averageCalcHR(long heartRate) {
				if (heartRate > 0) {// don't average zeros
					mAntManager.addNumHREvents();
					mAntManager.addTotalHRCounts(heartRate);
					myBikeStat.setAvgHeartRate((int) (mAntManager
							.getTotalHRCounts() / mAntManager.getNumHREvents()));
				}
			}

			@Override
			public void onNewHeartRateData(final long estTimestamp,
					EnumSet<EventFlag> eventFlags, final int computedHeartRate,
					final long heartBeatCounter,
					final BigDecimal heartBeatEventTime,
					final DataState dataState) {
				if (mAntManager.isMDSStarting()) {
					if (debugMDS) Log.e(this.getClass().getName(), "couldn't do onNewHeartRateData(), MDSStarting");
					return;
				}
				//Log.i(this.getClass().getName(), "onNewHeartRateData(): " +computedHeartRate );
				if (heartBeatCounter > mAntManager.hrData.prevCount) {
					// don't store the ets if heartbeat counter hasn't
					// incremented; this is reporting old data
					// then the sensorWatchdog will detect old data and take action
					mAntManager.hrData.currTime = SystemClock.elapsedRealtime();
					mAntManager.hrData.prevCount = heartBeatCounter;
					mAntManager.hrData.isDataCurrent = true;
					if ((computedHeartRate >= 0) && !writingTrackRecord) {
						// only update display if this is new data
						averageCalcHR(computedHeartRate);
						myBikeStat.setHR(computedHeartRate);
						if (computedHeartRate > myBikeStat.getMaxHeartRate()) {
							myBikeStat.setMaxHeartRate(computedHeartRate);
						}
						if (showANTData) {
							refreshHR();
						}// if showANTData
					}// HR>0
				}// increased HR counter
				if (heartBeatCounter < mAntManager.hrData.prevCount) {
					// heartBeatCounter rolled-over or re-set somehow
					mAntManager.hrData.prevCount = heartBeatCounter;
				}
			}
		});
		hrPcc.subscribeCumulativeOperatingTimeEvent(new ICumulativeOperatingTimeReceiver() {
			@Override
			public void onNewCumulativeOperatingTime(final long estTimestamp,
					final EnumSet<EventFlag> eventFlags,
					final long cumulativeOperatingTime) {
				if (mAntManager.isMDSStarting()) {
					if (debugMDS) Log.e(this.getClass().getName(), "couldn't do hrCumOperatingTime(), MDSStarting");
					return;
				}
				ContentValues content = new ContentValues();
				String upTime = mAntManager.convertUpTimeToString(cumulativeOperatingTime);
				content.put(DB_KEY_UPTIME, upTime);
				mAntManager.updateActiveDBDeviceData(hrPcc.getAntDeviceNumber(), content);
			}
		});
		hrPcc.subscribeManufacturerAndSerialEvent(new IManufacturerAndSerialReceiver() {

			@Override
			public void onNewManufacturerAndSerial(long estTimestamp,
					EnumSet<EventFlag> eventFlags, int manufacturerID,
					int serialNumber) {
				ContentValues content = new ContentValues();
				content.put(DB_KEY_SERIAL_NUM, String.valueOf(serialNumber));
				content.put(DB_KEY_MANUFACTURER, String.valueOf(manufacturerID));
				mAntManager.updateActiveDBDeviceData(hrPcc.getAntDeviceNumber(), content);
				//unsubscribe once we have the data
				hrPcc.subscribeManufacturerAndSerialEvent(null);
			}
		});
		hrPcc.subscribeVersionAndModelEvent(new IVersionAndModelReceiver() {

			@Override
			public void onNewVersionAndModel(long estTimestamp,
					EnumSet<EventFlag> eventFlags, int hardwareVersion,
					int softwareVersion, int modelNumber) {
				ContentValues content = new ContentValues();
				content.put(DB_KEY_SOFTWARE_REV, String.valueOf(softwareVersion));
				content.put(DB_KEY_MODEL_NUM, String.valueOf(modelNumber));
				mAntManager.updateActiveDBDeviceData(hrPcc.getAntDeviceNumber(), content);
				//unsubscribe once we have the data
				hrPcc.subscribeVersionAndModelEvent(null);
			}
		});
	}// subscribeToHrEvents()

	/**
	 * Handle the result of accessing the HRM Pcc plug-in, connecting to events
	 * on success or reporting failure.
	 */
	protected IPluginAccessResultReceiver<AntPlusHeartRatePcc> HR_IPluginAccessResultReceiver = new IPluginAccessResultReceiver<AntPlusHeartRatePcc>() {
		@Override
		public void onResultReceived(final AntPlusHeartRatePcc result,
				final RequestAccessResult resultCode,
				final DeviceState initialDeviceState) {
			if (result != null && debugMDS) Log.i(this.getClass().getName(), "result: " + result.toString());
			if (debugMDS) Log.i(this.getClass().getName(), "resultCode: " + resultCode.getIntValue());
			if (debugMDS) Log.i(this.getClass().getName(), "initialDeviceState: " + initialDeviceState);
			String toastText = "";
			mAntManager.setAntChannelAvailable(true);
			switch (resultCode) {
				case SUCCESS:
					hrPcc = result;
					if (initialDeviceState == DeviceState.TRACKING) {
						trackingHRM();
					}
					break;
				case ADAPTER_NOT_DETECTED:
					useANTData = false;
					hasANT = false;
					toastText = getString(R.string.ant_not_available);
					break;
				case CHANNEL_NOT_AVAILABLE:
					//				mAntManager.setAntChannelAvailable(false);
					releaseHrPcc();
					break;
				// case BAD_PARAMS:
				case DEPENDENCY_NOT_INSTALLED:
					goGetANTPlugins();
					break;
				case SEARCH_TIMEOUT:
				case USER_CANCELLED:
				case DEVICE_ALREADY_IN_USE:
				case OTHER_FAILURE:
					break;
				case UNRECOGNIZED:
					toastText = getString(R.string.connection_to_hrm_failed_unrecognized_upgrade_required_);
					goUpgradeANTRadioService();
					break;
				default:
					toastText = getString(R.string.unrecognized_result_) + resultCode;
					break;
			}
			if (toastText.length() > 0) {
				viewToast(toastText, 40, ANT_TOAST_GRAVITY, antToastAnchor, res_white);
			}
		}// onResultReceived()
	};// HR_IPluginAccessResultReceiver

	/**
	 * Receives Cadence state changes and alters channel configurations
	 */
	protected IDeviceStateChangeReceiver CAD_IDeviceStateChangeReceiver = new IDeviceStateChangeReceiver() {
		@Override
		public void onDeviceStateChange(final DeviceState newDeviceState) {
			switch (newDeviceState) {
				case TRACKING:
					subscribeToCadEvents();
					trackingCad();
					break;
				case PROCESSING_REQUEST:
				case UNRECOGNIZED:
				case CLOSED:
					break;
				case DEAD:
					// start the MDS if device changed state to DEAD
					mAntManager.setForceMDSStartTime(System.currentTimeMillis());
					releaseCadPcc();
					break;
				case SEARCHING:
				default:
					// must not be tracking the Cadence sensor anymore, or it stopped transmitting...
					notTrackingCad();
					break;
			}
			if (showANTData) {
				// don't have access to UI
				hrCell.post(new Runnable() {
					@Override
					public void run() {
						updateCadHrPowerLabels();
					}
				});
			}// if showAntData
		}// onDeviceStateChange()
	};// CAD_IDeviceStateChangeReceiver

	/**
	 * unsubscribes from Cadence data events
	 */
	protected void unsubscribeCadEvents() {
		if ((cadPcc == null)
				|| !mAntManager.isChannelSubscribed(DeviceType.BIKE_CADENCE)) {
			mAntManager.setChannelSubscribed(false, DeviceType.BIKE_CADENCE);
			return;
		}
		mAntManager.setChannelSubscribed(false, DeviceType.BIKE_CADENCE);
		try {
			cadPcc.subscribeCalculatedCadenceEvent(null);
			cadPcc.subscribeRawCadenceDataEvent(null);
			// speed & cadence sensor doesn't have these data
			if (cadPcc != null) {
				if (!cadPcc.isSpeedAndCadenceCombinedSensor()) {
					cadPcc.subscribeCumulativeOperatingTimeEvent(null);
					cadPcc.subscribeManufacturerAndSerialEvent(null);
					cadPcc.subscribeVersionAndModelEvent(null);
					cadPcc.subscribeBatteryStatusEvent(null);
				}
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Subscribes to some of the Cadence data events
	 */
	protected void subscribeToCadEvents() {
		if ((cadPcc == null)
				|| cadPcc.getCurrentDeviceState() != DeviceState.TRACKING) {
			return;
		}
		mAntManager.setChannelSubscribed(true, DeviceType.BIKE_CADENCE);

		cadPcc.subscribeCalculatedCadenceEvent(new ICalculatedCadenceReceiver() {
			@Override
			public void onNewCalculatedCadence(final long estTimestamp,
					final EnumSet<EventFlag> eventFlags,
					final BigDecimal calculatedCadence) {

				if (mAntManager.isMDSStarting()) {
					if (debugMDS)
						Log.e(this.getClass().getName(), "couldn't do onNewCalculatedCadence(), MDSStarting");
					return;
				}
				cadCell.post(new Runnable() {
					@Override
					public void run() {
						// seems calculatedCadence function doesn't account for
						// roll-over in cadence counts got a negative cadence value
						// time-tag the latest data for checking sensorDataCurrent
						long startTime = System.nanoTime();
						mAntManager.pedalCadenceCnts.currTime = SystemClock.elapsedRealtime();
						mAntManager.pedalCadenceCnts.isDataCurrent = true;
						if ((calculatedCadence.intValue() >= 0) && !writingTrackRecord
								&& (calculatedCadence.intValue() < 241)) {
							myBikeStat.setPedalCadence(calculatedCadence.intValue());
							if (calculatedCadence.intValue() > myBikeStat.getMaxCadence()) {
								myBikeStat.setMaxCadence(calculatedCadence.intValue());
							}
							if (showANTData) {
								refreshCadence();
							}// if showANTData
							if (calculatedCadence.intValue() > 0) {// don't average zeros
								mAntManager.addNumPedalCad();
								mAntManager.addTotalPedalCad(calculatedCadence.intValue());
								myBikeStat.setAvgCadence((int) (mAntManager.getTotalPedalCad() / mAntManager.getNumPedalCad()));
/*								if (debugCrankCadence) {
									Log.i(this.getClass().getName(), "num ped count: " + mAntManager.getNumPedalCad());
									Log.i(this.getClass().getName(), "total ped count: " + mAntManager.getTotalPedalCad());
									Log.i(this.getClass().getName(), "avg Pedal Cadence: " + myBikeStat.getAvgCadence());
								}*/
							}
						}// positive value
						if (debugRefreshTiming) {
							Log.w(this.getClass().getName(), "new CalcCad Event duration: "
									+ String.format(FORMAT_4_1F, (System.nanoTime() - startTime) / 1000000.) + " msec");
						}
					}
				});
			}
		});
		if (cadPcc.isSpeedAndCadenceCombinedSensor()) {
			// If speedPcc null, speed is not tracking, or a stand-alone speed sensor is open,
			// close previous channel & open new speed channel
			if (speedPcc == null
					|| speedPcc.getCurrentDeviceState() != DeviceState.TRACKING
					|| cadPcc.getAntDeviceNumber() != speedPcc.getAntDeviceNumber()) {
				releaseSpeedPcc();
				mAntManager.restartWheelCal(myBikeStat.getWheelTripDistance());
				int searchProximityThreshold = 0;
				if (debugMDS) {Log.w(this.getClass().getName(), "trying to open speed in subscribe to cad events");}
				speedReleaseHandle = AntPlusBikeSpeedDistancePcc.requestAccess(
						context, cadPcc.getAntDeviceNumber(),
						searchProximityThreshold,
						cadPcc.isSpeedAndCadenceCombinedSensor(),
						Speed_IPluginAccessResultReceiver,
						Speed_IDeviceStateChangeReceiver);
			}
		} else {// only subscribe to operating time, etc if not combined sensor
			// If we said we were going to pair a speed/cadence sensor, but then
			// chose a cad only sensor we must now release speed channel.
			cadPcc.subscribeCumulativeOperatingTimeEvent(new ICumulativeOperatingTimeReceiver() {
				@Override
				public void onNewCumulativeOperatingTime(
						final long estTimestamp,
						final EnumSet<EventFlag> eventFlags,
						final long cumulativeOperatingTime) {
					if (mAntManager.isMDSStarting()) {
						if (debugMDS) Log.e(this.getClass().getName(), "couldn't do cadCumOperatingTime(), MDSStarting");
						return;
					}
					ContentValues content = new ContentValues();
					String upTime = mAntManager.convertUpTimeToString(cumulativeOperatingTime);
					content.put(DB_KEY_UPTIME, upTime);
					mAntManager.updateActiveDBDeviceData(cadPcc.getAntDeviceNumber(), content);
				}
			});
			cadPcc.subscribeManufacturerAndSerialEvent(new IManufacturerAndSerialReceiver() {

				@Override
				public void onNewManufacturerAndSerial(long estTimestamp,
						EnumSet<EventFlag> eventFlags, int manufacturerID,
						int serialNumber) {
					ContentValues content = new ContentValues();
					content.put(DB_KEY_SERIAL_NUM, String.valueOf(serialNumber));
					content.put(DB_KEY_MANUFACTURER, String.valueOf(manufacturerID));
					mAntManager.updateActiveDBDeviceData(cadPcc.getAntDeviceNumber(), content);
				}
			});
			cadPcc.subscribeVersionAndModelEvent(new IVersionAndModelReceiver() {

				@Override
				public void onNewVersionAndModel(long estTimestamp,
						EnumSet<EventFlag> eventFlags, int hardwareVersion,
						int softwareVersion, int modelNumber) {
					ContentValues content = new ContentValues();
					content.put(DB_KEY_SOFTWARE_REV, String.valueOf(softwareVersion));
					content.put(DB_KEY_MODEL_NUM, String.valueOf(modelNumber));
					mAntManager.updateActiveDBDeviceData(cadPcc.getAntDeviceNumber(), content);

				}
			});
			cadPcc.subscribeBatteryStatusEvent(new AntPlusBikeSpdCadCommonPcc.IBatteryStatusReceiver() {

				@Override
				public void onNewBatteryStatus(final long estTimestamp,
						EnumSet<EventFlag> eventFlags,
						final BigDecimal batteryVoltage,
						final BatteryStatus batteryStatus) {
					if (mAntManager.isMDSStarting()) {
						if (debugMDS) Log.e(this.getClass().getName(), "couldn't do cad onNewBatteryStatus(), MDSStarting");
						return;
					}
					Log.d(this.getClass().getName(), "onNewBatteryStatus() - CAD");
					ContentValues content = new ContentValues();
					content.put(DB_KEY_BATT_VOLTS,
							String.format(FORMAT_4_3F, batteryVoltage.doubleValue()));
					content.put(DB_KEY_BATT_STATUS, batteryStatus.name());
					mAntManager.updateActiveDBDeviceData(cadPcc.getAntDeviceNumber(), content);
					if (batteryStatus == BatteryStatus.CRITICAL) {
						antToastAnchor.post(new Runnable() {
							@Override
							public void run() {
								viewToast("Replace Cadence Sensor battery", 40,
										ANT_TOAST_GRAVITY, antToastAnchor,
										ContextCompat.getColor(context, R.color.gpsred));
							}
						});
					}// battery status
				}
			});
		}// not speed&cadence
	}// subscribeToCadEvents()

	/**
	 * Handle the result of accessing the Cadence Pcc plug-in, connecting to
	 * events on success or reporting failure.
	 */
	protected IPluginAccessResultReceiver<AntPlusBikeCadencePcc> CAD_IPluginAccessResultReceiver = new IPluginAccessResultReceiver<AntPlusBikeCadencePcc>() {
		@Override
		public void onResultReceived(final AntPlusBikeCadencePcc result,
				final RequestAccessResult resultCode,
				final DeviceState initialDeviceState) {
			String toastText = "";

			switch (resultCode) {
				case SUCCESS:
					cadPcc = result;
					if (initialDeviceState == DeviceState.TRACKING) {
						trackingCad();
					}
					subscribeToCadEvents();
					break;
				case ADAPTER_NOT_DETECTED:
					useANTData = false;
					hasANT = false;
					toastText = getString(R.string.ant_not_available);
					break;
				case CHANNEL_NOT_AVAILABLE:
					//				mAntManager.setAntChannelAvailable(false);
					break;
				// case BAD_PARAMS:
				case DEPENDENCY_NOT_INSTALLED:
					goGetANTPlugins();
					break;
				case SEARCH_TIMEOUT:
				case USER_CANCELLED:
					// this result comes from shutDownAntPlugins()
					mAntManager.setAntChannelAvailable(true);
					break;
				case DEVICE_ALREADY_IN_USE:
				case OTHER_FAILURE:
					mAntManager.setAntChannelAvailable(true);
					break;
				case UNRECOGNIZED:
					toastText = getString(R.string.connection_to_cadence_failed_unrecognized_upgrade_required_);
					goUpgradeANTRadioService();
					break;
				default:
					toastText = getString(R.string.unrecognized_result_) + resultCode;
					break;
			}
			if (toastText.length() > 0) {
				viewToast(toastText, 40, ANT_TOAST_GRAVITY, antToastAnchor, res_white);
			}
		}// onResultReceived()
	};// CAD_IPluginAccessResultReceiver

	/**
	 * Receives Speed state changes and alters channel configurations
	 */
	protected IDeviceStateChangeReceiver Speed_IDeviceStateChangeReceiver = new IDeviceStateChangeReceiver() {

		@Override
		public void onDeviceStateChange(final DeviceState newDeviceState) {
			switch (newDeviceState) {
				case TRACKING:
					// If wheel is not calibrated, we're already traveling with GPS
					// locations and accumulating distance, but speed channel is not open, so
					// we're not accumulating wheel counts.
					if (!mAntManager.wheelCnts.isCalibrated && !trainerMode) {
						mAntManager.restartWheelCal(myBikeStat.getWheelTripDistance());
					} else {// already calibrated, so enable .hasSpeed
						myBikeStat.hasSpeed = true;
					}
					trackingSpeed();
					break;
				case PROCESSING_REQUEST:
				case UNRECOGNIZED:
				case CLOSED:
					break;
				case DEAD:
					// start the MDS if device changed state to DEAD
					mAntManager.setForceMDSStartTime(System.currentTimeMillis());
					releaseSpeedPcc();
					break;
				case SEARCHING:
					notTrackingSpeed();
					break;
				default:
					// must not be tracking the Speed sensor anymore, or it stopped transmitting...
					notTrackingSpeed();
					break;
			}
		}// onDeviceStateChange()
	};// Speed_IDeviceStateChangeReceiver

	protected void unsubscribeSpeedEvents() {
		if ((speedPcc == null)
				|| !mAntManager.isChannelSubscribed(DeviceType.BIKE_SPD)) {
			mAntManager.setChannelSubscribed(false, DeviceType.BIKE_SPD);
			return;
		}
		try {
			if (speedPcc != null) {
				speedPcc.subscribeCalculatedSpeedEvent(null);
				speedPcc.subscribeRawSpeedAndDistanceDataEvent(null);
				if (!speedPcc.isSpeedAndCadenceCombinedSensor()) {
					speedPcc.subscribeCumulativeOperatingTimeEvent(null);
					speedPcc.subscribeManufacturerAndSerialEvent(null);
					speedPcc.subscribeVersionAndModelEvent(null);
					speedPcc.subscribeBatteryStatusEvent(null);
				}
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		mAntManager.setChannelSubscribed(false, DeviceType.BIKE_SPD);
	}

	protected void subscribeToSpeedEvents() {
		// subscribe to these events and save the results;
		if ((speedPcc == null)
				|| speedPcc.getCurrentDeviceState() != DeviceState.TRACKING) {
			return;
		}
		mAntManager.setChannelSubscribed(true, DeviceType.BIKE_SPD);
		speedPcc.subscribeRawSpeedAndDistanceDataEvent(new IRawSpeedAndDistanceDataReceiver() {
			/*
			 * timestampOfLastEvent - Sensor reported time counter value of last
			 * distance or speed computation cumulativeRevolutions - Total
			 * number of revolutions since the sensor was first connected. Note:
			 * If the subscriber is not the first PCC connected to the device
			 * the accumulation will probably already be at a value greater than
			 * 0;save the first received value as a relative zero for itself.
			 * Units: revolutions.
			 */
			@Override
			public void onNewRawSpeedAndDistanceData(final long estTimestamp,
					final EnumSet<EventFlag> eventFlags,
					final BigDecimal timestampOfLastEvent,
					final long cumulativeRevolutions) {
				if (mAntManager.isMDSStarting()) {
					if (debugMDS) Log.e(this.getClass().getName(), "couldn't do speed NewRawSpeedAndDistanceData(), MDSStarting");
					return;
				}
				speedCell.post(new Runnable() {
					@Override
					public void run() {
						long startTime = System.nanoTime();
						myBikeStat.hasSpeedSensor = true;
						// time-tag the latest data for checking sensorDataCurrent in sensor Watchdog
						mAntManager.wheelCnts.currTime = SystemClock.elapsedRealtime();
						double deltaTimeSec = (mAntManager.wheelCnts.currTime - mAntManager.wheelCnts.prevTime) / msecPerSec;

						if (deltaTimeSec <= 0) {
							return;
						}
						mAntManager.wheelCnts.isDataCurrent = true;
						myBikeStat.setSensorSpeedCurrent(true);
						// .cumulativeRevsAtCalStart is set in restartWheelCal()
						mAntManager.wheelCnts.calTotalCount = cumulativeRevolutions
								- mAntManager.wheelCnts.cumulativeRevsAtCalStart;
						mAntManager.wheelCnts.cumulativeRevolutions = cumulativeRevolutions;
						// if GPS not active, or loses signal during wheel calibration, restart;
						// except in trainer mode, where we don't calibrate the wheel
						long deltaCount = cumulativeRevolutions - mAntManager.wheelCnts.prevCount;
						if (!mAntManager.wheelCnts.isCalibrated
								&& !gpsLocationCurrent
								&& !trainerMode) {
							mAntManager.restartWheelCal(myBikeStat.getWheelTripDistance());
						}
						// If deltaCount < 0 it may be that the speed sensor stopped and
						// restarted or there was an overflow in cumulativeRevolutions
						// if wheel is not calibrated and delta is < 0 restart wheel cal
						// can't have cumRevsatCalStart be greater than cumRevs!
						// Sensor must have restarted
						if (!mAntManager.wheelCnts.isCalibrated
								&& ((deltaCount < 0) || (mAntManager.wheelCnts.cumulativeRevsAtCalStart > cumulativeRevolutions))) {
							mAntManager.restartWheelCal(myBikeStat.getWheelTripDistance());
						}
						// also test for maximum deltaCount
						double deltaDistance = deltaCount * mAntManager.wheelCnts.wheelCircumference;
						boolean tooFast = (deltaDistance / deltaTimeSec) > MAXIMUM_SPEED;
						if ((deltaCount > 0) && !tooFast) {
							double distance = deltaDistance + myBikeStat.getWheelTripDistance();
							myBikeStat.setWheelTripDistance(distance);
							myBikeStat.setSpoofWheelTripDistance(distance);
						}
						mAntManager.wheelCnts.prevCount = cumulativeRevolutions;
						if (!myBikeStat.isPaused()) {
							myBikeStat.setWheelRideTime(myBikeStat.getWheelRideTime() + deltaTimeSec);
						}
						mAntManager.wheelCnts.prevTime = mAntManager.wheelCnts.currTime;
						// If gps location is not current, or if wheel is calibrated,
						// use speed sensor to measure distance and ride time.
						// Copy values over to GPS Trip distance and ride time
						if ((myBikeStat.hasSpeed || !gpsLocationCurrent)
								&& !trainerMode) {
							myBikeStat.setGPSTripDistance(myBikeStat.getWheelTripDistance());
							myBikeStat.setGPSTripTime(myBikeStat.getWheelRideTime());

							myBikeStat.setPowerWheelTripDistance(myBikeStat.getWheelTripDistance());
							myBikeStat.setPowerWheelRideTime(myBikeStat.getWheelRideTime());
						}
						if (!trainerMode) {
							refreshTimeDistance();
						}
						if (debugRefreshTiming) {
							Log.w(this.getClass().getName(), "new RawSpeed Event duration: "
									+ String.format(FORMAT_4_1F, (System.nanoTime() - startTime) / 1000000.) + " msec");
						}
					}//run()
				});//Runnable()

			}// newRawSpeedandDistance
		});
		if (speedPcc.isSpeedAndCadenceCombinedSensor()) {
			// Now open the cadPcc
			// If a stand-alone cad sensor was previously opened or no cad
			// sensor is open or tracking.
			if (cadPcc == null
					|| cadPcc.getCurrentDeviceState() != DeviceState.TRACKING
					|| cadPcc.getAntDeviceNumber() != speedPcc.getAntDeviceNumber()) {
				releaseCadPcc();
				int searchProximityThreshold = 0;

				cadReleaseHandle = AntPlusBikeCadencePcc.requestAccess(context,
						speedPcc.getAntDeviceNumber(),
						searchProximityThreshold,
						speedPcc.isSpeedAndCadenceCombinedSensor(),
						CAD_IPluginAccessResultReceiver,
						CAD_IDeviceStateChangeReceiver);
			}
		} else {
			// speed is not combined sensor, so we can subscribe to these events
			speedPcc.subscribeBatteryStatusEvent(new AntPlusBikeSpdCadCommonPcc.IBatteryStatusReceiver() {

				@Override
				public void onNewBatteryStatus(final long estTimestamp,
						EnumSet<EventFlag> eventFlags,
						final BigDecimal batteryVoltage,
						final BatteryStatus batteryStatus) {
					if (mAntManager.isMDSStarting()) {
						if (debugMDS) Log.e(this.getClass().getName(), "couldn't speed do batteryStatus, MDSStarting");
						return;
					}

					ContentValues content = new ContentValues();
					content.put(DB_KEY_BATT_VOLTS,
							String.format(FORMAT_4_3F, batteryVoltage.doubleValue()));
					content.put(DB_KEY_BATT_STATUS, batteryStatus.name());
					mAntManager.updateActiveDBDeviceData(speedPcc.getAntDeviceNumber(), content);
					if (batteryStatus == BatteryStatus.CRITICAL) {
						antToastAnchor.post(new Runnable() {
							@Override
							public void run() {
								viewToast("Replace Speed Sensor battery", 40,
										ANT_TOAST_GRAVITY, antToastAnchor,
										ContextCompat.getColor(context, R.color.gpsred));
							}
						});
					}
				}
			});
			speedPcc.subscribeCumulativeOperatingTimeEvent(new ICumulativeOperatingTimeReceiver() {
				@Override
				public void onNewCumulativeOperatingTime(
						final long estTimestamp,
						final EnumSet<EventFlag> eventFlags,
						final long cumulativeOperatingTime) {
					if (mAntManager.isMDSStarting()) {
						if (debugMDS) Log.e(this.getClass().getName(), "couldn't do speedCumOperatingTime(), MDSStarting");
						return;
					}
					ContentValues content = new ContentValues();
					String upTime = mAntManager.convertUpTimeToString(cumulativeOperatingTime);
					content.put(DB_KEY_UPTIME, upTime);
					mAntManager.updateActiveDBDeviceData(speedPcc.getAntDeviceNumber(), content);
				}
			});
			speedPcc.subscribeManufacturerAndSerialEvent(new IManufacturerAndSerialReceiver() {

				@Override
				public void onNewManufacturerAndSerial(final long estTimestamp,
						final EnumSet<EventFlag> eventFlags,
						final int manufacturerID, final int serialNumber) {
					ContentValues content = new ContentValues();
					content.put(DB_KEY_SERIAL_NUM, String.valueOf(serialNumber));
					content.put(DB_KEY_MANUFACTURER, String.valueOf(manufacturerID));
					mAntManager.updateActiveDBDeviceData(speedPcc.getAntDeviceNumber(), content);
				}
			});
			speedPcc.subscribeVersionAndModelEvent(new IVersionAndModelReceiver() {

				@Override
				public void onNewVersionAndModel(final long estTimestamp,
						final EnumSet<EventFlag> eventFlags,
						final int hardwareVersion, final int softwareVersion,
						final int modelNumber) {
					ContentValues content = new ContentValues();
					content.put(DB_KEY_SOFTWARE_REV, String.valueOf(softwareVersion));
					content.put(DB_KEY_MODEL_NUM, String.valueOf(modelNumber));
					mAntManager.updateActiveDBDeviceData(speedPcc.getAntDeviceNumber(), content);
				}
			});
		}// speed combined sensor
		// Have to subscribe to calibrated speed, even tho' we're not sure
		// if wheel is calibrated yet
		subscribeCalibratedSpeed();
	}// subscribeToSpeedEvents()

	private void subscribeCalibratedSpeed() {
		if (speedPcc == null) {
			return;
		}
		// if the speed sensor is not calibrated, read shared prefs to use
		// entered value, or default
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		if (!myBikeStat.hasSpeed) {
			mAntManager.wheelCnts.wheelCircumference = Double.valueOf(settings
					.getString(WHEEL_CIRCUM, DOUBLE_ZERO));
			// set wheel circumference to a default value if stored value out of range
			if ((mAntManager.wheelCnts.wheelCircumference > UPPER_WHEEL_CIRCUM)
					|| (mAntManager.wheelCnts.wheelCircumference < LOWER_WHEEL_CIRCUM)) {
				mAntManager.wheelCnts.wheelCircumference = DEFAULT_WHEEL_CIRCUM;
			}
		}

		BigDecimal wheelCircumference = new BigDecimal(String.valueOf(mAntManager.wheelCnts.wheelCircumference));
		speedPcc.subscribeCalculatedSpeedEvent(new CalculatedSpeedReceiver(wheelCircumference) {
			@Override
			public void onNewCalculatedSpeed(final long estTimestamp,
					final EnumSet<EventFlag> eventFlags,
					final BigDecimal calculatedSpeed) {

				if (mAntManager.isMDSStarting()) {
					if (debugMDS) Log.e(this.getClass().getName(), "couldn't do onNewCalculatedSpeed(), MDSStarting");
					return;
				}
				speedCell.post(new Runnable() {
					@Override
					public void run() {
						// Just receiving a calculated speed event means the data is current
						long startTime = System.nanoTime();
						mAntManager.wheelCnts.isDataCurrent = true;
						myBikeStat.setSensorSpeedCurrent(true);
						mAntManager.wheelCnts.currTime = SystemClock.elapsedRealtime();
						// If function doesn't account for roll-over speed might be negative
						if ((calculatedSpeed.doubleValue() >= 0.)
								&& calculatedSpeed.doubleValue() < MAXIMUM_SPEED) {
							myBikeStat.setSensorSpeed(calculatedSpeed.doubleValue());
							myBikeStat.setSpeed(trainerMode);
							refreshSpeed();
						}// if calculateSpeed > 0
						if (debugRefreshTiming) {
							Log.w(this.getClass().getName(), "new Calculated Speed Event duration: "
									+ String.format(FORMAT_4_1F, (System.nanoTime() - startTime) / 1000000.) + " msec");
						}
					}
				});
			}// onNewCalculatedSpeed()
		});
	}

	/**
	 * Handle the result of accessing the Speed Pcc plug-in, connecting to
	 * events on success or reporting failure.
	 */
	protected IPluginAccessResultReceiver<AntPlusBikeSpeedDistancePcc> Speed_IPluginAccessResultReceiver = new IPluginAccessResultReceiver<AntPlusBikeSpeedDistancePcc>() {
		@Override
		public void onResultReceived(final AntPlusBikeSpeedDistancePcc result,
				final RequestAccessResult resultCode,
				final DeviceState initialDeviceState) {
			String toastText = "";
			switch (resultCode) {
				case SUCCESS:
					speedPcc = result;
					if (initialDeviceState == DeviceState.TRACKING) {
						// If wheel is not calibrated, we're already traveling with
						// GPS locations
						// and accumulating distance, but speed channel is not open,
						// so we're not accumulating wheel counts.
						if (!mAntManager.wheelCnts.isCalibrated && !trainerMode) {
							mAntManager.restartWheelCal(myBikeStat.getWheelTripDistance());
						} else {// already calibrated, so enable .hasSpeed
							myBikeStat.hasSpeed = true;
						}
						trackingSpeed();
						subscribeToSpeedEvents();
					}
					break;
				case ADAPTER_NOT_DETECTED:
					useANTData = false;
					hasANT = false;
					toastText = getString(R.string.ant_not_available);
					break;
				case CHANNEL_NOT_AVAILABLE:
					//				mAntManager.setAntChannelAvailable(false);
					break;
				case DEPENDENCY_NOT_INSTALLED:
					goGetANTPlugins();
					break;
				case SEARCH_TIMEOUT:
				case USER_CANCELLED:
					// this result comes from shutDownAntPlugins()
					mAntManager.setAntChannelAvailable(true);
					break;
				case OTHER_FAILURE:
				case DEVICE_ALREADY_IN_USE:
					mAntManager.setAntChannelAvailable(true);
					break;
				case UNRECOGNIZED:
					toastText = getString(R.string.connection_to_speed_failed_unrecognized_upgrade_required_);
					goUpgradeANTRadioService();
					break;
				default:
					toastText = getString(R.string.unrecognized_result_) + resultCode;
					break;
			}
			if (toastText.length() > 0) {
				viewToast(toastText, 40, ANT_TOAST_GRAVITY, antToastAnchor, res_white);
			}
		}// onResultReceived()
	};// Speed_IPluginAccessResultReceiver

	ICalibrationMessageReceiver powerCalReceiver = new ICalibrationMessageReceiver() {

		@Override
		public void onNewCalibrationMessage(final long estTimestamp,
				final EnumSet<EventFlag> eventFlags,
				final CalibrationMessage calibrationMessage) {
			//Log.i(this.getClass().getName(), "onNewCalibrationMessage() " + calibrationMessage.toString());
			powerCell.post(new Runnable() {

				@Override
				public void run() {
					switch (calibrationMessage.calibrationId) {
						case GENERAL_CALIBRATION_FAIL:
							viewToast(getString(R.string.power_calibration_failed_code_)
											+ calibrationMessage.calibrationData,
									40, ANT_TOAST_GRAVITY, antToastAnchor,
									ContextCompat.getColor(context, R.color.gpsred));
							break;
						case GENERAL_CALIBRATION_SUCCESS:
							ContentValues content = new ContentValues();
							content.put(DB_KEY_POWER_CAL, String
									.valueOf(calibrationMessage.calibrationData));
							mAntManager.updateActiveDBDeviceData(powerPcc.getAntDeviceNumber(), content);
							viewToast(getString(R.string.power_calibration_success_value_)
											+ calibrationMessage.calibrationData,
									40, ANT_TOAST_GRAVITY, antToastAnchor,
									ContextCompat.getColor(context, R.color.gpsgreen));
							break;
						case CUSTOM_CALIBRATION_RESPONSE:
						case CUSTOM_CALIBRATION_UPDATE_SUCCESS:
							break;
						case CTF_ZERO_OFFSET:
							break;
						case UNRECOGNIZED:
							// value was sent by the service, an upgrade of your
							// PCC may be required to handle this new value.
							viewToast(
									getString(R.string.calibration_failed_unrecognized_calibrationid_upgrade_required_),
									40, ANT_TOAST_GRAVITY, antToastAnchor,
									ContextCompat.getColor(context, R.color.gpsred));
						case CAPABILITIES:
							break;
						case CTF_MESSAGE:
						case CTF_SERIAL_NUMBER_ACK:
						case CTF_SLOPE_ACK:
						case INVALID:
						default:
							break;
					}// switch
				}// run
			});// runnable
		}// onNewCalibrationMessage()
	};

	/**
	 * Receives battery status updates from the power meter
	 */
	protected IBatteryStatusReceiver BatteryStatusReceiver = new IBatteryStatusReceiver() {

		@Override
		public void onNewBatteryStatus(final long estTimestamp,
				final EnumSet<EventFlag> eventFlags,
				final long cumulativeOperatingTime,
				final BigDecimal batteryVoltage,
				final BatteryStatus batteryStatus,
				final int cumulativeOperatingTimeResolution,
				final int numberOfBatteries, final int batteryIdentifier) {
			if (mAntManager.isMDSStarting()) {
				if (debugMDS) Log.e(this.getClass().getName(), "couldn't do batteryStatus(), MDSStarting");
				return;
			}
			ContentValues content = new ContentValues();
			content.put(DB_KEY_BATT_VOLTS,
					String.format(FORMAT_4_3F, batteryVoltage.doubleValue()));
			content.put(DB_KEY_BATT_STATUS, batteryStatus.name());
			String upTime = mAntManager.convertUpTimeToString(cumulativeOperatingTime);
			content.put(DB_KEY_UPTIME, upTime);
			mAntManager.updateActiveDBDeviceData(powerPcc.getAntDeviceNumber(), content);
			if (batteryStatus == BatteryStatus.CRITICAL) {
				antToastAnchor.post(new Runnable() {
					@Override
					public void run() {
						viewToast("Replace Power Sensor battery", 40,
								ANT_TOAST_GRAVITY, antToastAnchor,
								ContextCompat.getColor(context, R.color.gpsred));
					}
				});
			}
		}// onNewBatteryStatus()
	};

	/**
	 * Receives Power state changes and alters channel configurations
	 */
	protected IDeviceStateChangeReceiver Pow_IDeviceStateChangeReceiver = new IDeviceStateChangeReceiver() {
		@Override
		public void onDeviceStateChange(final DeviceState newDeviceState) {
			switch (newDeviceState) {
				case TRACKING:
					// If wheel is not calibrated, we're already traveling with GPS locations
					// and accumulating distance, but power channel is not open, so
					// we're not accumulating wheel counts.
					if (!mAntManager.powerWheelCnts.isCalibrated && !trainerMode) {
						mAntManager.restartPowerWheelCal(myBikeStat.getWheelTripDistance());
					} else {// already calibrated, so enable .hasPowerSpeed
						myBikeStat.hasPowerSpeed = true;
					}
					trackingPower();
					break;
				case PROCESSING_REQUEST:
				case UNRECOGNIZED:
				case CLOSED:
					break;
				case DEAD:
					// start the MDS if device changed state to DEAD
					mAntManager.setForceMDSStartTime(System.currentTimeMillis());
					releasePowerPcc();
					break;
				case SEARCHING:
					notTrackingPower();
					break;
				default:
					// must not be tracking the power sensor anymore, or it stopped
					// transmitting...
					notTrackingPower();
					break;
			}
			if (showANTData) {
				// don't have access to UI
				powerCell.post(new Runnable() {
					@Override
					public void run() {
						updateCadHrPowerLabels();
					}
				});
			}// if showANTData
		}// onDeviceStateChange()
	};// Pow_IDeviceStateChangeReceiver

	/**
	 * Handle the result of accessing the Power Pcc plug-in, connecting to
	 * events on success or reporting failure.
	 */
	protected IPluginAccessResultReceiver<AntPlusBikePowerPcc> Pow_IPluginAccessResultReceiver = new IPluginAccessResultReceiver<AntPlusBikePowerPcc>() {
		@Override
		public void onResultReceived(final AntPlusBikePowerPcc result,
				final RequestAccessResult resultCode,
				final DeviceState initialDeviceState) {
			if (result != null && debugMDS) Log.i(this.getClass().getName(), "result: " + result.toString());
			if (debugMDS)Log.i(this.getClass().getName(), "resultCode: " + resultCode.getIntValue());
			if (debugMDS)Log.i(this.getClass().getName(), "initialDeviceState: " + initialDeviceState);
			String toastText = "";
			switch (resultCode) {
				case SUCCESS:
					powerPcc = result;
					if (initialDeviceState == DeviceState.TRACKING) {
						// If wheel is not calibrated, we're already traveling with GPS locations
						// and accumulating distance, but speed channel is not open,
						// so we're not accumulating wheel counts.
						if (!mAntManager.powerWheelCnts.isCalibrated && !trainerMode) {
							mAntManager.restartPowerWheelCal(myBikeStat.getWheelTripDistance());
						} else {// already calibrated, so enable .hasSpeed
							myBikeStat.hasPowerSpeed = true;
						}
						trackingPower();
					}
					break;
				case ADAPTER_NOT_DETECTED:
					useANTData = false;
					hasANT = false;
					toastText = getString(R.string.ant_not_available);
					mAntManager.setAntChannelAvailable(true);
					break;
				case CHANNEL_NOT_AVAILABLE:
					//				mAntManager.setAntChannelAvailable(false);
					break;
				// case BAD_PARAMS:
				case DEPENDENCY_NOT_INSTALLED:
					goGetANTPlugins();
					mAntManager.setAntChannelAvailable(true);
					break;
				case SEARCH_TIMEOUT:
				case USER_CANCELLED:
				case DEVICE_ALREADY_IN_USE:
				case OTHER_FAILURE:
					break;
				case UNRECOGNIZED:
					toastText = getString(R.string.connection_to_power_failed_unrecognized_upgrade_required_);
					goUpgradeANTRadioService();
					break;
				default:
					toastText = getString(R.string.unrecognized_result_) + resultCode;
					break;
			}
			if (toastText.length() > 0) {
				viewToast(toastText, 40, ANT_TOAST_GRAVITY, antToastAnchor, res_white);
			}
		}// onResultReceived()
	};// Pow_IPluginAccessResultReceiver

	/**
	 * initially set powerWheelCircum to stored value; after hub power sensor
	 * wheel size calibrated by comparing wheel distance to GPS, re-subscribe to
	 * calculated speed event
	 */
	private void subscribeCalibratedPowerSpeed() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		// if the power speed sensor is not calibrated, read shared prefs to use
		// same default value of wheel circumference
		if (!myBikeStat.hasPowerSpeed) {
			mAntManager.powerWheelCnts.wheelCircumference = Double
					.valueOf(settings.getString(POWER_WHEEL_CIRCUM, DOUBLE_ZERO));
			// set wheel circumference to a default value if stored value out of range
			if ((mAntManager.powerWheelCnts.wheelCircumference > UPPER_WHEEL_CIRCUM)
					|| (mAntManager.powerWheelCnts.wheelCircumference < LOWER_WHEEL_CIRCUM)) {
				mAntManager.powerWheelCnts.wheelCircumference = DEFAULT_WHEEL_CIRCUM;
			}
		}
		BigDecimal wheelCircumference = new BigDecimal(
				String.valueOf(mAntManager.powerWheelCnts.wheelCircumference));
		powerPcc.subscribeCalculatedWheelSpeedEvent(new CalculatedWheelSpeedReceiver(
				wheelCircumference) {
			@Override
			public void onNewCalculatedWheelSpeed(final long estTimestamp,
					final EnumSet<EventFlag> eventFlags,
					final DataSource dataSource,
					final BigDecimal calculatedSpeed) {
				// calculated speed is in kph
				// Just receiving a calculated speed event means the data is current
				if (mAntManager.isMDSStarting()) {
					if (debugMDS)
						Log.e(this.getClass().getName(), "couldn't do power onNewCalculatedWheelSpeed(), MDSStarting");
					return;
				}
				speedCell.post(new Runnable() {
					@Override
					public void run() {
						mAntManager.powerWheelCnts.isDataCurrent = true;
						mAntManager.powerWheelCnts.currTime = SystemClock.elapsedRealtime();
						myBikeStat.setPowerSpeedCurrent(true);

						if ((calculatedSpeed.doubleValue() >= 0.)
								&& (calculatedSpeed.doubleValue() < MAXIMUM_SPEED)) {
							myBikeStat.setPowerSpeed(calculatedSpeed.doubleValue() / kph_per_mps);
							//only refresh speed if no speed sensor. Don't want to cll this too often
							if (!myBikeStat.hasSpeedSensor) {
								myBikeStat.setSpeed(trainerMode);
								refreshSpeed();
							}
						}// if calculatedSpeed > 0
					}
				});
			}// onNewCalculatedSpeed() - power
		});
	}

	protected void unsubscribePowerEvents() {
		if ((powerPcc == null)
				|| !mAntManager.isChannelSubscribed(DeviceType.BIKE_POWER)) {
			mAntManager.setChannelSubscribed(false, DeviceType.BIKE_POWER);
			return;
		}
		try {
			if (powerPcc != null) {
				powerPcc.subscribeBatteryStatusEvent(null);
				powerPcc.subscribeCalculatedCrankCadenceEvent(null);
				powerPcc.subscribeCalculatedPowerEvent(null);
				// powerPcc.subscribeCalculatedTorqueEvent(null);
				powerPcc.subscribeCalibrationMessageEvent(null);
				powerPcc.subscribeInstantaneousCadenceEvent(null);
				powerPcc.subscribeProductInformationEvent(null);
				powerPcc.subscribeRawCrankTorqueDataEvent(null);
				powerPcc.subscribeRawPowerOnlyDataEvent(null);
				powerPcc.subscribeRawWheelTorqueDataEvent(null);
				powerPcc.subscribeCalculatedWheelSpeedEvent(null);
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		mAntManager.setChannelSubscribed(false, DeviceType.BIKE_POWER);
	}

	private void averageCalcCrankCad(int intValue) {
		if (intValue > 0) {// don't average zeros
			mAntManager.addNumCalcCrankCad();
			mAntManager.addTotalCalcCrankCad(intValue);
			myBikeStat.setAvgCadence((int) (mAntManager
					.getTotalCalcCrankCad() / mAntManager
					.getNumCalcCrankCad()));
/*			if (debugCrankCadence) {
				Log.i(this.getClass().getName(), "num crank cad count: "
						+ mAntManager.getNumCalcCrankCad());
				Log.i(this.getClass().getName(), "total crank cad count: "
						+ mAntManager.getTotalCalcCrankCad());
				Log.i(this.getClass().getName(), "avg Calculated Crank Cadence: "
						+ myBikeStat.getAvgCadence());
			}*/
		}
	}

	private void averageCalcPower(final long currCount) {
		// Now accumulate energy and pedaling time to calculate average power.
		// PowerTap only sends wheel_torque_data, not Power_only_data. Stages
		// sends crank_torque_data. When coasting, Stages doesn't send zeros; if
		// coasting for longer than MAX_DELTAT we won't keep adding the last
		// power value sent
		long deltaT = (mAntManager.calcPowerData.est - mAntManager.calcPowerData.prevTime);
		if ((currCount > 0.) && (deltaT > MIN_DELTAT) && (deltaT < MAX_DELTAT)) {
			mAntManager.addCumEnergy(currCount * deltaT / msecPerSec);
			mAntManager.addCumPowerTime(deltaT / msecPerSec);
			if (!writingTrackRecord) {
				myBikeStat.setAvgPower((int) (mAntManager.getCumEnergy() / (mAntManager
						.getCumPowerTime() + 1)));
			}
/*			if (debugCrankCadence) {
				Log.i(this.getClass().getName(), "deltaT (sec): "
						+ (mAntManager.calcPowerData.est - mAntManager.calcPowerData.prevTime)
						/ msecPerSec);
				Log.i(this.getClass().getName(), "cumulative energy: " + mAntManager.getCumEnergy());
				Log.i(this.getClass().getName(), "total power time " + mAntManager.getCumPowerTime());
				Log.i(this.getClass().getName(), "avg Power: " + myBikeStat.getAvgPower());
			}*/
		}
		// either the same power event deltaT too small, there was a long delay
		// in power events, or power was zero.
		// record current power est in prev est for next event
		mAntManager.calcPowerData.prevTime = mAntManager.calcPowerData.est;
	}

	private boolean rejectCadPower = false;

	private void dealWithCrankTorquePowerEvent() {
		// Test for concurrent power record with instantaneous cadence,
		// calculated cadence, and calculated power values. If a complete record
		// and Instantaneous cadence is positive (!rejectCadPower), save power
		// record values in BikeStat
		boolean completePowerRecord = testPowerRecord();
		if ((mAntManager.crankCadenceCnts.currCount >= 0)
				&& (mAntManager.crankCadenceCnts.currCount < 245)
				&& completePowerRecord && !rejectCadPower) {
			//Log.i(this.getClass().getName(), "Complete PowerRecord?" + (completePowerRecord?" yes":"no"));
			myBikeStat.setPowerCadence(((int) mAntManager.crankCadenceCnts.currCount));
			//Log.i(this.getClass().getName(),"powerCad: " + myBikeStat.getPowerCadence());
			if (myBikeStat.getCadence() > myBikeStat.getMaxCadence()) {
				myBikeStat.setMaxCadence(myBikeStat.getCadence());
			}
			refreshCadence();
			averageCalcCrankCad((int) mAntManager.crankCadenceCnts.currCount);
			// don't store any power values if cadence is zero, or raw power is zero
			if ((mAntManager.crankCadenceCnts.currCount > 0)
					&& (myBikeStat.getInstantaneousCrankCadence() > 0)
					&& (myBikeStat.getRawPower() > 0)) {
				myBikeStat.setPower((int) mAntManager.calcPowerData.currCount);
				if (myBikeStat.getPower() > myBikeStat.getMaxPower()) {
					myBikeStat.setMaxPower(myBikeStat.getPower());
				}
				averageCalcPower(mAntManager.calcPowerData.currCount);
				refreshPower();
				// use prevPower to display average of last two power values
				myBikeStat.setPrevPower(myBikeStat.getPower());
			} else { // if cadence = 0, set power to 0
				myBikeStat.setPower(0);
			}
			// once we have dealt with this power event, have to prevent
			// it from counting the same event again; this will wait for the
			// next crankCadence event thru testPowerRecord()
			mAntManager.crankCadenceCnts.est = 1;
			logPowerRecord();
		}
		logBadPowerEvent(completePowerRecord);
	}

	private void logPowerRecord() {
		if (debugCrankCadence) {
			Log.i(this.getClass().getName(), "CRANK_TORQUE_DATA power: " + myBikeStat.getPower());
			Log.i(this.getClass().getName(), "raw power: " + myBikeStat.getRawPower());
			Log.i(this.getClass().getName(), "calculated crank cadence: " + myBikeStat.getCadence());
			Log.i(this.getClass().getName(), "instantaneous crank cadence: " + myBikeStat.getInstantaneousCrankCadence());
		}
	}

	private boolean testPowerRecord() {
		// time-tag of calcCrankCadence, calcPower and instantaneous cadence is
		// within 0.1 sec
		final long MIN_EST = 100;
		return (Math.abs(myBikeStat.getInstantaneousCrankCadenceEST()
				- mAntManager.crankCadenceCnts.est) < MIN_EST)
				&& (Math.abs(myBikeStat.getInstantaneousCrankCadenceEST()
				- mAntManager.calcPowerData.est) < MIN_EST);
	}

	private void logBadPowerEvent(boolean completePowerRecord) {
		if (((mAntManager.calcPowerData.currCount > 1000)
				|| (myBikeStat.getCadence() > 200) || (myBikeStat
				.getInstantaneousCrankCadence() > 200)) && debugCrankCadence) {
			Log.w(this.getClass().getName(), "CRANK_TORQUE_DATA power: " + myBikeStat.getPower()
					+ " est: " + mAntManager.calcPowerData.est);
			Log.w(this.getClass().getName(), "prev CRANK_TORQUE_DATA power: " + myBikeStat.getPrevPower());
			Log.w(this.getClass().getName(), "raw power: " + myBikeStat.getRawPower());
			Log.w(this.getClass().getName(), "prev raw power: " + myBikeStat.getPrevRawPower());
			Log.w(this.getClass().getName(), "pedal cadence: " + myBikeStat.getCadence());
			Log.w(this.getClass().getName(), "calculated crank cadence: "
					+ mAntManager.crankCadenceCnts.currCount + " est: "
					+ mAntManager.crankCadenceCnts.est);
			Log.w(this.getClass().getName(), "instantaneous crank cadence: "
					+ myBikeStat.getInstantaneousCrankCadence()
					+ " est: " + myBikeStat.getInstantaneousCrankCadenceEST());
			Log.w(this.getClass().getName(), "prev instantaneous crank cadence: "
					+ myBikeStat.getPrevInstantaneousCrankCadence());
			Log.w(this.getClass().getName(), "rejectCadPower: " + (rejectCadPower ? "true" : "false"));
			Log.w(this.getClass().getName(), "complete record: " + (completePowerRecord ? "true" : "false"));
		}// log bad event
	}

	/**
	 * Subscribes to some of the Power data events
	 */
	protected void subscribeToPowerEvents() {
		if ((powerPcc == null) || powerPcc.getCurrentDeviceState() != DeviceState.TRACKING) {
			Log.i(this.getClass().getName(), "not subscribing to Power: " + (mAntManager.isChannelSubscribed(DeviceType.BIKE_POWER) ? "alreadySubscribed" : "")
					+ (powerPcc == null ? "(powerPcc == null)" : "") + (powerPcc.getCurrentDeviceState() != DeviceState.TRACKING ? "notTracking" : ""));
			return;
		}
		mAntManager.setChannelSubscribed(true, DeviceType.BIKE_POWER);
		powerPcc.subscribeBatteryStatusEvent(BatteryStatusReceiver);
		powerPcc.subscribeCalibrationMessageEvent(powerCalReceiver);

		powerPcc.subscribeCalculatedCrankCadenceEvent(new ICalculatedCrankCadenceReceiver() {
			// This data can only be sent by crank torque power sensors
			// compensate for roll-over bug where Instantaneous Cadence = -1
			@Override
			public void onNewCalculatedCrankCadence(final long estTimestamp,
					final EnumSet<EventFlag> eventFlags,
					final DataSource dataSource,
					final BigDecimal calculatedCrankCadence) {
				if (mAntManager.isMDSStarting()) {
					if (debugMDS) Log.e(this.getClass().getName(), "couldn't do onNewCalculatedCrankCadence(), MDSStarting");
					return;
				}
				cadCell.post(new Runnable() {
					@Override
					public void run() {
						mAntManager.crankCadenceCnts.est = estTimestamp;
						mAntManager.crankCadenceCnts.currCount = calculatedCrankCadence.intValue();
						myBikeStat.setPowerCadence(calculatedCrankCadence.intValue());
						mAntManager.crankCadenceCnts.currTime = SystemClock.elapsedRealtime();
						mAntManager.crankCadenceCnts.isDataCurrent = true;
						myBikeStat.hasPowerCadence = true;
						dealWithCrankTorquePowerEvent();
					}
				});
			}
		});

		powerPcc.subscribeCalculatedPowerEvent(new ICalculatedPowerReceiver() {

			@Override
			public void onNewCalculatedPower(final long estTimestamp,
					final EnumSet<EventFlag> eventFlags,
					final DataSource dataSource,
					final BigDecimal calculatedPower) {
				/*
				 * NOTE: The calculated power event will send an initial value
				 * code if it needed to calculate a NEW average. This is
				 * important if using the calculated power event to record user
				 * data, as an initial value indicates an average could not be
				 * guaranteed. The event prioritizes calculating with power only
				 * data over torque data.
				 */

				if (mAntManager.isMDSStarting()) {
					if (debugMDS) Log.e(this.getClass().getName(), "couldn't do onNewCalculatedPower(), MDSStarting");
					return;
				}
				// time-tag the latest data for checking sensorDataCurrent
				mAntManager.calcPowerData.currCount = Math.round(calculatedPower.doubleValue());
				mAntManager.calcPowerData.est = estTimestamp;
				mAntManager.calcPowerData.currTime = SystemClock.elapsedRealtime();
				mAntManager.calcPowerData.isDataCurrent = true;
				if (!mAntManager.calcPowerData.initialized) {
					mAntManager.calcPowerData.prevTime = mAntManager.calcPowerData.est;
					mAntManager.calcPowerData.initialized = true;
				}
				switch (dataSource) {
					case POWER_ONLY_DATA:
					case WHEEL_TORQUE_DATA:
						if (!writingTrackRecord) {
							myBikeStat.setPower((int) mAntManager.calcPowerData.currCount);
						}
						if ((myBikeStat.getPower() > myBikeStat.getMaxPower())
								&& !writingTrackRecord) {
							myBikeStat.setMaxPower(myBikeStat.getPower());
						}
						averageCalcPower(mAntManager.calcPowerData.currCount);
						//if (debugMDS){Log.i(this.getClass().getName(), "newCalcPower() _ power: " + myBikeStat.getPower());}
						refreshPower();
						// use prevPower to display average of last two power values
						myBikeStat.setPrevPower(myBikeStat.getPower());
						break;
					case CRANK_TORQUE_DATA:
						// need to deal with ANT plug-in bugs
						dealWithCrankTorquePowerEvent();
						break;
					case INITIAL_VALUE_CRANK_TORQUE_DATA:
						if (debugCrankCadence) {
							Log.i(this.getClass().getName(), "Initial CRANK_TORQUE_DATA power: " + calculatedPower.intValue());
						}
						break;
					// New data calculated from initial value data source
					case INITIAL_VALUE_POWER_ONLY_DATA:
						break;
					case INITIAL_VALUE_WHEEL_TORQUE_DATA:
						break;
					// case COAST_OR_STOP_DETECTED:
					// //A coast or stop condition detected by the ANT+ Plugin.
					// //This is automatically sent by the plugin after 3 seconds of
					// unchanging events.
					// break;
					case CTF_DATA:
					case INITIAL_VALUE_CTF_DATA:
					case INVALID_CTF_CAL_REQ:
						break;
					case UNRECOGNIZED:
					case INVALID:
						// This flag indicates that an unrecognized value was sent
						// by the service, an upgrade of your PCC may be required to
						// handle this new value.
						powerCell.post(new Runnable() {
							@Override
							public void run() {
								writeAppMessage(
										getString(R.string.unrecognized_power_value_upgrade_required_),
										ContextCompat.getColor(context, R.color.gpsred));
							}// run
						});// runOnUi
					default:
						break;
				}// switch
			}// onNewCalculatedPower()

		});

		// powerPcc.subscribeCalculatedTorqueEvent(new
		// ICalculatedTorqueReceiver() {
		// @Override
		// public void onNewCalculatedTorque(final long estTimestamp,
		// final EnumSet<EventFlag> eventFlags,
		// final DataSource dataSource,
		// final BigDecimal calculatedTorque) {
		// }
		// });
		//
		powerPcc.subscribeInstantaneousCadenceEvent(new IInstantaneousCadenceReceiver() {
			// This data is optional and may not be sent by all sensors.
			@Override
			public void onNewInstantaneousCadence(final long estTimestamp,
					final EnumSet<EventFlag> eventFlags,
					final DataSource dataSource, final int instantaneousCadence) {
				// Only use this data to reject wild data points from crank
				// torque power meter. Seems like a bug in ANT+ plug-ins lets
				// this value go negative which gives a weird calculated crank
				// cadence and sometimes gives a weird calculated power
				if (mAntManager.isMDSStarting()) {
					if (debugMDS) Log.e(this.getClass().getName(), "couldn't do onNewInstantaneousCadence(), MDSStarting");
					return;
				}
				switch (dataSource) {
					case INITIAL_VALUE_CRANK_TORQUE_DATA:
					case CRANK_TORQUE_DATA:
						myBikeStat.setPrevInstantaneousCrankCadence(myBikeStat
								.getInstantaneousCrankCadence());
						myBikeStat.setInstantaneousCrankCadence(instantaneousCadence);
						myBikeStat.setInstantaneousCrankCadenceEST((int) estTimestamp);
						rejectCadPower = false;
						// Test Instantaneous Cadence for -1 values and set
						// rejectCadPower; this is a roll-over bug in ANT+Plug-ins.
						// The calculatedCadence and calculatedPower values are wrong
						if ((myBikeStat.getInstantaneousCrankCadence() < 0)
								|| (myBikeStat.getPrevInstantaneousCrankCadence() < 0)) {
							rejectCadPower = true;
							if (debugCrankCadence) {
								Log.w(this.getClass().getName() + " powerCad", dataSource.name()
										+ " PowerInstantaneousCadence: "
										+ instantaneousCadence + " est: "
										+ estTimestamp);
								Log.w(this.getClass().getName(), "rejectCadPower: " + (rejectCadPower ? "true" : "false"));
							}
						}
						dealWithCrankTorquePowerEvent();
						break;
					case UNRECOGNIZED:
					case INVALID:
						// This flag indicates that an unrecognized value was sent
						// by the service, an upgrade of your PCC may be required to
						// handle this new value.
						powerCell.post(new Runnable() {
							@Override
							public void run() {
								writeAppMessage(
										getString(R.string.unrecognized_power_value_upgrade_required_),
										ContextCompat.getColor(context, R.color.gpsred));
							}// run
						});// runOnUi
					default:
						break;
				}// switch

			}// onInstantaneousCadence()
		});

		powerPcc.subscribeProductInformationEvent(new IProductInformationReceiver() {

			@Override
			public void onNewProductInformation(final long estTimestamp,
					final EnumSet<EventFlag> eventFlags,
					final int mainSoftwareRevision,
					final int supplementalSoftwareRevision,
					final long serialNumber) {
				ContentValues content = new ContentValues();
				content.put(DB_KEY_SERIAL_NUM, String.valueOf(serialNumber));
				content.put(DB_KEY_SOFTWARE_REV, String.valueOf(mainSoftwareRevision));
				mAntManager.updateActiveDBDeviceData(powerPcc.getAntDeviceNumber(), content);
				//unsubscribe once we have the data
				powerPcc.subscribeProductInformationEvent(null);
			}
		});
		powerPcc.subscribeManufacturerIdentificationEvent(new IManufacturerIdentificationReceiver() {
			@Override
			public void onNewManufacturerIdentification(long estTimestamp,
					EnumSet<EventFlag> eventFlags, int hardwareRevision,
					int manufacturerID, int modelNumber) {
				ContentValues content = new ContentValues();
				content.put(DB_KEY_MODEL_NUM, String.valueOf(modelNumber));
				content.put(DB_KEY_MANUFACTURER, String.valueOf(manufacturerID));
				mAntManager.updateActiveDBDeviceData(powerPcc.getAntDeviceNumber(), content);
				//unsubscribe once we have the data
				powerPcc.subscribeManufacturerIdentificationEvent(null);
			}
		});
		powerPcc.subscribeRawCrankTorqueDataEvent(new IRawCrankTorqueDataReceiver() {
			@Override
			public void onNewRawCrankTorqueData(final long estTimestamp,
					final EnumSet<EventFlag> eventFlags,
					final long crankTorqueUpdateEventCount,
					final long accumulatedCrankTicks,
					final BigDecimal accumulatedCrankPeriod,
					final BigDecimal accumulatedCrankTorque) {
			}
		});

		// Subscribing to raw power to help display when calculated power
		// doesn't report zero power events. Watch-dog timer will display zeros
		// after three sec, when calculatedPower & raw power is declared not
		// current.
		powerPcc.subscribeRawPowerOnlyDataEvent(new IRawPowerOnlyDataReceiver() {
			@Override
			public void onNewRawPowerOnlyData(final long estTimestamp,
					final EnumSet<EventFlag> eventFlags,
					final long powerOnlyUpdateEventCount,
					final int instantaneousPower, final long accumulatedPower) {
				if (mAntManager.isMDSStarting()) {
					if (debugMDS) Log.e(this.getClass().getName(), "couldn't do onNewRawPowerOnlyData(), MDSStarting");
					return;
				}				// Time-tag the latest data for checking sensorDataCurrent
				// Only call this raw data current if this is a new event
				if (powerOnlyUpdateEventCount > mAntManager.rawPowerData.eventCount) {
					mAntManager.rawPowerData.eventCount = powerOnlyUpdateEventCount;
					mAntManager.rawPowerData.currTime = SystemClock.elapsedRealtime();
					mAntManager.rawPowerData.isDataCurrent = true;
					if (!writingTrackRecord) {
						myBikeStat.setRawPower(instantaneousPower);
					}
/*					if (debugCrankCadence) {
						Log.i(this.getClass().getName(), "raw Power: " + instantaneousPower);
					}*/
					// Use raw power if calc power is not current and power
					// meter doesn't drop to zero; this will hold last power
					// level for a max of 3 sec. This will write zero power
					// values in tcx file
					if (!mAntManager.calcPowerData.isDataCurrent
							&& !writingTrackRecord) {
						myBikeStat.setPower(instantaneousPower);
					}
					refreshPower();
					if (!writingTrackRecord) {
						myBikeStat.setPrevRawPower(instantaneousPower);
					}
				} else {// protect against roll-over in
					// powerOnlyUpdateEventCount
					// if powerOnlyUpdateEventCount <
					// mAntManager.rawPowerData.eventCount
					mAntManager.rawPowerData.eventCount = powerOnlyUpdateEventCount;
				}
			}// onNewRawPowerOnlyData()
		});

		powerPcc.subscribeRawWheelTorqueDataEvent(new IRawWheelTorqueDataReceiver() {
			@Override
			public void onNewRawWheelTorqueData(final long estTimestamp,
					final EnumSet<EventFlag> eventFlags,
					final long wheelTorqueUpdateEventCount,
					final long accumulatedWheelTicks,
					final BigDecimal accumulatedWheelPeriod,
					final BigDecimal accumulatedWheelTorque) {
				if (mAntManager.isMDSStarting()) {
					if (debugMDS) Log.e(this.getClass().getName(), "couldn't do onNewRawWheelTorqueData(), MDSStarting");
					return;
				}
				myBikeStat.hasPowerSpeedSensor = true;
				mAntManager.powerWheelCnts.isDataCurrent = true;
				myBikeStat.setPowerSpeedCurrent(true);
				mAntManager.powerWheelCnts.currTime = SystemClock.elapsedRealtime();
				// .cumulativeRevsAtCalStart is set in restartPowerWheelCal()
				mAntManager.powerWheelCnts.calTotalCount = accumulatedWheelTicks
						- mAntManager.powerWheelCnts.cumulativeRevsAtCalStart;
				mAntManager.powerWheelCnts.cumulativeRevolutions = accumulatedWheelTicks;
				// if GPS not active, or loses signal during wheel calibration,
				// restart; except in trainer mode, where we don't calibrate the wheel
				if (!mAntManager.powerWheelCnts.isCalibrated
						&& !gpsLocationCurrent && !trainerMode) {
					mAntManager.restartPowerWheelCal(myBikeStat.getWheelTripDistance());
				}
				long deltaCount = accumulatedWheelTicks - mAntManager.powerWheelCnts.prevCount;
				// if deltaCount < 0 it may be that the wheel sensor stopped and
				// restarted or there was an overflow in accumulatedWheelTicks
				// If wheel is not calibrated and delta is < 0, or
				// cumRevsatCalStart < accumTicks restart wheel cal
				if (!mAntManager.powerWheelCnts.isCalibrated
						&& ((deltaCount < 0) || (mAntManager.powerWheelCnts.cumulativeRevsAtCalStart > accumulatedWheelTicks))) {
					mAntManager.restartPowerWheelCal(myBikeStat.getWheelTripDistance());
				}
				if (deltaCount > 0 && !writingTrackRecord) {
					myBikeStat.setPowerWheelTripDistance(deltaCount
							* mAntManager.powerWheelCnts.wheelCircumference
							+ myBikeStat.getPowerWheelTripDistance());
					myBikeStat.setSpoofWheelTripDistance(deltaCount
							* mAntManager.powerWheelCnts.wheelCircumference
							+ myBikeStat.getPowerWheelTripDistance());
				}
				mAntManager.powerWheelCnts.prevCount = accumulatedWheelTicks;
				if (!myBikeStat.isPaused() && !writingTrackRecord) {
					myBikeStat.setPowerWheelRideTime(myBikeStat.getPowerWheelRideTime()
							+ (mAntManager.powerWheelCnts.currTime - mAntManager.powerWheelCnts.prevTime)
							/ msecPerSec);
				}
				mAntManager.powerWheelCnts.prevTime = mAntManager.powerWheelCnts.currTime;
				// If gps location not current and we don't have a speed sensor,
				// or if power wheel is calibrated and we don't have a
				// calibrated speed sensor,
				// use power wheel sensor to measure distance and ride time.
				// Copy values over to GPS Trip distance and ride time
				boolean condition1 = !gpsLocationCurrent && !myBikeStat.hasSpeedSensor;
				boolean condition2 = myBikeStat.hasPowerSpeed && !myBikeStat.hasSpeed;
				if ((condition1 || condition2) && !writingTrackRecord) {
					myBikeStat.setGPSTripDistance(myBikeStat.getPowerWheelTripDistance());
					myBikeStat.setGPSTripTime(myBikeStat.getPowerWheelRideTime());
					myBikeStat.setWheelTripDistance(myBikeStat.getPowerWheelTripDistance());
					myBikeStat.setWheelRideTime(myBikeStat.getPowerWheelRideTime());
				}
			}
		});
		// have to subscribe to calibrated speed, even tho' we're not sure if
		// wheel is calibrated yet
		subscribeCalibratedPowerSpeed();
	}

	private void openReopenTCX_FIT() {
		if (debugAppState) Log.i(this.getClass().getName(), "openReopenTCX_FIT()");
		//called from onLocationChanged, when firstLocation is true,
		// and in writeTrackRecord() if an error occurred
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		myBikeStat.fitLog.purgeSmallActivityFiles(myBikeStat, settings.getString(KEY_CHOSEN_TCXFILE, ""));
		boolean old = myBikeStat.tcxLog.readTCXFileLastModTime(myBikeStat.tcxLog.outFileName, getTCXFileAutoReset());
		myBikeStat.tcxLog.outFileFooterLength = settings.getInt(TCX_LOG_FILE_FOOTER_LENGTH, 1);
		// open a new tcx file if the previous one is old, we force a new one
		// thru reset, or loading a new route and clearing data, or the file was
		// not found when testing last modified date
		if (old || forceNewTCX_FIT || !myBikeStat.tcxLog.getError().equals("")) {
			if (debugAppState) Log.i(this.getClass().getName(), "openReopenTCX_FIT() - file old or forceNew");
			// compose filename using current date-time
			// need to do this before calling .fitLog.openNewFIT,
			// because the .fit file has the same name
			resetData();
			forceNewTCX_FIT = false;
			myBikeStat.tcxLog.outFileName = myBikeStat.tcxLog.composeTCXFileName();
			myBikeStat.tcxLog.openNewTCX(myBikeStat, myNavRoute);
			new OpenNewFitFileBackground().execute();
		} else {
			if (debugAppState) Log.i(this.getClass().getName(), "openReopenTCX_FIT() - file not old & not forceNew");
			// not old and not forceNewTCX, so re-open tcx & fit
			// restore outfilefooterlength before re-opening
			myBikeStat.tcxLog.reopenTCX(myBikeStat, myNavRoute);
			// re-open the fit file
			new ReopenFitFileBackground().execute();
		}
		editor.putString(TCX_LOG_FILE_NAME, myBikeStat.tcxLog.outFileName);
		editor.putInt(TCX_LOG_FILE_FOOTER_LENGTH, myBikeStat.tcxLog.outFileFooterLength).apply();
	}


	private void trackingPower() {
		if (debugMDS) {Log.i(this.getClass().getName(), "trackingPower()");}
		if (powerPcc != null) {
			subscribeToPowerEvents();
			ContentValues content = new ContentValues();
			content.put(DB_KEY_DEV_NUM, powerPcc.getAntDeviceNumber());
			content.put(DB_KEY_DEV_TYPE, DeviceType.BIKE_POWER.getIntValue());
			content.put(DB_KEY_SEARCH_PRIORITY, 1);
			content.put(DB_KEY_ACTIVE, 1);
			mAntManager.updateActiveDBDeviceData(powerPcc.getAntDeviceNumber(), content);
			mAntManager.resetSearchPriority(powerPcc.getAntDeviceNumber(), DeviceType.BIKE_POWER);
			updateDBTracking(content);
		}
		myBikeStat.hasPower = true;
		// time-tag sensor start for ride-time calculation
		mAntManager.powerWheelCnts.prevTime = SystemClock.elapsedRealtime();
		myBikeStat.hasPowerSpeed = mAntManager.powerWheelCnts.isCalibrated;
	}

	/**
	 * When tracking a new device, update the data base about the new device
	 * status and also change the search priority of the other devices of the
	 * same type. Put this in a new Thread to prevent blocking UI thread.
	 *
	 * @param content is data about the device
	 */
	private void updateDBTracking(final ContentValues content) {
		if (debugMDS) {Log.i(this.getClass().getName(), "updateDBTracking()");}
		new Thread(new Runnable() {
			@Override
			public void run() {
				// update data and reset search priority in antDBDeviceList
				dataBaseAdapter.addDeviceToDB(content);
				// resetSearchPriority				
				dataBaseAdapter.resetSearchPriorityFromAcq(content.getAsInteger(DB_KEY_DEV_NUM),
						content.getAsInteger(DB_KEY_DEV_TYPE));
			}
		}).start();
	}

	private void notTrackingPower() {
		if (debugMDS) {Log.i(this.getClass().getName(), "notTrackingPower()");}
		myBikeStat.hasPower = false;
		myBikeStat.hasPowerSpeed = false;
		myBikeStat.hasPowerSpeedSensor = false;
		myBikeStat.hasPowerCadence = false;
		refreshPower();
		myBikeStat.setPower(0);
		releasePowerPcc();
	}

	private void trackingSpeed() {
		if (debugMDS) {Log.i(this.getClass().getName(), "trackingSpeed()");}
		if (speedPcc != null) {
			ContentValues content = new ContentValues();
			int deviceType;
			content.put(DB_KEY_DEV_NUM, speedPcc.getAntDeviceNumber());
			content.put(DB_KEY_SEARCH_PRIORITY, 1);
			content.put(DB_KEY_ACTIVE, 1);
			if (speedPcc.isSpeedAndCadenceCombinedSensor()) {
				deviceType = DeviceType.BIKE_SPDCAD.getIntValue();
				// Having this mirrored in trackingCad() lets us pair a
				// speed-cadence sensor in either speed or cadence dialog
				// If this came from the doSearchPair dialog,
				// just assign the deviceNumber to Speed channel & cadence channel.
				// The cadence channel will be opened later under subscribeSpeedEvents()
			} else {
				deviceType = DeviceType.BIKE_SPD.getIntValue();
			}
			content.put(DB_KEY_DEV_TYPE, deviceType);
			// just update the content
			mAntManager.updateActiveDBDeviceData(speedPcc.getAntDeviceNumber(), content);
			mAntManager.resetSearchPriority(speedPcc.getAntDeviceNumber(), DeviceType.BIKE_SPD);
			updateDBTracking(content);
		}
		// time-tag sensor start for ride-time calculation
		mAntManager.wheelCnts.prevTime = SystemClock.elapsedRealtime();
		// we don't claim .hasSpeed until the wheel is calibrated
		myBikeStat.hasSpeed = mAntManager.wheelCnts.isCalibrated;
		// wait until we get speed data before claiming we have the speed sensor
	}

	private void notTrackingSpeed() {
		if (debugMDS) {Log.i(this.getClass().getName(), "notTrackingSpeed()");}
		myBikeStat.hasSpeedSensor = false;
		myBikeStat.hasSpeed = false;
		refreshSpeed();
		releaseSpeedPcc();
	}

	private void trackingCad() {
		if (debugMDS) {Log.i(this.getClass().getName(), "trackingCad()");}
		ContentValues content = new ContentValues();
		int deviceType;
		myBikeStat.hasCadence = true;
		if (cadPcc != null) {
			content.put(DB_KEY_DEV_NUM, cadPcc.getAntDeviceNumber());
			content.put(DB_KEY_SEARCH_PRIORITY, 1);
			content.put(DB_KEY_ACTIVE, 1);
			if (cadPcc.isSpeedAndCadenceCombinedSensor()) {
				deviceType = DeviceType.BIKE_SPDCAD.getIntValue();
				// Having this mirrored in trackingSpeed() lets us pair a
				// speed-cadence sensor in either speed or cadence dialog
				// If this came from the doSearchPair dialog, just assign 
				// the deviceNumber to Speed channel & cadence channel. 
				// The speed channel will be opened later under subscribeCadEvents()
			} else {
				deviceType = DeviceType.BIKE_CADENCE.getIntValue();
			}
			content.put(DB_KEY_DEV_TYPE, deviceType);
			// otherwise, just update the contentmAntManager.updateActiveDBDeviceData(cadPcc.getAntDeviceNumber(), content);
			mAntManager.resetSearchPriority(cadPcc.getAntDeviceNumber(), DeviceType.BIKE_CADENCE);
			updateDBTracking(content);
		}
	}

	private void notTrackingCad() {
		if (debugMDS) {Log.i(this.getClass().getName(), "notTrackingCad()");}
		myBikeStat.hasCadence = false;
		myBikeStat.setCadence(0);
		refreshCadence();
		releaseCadPcc();
	}

	private void trackingHRM() {
		if (debugMDS) {Log.i(this.getClass().getName(), "trackingHRM()");}
		if (hrPcc != null) {
			subscribeToHrEvents();
			ContentValues content = new ContentValues();
			content.put(DB_KEY_DEV_NUM, hrPcc.getAntDeviceNumber());
			content.put(DB_KEY_DEV_TYPE, DeviceType.HEARTRATE.getIntValue());
			content.put(DB_KEY_SEARCH_PRIORITY, 1);
			content.put(DB_KEY_ACTIVE, 1);
			// otherwise, just update the content
			mAntManager.updateActiveDBDeviceData(hrPcc.getAntDeviceNumber(), content);
			mAntManager.resetSearchPriority(hrPcc.getAntDeviceNumber(), DeviceType.HEARTRATE);
			updateDBTracking(content);
		}
		myBikeStat.hasHR = true;
	}

	private void notTrackingHRM() {
		if (debugMDS) {Log.i(this.getClass().getName(), "notTrackingHRM()");}
		myBikeStat.hasHR = false;
		refreshHR();
		releaseHrPcc();
	}

	/**
	 * The ANT Radio Service needs to be the latest version to use the ANT+
	 * plug-ins If a request-access {other failure} or {Unrecognized} result is
	 * received have the user go to the play store to upgrade ANT Radio
	 */
	private void goUpgradeANTRadioService() {
		AlertDialog.Builder adlgBldr = new AlertDialog.Builder(
				MainActivity.this);
		adlgBldr.setTitle(getString(R.string.missing_service));
		adlgBldr.setMessage(getString(R.string.couldn_t_access_the_)
				+ ANTplus
				+ getString(R.string._plug_in_service_perhaps_the_ant_radio_service_needs_to_be_upgraded_)
				+ "\n"
				+ getString(R.string.do_you_want_to_launch_the_play_store_to_get_it_));
		adlgBldr.setCancelable(true);
		adlgBldr.setPositiveButton(getString(R.string.go_to_store),
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent startStore = new Intent(
								Intent.ACTION_VIEW,
								Uri.parse("market://details?id="
										+ AntPlusBikeCadencePcc
										.getMissingDependencyPackageName()));
						startStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						MainActivity.this.startActivity(startStore);
					}
				});
		adlgBldr.setNegativeButton(getString(R.string.cancel),
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		adlgBldr.show();
	}

	/**
	 * Need to have ANT+ plug-ins installed If a request-access
	 * {DEPENDENCY_NOT_INSTALLED} result is received have the user go to the
	 * play store to get the ANT+ plug-ins
	 */
	private void goGetANTPlugins() {
		AlertDialog.Builder adlgBldr = new AlertDialog.Builder(
				MainActivity.this);
		adlgBldr.setTitle(getString(R.string.missing_service));
		adlgBldr.setMessage(getString(R.string.couldn_t_find_the_)
				+ ANTplus
				+ getString(R.string._plug_in_service_)
				+ "\n"
				+ getString(R.string.do_you_want_to_launch_the_play_store_to_get_it_));
		adlgBldr.setCancelable(true);
		adlgBldr.setPositiveButton(getString(R.string.go_to_store),
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent startStore = new Intent(
								Intent.ACTION_VIEW,
								Uri.parse("market://details?id="
										+ AntPluginPcc
										.getMissingDependencyPackageName()));
						startStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(startStore);
					}
				});
		adlgBldr.setNegativeButton(getString(R.string.cancel),
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		adlgBldr.show();
	}

	private void viewToast(final String toastText, final int yOffset,
			final int gravity, final View view, final int color) {
		if (MainActivity.this.isFinishing()) {
			return;
		}
		Log.i(this.getClass().getName(), "showing toast");
		view.post(new Runnable() {
			@Override
			public void run() {
				int loc[] = new int[2];
				view.getLocationOnScreen(loc);
				Toast toast = Toast.makeText(antToastAnchor.getContext(),
						toastText, Toast.LENGTH_SHORT);
				toast.setGravity(gravity, 0, loc[1] + yOffset);
				TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
				v.setTextColor(color);
				toast.show();
			}
		});
	}

	MultiDeviceSearch.SearchCallbacks mCallback = new MultiDeviceSearch.SearchCallbacks() {
		@Override
		public void onSearchStarted(MultiDeviceSearch.RssiSupport rssiSupport) {
			if (debugMDS) { Log.w(this.getClass().getName(), "onMDSSearchStarted"); }
			// now it is safe to use UI
			mAntManager.setIsMDSStarting(false);
		}

		@Override
		public void onDeviceFound(com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch.MultiDeviceSearchResult multiDeviceSearchResult) {
			addDeviceToActiveList(multiDeviceSearchResult);
		}

		@Override
		public void onSearchStopped(RequestAccessResult requestAccessResult) {
			if (debugMDS) {Log.w(this.getClass().getName(), "onMDSSearchStopped - reason: " + requestAccessResult);}
		}
	};

	/**
	 * Start the Multi-deviceSearch from the autoConnect TimerTask if conditions allow.
	 * Only look for devices that are not tracking already
	 * Condition for starting the search, like "3 minutes after restarting from paused",
	 * or "3 minutes after a Pcc has stopped tracking"
	 * forceMDSStartTime is set in testPaused and deviceStateChange receivers for case DEAD
	 */
	private void startMultiDeviceSearch() {

		long deltaTime = System.currentTimeMillis() - mAntManager.getForceMDSStartTime();
		if (debugMDS) { Log.i(this.getClass().getName(), "forceMDS delta: " + String.format(FORMAT_3_1F, deltaTime/1000.) + " sec"
				+ " num MDS cycles: " + mAntManager.getNumMDSSearchCycles());}

		if (!useANTData || (deltaTime > THREE_MINUTES) || !pm.isScreenOn() || mAntManager.getNumMDSSearchCycles()>=NUM_MDSCYCLES){
			return;
		}
		if (debugMDS) { Log.w(this.getClass().getName(), "onMDSSearchStarting"); }
		mAntManager.setIsMDSStarting(true);
		// if MDS is starting, don't refresh values in UI like speed, cadence
		// don't refresh turn-by-turn list or respond to new locations
		// MDS starting only lasts a short while
		EnumSet<DeviceType> devices = EnumSet.noneOf(DeviceType.class);
		// add sensor types, don't reset active status when pairing
			devices.add(DeviceType.BIKE_CADENCE);
		if (!mAntManager.isPairing()) {
			mAntManager.resetActiveStatusByType(DeviceType.BIKE_CADENCE.getIntValue());
		}
			devices.add(DeviceType.BIKE_POWER);
		if (!mAntManager.isPairing()) {
			mAntManager.resetActiveStatusByType(DeviceType.BIKE_POWER.getIntValue());
		}

			devices.add(DeviceType.BIKE_SPD);
			devices.add(DeviceType.BIKE_SPDCAD);
		if (!mAntManager.isPairing()) {
			mAntManager.resetActiveStatusByType(DeviceType.BIKE_SPD.getIntValue());
			mAntManager.resetActiveStatusByType(DeviceType.BIKE_SPDCAD.getIntValue());
		}
			devices.add(DeviceType.HEARTRATE);
		if (!mAntManager.isPairing()) {
			mAntManager.resetActiveStatusByType(DeviceType.HEARTRATE.getIntValue());
		}

		// anything that uses UI thread or may block UI thread should return if MDS is starting
		mSearch = new MultiDeviceSearch(context, devices, mCallback);
	}

	private void stopMultiDeviceSearch() {
		if (mSearch != null) mSearch.close();
	}

	/**
	 * Decide what to do when we've found a new device during an MDS scan: add to database list, add to other list
	 * or just update the active status. autoConnect will look at the active status to choose which device to connect to
	 * Search/Pair will look at both lists and offer the user a list to connect to
	 *
	 * @param deviceInfo result of MDS Scan controller detecting a device
	 */
	private void addDeviceToActiveList(com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch.MultiDeviceSearchResult deviceInfo) {
		if (debugMDS) {Log.i(this.getClass().getName(), "onDeviceFound() - device# " + deviceInfo.getAntDeviceNumber());}
		int devNumber = deviceInfo.getAntDeviceNumber();
		ContentValues content = new ContentValues();
		content.put(DB_KEY_ACTIVE, 1);
		content.put(DB_KEY_DEV_TYPE, deviceInfo.getAntDeviceType().getIntValue());
		content.put(DB_KEY_DEV_NUM, devNumber);
		ActiveANTDeviceData newDevice = new ActiveANTDeviceData(deviceInfo);
		newDevice.setData(content);
		newDevice.setDeviceNum(devNumber);
		// if device is in DBActiveList updateDBDeviceData
		// else if device is in DeviceInOtherActiveList, update OtherDeviceData
		// else if device is in DB, .addToANTDBDeviceList(newDevice)
		// else mAntManager.addToANTOtherDeviceList(newDevice);
		if (mAntManager.isDeviceInDBActiveList(devNumber)) {
			if (debugMDS) { Log.i(this.getClass().getName(), "updating DB active list - device# " + devNumber);}
			mAntManager.setDevNumberActiveStatus(devNumber, 1);
			// have to update deviceInfo, because when we created the list, we had deviceInfo as null
			mAntManager.setDeviceInfo(devNumber, deviceInfo);
		} else if (mAntManager.isDeviceInOtherActiveList(devNumber)) {
			//update active status if device was already in the list, but we've reset active status
			if (debugMDS) {Log.i(this.getClass().getName(), "updating active other list - device# " + devNumber);}
			mAntManager.updateActiveOtherDeviceData(devNumber, content);
		} else if (dataBaseAdapter.isDeviceInDataBase(devNumber)){
			// since we've added all DB devices during loadAntConfig, this will happen if we find a
			// device for the first time that's not in the DB, or if we couldn't add DB devices
			// get the search priority from database
			int searchPriority = dataBaseAdapter.getSearchPriorityFromDevNum(devNumber);
			content.put(DB_KEY_SEARCH_PRIORITY, searchPriority);
			String devName = dataBaseAdapter.getDevNameFromDevNum(devNumber);
			content.put(DB_KEY_DEV_NAME, devName);
			newDevice.setData(content);
			if (debugMDS) { Log.i(this.getClass().getName(), "adding to DB active list - device# " + devNumber
					+ " search priority: " + searchPriority);}
			mAntManager.addToANTDBDeviceList(newDevice);
		} else {
			content.put(DB_KEY_SEARCH_PRIORITY, 99);
			newDevice.setData(content);
			if (debugMDS) {Log.i(this.getClass().getName(), "adding to Other active list - device# " + devNumber);}
			mAntManager.addToANTOtherDeviceList(newDevice);
		}
	}

	private void autoConnectHRM(final com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch.MultiDeviceSearchResult devInfo) {

		if (hrPcc != null && hrPcc.getCurrentDeviceState() == DeviceState.TRACKING) {
			if (debugMDS) Log.i(this.getClass().getName(), "autoConnectHRM(): return (Tracking Device)");
			return;
		}
		if (mAntManager.isPairing()) {
			if (debugMDS) Log.i(this.getClass().getName(), "autoConnectHRM(): return (Pairing)");
			return;
		}
		if (devInfo == null) {
			if (debugMDS) Log.i(this.getClass().getName(), "autoConnectHRM(): return (null devInfo)");
			return;
		}
		//Inform the user we are connecting
		hrCell.post(new Runnable() {
			public void run() {
				if (debugMDS) Log.i(this.getClass().getName(), "autoConnectHRM() - requestDeviceAccess()");
				hrmReleaseHandle = AntPlusHeartRatePcc.requestAccess(context, devInfo.getAntDeviceNumber(), 0,
						HR_IPluginAccessResultReceiver, HR_IDeviceStateChangeReceiver);
			}
		});
	}

	private void autoConnectCad(final com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch.MultiDeviceSearchResult devInfo) {

		if (cadPcc != null && (cadPcc.getCurrentDeviceState() == DeviceState.TRACKING
							|| cadPcc.getCurrentDeviceState() == DeviceState.SEARCHING
							|| cadPcc.getCurrentDeviceState() == DeviceState.PROCESSING_REQUEST)) {
			if (debugMDS) Log.i(this.getClass().getName(), "autoConnectCad(): return (Tracking Device)");
			return;
		}
		if (mAntManager.isPairing()) {
			if (debugMDS) Log.i(this.getClass().getName(), "autoConnectCad(): return (Pairing)");
			return;
		}
		if (devInfo == null) {
			if (debugMDS) Log.i(this.getClass().getName(), "autoConnectCad(): return (null devInfo)");
			return;
		}
		//Inform the user we are connecting
		cadCell.post(new Runnable() {
			public void run() {
				if (debugMDS) Log.i(this.getClass().getName(), "autoConnectCad() - requestDeviceAccess()");
				cadReleaseHandle = AntPlusBikeCadencePcc.requestAccess(context, devInfo.getAntDeviceNumber(), 0, false,
						CAD_IPluginAccessResultReceiver, CAD_IDeviceStateChangeReceiver);
			}
		});
	}

	private void autoConnectSpeed(final com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch.MultiDeviceSearchResult devInfo) {
		if (speedPcc != null && (speedPcc.getCurrentDeviceState() == DeviceState.TRACKING
							|| speedPcc.getCurrentDeviceState() == DeviceState.SEARCHING
							|| speedPcc.getCurrentDeviceState() == DeviceState.PROCESSING_REQUEST)) {
			if (debugMDS) Log.i(this.getClass().getName(), "autoConnectSpeed(): return (Tracking Device)");
			return;
		}
		if (mAntManager.isPairing()) {
			if (debugMDS) Log.i(this.getClass().getName(), "autoConnectSpeed(): return (Pairing)");
			return;
		}
		if (devInfo == null) {
			if (debugMDS) Log.i(this.getClass().getName(), "autoConnectSpeed(): return (null devInfo)");
			return;
		}
		//Inform the user we are connecting
		speedCell.post(new Runnable() {
			public void run() {
				if (debugMDS) Log.i(this.getClass().getName(), "autoConnectSpeed() - requestDeviceAccess()");
				speedReleaseHandle = AntPlusBikeSpeedDistancePcc.requestAccess(context, devInfo.getAntDeviceNumber(), 0, false,
						Speed_IPluginAccessResultReceiver, Speed_IDeviceStateChangeReceiver);
			}
		});
	}

	private void autoConnectSC(final com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch.MultiDeviceSearchResult devInfo) {

		if (cadPcc != null && cadPcc.getCurrentDeviceState() == DeviceState.TRACKING) {
			if (debugMDS) Log.i(this.getClass().getName(), "autoConnectSpeedCad(): return (Tracking Device)");
			return;
		}
		if (mAntManager.isPairing()) {
			if (debugMDS) Log.i(this.getClass().getName(), "autoConnectSpeedCad(): return (Pairing)");
			return;
		}
		if (devInfo == null) {
			if (debugMDS) Log.i(this.getClass().getName(), "autoConnectSpeedCad(): return (null devInfo)");
			return;
		}
		//Inform the user we are connecting
		cadCell.post(new Runnable() {
			public void run() {
				if (debugMDS) Log.i(this.getClass().getName(), "autoConnectSpeedCad() - requestDeviceAccess()");
				cadReleaseHandle = AntPlusBikeCadencePcc.requestAccess(context, devInfo.getAntDeviceNumber(), 0, true,
						CAD_IPluginAccessResultReceiver, CAD_IDeviceStateChangeReceiver);
			}
		});
	}// autoConnectSC()

	private void autoConnectPower(final com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch.MultiDeviceSearchResult devInfo) {

		if (powerPcc != null && powerPcc.getCurrentDeviceState() == DeviceState.TRACKING) {
			if (debugMDS) Log.i(this.getClass().getName(), "autoConnectPower(): return (Tracking Device)");
			return;
		}
		if (mAntManager.isPairing()) {
			if (debugMDS) Log.i(this.getClass().getName(), "autoConnectPower(): return (Pairing)");
			return;
		}
		if (devInfo == null) {
			if (debugMDS) Log.i(this.getClass().getName(), "autoConnectPower(): return (null devInfo)");
			return;
		}
		//Inform the user we are connecting
		powerCell.post(new Runnable() {
			public void run() {
				if (debugMDS) Log.i(this.getClass().getName(), "autoConnectPower() - requestDeviceAccess()");
				powerReleaseHandle = AntPlusBikePowerPcc.requestAccess(context, devInfo.getAntDeviceNumber(), 0,
						Pow_IPluginAccessResultReceiver, Pow_IDeviceStateChangeReceiver);
			}
		});
	}

	/**
	 * For each device in the antDBDeviceList, get the content and update the
	 * database. mAntManager doesn't know about the database, so we'll get
	 * content from mAntManager and pass it to ANTDBAdapter
	 * Only call this  in saveState()
	 * Must not call this on the main thread;
	 *
	 * @param string just an indication for debugging as to where we called this method
	 */
	private void updateDBData(String string) {

		int activeListSize = mAntManager.antDBDeviceList.size();
		if (activeListSize == 0) {
			return;
		}
		if (debugMDS)Log.i(this.getClass().getName(), "update DB - " + string + " activeListSize: " + activeListSize);
		for (int index = 0; index < activeListSize; index++) {
			ActiveANTDeviceData deviceData = mAntManager.getActiveDBDeviceData(index);
			if (debugMDS) Log.i(this.getClass().getName(), "deviceData - devNum: " + deviceData.getDeviceNum());
			dataBaseAdapter.updateDeviceRecord(deviceData.getDeviceNum(), deviceData.getData());
		}
	}

	TimerTask testLocationCurrent;
	final Handler locationWatchdogHandler = new Handler();
	Timer locationWatchdogTimer = new Timer();

	/**
	 * a watchdog timer to check if location is current also check calibration
	 * of speed sensors, and decide whether to reset GPS ephemeris data
	 */
	private void startLocationWatchdog() {
		testLocationCurrent = new TimerTask() {
			@Override
			public void run() {
				locationWatchdogHandler.post(new Runnable() {
					@Override
					public void run() {
						turnByturnList.post(new Runnable() {

							@Override
							public void run() {
								checkLocCurrent();
                                writeAppMessage("", res_white);
								// check if ANT speed sensor is calibrated
								calWheel();
								calPowerWheel();
								// If it's taking too long to acquire satellites, try resetting the GPS
								remedyBadGPSAlmanac();
							}

							private void checkLocCurrent() {
								// used to indicate loss of GPS location data in
								// the display speed will read XX.x, distance to way points will show ??
								if (trainerMode) {
									gpsLocationCurrent = false;
									return;
								}
								gpsLocationCurrent = true;
								if ((SystemClock.elapsedRealtime() - newLocSysTimeStamp) > TEN_SEC) {
									gpsLocationCurrent = false;
									myBikeStat.gpsSpeedCurrent = false;
									refreshScreen();
									myBikeStat.setSpeed(trainerMode);
									refreshSpeed();
									// if location not current some time during wheel cal, start calibration over again
									// so set startDist to current trip distance, zero-out wheel total counts
									if (!mAntManager.wheelCnts.isCalibrated) {
										if (debugWheelCal) {Log.i(this.getClass().getName(),
													"Location not current; restarting wheelCal"); }
										mAntManager.restartWheelCal(myBikeStat.getWheelTripDistance());
									}
									if (!mAntManager.powerWheelCnts.isCalibrated) {
										if (debugPowerWheelCal) {Log.i(this.getClass().getName(),
													"Location not current; restarting powerWheelCal"); }
										mAntManager.restartPowerWheelCal(myBikeStat.getWheelTripDistance());
									}
								}
							}// checkLocCurrent()

							void calWheel() {
								if (mAntManager.wheelCnts.isCalibrated
										|| speedPcc == null
										|| (speedPcc.getCurrentDeviceState() != DeviceState.TRACKING)
										|| !mAntManager.wheelCnts.isDataCurrent
										|| (mAntManager.wheelCnts.calTotalCount == 0)
										|| (myBikeStat.getGPSTripDistance() - mAntManager.wheelCnts.calGPSStartDist) < MIN_CAL_DIST
										|| trainerMode) {
									return;
								}
								// the startDist accounts for non-zero distance
								// when the Speed channel is opened and accumulating wheel counts
								double wheelCircum = (myBikeStat
										.getGPSTripDistance() - mAntManager.wheelCnts.calGPSStartDist)
										/ mAntManager.wheelCnts.calTotalCount;
								// handle wheelCircum out of limits = calib. failure
								// should be 2.140 for 25 mm x 700c wheels
								// start over
								if ((wheelCircum > UPPER_WHEEL_CIRCUM)
										|| (wheelCircum < LOWER_WHEEL_CIRCUM)) {
									mAntManager.restartWheelCal(myBikeStat.getWheelTripDistance());
									return;
								}
								mAntManager.wheelCnts.wheelCircumference = wheelCircum;
								mAntManager.wheelCnts.isCalibrated = true;
								myBikeStat.hasSpeed = true;
								// When subscribing to calculated speed events
								// initially, the wheel was not calibrated.
								// Now must unsubscribe to speed events, then
								// re-subscribe with calibrated wheel circumference
								speedPcc.subscribeCalculatedSpeedEvent(null);
								subscribeCalibratedSpeed();
								// force refreshTitles() to show "cal"
								refreshTitles();
							}// calWheel()

							void calPowerWheel() {
								if (mAntManager.powerWheelCnts.isCalibrated
										|| powerPcc == null
										|| (powerPcc.getCurrentDeviceState() != DeviceState.TRACKING)
										|| !mAntManager.powerWheelCnts.isDataCurrent
										|| (mAntManager.powerWheelCnts.calTotalCount == 0)
										|| (myBikeStat.getGPSTripDistance() - mAntManager.powerWheelCnts.calGPSStartDist) < MIN_CAL_DIST
										|| trainerMode) {
									return;
								}
								// the startDist accounts for non-zero distance
								// when the power channel is opened and accumulating wheel counts
								double wheelCircum = (myBikeStat
										.getGPSTripDistance() - mAntManager.powerWheelCnts.calGPSStartDist)
										/ mAntManager.powerWheelCnts.calTotalCount;
								// handle wheelCircum out of limits = calib. failure
								// should be 2.160 for 25 mm x 700c wheels start over
								if ((wheelCircum > UPPER_WHEEL_CIRCUM)
										|| (wheelCircum < LOWER_WHEEL_CIRCUM)) {
									mAntManager.restartPowerWheelCal(myBikeStat.getWheelTripDistance());
									return;
								}
								mAntManager.powerWheelCnts.wheelCircumference = wheelCircum;
								mAntManager.powerWheelCnts.isCalibrated = true;
								myBikeStat.hasPowerSpeed = true;
								// When subscribing to calculated speed events
								// initially, the wheel was not calibrated.
								// Now must unsubscribe to wheel speed event,
								// then re-subscribe with calibrated wheel circumference
								powerPcc.subscribeCalculatedWheelSpeedEvent(null);
								subscribeCalibratedPowerSpeed();
								// force refreshTitles() to show "cal"
								refreshTitles();
							}// calPowerWheel()

							/**
							 * If locationCurrent or GPS acquisition time < 3 minutes, or we've recently reset the GPS almanac,
							 * return. Otherwise, reset the ephemeris data save the time of reset in Shared Preferences
							 */
							private void remedyBadGPSAlmanac() {
								// if no Location permission return
								if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
										!= PackageManager.PERMISSION_GRANTED) {
									return;
								}
								// don't reset the almanac if location is current
								if (gpsLocationCurrent
										// don't reset the almanac if gps not enabled yet...
										|| !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
										// don't reset the almanac if initial acquisition hasn't yet taken too long
										|| (gpsFirstLocation
										&& (SystemClock.elapsedRealtime() - gpsAcqSysTimeStamp) < THREE_MINUTES
										// don't reset the almanac if  gps hasn't dropped-out for 3  minutes
										|| (!gpsFirstLocation && (SystemClock.elapsedRealtime() - newLocSysTimeStamp) < THREE_MINUTES)
										// don't reset the almanac if already reset the almanac within the past day
										|| (SystemClock.elapsedRealtime() - almanacResetTime) < TWENTYFOUR_HOURS)) {
									return;
								}
								Bundle bundle = new Bundle();
								if (locationManager.sendExtraCommand(
										LocationManager.GPS_PROVIDER, "delete_aiding_data", null)
										& locationManager.sendExtraCommand(
										LocationManager.GPS_PROVIDER, "force_xtra_injection", bundle)
										& locationManager.sendExtraCommand(
										LocationManager.GPS_PROVIDER, "force_time_injection", bundle)) {
									// write app message about resetting ephemeris
									writeAppMessage("Resetting GPS Ephemeris", res_white);
									if (debugGPSAlmanac) {
										Log.i(this.getClass().getName(), "remedyBadGPSAlmanac()");
										Log.i(this.getClass().getName(), bundle.toString());
										Log.i(this.getClass().getName(), "old almanac reset-time: " + (almanacResetTime));
									}
									almanacResetTime = SystemClock.elapsedRealtime();
								}
							}// remedyBadGPSAlmanac
						});// post(Runnable)
					}
				});// locationWatchdog Runnable
			}
		};// TimerTask()
		locationWatchdogTimer.schedule(testLocationCurrent, 1050, FIVE_SEC);
	}

	public void stopLocationWatchdog() {
        if (debugAppState) Log.i(this.getClass().getName(), "stopping LOcation Watchdog");
		locationWatchdogHandler.removeCallbacksAndMessages(null);
		if (testLocationCurrent != null) {
			testLocationCurrent.cancel();
		}
	}

	TimerTask autoConnectAnt;
	final Handler autoConnectAntHandler = new Handler();
	Timer autoConnectAntTimer = new Timer();

	/**
	 * A watchdog timer to connect ANT devices. Start and stop the MultiDeviceSearch, then
	 * try to connect to devices found. Timer runs every minute from OnCreate until onDestroy
	 */
	public void startAutoConnectANT() {
		autoConnectAnt = new TimerTask() {

			@Override
			public void run() {

				autoConnectAntHandler.post(new Runnable() {
					@Override
					public void run() {
						MainActivity.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								startMultiDeviceSearch();
							}
						});
					}
				});
				// let the MDS run for ten seconds, then stop it. Can't autoConnect devices with MDS running
				autoConnectAntHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						MainActivity.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								stopMultiDeviceSearch();
							}
						});
					}
				}, TEN_SEC);
				// can't autoConnect to devices while MDS is running, wait a couple seconds for MDS to stop
				cadCell.postDelayed(new Runnable() {
					@Override
					public void run() {
						autoConnectSC(mAntManager.getActiveDevInfoByType(DeviceType.BIKE_SPDCAD));
					}
				}, TEN_SEC + 2 * ONE_SEC);
				hrCell.postDelayed(new Runnable() {
					@Override
					public void run() {
						autoConnectHRM(mAntManager.getActiveDevInfoByType(DeviceType.HEARTRATE));
					}
				}, TEN_SEC + 3 * ONE_SEC);
				powerCell.postDelayed(new Runnable() {
					@Override
					public void run() {
						autoConnectPower(mAntManager.getActiveDevInfoByType(DeviceType.BIKE_POWER));
					}
				}, TEN_SEC + 4 * ONE_SEC);
				cadCell.postDelayed(new Runnable() {
					@Override
					public void run() {
						autoConnectCad(mAntManager.getActiveDevInfoByType(DeviceType.BIKE_CADENCE));
					}
				}, TEN_SEC + 5 * ONE_SEC);
				speedCell.postDelayed(new Runnable() {
					@Override
					public void run() {
						autoConnectSpeed(mAntManager.getActiveDevInfoByType(DeviceType.BIKE_SPD));
					}
				}, TEN_SEC + 6 * ONE_SEC);
			}
		};
		autoConnectAntTimer.schedule(autoConnectAnt, ONE_SEC, THIRTY_SEC);
	}

	public void stopAutoConnectAnt() {
		autoConnectAntHandler.removeCallbacksAndMessages(null);
		if (autoConnectAnt != null) {
			autoConnectAnt.cancel();
		}
	}

	TimerTask testSensorData;
	final Handler sensorWatchdogHandler = new Handler();
	Timer sensorWatchdogTimer = new Timer();

	/**
	 * A watchdog timer to check if sensor data is current; also detect faulty
	 * PowerTap
	 */
	public void startSensorWatchdog() {
		testSensorData = new TimerTask() {
			@Override
			public void run() {

				sensorWatchdogHandler.post(new Runnable() {

					@Override
					public void run() {
						if (mAntManager.isMDSStarting()) {
							if (debugMDS) Log.e(this.getClass().getName(), "couldn't do SensorWatchdog(), MDSStarting");
							return;
						}
						if (debugMDS) Log.i(this.getClass().getName(), "Sensor Watchdog");
						long currentTime = SystemClock.elapsedRealtime();
						// ert put in .currTime in RawSpeed, calculatedSpeed, calcPower, rawPower, and HR
						mAntManager.hrData.isDataCurrent = ((currentTime - mAntManager.hrData.currTime) < THREE_SEC);
						if (!mAntManager.hrData.isDataCurrent) {
							refreshHR();
						}// HR !current

						mAntManager.wheelCnts.isDataCurrent = ((currentTime - mAntManager.wheelCnts.currTime) < THREE_SEC);
						// alert BikeStatRow if data not current; bikeStatRow will look for a data source
						myBikeStat.setSensorSpeedCurrent(mAntManager.wheelCnts.isDataCurrent);
						if ((!mAntManager.wheelCnts.isDataCurrent && myBikeStat.hasSpeed)) {
							//Log.d(this.getClass().getName(), "wheel counts !current");
							// Wheel stopped turning, set sensor speed to 0.
							myBikeStat.setSensorSpeed(0.);
							// this is okay, only called rarely
							myBikeStat.setSpeed(trainerMode);
							refreshSpeed();
						}// speed !current

						mAntManager.pedalCadenceCnts.isDataCurrent = ((currentTime - mAntManager.pedalCadenceCnts.currTime) < THREE_SEC);
						// set cadence display to 0 if data not current
						if (!mAntManager.pedalCadenceCnts.isDataCurrent) {
							//Log.d(this.getClass().getName(), "pedal cadence counts !current");
							myBikeStat.setPedalCadence(0);
							refreshCadence();
						}// cadence !current

						mAntManager.calcPowerData.isDataCurrent =
								((currentTime - mAntManager.calcPowerData.currTime) < THREE_SEC);
						mAntManager.rawPowerData.isDataCurrent =
								((currentTime - mAntManager.rawPowerData.currTime) < THREE_SEC);
						if (!mAntManager.calcPowerData.isDataCurrent
								&& !mAntManager.rawPowerData.isDataCurrent) {
							// Stages keeps sending the last power value when coasting;
							// Set power to 0 if last value is > 3 seconds old
							// We use the instantaneous power if calc power not current
							myBikeStat.setPower(0);
							myBikeStat.setRawPower(0);
							myBikeStat.setPrevPower(0);
							myBikeStat.setPrevRawPower(0);
							refreshPower();
						}// calcPowerData !current

						mAntManager.crankCadenceCnts.isDataCurrent =
								((currentTime - mAntManager.crankCadenceCnts.currTime) < THREE_SEC);
						// Stages keeps sending the last cadence value when coasting;
						// Set cadence to 0 if last value is > 3 seconds old
						if (!mAntManager.crankCadenceCnts.isDataCurrent && myBikeStat.hasPowerCadence) {
							myBikeStat.setPowerCadence(0);
							refreshCadence();
						}// powerCadence !current

						mAntManager.powerWheelCnts.isDataCurrent =
								((currentTime - mAntManager.powerWheelCnts.currTime) < THREE_SEC);
						myBikeStat.setPowerSpeedCurrent(mAntManager.powerWheelCnts.isDataCurrent);
						// alert BikeStatRow if data not current; bikeStatRow will look for a data source
						if (!mAntManager.powerWheelCnts.isDataCurrent && myBikeStat.hasPowerSpeed) {
							// Power wheel stopped turning, set power sensor speed to 0.
							// When gps decides we've paused, the speed display will go to zero
							myBikeStat.setPowerSpeed(0.);
							// this is okay, only called rarely
							myBikeStat.setSpeed(trainerMode);
							refreshSpeed();
						}// powerWheel speed !current

						// if PowerTap battery weak, data still reported, but values are zero
						detectFaultyPowerTap();
						// detect odd SpeedSensor values; Garmin accelerometer sometimes reports low values
						detectFaultyAccelSpeedSensor();
						// if no GPS locations, can check if we're paused using wheel and power wheel sensors
						testZeroPaused();
					}

					/**
					 * If Garmin speed sensor battery weak it sends out faulty "data"
					 * where speed is too low. This can cause a 'paused' condition,
					 * which requires a calibrated speed sensor. Check for low speed
					 * values against GPS; if different, set speed sensor to "uncalibrated"
					 */
					private void detectFaultyAccelSpeedSensor() {
						// do nothing if there is no calibrated speed sensor
						if (!myBikeStat.hasSpeed) {
							return;
						}
						final double faultSpeed = 2.2352;// 5 mph in mps
						// current, gps speed > 5mph and current calibrated
						// speed sensor < .1
						boolean faultCondition = gpsLocationCurrent
								&& (myBikeStat.getGpsSpeed() > faultSpeed)
								&& mAntManager.wheelCnts.isDataCurrent
								&& (myBikeStat.getSensorSpeed() < 0.1);
						if (faultCondition) {
							myBikeStat.hasSpeed = false;
							mAntManager.wheelCnts.isCalibrated = false;
						}
					}

					/**
					 * If PowerTap battery weak it sends out faulty "data" where
					 * speed and power are zero. This can cause a 'paused'
					 * condition if the PowerTap is calibrated. The paused
					 * condition requires a calibrated PowerTap. Check for zero
					 * PowerTap values against GPS or SpeedSensor values; if
					 * different, set PowerTap to "uncalibrated"
					 */
					private void detectFaultyPowerTap() {
						if (!myBikeStat.hasPowerSpeedSensor) {
							return;
						}
						final double faultSpeed = 2.2352;// 5 mph in mps
						// current GPS location, speed > 5mph, and current
						// powerSpeed < .1
						boolean faultCondition = gpsLocationCurrent
								&& (myBikeStat.getGpsSpeed() > faultSpeed)
								&& (myBikeStat.getPowerSpeed() < 0.1)
								&& mAntManager.powerWheelCnts.isDataCurrent;
						if (faultCondition) {
							myBikeStat.hasPowerSpeed = false;
							mAntManager.powerWheelCnts.isCalibrated = false;
						}
					}
				});// sensorWatchdog Runnable
			}
		};// TimerTask()
		sensorWatchdogTimer.schedule(testSensorData, TEN_SEC, THREE_SEC);
	}// startSensorWatchdog()

	public void stopSensorWatchdog() {
        if (debugAppState) Log.i(this.getClass().getName(), "stopping sensor Watchdog");
		sensorWatchdogHandler.removeCallbacksAndMessages(null);
		if (testSensorData != null) {
			testSensorData.cancel();
		}
	}

	private void resetPcc() {
		releaseSpeedPcc();
		releaseCadPcc();
		releaseHrPcc();
		releasePowerPcc();
	}

	/**
	 * releasePcc() is called from onDestroy()
	 */
	private void releaseSpeedPcc() {
		unsubscribeSpeedEvents();
		myBikeStat.hasSpeed = false;
		myBikeStat.hasSpeedSensor = false;
		if (speedReleaseHandle != null) {
			speedReleaseHandle.close();
			speedReleaseHandle = null;
		}
		if (speedPcc != null) {
			speedPcc.releaseAccess();
			speedPcc = null;
		}
	}

	private void releaseCadPcc() {
		unsubscribeCadEvents();
		myBikeStat.hasCadence = false;
		if (cadReleaseHandle != null) {
			cadReleaseHandle.close();
			cadReleaseHandle = null;
		}
		if (cadPcc != null) {
			cadPcc.releaseAccess();
			cadPcc = null;
		}
	}

	private void releaseHrPcc() {
		unsubscribeHrEvents();
		if (hrmReleaseHandle != null) {
			hrmReleaseHandle.close();
			hrmReleaseHandle = null;
		}
		if (hrPcc != null) {
			hrPcc.releaseAccess();
			hrPcc = null;
		}
		myBikeStat.hasHR = false;
	}

	private void releasePowerPcc() {
		//unsubscribePowerEvents();
		if (powerReleaseHandle != null) {
			//Log.i(this.getClass().getName(), "powerReleaseHandle not null");
			powerReleaseHandle.close();
			powerReleaseHandle = null;
		}
		if (powerPcc != null) {
			//Log.i(this.getClass().getName(), "powerPcc not null");
			powerPcc.releaseAccess();
			powerPcc = null;
		}
		myBikeStat.hasPowerCadence = false;
		myBikeStat.hasPower = false;
		myBikeStat.hasPowerSpeed = false;
		myBikeStat.hasPowerSpeedSensor = false;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
			@NonNull String[] permissions,
			@NonNull int[] grantResults) {
		switch (requestCode) {
			case MY_PERMISSIONS_REQUEST_LOCATION: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					startSensors();
                    if (!mLocationHelper.mGoogleApiClient.isConnected()) {
                        // reconnect() will also do .startLocationUpdates() in onConnect() callback
                        mLocationHelper.mGoogleApiClient.reconnect();
                    } else {
                        mLocationHelper.stopLocationUpdates();
                        mLocationHelper.startLocationUpdates(mLocationHelper.createLocationRequest());
                    }
				} else {
					writeAppMessage(getString(R.string.loc_permission_denied), ContextCompat.getColor(context, R.color.gpsred));
				}
                break;
			}
			case MY_PERMISSIONS_REQUEST_WRITE: {
				if (!Utilities.hasStoragePermission(getApplicationContext())){
					writeAppMessage(getString(R.string.write_permission_denied), ContextCompat.getColor(context, R.color.gpsred));
				}
				break;
			}
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
				// other 'case' lines to check for other
				// permissions this app might request
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        if (requestCode == RC_SHOW_FILE_LIST) {
            if (resultCode == RESULT_OK) {
                switch (data.getExtras().getInt(KEY_CHOOSER_CODE)) {
                    case REQUEST_RESOLVE_ERROR:
                        mLocationHelper.mResolvingError = false;
                        mLocationHelper.connectionFailureResult = null;
                        // Make sure the app is not already connected or attempting to connect
                        if (!mLocationHelper.mGoogleApiClient.isConnecting() &&
                                !mLocationHelper.mGoogleApiClient.isConnected()) {
                            mLocationHelper.mGoogleApiClient.connect();
                        }

                        break;
                    case CHOOSER_TYPE_GPX_DIRECTORY:
                        // intent -> start chooser activity
                        Intent loadFileIntent = new Intent(this, ShowFileList.class);
                        //indicate the chooser type is choosing gpx file
                        loadFileIntent.putExtra(CHOOSER_TYPE, ROUTE_FILE_TYPE);
                        loadFileIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(loadFileIntent, RC_SHOW_FILE_LIST);
                        break;
                    case CHOOSER_TYPE_GPX_FILE:
                        prevChosenFile = chosenGPXFile;
                        chosenGPXFile = settings.getString(KEY_CHOSEN_GPXFILE, "");
                        if (!chosenGPXFile.equals("")) {
                            myNavRoute.mChosenFile = new File(chosenGPXFile);
                            //refresh the screen to indicate we've moved out of Chooser
                            refreshScreen();
                            //we're not trying to restore the route and force a new tcx file
                            resumingRoute = false;
                            //load file in async task with progress bar
                            new LoadData().execute(this);
                        }
                        break;
                    case CHOOSER_TYPE_TCX_DIRECTORY:
                        // We don't actually let user choose a different directory when searching for an activity file.
                        // If we did, this is how we would go back to the chooser
                        Intent loadFileIntent1 = new Intent(this, ShowFileList.class);
                        //indicate the chooser type is choosing tcx file
                        loadFileIntent1.putExtra(CHOOSER_TYPE, ACTIVITY_FILE_TYPE);
                        loadFileIntent1.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(loadFileIntent1, RC_SHOW_FILE_LIST);
                        break;
                    case CHOOSER_TYPE_TCX_FILE:
                        String mChosenTCXFile = settings.getString(KEY_CHOSEN_TCXFILE, "");
                        String sharingFileName = mChosenTCXFile;
                        //Log.i(this.getClass().getName(), "onActivityResult()- TCX choice: " + mChosenTCXFile);
                        if (readActivityFileType() == Integer.valueOf(FIT_ACTIVITY_TYPE)) {
                            // Replace the suffix to indicate a fit file instead of a tcx file
                            sharingFileName = myBikeStat.fitLog.delTCXFITSuffix(mChosenTCXFile) + ".fit";
                        }
                        String sharingName_noPath = myBikeStat.fitLog.delTCXFITSuffix(myBikeStat.fitLog.stripFilePath(sharingFileName));
                        if (mustCloseFit(myBikeStat.tcxLog.outFileName, sharingName_noPath)) {
                            // Sharing the current log files, close the activity files before uploading them
                            // Give CFFB the sharing filename so it can pass it on to UploadFileSend when finished closing
                            new CloseFitFileBackground().execute(sharingFileName);
                        } else {
                            uploadFileSend(sharingFileName);
                        }
                        break;
                    default:
                        break;
                }//returned from ShowFileList Activity
            }
        } else if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    // All required changes were successfully made user changed location settings
                    break;
                case Activity.RESULT_CANCELED:
                    // The user was asked to change Location settings, but chose not to
                    break;
                default:
                    break;
            }
        } else if (requestCode == UPLOAD_FILE_SEND_REQUEST_CODE) {
			// This hasn't really finished the sharing operation. The Intent returns after user selects an app to use for sharing
			// We've closed the activity files while sharing. Now re-open them. Now we can re-write track data to the files
			// If we've come back from turning-on WiFi when authorizing StravaShare, go back
			String sharingFileName = settings.getString(KEY_CHOSEN_TCXFILE, "");
			try {
				if ((data != null) && data.hasExtra(AUTH_NO_NETWORK_INTENT_RC)
						&& (resultCode != RESULT_CANCELED)) {
					goBackToStravaShare(resultCode, data, sharingFileName);
				}
			} catch (Exception e) {
				// Dropbox throws a "ClassNotFoundException" here. Just catch it
			}
			sharingFileName = myBikeStat.fitLog.delTCXFITSuffix(myBikeStat.fitLog.stripFilePath(sharingFileName));
			String tcxLogFileName = myBikeStat.fitLog.delTCXFITSuffix(myBikeStat.fitLog.stripFilePath(myBikeStat.tcxLog.outFileName));
			// Can't re-open the current outFile if we're trying to share it, so we have to reset
			// If we're sharing an old file there is no need to reset the current data
			forceNewTCX_FIT = tcxLogFileName.contains(sharingFileName);
			openReopenTCX_FIT();
		} else if (requestCode == RC_ANT_SETTINGS) {

			switch (resultCode) {
				case RESULT_CANCELED:
					break;
				case RESULT_OK:
					switch (data.getExtras().getInt(KEY_CHOOSER_CODE)) {
						case ANTSETTINGS_TYPE_CAL:
							doCalibratePower();
							break;
						case ANTSETTINGS_TYPE_SEARCH_PAIR:
							int deviceType = data.getExtras().getInt(KEY_PAIR_CHANNEL, WILDCARD);
							Log.i(this.getClass().getName(), "doSearchPair() - devType: " + deviceType);
							doSearchPair(deviceType);
							break;
						default:
							break;
					}//switch on ANTSettings request type
					break;//RESULT_OK:
				default:
					break;
			}//switch on result code
		}// returned from ANTSettings Activity
	}

	/**
	 * Test if we have to close the FileEncoder. Only have to close before sharing
	 * if we're sharing the current log file and we're sharing a fit file
	 *
	 * @param tcxLogFileName  current log file
	 * @param sharingFileName file we're sharing
	 * @return true if we have to close FileEncoder
	 */
	private boolean mustCloseFit(String tcxLogFileName, String sharingFileName) {
/*
		Log.w(this.getClass().getName(),"tcxLog: " + tcxLogFileName + "  sharingName: " + sharingFileName);
		Log.w(this.getClass().getName(), " activityType: " + readActivityFileType());
		Log.v(this.getClass().getName(), "mustCloseFit() : "
				+ ((readActivityFileType() == Integer.valueOf(FIT_ACTIVITY_TYPE)
				&& tcxLogFileName.contains(sharingFileName))?"true":"false"));
*/
		return readActivityFileType() == Integer.valueOf(FIT_ACTIVITY_TYPE)
				&& tcxLogFileName.contains(sharingFileName);
	}

	/**
	 * If we needed to Authorize user Strava account, and we didn't have WiFi, user had to go to Settings
	 * Settings intent would return here, so send user back to try Authorize again
	 *
	 * @param resultCode      Intent result: OKAY or CANCELLED
	 * @param data            extras to let us know we were asking for WiFi
	 * @param sharingFileName file to upload to Strava
	 */
	private void goBackToStravaShare(int resultCode, Intent data, String sharingFileName) {
		int extras = 0;
		if ((data != null) && data.hasExtra(AUTH_NO_NETWORK_INTENT_RC)) {
			extras = data.getExtras().getInt(AUTH_NO_NETWORK_INTENT_RC);
		}
		//			Log.i(this.getClass().getName(), "extras: " + extras + " resultCode: " + resultCode);
		if (resultCode == Activity.RESULT_OK && extras == 1) {
			Intent stravaUploadIntent = new Intent(this, StravaShareCBPlus.class);
			stravaUploadIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			stravaUploadIntent.putExtra(UPLOAD_FILENAME, sharingFileName);
			startActivityForResult(stravaUploadIntent, UPLOAD_FILE_SEND_REQUEST_CODE);
		}
	}

	/**
	 * Read user preference for type of activity file
	 *
	 * @return an integer indicating activity file type 0 = .tcx file, 1 = .fit file
	 */
	private int readActivityFileType() {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		String theString = sharedPref.getString(
				getResources().getString(R.string.pref_activity_file_key), TCX_ACTIVITY_TYPE);
		return Integer.parseInt(theString);
	}

	/**
	 * intermediate step to alert user if activity file will be closed
	 * If file to share is not the current output file, just proceed to sharingIntent
	 *
	 * @param uploadFilename user choice of file to share
	 */
	private void uploadFileSend(String uploadFilename) {
		//Log.i(this.getClass().getName(), "uploadFileSend()");
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		boolean showSharingAlert = settings.getBoolean(SHOW_SHARING, true);
		String sharingFileName = myBikeStat.fitLog.delTCXFITSuffix(myBikeStat.fitLog.stripFilePath(chosenTCXFile));
		String tcxLogFileName = myBikeStat.fitLog.delTCXFITSuffix(myBikeStat.fitLog.stripFilePath(myBikeStat.tcxLog.outFileName));
		//Log.v(this.getClass().getName(), "upload onActivityResult() - sharingFileName: " + sharingFileName);
		//Log.v(this.getClass().getName(), "upload onActivityResult() - tcxLogFileName: " + tcxLogFileName);
		if (tcxLogFileName.contains(sharingFileName) && showSharingAlert) {
			// warn user that a new activity will start
			doShowSharingAlert(uploadFilename);
		} else {
			doUploadIntent(uploadFilename);
		}
	}

	/**
	 * Now that we've closed the FileEncoder if we're sharing the current log file
	 * and the user has agreed to restart (if sharing current activity) let user choose how to share the file.
	 * We've made intent filters for RWGPS and Strava that we can intercept, or let user attach file to e-mail
	 * or upload to DropBox, Drive, etc. Those implicit actions are handled by those apps
	 *
	 * @param uploadFilename file to share
	 */
	protected void doUploadIntent(String uploadFilename) {
		// Depending on where we're sending the file either use OAuth, an e-mail intent, etc
		//Log.w(this.getClass().getName(), "now doing upload Intent");
		Uri fileUri = Uri.fromFile(new File(uploadFilename));
		//		Log.i(this.getClass().getName(), fileUri.toString());
		String bodyText = "Uploading new file";
		String subjectText = "new activity file";
		Intent uploadFileIntent;
		uploadFileIntent = new Intent(Intent.ACTION_SEND);
		uploadFileIntent.putExtra(UPLOAD_FILENAME, uploadFilename);
		uploadFileIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		uploadFileIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		uploadFileIntent.putExtra(Intent.EXTRA_EMAIL, RWGPS_EMAIL);
		uploadFileIntent.putExtra(Intent.EXTRA_SUBJECT, subjectText);
		uploadFileIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
		uploadFileIntent.putExtra(Intent.EXTRA_TEXT, bodyText);
		uploadFileIntent.setType("text/cbplustype");
		startActivityForResult(Intent.createChooser(uploadFileIntent, getString(R.string.upload_file)), UPLOAD_FILE_SEND_REQUEST_CODE);
	}

	/**
	 * Give user a chance to cancel sharing because sharing current output file will close that file
	 *
	 * @param uploadFilename activity file to share
	 */
	private void doShowSharingAlert(final String uploadFilename) {
		View checkBoxView = View.inflate(this, R.layout.sharing_checkbox, null);
		CheckBox checkBox = (CheckBox) checkBoxView.findViewById(R.id.checkbox);
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean(SHOW_SHARING, !isChecked).apply();
			}
		});
		checkBox.setText(R.string.dont_remind);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// Set the dialog title
		builder.setTitle(R.string.sharing_alert)
				.setMessage(R.string.sharing_text)
				.setView(checkBoxView)
						// Set the action buttons
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						// User clicked OK, so save the mSelectedItems results somewhere
						// or return them to the component that opened the dialog
						doUploadIntent(uploadFilename);
					}
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
					}
				});
		builder.create().show();
	}
    
	private void exitTrainerMode() {
		trainerMode = false;
		gpsLocationCurrent = false;
		gpsFirstLocation = true;
		stopSpoofingLocations();
		forceNewTCX_FIT = true;
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(KEY_TRAINER_MODE, trainerMode);
		editor.putBoolean(KEY_FORCE_NEW_TCX, forceNewTCX_FIT);
		editor.apply();
		locationWatchdogHandler.post(new Runnable() {
			@Override

			public void run() {
				resetData();
				createTitle("");
				refreshScreen();
			}
		});// post(Runnable)
    }

}
