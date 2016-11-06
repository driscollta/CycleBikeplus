package com.cyclebikeapp.plus1;

import android.view.Gravity;

final class Constants {
	private Constants() {}
	// some random static Strings
	static final int MY_PERMISSIONS_REQUEST_LOCATION = 924;
	static final int MY_PERMISSIONS_REQUEST_WRITE = 824;
    static final int REQUEST_CHECK_SETTINGS = 94;
	static final String TMP_CB_ROUTE = ".tmpCBRoute";
	static final String TP_DENSITY = "tpDensity_";
	static final String RESTORE_ROUTE_FILE_GPXFILENAME = "restoreRouteFile() - gpxfilename: ";
	static final String EXCEPTION = "Exception";
	static final String FILE_NOT_FOUND = "file not found";
	static String FIT_ACTIVITY_TYPE  = "1";
	static String TCX_ACTIVITY_TYPE  = "0";
	static final String UPLOAD_FILENAME = "upload_filename";
	static final String SHOW_SHARING = "show_sharing_alert";
	static final String USER_CANCELED = "user-canceled";
	static final String INITIALIZING_ROUTE = "Initializing Route";
	static final String NO_ROUTE_DATA_IN_FILE = "No route data in file!";
	static final String LOOKING_FOR_ROUTE_DATA = "Looking for route data";
	static final String LOADING_FILE = "Loading File";
	static final String XML = ".xml";
	static final String TCX = ".tcx";
	static final String GPX = ".gpx";
    // Unique tag for the error dialog fragment
    static final String DIALOG_ERROR = "dialog_error";
//database key tags
	static final String DB_KEY_DEV_NUM = "db_key_device_number";
	static final String DB_KEY_DEV_NAME = "db_key_device_name";
	static final String DB_KEY_DEV_TYPE = "db_key_device_type";
	static final String DB_KEY_BATT_VOLTS = "db_key_batt_volts";
	static final String DB_KEY_BATT_STATUS = "db_key_batt_status";
	static final String DB_KEY_SERIAL_NUM = "db_key_serial_num";
	static final String DB_KEY_MANUFACTURER = "db_key_manufacturer";
	static final String DB_KEY_SOFTWARE_REV = "db_key_software_rev";
	static final String DB_KEY_MODEL_NUM = "db_key_model_num";
	static final String DB_KEY_POWER_CAL = "db_key_power_cal";
	static final String DB_KEY_UPTIME = "db_key_uptime";
	static final String DB_KEY_SEARCH_PRIORITY = "db_key_priority";
	static final String DB_KEY_ACTIVE = "db_key_active";
	static final String CHOOSER_TYPE = "type";
	static final int CHOOSER_TYPE_TCX_DIRECTORY = 300;
	static final int CHOOSER_TYPE_TCX_FILE = 400;
	static final int CHOOSER_TYPE_GPX_DIRECTORY = 100;
	static final int CHOOSER_TYPE_GPX_FILE = 200;
	static final int ANTSETTINGS_TYPE_SEARCH_PAIR = 600;
	static final int ANTSETTINGS_TYPE_CAL = 700;
	static final int RC_SHOW_FILE_LIST = 66;
	static final int RC_ANT_SETTINGS = 56;
    static final int PERMISSIONS_REQUEST_LOCATION = 51;
    // Request code to use when launching the resolution activity
    static final int REQUEST_RESOLVE_ERROR = 1001;
	static final int UPLOAD_FILE_SEND_REQUEST_CODE = 2000;
	static final int ACTIVITY_FILE_TYPE = 1;
	static final int ROUTE_FILE_TYPE = 0;

	/**	key tags for shared preferences with restore route */	
	static final String DEVICE_NUMBER_POWER = "DeviceNumberPower";
	static final String DEVICE_NUMBER_CADENCE = "DeviceNumberCadence";
	static final String DEVICE_NUMBER_SPEED = "DeviceNumberSpeed";
	static final String DEVICE_NUMBER_SPEEDCADENCE = "DeviceNumberSpeedCadence";
	static final String DEVICE_NUMBER_HRM = "DeviceNumberHRM";	

	static final String DEVICE_SUBSCRIBED_HRM = "device_subscribed_hrm";
	static final String DEVICE_SUBSCRIBED_CADENCE = "device_subscribed_cadence";
	static final String DEVICE_SUBSCRIBED_SPEED = "device_subscribed_speed";
	static final String DEVICE_SUBSCRIBED_POWER = "device_subscribed_power";
	static final int ANT_TOAST_GRAVITY = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
	static final String DOUBLE_ZERO = "0.0";
	//cadence data Keys to shared preferences
	static final String NUM_PED_CNTS = "tot_pedCounts";
	static final String PREV_PED_CNTS = "prev_ped_cnts";
	static final String PED_CNTS_INIT = "ped_cnts_init";
	static final String NUM_PEDAL_CAD = "num_pedal_cadence";
	static final String TOTAL_PEDAL_CAD = "total_pedal_cadence";
	static final String AVG_CADENCE = "avg_cadence";
	static final String MAX_CADENCE = "max_cadence";
	//HR data Keys to shared preferences
	static final String TOTAL_HR_COUNTS = "total_hr_counts";
	static final String NUM_HR_EVENTS = "num_hr_events";
	static final String AVG_HR = "avg_heartrate";
	static final String MAX_HR = "max_heartrate";
	//wheel data Keys to shared preferences

	static final String WHEEL_CUMREV = "wheel_cumrev";
	static final String WHEEL_CUMREV_AT_START = "wheel_cumrev_at_start";
	static final String NUM_WHEEL_CNTS = "tot_wheelCounts";	
	static final String WHEEL_PREV_COUNT = "wheel_prev_count";
	static final String WHEEL_IS_CAL = "wheel_is_cal";
	static final String WHEEL_CIRCUM = "wheel_circumference";
	static final String START_DIST = "wheel_start_distance";
	static final String MAX_SPEED = "maxSpeed";
	//power data Keys to shared preferences

	static final String POWER_WHEEL_CUMREV = "power_wheel_cumrev";
	static final String POWER_WHEEL_PREV_COUNT = "power_wheel_prev_count";
	static final String POWER_WHEEL_CUMREV_AT_START = "power_wheel_cumrev_at_start";
	static final String POWER_WHEEL_IS_CAL = "power_wheel_is_cal";

	static final String POWER_CNTS_INIT = "power_cnts_init";
	static final String CUM_ENERGY = "cum_energy";
	static final String CUM_POWER_TIME = "cum_power_time";
	static final String AVG_POWER = "avg_power";
	static final String MAX_POWER = "max_power";
	static final String POWER_WHEEL_CIRCUM = "power_wheel_circumference";
	static final String NUM_POWER_WHEEL_CNTS = "tot_power_wheelCounts";
	static final String POWER_START_DIST = "power_wheel_start_distance";
	//calculated crank cadence data Keys to shared preferences
	static final String TOTAL_CALC_CAD = "total_calc_crank_cadence";
	static final String NUM_CALC_CAD = "num_calc_crank_cadence";
	static final String AVG_CALC_CADENCE = "avg_calc_crank_cadence";
	static final String MAX_CALC_CADENCE = "max_calc_crank_cadence";
	// Keys to shared preferences
    static final String SAVED_LAT = "savedLat";
    static final String SAVED_LON = "savedLon";
    static final String SAVED_LOC_TIME = "savedTime";
    static final String PREF_SAVED_LOC_TIME = "prefs_saved_time";
    static final String TCX_LOG_FILE_NAME = "tcxLogFileName";
	static final String TCX_LOG_FILE_FOOTER_LENGTH = "tcxLogFileFooterLength";
	static final String CURR_WP = "curr_WP";
	static final String FIRST_LIST_ELEM = "first_ListElem";
	static final String TRIP_DISTANCE = "tripDistance";
	static final String TRIP_TIME = "tripTime";
	static final String WHEEL_TRIP_DISTANCE = "wheelTripDistance";
	static final String WHEEL_TRIP_TIME = "wheeltriptime";
	static final String POWER_WHEEL_TRIP_TIME = "powerwheeltriptime";
	static final String POWER_WHEEL_TRIP_DISTANCE = "powerwheelTripDistance";
	static final String PREV_WHEEL_TRIP_DISTANCE = "prevWheelTripDistance";
	static final String SPOOF_WHEEL_TRIP_DISTANCE = "spoofWheelTripDistance";
	static final String PREV_SPOOF_WHEEL_TRIP_DISTANCE = "prevSpoofWheelTripDistance";
	static final String ALMANAC_RESET_TIME = "almanac_reset_time";
	static final int _360 = 360;
	static final String PREFS_NAME = "MyPrefsFile_pro";
	static final String APP_NAME = "CycleBike+";
	static final String BONUS_MILES = "bonusMiles ";
	static final String AUTH_NO_NETWORK_INTENT_RC = "88";
	static final String KEY_CHOSEN_GPXFILE = "chosenGPXFile";
	static final String KEY_CHOSEN_TCXFILE = "chosenTCXFile";
	static final String KEY_GPXPATH = "gpxPath";
	static final String KEY_TCXPATH = "tcxPath";
	static final String KEY_PAIR_CHANNEL = "key_pair_channel";
	static final String KEY_CAL_CHANNEL = "key_cal_channel";
	static final String KEY_CHOOSER_CODE = "chooserCode";
	static final String MOBILE_DATA_SETTING_KEY = "mobile_data_setting_key";
	static final String STATE_RESOLVING_ERROR = "resolving_error";
	static final String[] RWGPS_EMAIL = {"upload@rwgps.com"};
	static final String[] OTHER_EMAIL = {""};
	static final String USE_ANT = "USE_ANT";
	static final String SHOW_ANT = "SHOW_ANT";
	static final String HI_VIZ = "hi_viz";
	// ANT constants
	/** Pair to any device. */
	static final int WILDCARD = 0;
	static final String KEY_PLUG_IN_VERSION = "key_plugin_version";
	static final String HAS_ANT = "has_ant";
	static final String KEY_TRAINER_MODE = "key_trainer_mode";
	static final String KEY_VELO_CHOICE = "velo_default";
	static final String KEY_FORCE_NEW_TCX = "force_new_tcx";

	static final long ONE_SEC = 1000;
	/** set location current if no older than this (in millisec)*/
	static final long TEN_SEC = 10 * 1000;
	static final long FIVE_SEC = 5 * 1000;
	/** set sensor data current if no older than this (in millisec)*/
	static final long THREE_SEC = 3 * 1000;
	static final int NUM_MDSCYCLES = 3;
	/** autoConnect ant this often (in millisec)*/
	static final long THIRTY_SEC = 30 * 1000;
	static final long TWO_MINUTES = 2 * 60 * 1000;
	static final long THREE_MINUTES = 3 * 60 * 1000;
	static final long TWENTYFOUR_HOURS = 24 * 60 * 60 * 1000;
	/**default location */
	static final double googleLon = -122.085144;
	static final double googleLat = 37.422151;
	// distance conversions
	static final double msecPerSec = 1000.;
	static final double mph_per_mps = 2.23694;
	static final double kph_per_mps = 3.6;
	static final double km_per_meter = 0.001;
	static final double mile_per_meter = 0.00062137119224;

	// some GPS constants and threshold constants
    static final String PREFS_DEFAULT_LATITUDE = "37.1";
    static final String PREFS_DEFAULT_LONGITUDE = "-122.1";
    static final long PREFS_DEFAULT_TIME = 123456;
	/** glitch protection for speed sensors(mps) = 650 mph */
	static final double MAXIMUM_SPEED = 290;
	/** min trip distance (meters) before calibrating wheel circumference
	 * calWheel is actually called every minute, so distance may be longer */
	static final double MIN_CAL_DIST = 1606.1;
	/** smallest wheel circumference */
	static final double LOWER_WHEEL_CIRCUM = 1.075;
	/** largest wheel circumference */
	static final double UPPER_WHEEL_CIRCUM = 2.51;
	/** default wheel circumference */
	static final double DEFAULT_WHEEL_CIRCUM = 2.142;
	/** smallest time difference between ANT events (ms) */
	static final long MIN_DELTAT = 200;
	/** largest time difference between ANT power events (ms) */
	static final long MAX_DELTAT = THREE_SEC;
	/**want good location accuracy when calibrating the speed sensors (meters) */
	static final float goodLocationAccuracy = 18;
	/**want good enough location accuracy when writing locations to track file (meters) */
	static final float goodEnoughLocationAccuracy = 50;
	 /** when re-starting nav from long-pressed WP make sure we're nearEnough (meters) */
	static final double nearEnough = 402.25;
	/** detection threshold for the paused condition: deltaDOT and speed less
	 * than */
	static final double speedDOTPausedVal = .01;
	/** gps speed at which we can trust that the direction of travel bearing is
	 * accurate (m/sec) */
	static final double accurateGPSSpeed = 2. / mph_per_mps;
	/** minimum distance (m) changed before GPS update */
	static final float minGPSDistance = 0f;
	/** minimum milliseconds between GPS location updates */
	static final long gpsUpdateTime = 900;
	static final double DEG_PER_BEARING_ICON = 22.5;

	//program constants	
	static final String FORMAT_3D = "%3d";
	static final String FORMAT_4_1F = "%4.1f";
	static final String FORMAT_4_3F = "%4.3f";
	static final String FORMAT_3_1F = "%3.1f";
	static final String FORMAT_1F = "%.1f";
	static final String MILE = "mi";
	static final String KM = "km";
	static final String METER = "m";
	static final String FOOT = "ft";
	static final String ZERO = "0";
	static final String DASHES = "---";
	/** auto-connect to ant devices using all known devices in database, or just last connected device*/
	static final String KEY_AUTO_CONNECT_ALL = "autoconnect_all";
	static final String KEY_TRACK_DENSITY = "track_density";
	//routeHashMap Keys
	/** street is the text string of the street name */
	static final String KEY_STREET = "street";
	/** street unit is the units to display (ft, mi, m, km) */
	static final String KEY_UNIT = "street_unit";
	/** distance is the distance to the next turn, updated as locations are received */
	static final String KEY_DISTANCE = "distance";
	/** turn level is the numeric value that defines the turn icon to display
		 defined in the turn_levels.xml document in res/drawable */
	static final String KEY_TURN = "turn_level";
	/** bearing level is the numeric value that defines the bearing arrow to display
		 the icons are defined in the arrow_levels.xml document in res/drawable */
	static final String KEY_BEARING = "bearing_level";
	/** dimmed is an indication of how to display the data, dimmed when a way
		 point has been passed or just within reach */
	static final String KEY_DIM = "dimmed";

}
