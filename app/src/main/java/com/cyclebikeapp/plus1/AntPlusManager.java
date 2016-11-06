/*
 * Copyright 2010 Dynastream Innovations Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.cyclebikeapp.plus1;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.dsi.ant.plugins.antplus.pcc.defines.DeviceType;
import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.cyclebikeapp.plus1.Constants.AVG_HR;
import static com.cyclebikeapp.plus1.Constants.CUM_ENERGY;
import static com.cyclebikeapp.plus1.Constants.CUM_POWER_TIME;
import static com.cyclebikeapp.plus1.Constants.DB_KEY_DEV_NAME;
import static com.cyclebikeapp.plus1.Constants.DB_KEY_DEV_NUM;
import static com.cyclebikeapp.plus1.Constants.DB_KEY_DEV_TYPE;
import static com.cyclebikeapp.plus1.Constants.DB_KEY_SEARCH_PRIORITY;
import static com.cyclebikeapp.plus1.Constants.KEY_AUTO_CONNECT_ALL;
import static com.cyclebikeapp.plus1.Constants.MAX_HR;
import static com.cyclebikeapp.plus1.Constants.NUM_CALC_CAD;
import static com.cyclebikeapp.plus1.Constants.NUM_HR_EVENTS;
import static com.cyclebikeapp.plus1.Constants.NUM_PEDAL_CAD;
import static com.cyclebikeapp.plus1.Constants.PED_CNTS_INIT;
import static com.cyclebikeapp.plus1.Constants.POWER_CNTS_INIT;
import static com.cyclebikeapp.plus1.Constants.POWER_WHEEL_IS_CAL;
import static com.cyclebikeapp.plus1.Constants.PREFS_NAME;
import static com.cyclebikeapp.plus1.Constants.TOTAL_CALC_CAD;
import static com.cyclebikeapp.plus1.Constants.TOTAL_HR_COUNTS;
import static com.cyclebikeapp.plus1.Constants.TOTAL_PEDAL_CAD;
import static com.cyclebikeapp.plus1.Constants.WHEEL_IS_CAL;

/**
 * This class handles setting up the channels,
 * and processing Ant events.
 */
class AntPlusManager {

	private static final String DB_KEY_ACTIVE = "db_key_active";
	private static final String MIN = "min";
	private static final String HRS = "hrs ";
	private static final String SENSOR_UP_TIME = "Sensor up-time: ";
	static final int HRM_CHANNEL = 0;
	static final int POWER_CHANNEL = 1;
	static final int SPEED_CHANNEL = 2;
	static final int CADENCE_CHANNEL = 3;
	static final int SPEEDCADENCE_CHANNEL = 4;


	/** structure for combining all wheel sensor data */
	SpeedCadenceCounts wheelCnts = new SpeedCadenceCounts();
    /** structure for combining all crank cadence sensor data */
	SpeedCadenceCounts crankCadenceCnts = new SpeedCadenceCounts();
    /** structure for combining all cadence sensor data */
	SpeedCadenceCounts pedalCadenceCnts = new SpeedCadenceCounts();
    /** structure for combining all power meter calculated power sensor data */
	SpeedCadenceCounts calcPowerData = new SpeedCadenceCounts();
    /** structure for combining all PowerTap wheel sensor data */
	SpeedCadenceCounts powerWheelCnts = new SpeedCadenceCounts();
	SpeedCadenceCounts hrData= new SpeedCadenceCounts();
    /** structure for combining all power meter raw power sensor data */
	SpeedCadenceCounts rawPowerData= new SpeedCadenceCounts();
      
    // Variables to keep track of the status of each sensor    
    /** structure to hold all the channel data */
    ChannelConfiguration channelConfig[];
	private final Context mContext;
    private double powerTime = .1;
	private double totalEnergy = 0;
	private long totalCalcCrankCad = 0;
	private long numCalcCrankCad = 0;
	private long totalPedalCad = 0;
	private long numPedalCad = 0;
	private long totalHRCounts = 0;
	private long numHREvents = 0;
	private boolean antChannelAvailable = true;
	ArrayList<ActiveANTDeviceData> antDBDeviceList;
	ArrayList<ActiveANTDeviceData> antOtherDeviceList;
	// need to avoid using UI thread while MDS is starting up
	private boolean isMDSStarting = false;
	// the System.currentTimeMillis when we've asked the MDS to start again when paused, for example
	private long forceMDSStartTime;
	private int numMDSSearchCycles;

	/**
     * Default Constructor
	 * @param context the app context
     */
    AntPlusManager(Context context) {
        channelConfig = new ChannelConfiguration[5];                       
        channelConfig[HRM_CHANNEL] = new ChannelConfiguration();
        channelConfig[SPEED_CHANNEL] = new ChannelConfiguration();
        channelConfig[POWER_CHANNEL] = new ChannelConfiguration();
        channelConfig[CADENCE_CHANNEL] = new ChannelConfiguration();
        channelConfig[SPEEDCADENCE_CHANNEL] = new ChannelConfiguration();
   	 	antDBDeviceList = new ArrayList<>();
		antOtherDeviceList = new ArrayList<>();
   	 	mContext = context;
		forceMDSStartTime = System.currentTimeMillis();
    }

	/**
	 * Test if a device is already in the antDBDeviceList ArrayList
	 * 
	 * @param antDeviceNumber
	 *            the device number to look for
	 * @return true if the device number is already in the list
	 */
	boolean isDeviceInDBActiveList(int antDeviceNumber) {
		for (ActiveANTDeviceData activeDevice: antDBDeviceList) {
			if (activeDevice.getDeviceNum() == antDeviceNumber) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Test if a device is already in the antDBDeviceList ArrayList
	 *
	 * @param antDeviceNumber
	 *            the device number to look for
	 * @return true if the device number is already in the list
	 */
	boolean isDeviceInOtherActiveList(int antDeviceNumber) {
		for (ActiveANTDeviceData activeDevice: antOtherDeviceList) {
			if (activeDevice.getDeviceNum() == antDeviceNumber) {
				return true;
			}
		}
		return false;
	}

	/** Get requested content by specifying index to ArrayList
	 * @param index is the entry number in the ArrayList
	 * @return ActiveANTDeviceData containing the device data or null if index is greater than ArrayList size*/
	ActiveANTDeviceData getActiveDBDeviceData(int index) {
		// find index to ArrayList using deviceNum
		if (index < antDBDeviceList.size()) {
			return antDBDeviceList.get(index);
		} else {
			return null;
		}
	}

	/** Get requested content by specifying index to ArrayList
	 * @param index is the entry number in the ArrayList
	 * @return ActiveANTDeviceData containing the device data or null if index is greater than ArrayList size*/
	public ActiveANTDeviceData getActiveOtherDeviceData(int index) {
		// find index to ArrayList using deviceNum
		if (index < antOtherDeviceList.size()) {
			return antOtherDeviceList.get(index);
		} else {
			return null;
		}
	}

	/** Get a list of *active* devices by DeviceType. The DB list is populated b yreading the database
	 * Must check to make sure the device is active.
	 * @param deviceType device type of the ANT device
	 * @return ActiveANTDeviceData containing the device data or null if deviceNum is not in the List*/
	ArrayList<ActiveANTDeviceData> getActiveDeviceDBDataByDeviceType(int deviceType) {
		ArrayList<ActiveANTDeviceData> activeDeviceListByType = new ArrayList<>();
		for (ActiveANTDeviceData deviceData: antDBDeviceList) {
			if (deviceData.getDeviceType() == deviceType
					&& deviceData.getData().getAsInteger(DB_KEY_ACTIVE) == 1) {
				//deviceData.logData();
				if (MainActivity.debugMDS){Log.i(this.getClass().getName(), "getActiveDeviceDataByDeviceType - addDevice name: "
						+ deviceData.getData().getAsString(DB_KEY_DEV_NAME));}
				activeDeviceListByType.add(deviceData);
			}
		}
		return activeDeviceListByType;
	}
	/** Get requested content by specifying the device number
	 * @param deviceType device type of the ANT device
	 * @return ActiveANTDeviceData containing the device data or null if deviceNum is not in the List*/
	ArrayList<ActiveANTDeviceData> getActiveDeviceOtherDataByDeviceType(int deviceType) {
		ArrayList<ActiveANTDeviceData> activeDeviceListByType = new ArrayList<>();
		for (ActiveANTDeviceData deviceData: antOtherDeviceList) {
			if (deviceData.getDeviceType() == deviceType) {
				//deviceData.logData();
				if (MainActivity.debugMDS){Log.i(this.getClass().getName(), "getActiveDeviceDataByDeviceType - addDevice name: "
						+ deviceData.getData().getAsString(DB_KEY_DEV_NAME));}
				activeDeviceListByType.add(deviceData);
			}
		}
		return activeDeviceListByType;
	}

	/** Get requested content by specifying the device number
	 * @param deviceNum device number of the ANT device
	 * @return ActiveANTDeviceData containing the device data or null if deviceNum is not in the List*/
	public ActiveANTDeviceData getActiveDeviceDBDataByDeviceNum(int deviceNum) {
		// find index to ArrayList using deviceNum
		int i = 0;
		for (ActiveANTDeviceData deviceData: antDBDeviceList) {
			if (deviceData.getDeviceNum() == deviceNum) {
				//deviceData.logData();
				return antDBDeviceList.get(i);
			}
			i++;
		}
		return null;		
	}
	
	/** When new data is received about one of the active database ANT devices, update the content
	 * @param deviceNum device number of the ANT device
	 * @param data ContentValues containing the new data
	 */
	void updateActiveDBDeviceData(int deviceNum, ContentValues data) {
		// find index to ArrayList using deviceNum
		if (MainActivity.debugMDS) {Log.i(this.getClass().getName(), "updateActiveDBDeviceData() - devNum: " + deviceNum);}
		int i = 0;
		for (ActiveANTDeviceData deviceData: antDBDeviceList) {
			if (deviceData.getDeviceNum() == deviceNum) {
				deviceData.setData(data);
				antDBDeviceList.set(i, deviceData);
			}
			i++;
		}
		//logActiveDBDeviceListData("updateActiveDBDeviceData()");
	}
	/** When new device is detected in MDS, set deviceInfo
	 * @param deviceNum device number of the ANT device
	 * @param deviceInfo MultiDeviceSearchResult
	 */
	void setDeviceInfo(int deviceNum, MultiDeviceSearch.MultiDeviceSearchResult deviceInfo) {
		// find index to ArrayList using deviceNum
		if (MainActivity.debugMDS){Log.i(this.getClass().getName(), "setDeviceInfo() - devNum: " + deviceNum);}
		int i = 0;
		for (ActiveANTDeviceData deviceData: antDBDeviceList) {
			if (deviceData.getDeviceNum() == deviceNum) {
				deviceData.setAsyncScanDeviceInfo(deviceInfo);
				antDBDeviceList.set(i, deviceData);
			}
			i++;
		}
		//logActiveDBDeviceListData("updateActiveDBDeviceData()");
	}
	/** When new data is received about one of the active Other ANT devices, update the content.
	 * This will just be the active status; we don't care about other data, and we won't receive any other
	 * data because we won't connect to devices not in the database
	 * @param deviceNum device number of the ANT device
	 * @param data ContentValues containing the new data
	 * */
	void updateActiveOtherDeviceData(int deviceNum, ContentValues data) {
		// find index to ArrayList using deviceNum
		if (MainActivity.debugMDS){Log.i(this.getClass().getName(), "updateActiveOtherDeviceData() - devNum: " + deviceNum);}
		int i = 0;
		for (ActiveANTDeviceData deviceData: antOtherDeviceList) {
			if (deviceData.getDeviceNum() == deviceNum) {
				deviceData.setData(data);
				antOtherDeviceList.set(i, deviceData);
			}
			i++;
		}
		//logActiveOtherDeviceListData("updateActiveOtherDeviceData()");
	}

	/**
	 * called from addDeviceToActiveList from MDS onDeviceFound sets the active status of device devNumber
	 * @param devNumber the device to set status of
	 * @param activeStatus the status
	 */
	void setDevNumberActiveStatus(int devNumber, int activeStatus) {
		// find index to ArrayList using deviceNum
		int i = 0;
		ContentValues data = new ContentValues();
		data.put(DB_KEY_ACTIVE, activeStatus);
		for (ActiveANTDeviceData deviceData: antDBDeviceList) {
			if (deviceData.getDeviceNum() == devNumber) {
				deviceData.setData(data);
				antDBDeviceList.set(i, deviceData);
			}
			i++;
		}
		//logActiveDBDeviceListData("onDeviceFound");
		//logActiveOtherDeviceListData("onDeviceFound");
	}
	/**
	 * Reset status for all DeviceType when starting a new scan
	 * @param deviceType type of the ANT device
	*/
	void resetActiveStatusByType(int deviceType) {
		// find index to ArrayList using deviceNum
		int i = 0;
		ContentValues data = new ContentValues();
		data.put(DB_KEY_ACTIVE, 0);
		for (ActiveANTDeviceData deviceData: antDBDeviceList) {
			if (deviceData.getDeviceType() == deviceType) {
				deviceData.setData(data);
				antDBDeviceList.set(i, deviceData);
			}
			i++;
		}
		//logActiveDBDeviceListData("resetDBActiveStatus() devType: " + deviceType);
		i = 0;
		for (ActiveANTDeviceData deviceData: antOtherDeviceList) {
			if (deviceData.getDeviceType() == deviceType) {
				deviceData.setData(data);
				antOtherDeviceList.set(i, deviceData);
			}
			i++;
		}
		//logActiveOtherDeviceListData("resetOtherActiveStatus()");
	}

	/** For debugging, log all device data in the activeDeviceList 
	 * @param string an indication of where we called this from for debugging*/
	void logActiveDBDeviceListData(String string) {
		if (MainActivity.debugMDS){Log.i(this.getClass().getName(), string);}
		for (ActiveANTDeviceData deviceData: antDBDeviceList) {
			deviceData.logData();
		}
	}
	/** For debugging, log all device data in the activeDeviceList
	 * @param string an indication of where we called this from for debugging*/
	void logActiveOtherDeviceListData(String string) {
		if (MainActivity.debugMDS){Log.i(this.getClass().getName(), string);}
		for (ActiveANTDeviceData deviceData: antOtherDeviceList) {
			deviceData.logData();
		}
	}

	// Getters and setters

	@SuppressLint("DefaultLocale")
	String convertUpTimeToString(long time) {
		long hours = TimeUnit.SECONDS.toHours(time);
		long minutes = TimeUnit.SECONDS.toMinutes(time)
				- (TimeUnit.SECONDS.toHours(time) * 60);
		String upTimeMessage = "";
		if (time > 0) {
				upTimeMessage = SENSOR_UP_TIME
						+ String.format("%4d", hours) + HRS
						+ String.format("%02d", minutes) + MIN;
		}
		return upTimeMessage;
	}
		
	void addCumEnergy(double d) {
		this.totalEnergy += d;		
	}

	void setCumEnergy(long energy) {
		this.totalEnergy = energy;		
	}

	double getCumEnergy() {
		return this.totalEnergy;		
	}

	void addCumPowerTime(double deltaT) {
		this.powerTime += deltaT;
	}

	void setCumPowerTime(double time) {
		this.powerTime = time;
	}

	double getCumPowerTime() {
		return powerTime;
	}

	long getTotalCalcCrankCad() {
		return totalCalcCrankCad;
	}

	void addTotalCalcCrankCad(long newCalcCrankCad) {
		this.totalCalcCrankCad += newCalcCrankCad;
	}

	long getNumCalcCrankCad() {
		return numCalcCrankCad;
	}

	void addNumCalcCrankCad() {
		this.numCalcCrankCad++;
	}

	void setNumCalcCrankCad(int int1) {
		this.numCalcCrankCad = int1;		
	}

	void setTotalCalcCrankCad(int int1) {
		this.totalCalcCrankCad = int1;
	}

	long getTotalPedalCad() {
		return totalPedalCad;
	}

	void setTotalPedalCad(long totalPedalCad) {
		this.totalPedalCad = totalPedalCad;
	}

	void addTotalPedalCad(long totalPedalCad) {
		this.totalPedalCad += totalPedalCad;
	}

	long getNumPedalCad() {
		return numPedalCad;
	}
	long addNumPedalCad() {
		return numPedalCad++;
	}

	void setNumPedalCad(long numPedalCad) {
		this.numPedalCad = numPedalCad;
	}

	long getTotalHRCounts() {
		return totalHRCounts;
	}

	long addTotalHRCounts(long hrCount) {
		return totalHRCounts += hrCount;
	}

	void setTotalHRCounts(long totalHRCounts) {
		this.totalHRCounts = totalHRCounts;
	}

	long getNumHREvents() {
		return numHREvents;
	}

	long addNumHREvents() {
		return numHREvents++;
	}

	void setNumHREvents(long num) {
		this.numHREvents = num;
	}

	void setAntChannelAvailable(boolean antChannelAvailable) {
		this.antChannelAvailable = antChannelAvailable;
	}
	
	boolean isChannelSubscribed(DeviceType channel) {
		switch (channel) {
		case HEARTRATE:
			return channelConfig[HRM_CHANNEL].isSubscribed;
		case BIKE_POWER:
			return channelConfig[POWER_CHANNEL].isSubscribed;
		case BIKE_SPD:
			return channelConfig[SPEED_CHANNEL].isSubscribed;
		case BIKE_CADENCE:
			return channelConfig[CADENCE_CHANNEL].isSubscribed;
		case BIKE_SPDCAD:
			return channelConfig[SPEEDCADENCE_CHANNEL].isSubscribed;
		default:
			return false;
		}
	}
	
	void setChannelSubscribed(boolean subscribed, DeviceType channel) {
		switch (channel) {
		case HEARTRATE:
			this.channelConfig[HRM_CHANNEL].isSubscribed = subscribed;
			break;
		case BIKE_POWER:
			this.channelConfig[POWER_CHANNEL].isSubscribed = subscribed;
			break;
		case BIKE_SPD:
			this.channelConfig[SPEED_CHANNEL].isSubscribed = subscribed;
			break;
		case BIKE_CADENCE:
			this.channelConfig[CADENCE_CHANNEL].isSubscribed = subscribed;
			break;
		case BIKE_SPDCAD:
			this.channelConfig[SPEEDCADENCE_CHANNEL].isSubscribed = subscribed;
			break;
		default:
			
		}
	}

	/**
	 * Returns the device number of a particular device type in the activeDeviceList
	 * having highest search priority. If autoConnectAll == false, only return
	 * device number if search priority is 1, which indicates we have last
	 * connected to this device. 
	 * 
	 * @param devType
	 *            the type of device to find, which is DeviceType. The devType
	 *            stored in the activeList is DeviceType.getInt()
	 * @return the device number of the devType in the active list. Failure to find is devNum = -1
	 */
	public int getActiveDevNumByType(DeviceType devType) {
		int devTypeNum = devType.getIntValue();
		// find minimum priority device in activeList; this will be deprecated
		for (ActiveANTDeviceData activeDevice : antDBDeviceList) {
			if (activeDevice.getData().getAsInteger(DB_KEY_DEV_TYPE) == devTypeNum) {
			return activeDevice.getDeviceNum();
			}
		}			
		return -1;
	}

	/**
	 * We call this method to get the DeviceInfo from the highest priority active device before doing autoConnect
 	 * @param devType device type we're looking for
	 * @return AsyncScan DeviceInfo is a parameter for requesting access to a releaseHandle
	 */
	MultiDeviceSearch.MultiDeviceSearchResult getActiveDevInfoByType(DeviceType devType) {

		int devTypeNum = devType.getIntValue();
		int priority = 1000;
		MultiDeviceSearch.MultiDeviceSearchResult deviceInfo = null;
		// find minimum priority device in activeList
		for (ActiveANTDeviceData activeDevice : antDBDeviceList) {
			if (activeDevice.getData().getAsInteger(DB_KEY_DEV_TYPE) == devTypeNum
					&& activeDevice.getData().getAsInteger(DB_KEY_ACTIVE) == 1) {
				int activeDevicePriority = activeDevice.getData().getAsInteger(DB_KEY_SEARCH_PRIORITY);
				if (activeDevicePriority < priority){
					priority = activeDevicePriority;
					deviceInfo = activeDevice.getAsyncScanDeviceInfo();
					if (deviceInfo != null && MainActivity.debugMDS) {
						Log.i(this.getClass().getName(), "activeDeviceData devNumber: " + deviceInfo.getAntDeviceNumber() + " priority: " + priority);
					}
				}
			}
		}
		// If we're only connecting to the last device, priority will be 1.
		// If highest priority device doesn't have priority 1 return null
		// Get SharedPrefs for autoConnectAll, if it's false and priority>1 set devInfo null
		SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
		boolean autoConnectAll = settings.getBoolean(KEY_AUTO_CONNECT_ALL, false);
		if (!autoConnectAll && priority > 1){
			deviceInfo = null;
		}
		return deviceInfo;
	}
	void addToANTDBDeviceList(ActiveANTDeviceData newDevice) {
		if (!isDeviceInDBActiveList(newDevice.getDeviceNum())){
			antDBDeviceList.add(newDevice);
		}
		if (MainActivity.debugMDS)logActiveDBDeviceListData("addtoActiveANTDBDeviceList()");
	}
	void addToANTOtherDeviceList(ActiveANTDeviceData newDevice) {
		antOtherDeviceList.add(newDevice);
		if (MainActivity.debugMDS) logActiveOtherDeviceListData("addToANTOtherDeviceList()");
	}

	/**
	 * We've just acquired communication with a device. It has been added to the
	 * antDBDeviceList with search priority 1.
	 * Down-grade all other active devices of the same type.
	 * @param antDeviceNumber the device number of the newly acquired device
	 * @param devType the DeviceType of the newly acquired device
	 */
	void resetSearchPriority(int antDeviceNumber, DeviceType devType) {
		int i = 0;
		//logActiveDBDeviceListData("before resetSearchPriority");
		for (ActiveANTDeviceData activeDevice: antDBDeviceList) {
			int devNum = activeDevice.getData().getAsInteger(DB_KEY_DEV_NUM);
			int type = activeDevice.getData().getAsInteger(DB_KEY_DEV_TYPE);
			if (type == devType.getIntValue() && devNum != antDeviceNumber) {
				int searchPriority = activeDevice.getData().getAsInteger(DB_KEY_SEARCH_PRIORITY);
				ContentValues content = new ContentValues();
				content.put(DB_KEY_SEARCH_PRIORITY, searchPriority + 1);
				activeDevice.setData(content);
				antDBDeviceList.set(i, activeDevice);
			}
			i++;
		}
		//logActiveDBDeviceListData("after resetSearchPriority");
	}

	void restartWheelCal(Double wheelTripDistance) {
		mContext.getSharedPreferences(PREFS_NAME, 0).edit()
				.putBoolean(WHEEL_IS_CAL, false).apply();
		wheelCnts.isCalibrated = false;
		wheelCnts.calTotalCount = 0;
		// if we're restarting because GPS dropped out,
		// change GPS StartDistance
		wheelCnts.calGPSStartDist = wheelTripDistance;
		wheelCnts.cumulativeRevsAtCalStart = wheelCnts.cumulativeRevolutions;
	}


	void restartPowerWheelCal(Double wheelTripDistance) {
		mContext.getSharedPreferences(PREFS_NAME, 0).edit()
				.putBoolean(POWER_WHEEL_IS_CAL, false).apply();
		powerWheelCnts.isCalibrated = false;
		powerWheelCnts.calTotalCount = 0;
		// if we're restarting because GPS dropped out,
		// change GPS StartDistance
		powerWheelCnts.calGPSStartDist = wheelTripDistance;
		powerWheelCnts.cumulativeRevsAtCalStart = powerWheelCnts.cumulativeRevolutions;
	}
	
	void restartHR(int avgHeartRate, int maxHeartRate) {
		setNumHREvents(0);
		setTotalHRCounts(0);
		SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(TOTAL_HR_COUNTS, (int) getTotalHRCounts());
		editor.putInt(NUM_HR_EVENTS, (int) getNumHREvents());
		editor.putInt(AVG_HR, avgHeartRate);
		editor.putInt(MAX_HR, maxHeartRate).apply();
	}

	void restartPower() {
		calcPowerData.initialized = false;
		setCumEnergy(0);
		setCumPowerTime(0);
		SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(POWER_CNTS_INIT, false);
		editor.putInt(CUM_ENERGY, (int) getCumEnergy());
		editor.putString(CUM_POWER_TIME,
				Double.toString(getCumPowerTime())).apply();
	}

	void restartCadence() {
		crankCadenceCnts.initialized = false;
		pedalCadenceCnts.initialized = false;
		setNumPedalCad(0);
		setTotalPedalCad(0);
		setNumCalcCrankCad(0);
		setTotalCalcCrankCad(0);
		SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(PED_CNTS_INIT, false).apply();
		editor.putInt(NUM_PEDAL_CAD, 0);
		editor.putInt(NUM_CALC_CAD, 0);
		editor.putInt(TOTAL_PEDAL_CAD, 0);
		editor.putInt(TOTAL_CALC_CAD, 0).commit();
	}

	boolean isPairing() {
		return channelConfig[HRM_CHANNEL].pairing
				|| channelConfig[CADENCE_CHANNEL].pairing
				|| channelConfig[SPEED_CHANNEL].pairing
				|| channelConfig[POWER_CHANNEL].pairing;
	}

	boolean isMDSStarting() {
		return isMDSStarting;
	}

	void setIsMDSStarting(boolean isMDSStarting) {
		this.isMDSStarting = isMDSStarting;
	}

	long getForceMDSStartTime() {
		return forceMDSStartTime;
	}

	void setForceMDSStartTime(long forceMDSStartTime) {
		this.forceMDSStartTime = forceMDSStartTime;
	}

	int getNumMDSSearchCycles() {
		return numMDSSearchCycles;
	}

	void setNumMDSSearchCycles(int numMDSSearchCycles) {
		this.numMDSSearchCycles = numMDSSearchCycles;
	}
}
