package com.cyclebikeapp.plus1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dsi.ant.plugins.antplus.pcc.defines.DeviceType;

import java.util.ArrayList;

import static com.cyclebikeapp.plus1.Constants.DB_KEY_ACTIVE;
import static com.cyclebikeapp.plus1.Constants.DB_KEY_BATT_STATUS;
import static com.cyclebikeapp.plus1.Constants.DB_KEY_BATT_VOLTS;
import static com.cyclebikeapp.plus1.Constants.DB_KEY_DEV_NAME;
import static com.cyclebikeapp.plus1.Constants.DB_KEY_DEV_NUM;
import static com.cyclebikeapp.plus1.Constants.DB_KEY_DEV_TYPE;
import static com.cyclebikeapp.plus1.Constants.DB_KEY_MANUFACTURER;
import static com.cyclebikeapp.plus1.Constants.DB_KEY_MODEL_NUM;
import static com.cyclebikeapp.plus1.Constants.DB_KEY_POWER_CAL;
import static com.cyclebikeapp.plus1.Constants.DB_KEY_SEARCH_PRIORITY;
import static com.cyclebikeapp.plus1.Constants.DB_KEY_SERIAL_NUM;
import static com.cyclebikeapp.plus1.Constants.DB_KEY_SOFTWARE_REV;
import static com.cyclebikeapp.plus1.Constants.DB_KEY_UPTIME;

class ANTDBAdapter {
	private static final String ANT_DEVICE_TABLE = "antDeviceTable";
	private static final String ANT_DEVICE_DB = "antDeviceDataBase";
	private final Context mContext;
	private DBHelper mDBHelper;
	private SQLiteDatabase antDeviceDB;

	ANTDBAdapter(Context context) {
		this.mContext = context;
//		Log.e(this.getClass().getName(), "creating dataBaseAdapter");
	}

	/**
	 * Read the data pertaining to a particular deviceNum
	 * 
	 * @param deviceNum
	 *            the ANT+ device number of the device to read
	 * @return a Cursor containing the data
	 * */
	Cursor fetchDeviceData(long deviceNum) {
		String filter = DB_KEY_DEV_NUM + "= '" + Integer.toString((int) deviceNum) + "'";
		Cursor returnCursor = null;
		if (antDeviceDB != null && !isClosed()) {
			returnCursor = antDeviceDB.query(ANT_DEVICE_TABLE, null, filter,
					null, null, null, null);
			if (returnCursor != null) {
				returnCursor.moveToFirst();
			}
		}
		return returnCursor;
	}

	/**
	 * Get the device number by specifying the device name. Also specify device
	 * type and position in case there are devices with the same name, but
	 * different device type.
	 * 
	 * @param name
	 *            the device name
	 * @param childPosition
	 *            is the position in the list of devices of a particular type
	 * @param groupPosition
	 *            is the position in the list of device types like HRM, power
	 *            sensors, etc
	 * @return the device number
	 * */
	int fetchDeviceNumberByName(String name, int groupPosition, int childPosition) {
		DeviceType devType = DeviceType.UNKNOWN;
		switch (groupPosition) {
		case AntPlusManager.HRM_CHANNEL:
			devType = DeviceType.HEARTRATE;
			break;
		case AntPlusManager.POWER_CHANNEL:
			devType = DeviceType.BIKE_POWER;
			break;
		case AntPlusManager.SPEED_CHANNEL:
			devType = DeviceType.BIKE_SPD;
			break;
		case AntPlusManager.CADENCE_CHANNEL:
			devType = DeviceType.BIKE_CADENCE;
			break;
		case AntPlusManager.SPEEDCADENCE_CHANNEL:
			devType = DeviceType.BIKE_SPDCAD;
			break;
		default:
			break;
		}

		int deviceNum = 0;
		String[] columns = { DB_KEY_DEV_NUM };
		if (antDeviceDB != null && !isClosed()) {
			String filter = DB_KEY_DEV_NAME + " = '" + name + "'" + " AND "
					+ DB_KEY_DEV_TYPE + " = "
					+ Integer.toString(devType.getIntValue());
			Cursor mCursor = antDeviceDB.query(ANT_DEVICE_TABLE, columns,
					filter, null, null, null, null);
			if (mCursor != null && mCursor.moveToFirst()) {
				try {
					deviceNum = mCursor.getInt(mCursor.getColumnIndexOrThrow(DB_KEY_DEV_NUM));

				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} finally {
					mCursor.close();
				}
			}
		}
		return deviceNum;
	}

	/**
	 * 
	 * Set given deviceNum search priority to 1, down-grade other deviceNums of
	 * same deviceType. after acquisition.
	 * 
	 * @param deviceNum
	 *            the device just acquired
	 * @param deviceType
	 *            type of device just acquired
	 */
	void resetSearchPriorityFromAcq(int deviceNum, int deviceType) {
		if (antDeviceDB == null || isClosed()) {
			return;
		}
		// assume deviceNum row already exists
		// this would be called when this deviceNum is acquired;
		// on subsequent autoConnect() want this new device to be first in queue
		ContentValues content = new ContentValues();
		// set searchPriority of deviceNum to 1
		content.put(DB_KEY_SEARCH_PRIORITY, 1);
		try {
			antDeviceDB.update(ANT_DEVICE_TABLE, content, DB_KEY_DEV_NUM + "= '"
					+ Integer.toString(deviceNum) + "'", null);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		// to store dev_type in database use DeviceType.xxx.getIntValue()
		// to retrieve DeviceType from int in database use
		// DeviceType.getValueFromInt(integer)
		String orderByPriority = DB_KEY_SEARCH_PRIORITY;
		String devTypeFilter = DB_KEY_DEV_TYPE + "= " + Integer.toString(deviceType);
		String[] columns = { DB_KEY_DEV_NUM, DB_KEY_SEARCH_PRIORITY };
		// 1) get all deviceType entries from the table, ordered by search
		// priority
		Cursor mCursor = null;
		try {
			mCursor = antDeviceDB.query(ANT_DEVICE_TABLE, columns, devTypeFilter, null, null, null, orderByPriority);
			if (mCursor != null && mCursor.moveToFirst()) {
				// Set the first devNum searchPriority to 2
				int searchPriority = 2;
				int devNum;
				do {
					devNum = mCursor.getInt(mCursor.getColumnIndexOrThrow(DB_KEY_DEV_NUM));
					if (devNum != deviceNum) {
						// devices other than deviceNum set priority and increment
						content.clear();
						content.put(DB_KEY_SEARCH_PRIORITY, searchPriority++);
						antDeviceDB.update(ANT_DEVICE_TABLE,
								content,
								DB_KEY_DEV_NUM + "=" + Integer.toString(devNum),
								null);
					}
				} while (mCursor.moveToNext());

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (mCursor != null) {
				mCursor.close();
			}
		}
	}

	/**
	 * Set priority 1 to last priority, upgrade search priority for other
	 * devices of the same type.
	 * 
	 * @param deviceNum
	 *            the most recent device we've been looking for
	 * @param deviceType
	 *            the type of device we're looking for
	 */
	public void rotateSearchPriority(int deviceNum, int deviceType) {
		if (antDeviceDB == null || isClosed()) {
			return;
		}
		// assume deviceNum row already exists
		ContentValues content = new ContentValues();
		// to store dev_type in database use DeviceType.xxx.getIntValue()
		// to retrieve DeviceType from int in database use
		// DeviceType.getValueFromInt(integer)
		String orderByPriority = DB_KEY_SEARCH_PRIORITY;
		String devTypeFilter = DB_KEY_DEV_TYPE + "= " + Integer.toString(deviceType);
		String[] columns = { DB_KEY_DEV_NUM, DB_KEY_SEARCH_PRIORITY };
		// 1) get all deviceType entries from the table, ordered by search priority
		Cursor mCursor = null;
		try {
			mCursor = antDeviceDB.query(ANT_DEVICE_TABLE, columns,
					devTypeFilter, null, null, null, orderByPriority);
			if (mCursor != null && mCursor.moveToFirst()) {
				// Set the first devNum != deviceNum searchPriority to 1
				int searchPriority = 1;
				int devNum;
				do {
					devNum = mCursor.getInt(mCursor.getColumnIndexOrThrow(DB_KEY_DEV_NUM));
					if (devNum != deviceNum) {
						// devices other than deviceNum set priority and
						// increment
						content.clear();
						content.put(DB_KEY_SEARCH_PRIORITY, searchPriority++);
						antDeviceDB.update(ANT_DEVICE_TABLE,
								content,
								DB_KEY_DEV_NUM + "=" + Integer.toString(devNum), null);
					}
				} while (mCursor.moveToNext());
				// set searchPriority of deviceNum to end
				content.clear();
				content.put(DB_KEY_SEARCH_PRIORITY, searchPriority);
				antDeviceDB.update(ANT_DEVICE_TABLE, content, DB_KEY_DEV_NUM
						+ "= '" + Integer.toString(deviceNum) + "'", null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (mCursor != null) {
				mCursor.close();
			}
		}
	}

	/**
	 * After deleting a device, reset the searchPriority starting at 1 for the
	 * lowest remaining search priority
	 * 
	 * @param deviceType
	 *            the device type to reset search priority
	 */
	private void resetSearchPriorityAfterForget(int deviceType) {
		if (antDeviceDB == null || antDeviceDB.isOpen()) {
			return;
		}
		ContentValues content = new ContentValues();
		String orderByPriority = DB_KEY_SEARCH_PRIORITY;
		// if devType is speed or cadence, also include speed-cadence type
		String devTypeFilter = DB_KEY_DEV_TYPE + "= '" + Integer.toString(deviceType) + "'";
		String[] columns = { DB_KEY_DEV_NUM, DB_KEY_SEARCH_PRIORITY };
		// 1) get all deviceType entries from the table, ordered by search priority
		Cursor mCursor = null;
		try {
			mCursor  = antDeviceDB.query(ANT_DEVICE_TABLE, columns,
					devTypeFilter, null, null, null, orderByPriority);
			if (mCursor != null && mCursor.moveToFirst()) {
				// If there are no other devices? mCursor.moveToFirst() returns false
				// Set the lowest ordered devNum searchPriority to 1
				int searchPriority = 1;
				int devNum;
				do {
					devNum = mCursor.getInt(mCursor.getColumnIndexOrThrow(DB_KEY_DEV_NUM));
					// set priority and increment
					content.clear();
					content.put(DB_KEY_SEARCH_PRIORITY, searchPriority++);
					antDeviceDB.update(ANT_DEVICE_TABLE, content,
							DB_KEY_DEV_NUM + "=" + Integer.toString(devNum), null);
				} while (mCursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (mCursor != null) {
				mCursor.close();
			}
		}
	}

	/**
	 * Sets the active status ("0" = inactive; "1" = active) for a device.
	 * 
	 * @param activeStatus
	 *            the new status
	 * @param deviceNumber
	 *            the device to address
	 * */
	public void setActiveStatusByDevNum(int activeStatus, int deviceNumber) {
		ContentValues content = new ContentValues();
		content.clear();
		content.put(DB_KEY_ACTIVE, activeStatus);
		if (antDeviceDB != null 
				&& antDeviceDB.isOpen() 
				&& !antDeviceDB.isDbLockedByCurrentThread()) {
			try {
				antDeviceDB.update(ANT_DEVICE_TABLE,		
					content,
					DB_KEY_DEV_NUM + "= '" + Integer.toString(deviceNumber) + "'",
					null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/** Sets the active status for all devices to "0" (inactive) */
	public void resetAllActiveStatus() {
		ContentValues content = new ContentValues();
		content.clear();
		content.put(DB_KEY_ACTIVE, 0);
		if (antDeviceDB != null 
				&& antDeviceDB.isOpen()
				&& !antDeviceDB.isDbLockedByCurrentThread()) {
			try {
				antDeviceDB.update(ANT_DEVICE_TABLE, content, null, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Returns the active status for a device specified by deviceNumber 
	 *
	 * @param deviceNumber
	 *            the device in question
	 * @return status "0" = inactive; "1" = active
	 */
	public int getActiveStatusFromDevNum(int deviceNumber) {
		int active = 0;
		String devNumFilter = DB_KEY_DEV_NUM + "= '" + Integer.toString(deviceNumber) + "'";
		String[] columns = { DB_KEY_ACTIVE };
		if (antDeviceDB != null
				&& antDeviceDB.isOpen()
				&& !antDeviceDB.isDbLockedByCurrentThread()) {
			Cursor mCursor = antDeviceDB.query(ANT_DEVICE_TABLE, columns,
					devNumFilter, null, null, null, null);
			if (mCursor != null && mCursor.moveToFirst()) {
				try {
					active = mCursor.getInt(mCursor.getColumnIndexOrThrow(DB_KEY_ACTIVE));
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					mCursor.close();
				}
			}
		}
		return active;
	}

	/**
	 * Returns the type of device (power sensor, HRM, etc), knowing the unique
	 * ANT+ device number
	 * 
	 * @param devNum
	 *            the device in question
	 * @return devType
	 */
	private int getDevTypeFromDevNum(int devNum) {
		int devType = -1;
		String devNumFilter = DB_KEY_DEV_NUM + "= '" + Integer.toString(devNum) + "'";
		String[] columns = { DB_KEY_DEV_TYPE };
		if (antDeviceDB != null
				&& antDeviceDB.isOpen()
				&& !antDeviceDB.isDbLockedByCurrentThread()) {
			Cursor mCursor = antDeviceDB.query(ANT_DEVICE_TABLE, columns,
					devNumFilter, null, null, null, null);
			if (mCursor != null && mCursor.moveToFirst()) {
				try {
					devType = mCursor.getInt(mCursor.getColumnIndexOrThrow(DB_KEY_DEV_TYPE));
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					mCursor.close();
				}
			}
		}
		return devType;
	}
	/**
	 * Returns the type of device (power sensor, HRM, etc), knowing the unique
	 * ANT+ device number
	 *
	 * @param devNum
	 *            the device in question
	 * @return devName
	 */
	String getDevNameFromDevNum(int devNum) {
		String devName = "";
		String devNumFilter = DB_KEY_DEV_NUM + "= '" + Integer.toString(devNum) + "'";
		String[] columns = { DB_KEY_DEV_NAME };
		if (antDeviceDB != null && antDeviceDB.isOpen()
				&& !antDeviceDB.isDbLockedByCurrentThread()) {
			Cursor mCursor = null;
			try {
				mCursor = antDeviceDB.query(ANT_DEVICE_TABLE, columns, devNumFilter, null, null, null, null);
				if (mCursor != null && mCursor.moveToFirst()) {
					devName = mCursor.getString(mCursor.getColumnIndexOrThrow(DB_KEY_DEV_NAME));
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (mCursor != null){
					mCursor.close();
				}
			}
		}
		return devName;
	}
	/**
	 * Returns the search priority of device (power sensor, HRM, etc), knowing the unique
	 * ANT+ device number
	 *
	 * @param devNum
	 *            the device in question
	 * @return devType
	 */
	int getSearchPriorityFromDevNum(int devNum) {
		int searchPriority = 99;
		String devNumFilter = DB_KEY_DEV_NUM + "= '" + Integer.toString(devNum) + "'";
		String[] columns = { DB_KEY_SEARCH_PRIORITY };
		if (antDeviceDB != null && antDeviceDB.isOpen()
				&& !antDeviceDB.isDbLockedByCurrentThread()) {
			Cursor mCursor = null;
			try {
				mCursor = antDeviceDB.query(ANT_DEVICE_TABLE, columns, devNumFilter, null, null, null, null);
				if (mCursor != null && mCursor.moveToFirst()) {
					searchPriority = mCursor.getInt(mCursor.getColumnIndexOrThrow(DB_KEY_SEARCH_PRIORITY));
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (mCursor != null){
					mCursor.close();
				}
			}
		}
		return searchPriority;
	}

	/**
	 * Erases all knowledge of the device from the data base
	 * 
	 * @param deviceNum
	 *            the device in question
	 */
	void doForget(int deviceNum) {
		int devType = getDevTypeFromDevNum(deviceNum);
		if (antDeviceDB != null && antDeviceDB.isOpen()) {
			try {
				antDeviceDB.delete(ANT_DEVICE_TABLE,
						DB_KEY_DEV_NUM + "= '" + Integer.toString(deviceNum) + "'",
						null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (devType > -1) {
			resetSearchPriorityAfterForget(devType);
		}
		if (MainActivity.debugMDS) dumpDBToLog("ANTDBAdapter - doForget()");
	}

	/**
	 * Returns the device numbers of all active devices of a specified type in a
	 * Cursor
	 * 
	 * @param devType
	 *            the type of device in question
	 */
	public Cursor getAllActiveDevNumByPriority(DeviceType devType) {
		Cursor mCursor = null;
		String[] columns = { DB_KEY_DEV_NUM, DB_KEY_SEARCH_PRIORITY };
		String devTypeFilter = DB_KEY_DEV_TYPE + "= '"
				+ Integer.toString(devType.getIntValue()) + "'" + " AND "
				+ DB_KEY_ACTIVE + " = 1";
		String orderBy = DB_KEY_SEARCH_PRIORITY;
		if (antDeviceDB != null && antDeviceDB.isOpen()) {
			mCursor = antDeviceDB.query(ANT_DEVICE_TABLE, columns,
					devTypeFilter, null, null, null, orderBy);
		}
		return mCursor;
	}

	// public Cursor getAllDevNumByPriority(DeviceType devType) {
	// Cursor mCursor = null;
	// String[] columns = { DB_KEY_DEV_NUM, DB_KEY_SEARCH_PRIORITY};
	// String devTypeFilter = DB_KEY_DEV_TYPE + "= '" +
	// Integer.toString(devType.getIntValue()) + "'";
	// String orderBy = DB_KEY_SEARCH_PRIORITY;
	// if (antDeviceDB != null && !isClosed()){
	// mCursor = antDeviceDB.query(ANT_DEVICE_TABLE, columns,
	// devTypeFilter, null, null, null, orderBy);
	// }
	// return mCursor;
	// }

	/**
	 * Determines if a device is stored in the data base
	 * 
	 * @param antDeviceNumber
	 *            the device in question
	 * @return true if that device is in the data base
	 */
	boolean isDeviceInDataBase(int antDeviceNumber) {
		boolean found = false;
		if (antDeviceDB != null && !isClosed()) {
			// query db for matching deviceNum, ordered by search priority
			String devNumFilter = DB_KEY_DEV_NUM + "= '"
					+ Integer.toString(antDeviceNumber) + "'";
			String[] columns = { DB_KEY_DEV_NUM };
			Cursor mCursor = null;
			try {
				mCursor = antDeviceDB.query(ANT_DEVICE_TABLE, columns, devNumFilter, null, null, null, null);
				if (mCursor != null && mCursor.moveToFirst()) {
					int devNum;
					do {
						devNum = mCursor.getInt(mCursor.getColumnIndexOrThrow(DB_KEY_DEV_NUM));
						found = (devNum == antDeviceNumber);
					} while (mCursor.moveToNext());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (mCursor != null){
					mCursor.close();
				}
			}
		}
		return found;
	}
	/**
	 * When setting-up childItems to display device data, want all device names

	 * @return an ArrayList of ActiveANTDeviceData
	 */
	ArrayList<ActiveANTDeviceData> getAllDeviceData(){
		ArrayList<ActiveANTDeviceData> deviceDBData = new ArrayList<>();
		String[] columns = { DB_KEY_DEV_NAME, DB_KEY_SEARCH_PRIORITY, DB_KEY_DEV_NUM,DB_KEY_ACTIVE, DB_KEY_DEV_TYPE };
		String orderBy = DB_KEY_SEARCH_PRIORITY;
		if (antDeviceDB != null && !isClosed()) {
			// query db for matching deviceType, ordered by search priority
			Cursor mCursor = null;
			try {
				mCursor = antDeviceDB.query(ANT_DEVICE_TABLE, columns, null, null, null, null, orderBy);
				if (mCursor != null && mCursor.moveToFirst()) {
					do {
						ActiveANTDeviceData theData = new ActiveANTDeviceData(null);
						ContentValues values = new  ContentValues();
						values.put(DB_KEY_DEV_NAME, mCursor.getString(mCursor.getColumnIndexOrThrow(DB_KEY_DEV_NAME)));
						values.put(DB_KEY_SEARCH_PRIORITY, mCursor.getInt(mCursor.getColumnIndexOrThrow(DB_KEY_SEARCH_PRIORITY)));
						int devNum = mCursor.getInt(mCursor.getColumnIndexOrThrow(DB_KEY_DEV_NUM));
						values.put(DB_KEY_DEV_NUM, devNum);
						theData.setDeviceNum(devNum);
						values.put(DB_KEY_DEV_TYPE, mCursor.getInt(mCursor.getColumnIndexOrThrow(DB_KEY_DEV_TYPE)));
						values.put(DB_KEY_ACTIVE, mCursor.getInt(mCursor.getColumnIndexOrThrow(DB_KEY_ACTIVE)));
						theData.setData(values);
						deviceDBData.add(theData);
					} while (mCursor.moveToNext());
				}
			}catch (Exception e){
				e.printStackTrace();
			} finally {
				if (mCursor != null) {
					mCursor.close();
				}
			}
		}
		return deviceDBData;
	}

	/**
	 * When setting-up childItems to display device data, want all device names
	 * of a particular type.
	 * 
	 * @param deviceType
	 *            the type of device in question
	 * @return an ArrayList of device names of that type
	 */
	ArrayList<String> getAllDeviceNames(DeviceType deviceType)
			throws IllegalArgumentException {
		ArrayList<String> deviceNames = new ArrayList<>();
		String[] columns = { DB_KEY_DEV_NAME , DB_KEY_DEV_NUM};
		String orderBy = DB_KEY_SEARCH_PRIORITY;
		deviceNames.add(mContext.getString(R.string.search_pair));
		if (antDeviceDB != null && !isClosed()) {
			String filter = DB_KEY_DEV_TYPE + "= '"
					+ Integer.toString(deviceType.getIntValue()) + "'";
			// query db for matching deviceType, ordered by search priority
			Cursor mCursor = null;
			try {
				mCursor = antDeviceDB.query(ANT_DEVICE_TABLE, columns,
						filter, null, null, null, orderBy);
				if (mCursor != null && mCursor.moveToFirst()) {
					do {
						String name = mCursor.getString(mCursor.getColumnIndexOrThrow(DB_KEY_DEV_NAME));
						if (name == null || ("").equals(name)){
							name = "<" + mCursor.getString(mCursor.getColumnIndexOrThrow(DB_KEY_DEV_NUM) )+ ">";
						}
						deviceNames.add(name);
					} while (mCursor.moveToNext());
				}
			} catch (Exception e){
				e.printStackTrace();
			} finally {
				if (mCursor != null) {
					mCursor.close();
				}
			}
		}
		// Log.i(this.getClass().getName(), "getAllDeviceNames() size: "+deviceNames.size() +
		// " deviceType: "+deviceType);
		return deviceNames;
	}

	/**
	 * When tracking data after acquisition add that device to the data base if
	 * its not already there
	 * 
	 * @param content
	 *            has the device name, ANT+ device number
	 */
	void addDeviceToDB(ContentValues content) {
		int devNum = content.getAsInteger(DB_KEY_DEV_NUM);
		int devType = content.getAsInteger(DB_KEY_DEV_TYPE);
		String devName = content.getAsString(DB_KEY_DEV_NAME);
		if (antDeviceDB != null && !isClosed()) {
			// 1) see if this deviceNum is already in dB
			boolean inDB = isDeviceInDataBase(devNum);
			// Log.w("CycleBike", "inDB?  " + (inDB?"yes":"no"));
			if (!inDB) {
				// 2) if not, check for a deviceType with same devName
				boolean sameName = testDuplicateName(devType, devName);
				// 3) adjust devName if already exists by adding "_ devNum" to
				// devName
				// Log.w("CycleBike", "sameName: " + (sameName?"yes":"no"));
				if (sameName) {
					String newName = devName + "_" + Integer.toString(devNum);
					// Log.w("CycleBike", "newName: " + newName);
					content.clear();
					content.put(DB_KEY_DEV_NAME, newName);
					content.put(DB_KEY_DEV_NUM, devNum);
					content.put(DB_KEY_DEV_TYPE, devType);
				}
			}
			content.put(DB_KEY_BATT_VOLTS, "");
			content.put(DB_KEY_BATT_STATUS, "");
			content.put(DB_KEY_SERIAL_NUM, "");
			content.put(DB_KEY_MANUFACTURER, "");
			content.put(DB_KEY_SOFTWARE_REV, "");
			content.put(DB_KEY_MODEL_NUM, "");
			content.put(DB_KEY_POWER_CAL, "");
			content.put(DB_KEY_UPTIME, "");
			content.put(DB_KEY_SEARCH_PRIORITY, 1);
			content.put(DB_KEY_ACTIVE, 1);
			long conflictResult = antDeviceDB.insertWithOnConflict(
					ANT_DEVICE_TABLE, "", content,
					SQLiteDatabase.CONFLICT_IGNORE);
			// Log.w("CycleBike", "conflictResult: " + conflictResult);
			//dumpDBToLog("add  deviceNum = " + content.getAsInteger(DB_KEY_DEV_NUM));
		} else {
			Log.w(this.getClass().getName(), "couldn't add to DB for deviceNum = "
							+ content.getAsInteger(DB_KEY_DEV_NUM));
			//Log.w(this.getClass().getName(), ((antDeviceDB == null)? "antDB is null":"antDB is closed"));
		}
	}

	/**
	 * If there is already a device of the same name in the data base, we'll
	 * have to modify it.
	 * 
	 * @param deviceName
	 *            the name we have called this device
	 * @param devType
	 *            the type of this device
	 * @return true if this device has the same data base name as another device
	 *         of the same type
	 */
	private boolean testDuplicateName(Integer devType, String deviceName) {
		boolean found = false;
		Cursor mCursor = null;
		String[] columns = { DB_KEY_DEV_NAME };
		String devTypeFilter = DB_KEY_DEV_TYPE + "= " + Integer.toString(devType);
		try {
			if (antDeviceDB != null && !isClosed()) {
				mCursor = antDeviceDB.query(ANT_DEVICE_TABLE, columns,
						devTypeFilter, null, null, null, null);
			}
			if (mCursor != null && mCursor.moveToFirst()) {
				String devName;
				do {
					devName = mCursor.getString(mCursor.getColumnIndexOrThrow(DB_KEY_DEV_NAME));
					found = devName.trim().equals(deviceName.trim());
					// Log.w(this.getClass().getName(), "found" + " - devName: " + devName);
					if (found) {
						mCursor.close();
						return true;
					}
				} while (mCursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (mCursor != null) {
				mCursor.close();
			}
		}
		return found;
	}

	/**
	 * When receiving ANT device data, update the data base
	 * 
	 * @param deviceNum
	 *            the device transmitting the new data
	 * @param content
	 *            an Object containing the new data
	 */
	void updateDeviceRecord(int deviceNum, ContentValues content) {
		String[] whereArgs = { String.valueOf(deviceNum) };
		try {
			if (antDeviceDB != null && antDeviceDB.isOpen()) {
				antDeviceDB.update(ANT_DEVICE_TABLE, content, DB_KEY_DEV_NUM + "=?", whereArgs);
				//Log.w("CycleBike", "DB updated for deviceNum = " + deviceNum);
			} else {
				Log.w(this.getClass().getName(), "couldn't update DB for deviceNum = " + deviceNum);
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	private void dumpDBToLog(String message) {
		// list the entire database in Log file, message can be where the dump
		// was requested
		Log.v(this.getClass().getName(), message);
		Log.d(this.getClass().getName(), "num," + "name," + "type," + "batt-volt,"
				+ "batt-status," + "serial#," + "man," + "SW-rev," + "model#,"
				+ "cal," + "priority," + "uptime," + "active");
		if (antDeviceDB == null || isClosed()) {
			return;
		}
		Cursor cursor = antDeviceDB.rawQuery("SELECT * FROM antDeviceTable", null);
		if (cursor != null && cursor.moveToFirst()) {
			do {
				Log.i(this.getClass().getName(),
						cursor.getString(1) + "," + cursor.getString(2) + ","
								+ cursor.getString(3) + "," + cursor.getString(4) + ","
								+ cursor.getString(5) + "," + cursor.getString(6) + ","
								+ cursor.getString(7) + "," + cursor.getString(8) + ","
								+ cursor.getString(9) + "," + cursor.getString(10) + ","
								+ cursor.getString(11) + "," + cursor.getString(12) + ","
								+ cursor.getString(13));
			} while (cursor.moveToNext());
		} else {
			Log.i(this.getClass().getName(), "dumpDB null cursor");
		}
		if (cursor != null) {
			cursor.close();
		}
	}

	private class DBHelper extends SQLiteOpenHelper {
		static final String TEXT_NOT_NULL = " TEXT NOT NULL";
		static final String INTEGER_NOT_NULL = " INTEGER NOT NULL";
		static final String INTEGER_UNIQUE = " INTEGER UNIQUE";
		static final String ANT_DEVICE_TABLE = " antDeviceTable ";
		private static final int DB_VERSION = 1;

		DBHelper(Context context) {
			super(context, ANT_DEVICE_DB, null, DB_VERSION);
			if (MainActivity.debugAppState)
				Log.v(this.getClass().getName(), "Create DBHelper()");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			String dropString = "DROP TABLE IF EXISTS antDeviceTable;";
			db.execSQL(dropString);
			onCreate(db);
		}

		@Override
		public void onCreate(SQLiteDatabase db) throws SQLException {
			//Log.v("CycleBike - ANTAdapter", "onCreateSQLTable()");
			String createString = "CREATE TABLE IF NOT EXISTS antDeviceTable "
					+ "( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ DB_KEY_DEV_NUM + INTEGER_UNIQUE + ", "
					+ DB_KEY_DEV_NAME + TEXT_NOT_NULL + ", "
					+ DB_KEY_DEV_TYPE + TEXT_NOT_NULL + ", "
					+ DB_KEY_BATT_VOLTS + TEXT_NOT_NULL + ", "
					+ DB_KEY_BATT_STATUS + TEXT_NOT_NULL + ", "
					+ DB_KEY_SERIAL_NUM + TEXT_NOT_NULL + ", "
					+ DB_KEY_MANUFACTURER + TEXT_NOT_NULL + ", "
					+ DB_KEY_SOFTWARE_REV + TEXT_NOT_NULL + ", "
					+ DB_KEY_MODEL_NUM + TEXT_NOT_NULL + ", "
					+ DB_KEY_POWER_CAL + TEXT_NOT_NULL + ", "
					+ DB_KEY_SEARCH_PRIORITY + INTEGER_NOT_NULL + ", "
					+ DB_KEY_UPTIME + TEXT_NOT_NULL + ", "
					+ DB_KEY_ACTIVE + INTEGER_NOT_NULL + ");";
			db.execSQL(createString);
//			db.enableWriteAheadLogging();
		}
	}

	public ANTDBAdapter open() throws SQLiteException {
		 if (MainActivity.debugAppState) {Log.d(this.getClass().getName(), "openDB()");}
		mDBHelper = new DBHelper(mContext);
		antDeviceDB = mDBHelper.getWritableDatabase();
		return this;
	}

	/** close the antDeviceTable database */
	public void close() {
		 if (MainActivity.debugAppState) {Log.d(this.getClass().getName(), "closeDB()");}
		try {
			if (mDBHelper != null) {
					mDBHelper.close();
			}
		} catch (IllegalStateException e) {
				e.printStackTrace();
		}
	}

	public boolean isClosed() {
		return antDeviceDB == null || !antDeviceDB.isOpen();
	}

}
