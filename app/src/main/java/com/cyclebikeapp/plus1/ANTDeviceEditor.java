package com.cyclebikeapp.plus1;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

import com.dsi.ant.plugins.antplus.pcc.defines.DeviceType;

import java.util.ArrayList;

import static com.cyclebikeapp.plus1.Constants.ANTSETTINGS_TYPE_CAL;
import static com.cyclebikeapp.plus1.Constants.ANTSETTINGS_TYPE_SEARCH_PAIR;
import static com.cyclebikeapp.plus1.Constants.DB_KEY_ACTIVE;
import static com.cyclebikeapp.plus1.Constants.DB_KEY_BATT_STATUS;
import static com.cyclebikeapp.plus1.Constants.DB_KEY_BATT_VOLTS;
import static com.cyclebikeapp.plus1.Constants.DB_KEY_DEV_NAME;
import static com.cyclebikeapp.plus1.Constants.DB_KEY_DEV_TYPE;
import static com.cyclebikeapp.plus1.Constants.DB_KEY_MANUFACTURER;
import static com.cyclebikeapp.plus1.Constants.DB_KEY_MODEL_NUM;
import static com.cyclebikeapp.plus1.Constants.DB_KEY_POWER_CAL;
import static com.cyclebikeapp.plus1.Constants.DB_KEY_SEARCH_PRIORITY;
import static com.cyclebikeapp.plus1.Constants.DB_KEY_SERIAL_NUM;
import static com.cyclebikeapp.plus1.Constants.DB_KEY_SOFTWARE_REV;
import static com.cyclebikeapp.plus1.Constants.DB_KEY_UPTIME;
import static com.cyclebikeapp.plus1.Constants.KEY_CAL_CHANNEL;
import static com.cyclebikeapp.plus1.Constants.KEY_CHOOSER_CODE;
import static com.cyclebikeapp.plus1.Constants.KEY_PAIR_CHANNEL;
import static com.cyclebikeapp.plus1.Constants.PREFS_NAME;
import static com.cyclebikeapp.plus1.Constants.WHEEL_CIRCUM;
import static com.cyclebikeapp.plus1.Constants.ZERO;

public class ANTDeviceEditor extends ExpandableListActivity {
	ArrayList<String> groupItem = new ArrayList<>();
	ArrayList<ArrayList<String>> childItem = new ArrayList<>();
	private static String ANTplus = "ANT+";
	private static final String FORMAT_4_3F = "%4.3f";
	private static final String KEY_EXPAND_GROUP_0 = "key_expand_group0";
	private static final String KEY_EXPAND_GROUP_1 = "key_expand_group1";
	private static final String KEY_EXPAND_GROUP_2 = "key_expand_group2";
	private static final String KEY_EXPAND_GROUP_3 = "key_expand_group3";
	private static final String KEY_EXPAND_GROUP_4 = "key_expand_group4";
	private static final String[]  KEY_EXPAND_GROUP = {KEY_EXPAND_GROUP_0, KEY_EXPAND_GROUP_1,
		KEY_EXPAND_GROUP_2, KEY_EXPAND_GROUP_3, KEY_EXPAND_GROUP_4};
	ANTDBAdapter dataBaseAdapter = null;
	ExpListAdapter mExpAdapter;
	ExpandableListView expList;

	/** list of ANT+ manufacturers loaded from array resource */
	private String[] manufacturerIDs;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			dataBaseAdapter = new ANTDBAdapter(getApplicationContext());
			dataBaseAdapter.open();
		} catch(SQLException e){
			e.printStackTrace();
		}
        getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(this, R.color.bkgnd_black));
		expList = getExpandableListView();
		expList.setDividerHeight(2);
		expList.setGroupIndicator(null);
		expList.setClickable(true);
		setGroupData();
		setChildGroupData();
	    ANTplus = getResources().getString(R.string.ANTplus);
		ActionBar ab = getActionBar();
		if (ab!=null) {
			ab.setTitle(getString(R.string.ant_device_editor_title));
			ab.setDisplayHomeAsUpEnabled(true);
		}
	    manufacturerIDs = getResources().getStringArray(R.array.manufacturer_ids);
	    mExpAdapter = new ExpListAdapter(groupItem, childItem);
		mExpAdapter.setInflater(
						(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE),this);
		expList.setAdapter(mExpAdapter);
		restoreExpListState();
		expList.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent,
					View v, int groupPosition, long id) {
				// this just expands the list to show device names
				// store this value in SharedPrefs
				saveState();
				return false;
			}
		});
		expList.setOnChildClickListener(new OnChildClickListener() { 
			
			@Override
		    public boolean onChildClick(ExpandableListView parent, View v,
		                int groupPosition, int childPosition, long id) {
				if (childPosition == 0) {
					doSearch(groupPosition);
					// should return to DisplayActivity and show search progress
					//groupPosition 0 is heart_rate_sensor
					// 1 power_sensor
					// 2 speed_sensor
					// 3 cadence_sensor
					// 4 speed_cadence_sensor
				} else {
					String deviceName = childItem.get(groupPosition).get(childPosition);
					if (dataBaseAdapter != null) {
						int deviceNumber = dataBaseAdapter.fetchDeviceNumberByName(deviceName, groupPosition, childPosition);
						showDeviceDialog(deviceNumber);
					}
				}
				return true;
		    }
		});
	}

	private void restoreExpListState() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		// restore exp list state of groups expanded or collapsed
		int groupNum = 0;
		boolean[] expand = {
		settings.getBoolean(KEY_EXPAND_GROUP_0, false),
		settings.getBoolean(KEY_EXPAND_GROUP_1, false),
		settings.getBoolean(KEY_EXPAND_GROUP_2, false),
		settings.getBoolean(KEY_EXPAND_GROUP_3, false),
		settings.getBoolean(KEY_EXPAND_GROUP_4, false)};		
		for (boolean open:expand) {
			if (open) {
				expList.expandGroup(groupNum);
			} else {
				expList.collapseGroup(groupNum);
			}
			groupNum++;
		}
	}

	protected void showDeviceDialog(final int deviceNumber) {
		String upTimeMessage = "";
		String otherMessage = "";
		String modelMessage = "";
		boolean deviceActive;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String buttonTextID[] = {getString(R.string.forget),
				getString(R.string.calibrate), getString(R.string.cancel)};
		DeviceType deviceType = DeviceType.UNKNOWN;
		String deviceName = getString(R.string.device_name_);
		String priorityString = getString(R.string.search_priority);
		String deviceActiveString = getString(R.string.device_active);
		String deviceNumString = getString(R.string.device_number_);
		Cursor mCursor = null;
		try {
			mCursor = dataBaseAdapter.fetchDeviceData(deviceNumber);
			if (mCursor != null) {
				deviceType = DeviceType.getValueFromInt(mCursor.getInt(mCursor.getColumnIndexOrThrow(DB_KEY_DEV_TYPE)));
				String name = mCursor.getString(mCursor.getColumnIndexOrThrow(DB_KEY_DEV_NAME));
				if (name == null || name.equals("")){
					name = "<" + deviceNumber + ">";
					// correct database
					ContentValues content = new ContentValues();
					content.put(DB_KEY_DEV_NAME, name);
					dataBaseAdapter.updateDeviceRecord(deviceNumber,content);
				}
				deviceName = getString(R.string.device_name_) + name;
				deviceNumString = getString(R.string.device_number_) + deviceNumber;
				upTimeMessage = mCursor.getString(mCursor.getColumnIndexOrThrow(DB_KEY_UPTIME));
				otherMessage = getOtherMessage(mCursor);
				modelMessage = getModelMessage(mCursor);
				priorityString = getString(R.string.search_priority)
						+ mCursor.getString(mCursor.getColumnIndexOrThrow(DB_KEY_SEARCH_PRIORITY));
				deviceActive = mCursor.getInt(mCursor.getColumnIndexOrThrow(DB_KEY_ACTIVE)) == 1;
				deviceActiveString = getString(R.string.device_active)
						+ (deviceActive ? getString(R.string.yes) : getString(R.string.no));

				// Set other dialog properties
				if ((deviceType == DeviceType.BIKE_POWER) && deviceActive) {
					builder.setNeutralButton(buttonTextID[1],
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									doCalibrate(deviceNumber);
								}

							});
				}// power channel is open
			}// if cursor not null		
		} catch (IllegalArgumentException e) {
			Log.e(this.getClass().getName(), "IllegalArgumentException - " + e.toString());
		} finally {
			if (mCursor != null) {
				mCursor.close();
			}
		}
		builder.setPositiveButton(buttonTextID[0],
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						doForget(deviceNumber);
						//will return to expandable list view in ANTDeviceEditor
					}
				});
		builder.setNegativeButton(buttonTextID[2],
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						//will return to expandable list view in ANTDeviceEditor
					}
				});
		String message = priorityString + "\n" + deviceActiveString + "\n" + deviceName + "\n"
				+ deviceNumString
				+ "\n\n" + upTimeMessage + otherMessage + modelMessage;
		builder.setMessage(message).setTitle(composeTitle(deviceType));
		builder.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				//will return to expandable list view in ANTDeviceEditor
			}
		});
		builder.show();
	}

	private String composeTitle(DeviceType deviceType) {
		String returnString = "";
		switch (deviceType) {
		case BIKE_CADENCE:
			returnString = ANTplus + getString(R.string._cadence_sensor);
			break;
		case BIKE_POWER:
			returnString = ANTplus + getString(R.string._power_sensor);
			break;
		case BIKE_SPD:
			returnString = ANTplus + getString(R.string._speed_sensor);
			break;
		case BIKE_SPDCAD:
			returnString = ANTplus + getString(R.string._speed_cadence_sensor);
			break;
		case HEARTRATE:
			returnString = ANTplus + getString(R.string._heart_rate_sensor);
			break;
		default:
			break;
		}
		return returnString;
	}

	private String getModelMessage(Cursor deviceData) throws IllegalArgumentException {
		String manufacturer = "";
		int manID = deviceData.getInt(deviceData.getColumnIndexOrThrow(DB_KEY_MANUFACTURER));
		if ((manID > 0) && (manID < manufacturerIDs.length)) {
			manufacturer = manufacturerIDs[manID - 1];
		}
		return "\n" + manufacturer +"\n" + getString(R.string.model_)
				+ deviceData.getInt(deviceData.getColumnIndexOrThrow(DB_KEY_MODEL_NUM)) + "\n"
				+ getString(R.string.serial_)
				+ deviceData.getInt(deviceData.getColumnIndexOrThrow(DB_KEY_SERIAL_NUM)) + "\n"
				+ getString(R.string.sw_rev_)
				+ deviceData.getInt(deviceData.getColumnIndexOrThrow(DB_KEY_SOFTWARE_REV)) + "\n";
	}

	@SuppressLint("DefaultLocale")
	private String getOtherMessage(Cursor deviceData) throws IllegalArgumentException {
		String response = "";
		DeviceType deviceType = DeviceType.getValueFromInt(deviceData.getInt(deviceData.getColumnIndexOrThrow(DB_KEY_DEV_TYPE)));
		String battStatus = deviceData.getString(deviceData.getColumnIndexOrThrow(DB_KEY_BATT_STATUS));
		String volts = deviceData.getString(deviceData.getColumnIndexOrThrow(DB_KEY_BATT_VOLTS));
		String battVolts = "";
		try {
			battVolts = " (" + String.format(FORMAT_4_3F, Double.valueOf(volts)) + " V)";
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		String powerCal = deviceData.getString(deviceData.getColumnIndexOrThrow(DB_KEY_POWER_CAL));
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		String wheelCirc = String.format(FORMAT_4_3F, 
				Double.valueOf(settings.getString(WHEEL_CIRCUM, "2.142")));
		switch (deviceType) {
		case HEARTRATE:
			break;
		case BIKE_SPD:
			response = "\n" + getString(R.string.battery_status_) + battStatus
					+ battVolts
					+ "\n" + getString(R.string.wheel_circumference_) 
					+ wheelCirc;				
			break;
		case BIKE_CADENCE:
			response = "\n" + getString(R.string.battery_status_) + battStatus 
					+ battVolts;
			break;
		case BIKE_SPDCAD:
			response = "\n" + getString(R.string.wheel_circumference_) 
					+ wheelCirc;
			break;
		case BIKE_POWER:
			response = "\n" + getString(R.string.battery_status_) + battStatus
					+ battVolts
					+ "\n" + getString(R.string.calibration_data_) + powerCal;				
			break;
		default:
			break;
		}// switch
		return response;
	}

	/**
	 * Populate the groupItem ArrayList with titles of the device types
	 */
	private void setGroupData() {
		groupItem.add(getString(R.string._heart_rate_sensor));
		groupItem.add(getString(R.string._power_sensor));
		groupItem.add(getString(R.string._speed_sensor));
		groupItem.add(getString(R.string._cadence_sensor));
		groupItem.add(getString(R.string._speed_cadence_sensor));
	}

	/**
	 * Populate the childItem ArrayLists with all known device names for each
	 * device type from the database
	 */
	private void setChildGroupData() {
		childItem.clear();
		//Add Data For HRM
		try {
			ArrayList<String> child;
			DeviceType deviceType = DeviceType.HEARTRATE;
			child = dataBaseAdapter.getAllDeviceNames(deviceType);
			childItem.add(child);

			//Add Data For Power
			deviceType = DeviceType.BIKE_POWER;
			child = dataBaseAdapter.getAllDeviceNames(deviceType);
			childItem.add(child);

			//Add Data For Speed
			deviceType = DeviceType.BIKE_SPD;
			child = dataBaseAdapter.getAllDeviceNames(deviceType);
			childItem.add(child);

			//Add Data For Cadence
			deviceType = DeviceType.BIKE_CADENCE;
			child = dataBaseAdapter.getAllDeviceNames(deviceType);
			childItem.add(child);

			//Add Data For Speed&Cadence
			deviceType = DeviceType.BIKE_SPDCAD;
			child = dataBaseAdapter.getAllDeviceNames(deviceType);
			childItem.add(child);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			Log.e(this.getClass().getName(), "IllegalArgumentException in setChildGroupData()");
		}
	}
	
	/**
	 * When user presses the Calibrate button in the device List
	 * @param deviceNumber the device number to calibrate
	 */
	private void doCalibrate(final int deviceNumber) {
		if (MainActivity.debugAppState) Log.i(this.getClass().getName(), "doCalibrate()");
		Intent intent = getIntent();
		intent.putExtra(KEY_CHOOSER_CODE, ANTSETTINGS_TYPE_CAL);
		intent.putExtra(KEY_CAL_CHANNEL, deviceNumber);
		setResult(RESULT_OK, intent);
		finish();
		//should return to DisplayActivity thru onResume and do Calibrate
	}

	/**
	 * When user presses the "Forget" button on a device page, call the doForget
	 * method and re-build the device list child items
	 * @param deviceNum the device to forget
	 */
	private void doForget(int deviceNum) {
		if (dataBaseAdapter != null) {
			dataBaseAdapter.doForget(deviceNum);
		}
		// re-build childItems
		setChildGroupData();
	    mExpAdapter = new ExpListAdapter(groupItem, childItem);
		mExpAdapter.setInflater(
						(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE),this);
		expList.setAdapter(mExpAdapter);
		restoreExpListState();
	}
	
	/**
	 * When user presses the Search/Pair button in the device List
	 * @param groupPosition the device type to search for
	 */
	private void doSearch(int groupPosition) {
		if (MainActivity.debugAppState) Log.i(this.getClass().getName(), "doSearch()");
		// return to DisplayActivity and Pair the channel
		Intent intent = getIntent();
		intent.putExtra(KEY_CHOOSER_CODE, ANTSETTINGS_TYPE_SEARCH_PAIR);
		intent.putExtra(KEY_PAIR_CHANNEL, convertGroupPositionToDeviceType(groupPosition));
		setResult(RESULT_OK, intent);
		saveState();
		finish();
		// should return to DisplayActivity thru onResume and show search progress
	}// do Search

	/**
	 * User clicks on a group position in the expandable list, but we need to
	 * return the DeviceType (integer value) to main activity
	 * @param groupPosition user selection from expandable list
	 * @return corresponding DeviceType (integer value)
	 */
	private int convertGroupPositionToDeviceType(int groupPosition) {
//		static final int HRM_CHANNEL = 0;
//		static final int POWER_CHANNEL = 1;
//		static final int SPEED_CHANNEL = 2;
//		static final int CADENCE_CHANNEL = 3;
//		static final int SPEEDCADENCE_CHANNEL = 4;
		switch (groupPosition) {
		case 0:	
			return DeviceType.HEARTRATE.getIntValue();
		case 1:	
			return DeviceType.BIKE_POWER.getIntValue();
		case 2:	
			return DeviceType.BIKE_SPD.getIntValue();
		case 3:	
			return DeviceType.BIKE_CADENCE.getIntValue();
		case 4:	
			return DeviceType.BIKE_SPDCAD.getIntValue();
		default:	
			return -1;
		}
	}

	@Override
	protected void onPause() {
		if (dataBaseAdapter != null) {
			dataBaseAdapter.close();
		}
		super.onPause();
	}
	
	private void saveState() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		for (int group = 0; group < 5; group++){
			editor.putBoolean(KEY_EXPAND_GROUP[group], expList.isGroupExpanded(group));
		}
		editor.apply();
	}
	
	@Override
	protected void onStop() {
		if (MainActivity.debugAppState) Log.i(this.getClass().getName(), "onStop()");
		saveState();
		super.onStop();
		Intent intent = new Intent();
		intent.putExtra(KEY_CHOOSER_CODE, 0);
		setResult(RESULT_OK, intent);
	}

	@Override
	public void onBackPressed() {
		if (MainActivity.debugAppState) Log.i(this.getClass().getName(), "onBackPressed()");
		saveState();
		if (dataBaseAdapter != null) {
			dataBaseAdapter.close();
		}
		super.onStop();
		Intent intent = new Intent();
		intent.putExtra(KEY_CHOOSER_CODE, ZERO);
		setResult(RESULT_OK, intent);
		finish();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
			saveState();
			if (dataBaseAdapter != null) {
				dataBaseAdapter.close();
			}
	        NavUtils.navigateUpFromSameTask(this);
			this.overridePendingTransition(0, 0);
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
