package com.cyclebikeapp.plus1;

import android.content.ContentValues;
import android.util.Log;

import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch;

class ActiveANTDeviceData {
	private static final String DB_KEY_DEV_NUM = "db_key_device_number";
	private static final String DB_KEY_DEV_NAME = "db_key_device_name";
	private static final String DB_KEY_DEV_TYPE = "db_key_device_type";
	private static final String DB_KEY_BATT_VOLTS = "db_key_batt_volts";
	private static final String DB_KEY_BATT_STATUS = "db_key_batt_status";
	private static final String DB_KEY_SERIAL_NUM = "db_key_serial_num";
	private static final String DB_KEY_MANUFACTURER = "db_key_manufacturer";
	private static final String DB_KEY_SOFTWARE_REV = "db_key_software_rev";
	private static final String DB_KEY_MODEL_NUM = "db_key_model_num";
	private static final String DB_KEY_POWER_CAL = "db_key_power_cal";
	private static final String DB_KEY_UPTIME = "db_key_uptime";
	private static final String DB_KEY_SEARCH_PRIORITY = "db_key_priority";
	private static final String DB_KEY_ACTIVE = "db_key_active";
	private int deviceNum;
	private MultiDeviceSearch.MultiDeviceSearchResult mDevice;
	private ContentValues data;

	ActiveANTDeviceData(MultiDeviceSearch.MultiDeviceSearchResult mDevice) {
		this.mDevice = mDevice;
		if (mDevice != null) {
			this.deviceNum = mDevice.getAntDeviceNumber();
		} else {
			deviceNum = -1;
		}
		data = new ContentValues();
	}
	
	int getDeviceNum() {
		return deviceNum;
	}
	void setDeviceNum(int devNum) {this.deviceNum = devNum;}
	int getDeviceType() {
		return data.getAsInteger(DB_KEY_DEV_TYPE );
	}

	public ContentValues getData() {
		return data;
	}
	
	public void setData(ContentValues data) {
		this.data.putAll(data);
	}
	
	void logData() {
		//+ DB_KEY_DEV_NAME + TEXT_NOT_NULL + ", "
		String devName = data.getAsString(DB_KEY_DEV_NAME);
		//+ DB_KEY_DEV_TYPE + TEXT_NOT_NULL + ", "
		String devType = data.getAsString(DB_KEY_DEV_TYPE);
		//+ DB_KEY_BATT_VOLTS + TEXT_NOT_NULL + ", "
		String battVolts = data.getAsString(DB_KEY_BATT_VOLTS);
		//+ DB_KEY_BATT_STATUS + TEXT_NOT_NULL + ", "
		String battStatus = data.getAsString(DB_KEY_BATT_STATUS);
		//+ DB_KEY_SERIAL_NUM + TEXT_NOT_NULL + ", "
		String serialNum = data.getAsString(DB_KEY_SERIAL_NUM);
		//+ DB_KEY_MANUFACTURER + TEXT_NOT_NULL + ", "
		String manufacturer = data.getAsString(DB_KEY_MANUFACTURER);
		//+ DB_KEY_SOFTWARE_REV + TEXT_NOT_NULL + ", "
		String softwareRev = data.getAsString(DB_KEY_SOFTWARE_REV);
		//+ DB_KEY_MODEL_NUM + TEXT_NOT_NULL + ", "
		String modelNum = data.getAsString(DB_KEY_MODEL_NUM);
		//+ DB_KEY_POWER_CAL + TEXT_NOT_NULL + ", "
		String powerCal = data.getAsString(DB_KEY_POWER_CAL);
		//+ DB_KEY_SEARCH_PRIORITY + INTEGER_NOT_NULL + ", "
		int searchPriority = data.getAsInteger(DB_KEY_SEARCH_PRIORITY);
		//+ DB_KEY_UPTIME + TEXT_NOT_NULL + ", "
		String upTime = data.getAsString(DB_KEY_UPTIME);
		//+ DB_KEY_ACTIVE + INTEGER_NOT_NULL + ");";
		int deviceActive = data.getAsInteger(DB_KEY_ACTIVE);
		Log.i(this.getClass().getName(), "logData()" + deviceNum + ", " + devName + ", " + devType + ", " + battVolts + ", " + battStatus + ", " +
				serialNum + ", " + manufacturer + ", " + softwareRev + ", " + modelNum + ", " +
				powerCal + ", " + searchPriority + ", " + upTime + ", " + deviceActive);
	}

	MultiDeviceSearch.MultiDeviceSearchResult getAsyncScanDeviceInfo() {
		return mDevice;
	}
	void setAsyncScanDeviceInfo(MultiDeviceSearch.MultiDeviceSearchResult mDevice) {
		this.mDevice = mDevice;
	}
}
