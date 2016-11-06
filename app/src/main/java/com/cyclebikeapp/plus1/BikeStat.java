package com.cyclebikeapp.plus1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;

import java.util.concurrent.TimeUnit;

import static com.cyclebikeapp.plus1.Constants.HI_VIZ;
import static com.cyclebikeapp.plus1.Constants.MAX_SPEED;
import static com.cyclebikeapp.plus1.Constants.PREFS_NAME;
import static com.cyclebikeapp.plus1.Constants.TRIP_DISTANCE;
import static com.cyclebikeapp.plus1.Constants.TRIP_TIME;
/*
 * Copyright  2013 cyclebikeapp. All Rights Reserved.
*/


class BikeStat {
	private static final double msecPerSec = 1000.;
	/** in m */
	private double gpsTripDistance;
	/** in meters, distance using calibrated speed sensor*/
	private double wheelTripDistance;
	private double prevWheelTripDistance;
	/** in meters, distance using speed sensor for spoofing locations in trainer mode*/
	private double spoofWheelTripDistance;
	/** in meters, distance using speed sensor for spoofing locations in trainer mode*/
	private double prevSpoofWheelTripDistance;
	/** in meters, distance using calibrated speed sensor*/
	private double powerWheelTripDistance;
	/** speed value from wheel sensor */
	private double sensorSpeed;
    /** is sensorSpeed current */
	private boolean sensorSpeedCurrent;
	/** speed value from PowerTap sensor */
	private double powerSpeed;
    /** is powerSpeed current */
	private boolean powerSpeedCurrent;
    /** speed from gps receiver */
	private double gpsSpeed;
    /** is gps Speed current */
    boolean gpsSpeedCurrent;
	/** current bike Location (Latitude, Longitude, Altitude, time) */
	private Location lastGoodWP = new Location(LocationManager.GPS_PROVIDER);
	/** previous bike Location */
	private Location prevGoodWP = new Location(LocationManager.GPS_PROVIDER);
	private double wheelRideTime;
	private double powerWheelRideTime;
	/** Time in seconds since the current trip started */
	private double gpsRideTime;
	/** if we're paused, allow screen to dim, write app message
	calculate DOT using magnetic sensor and don't increment the ride time clock */
	private boolean paused = true;
    /** speed value to display and write to track record; combined in setSpeed() */
    private double speed = 0.;
    /** in meters per sec by dividing tripDistance by tripTime */
    private double avgSpeed = 0;
    /** in meters per sec */
    private double maxSpeed = 0;
	/** heart-rate value from ANT heart-rate monitor (BPM) */
	private int heartRate = 0;
	/** average heart rate (BPM) */
	private int avgHeartRate = 0;
	/** maximum heart rate (BPM) */
	private int maxHeartRate = 0;
	/** cadence value from ANT speed-cadence sensor rpm*/
	private int cadence = 0;
	/** average cadence value rpm */
	private int avgCadence = 0;
	/** maximum cadence value rpm */
	private int maxCadence = 0;
	/** prev power from ANT power meter (Watts) */
	private int prevPower = 0;
	/** power from ANT power meter (Watts) */
	private int power = 0;
	/** average power from ANT power meter (Watts) */
	private int avgPower = 0;
	/** maximum power from ANT power meter (Watts) */
	private int maxPower = 0;
	/** has ANT heart rate monitor */
	boolean hasHR = false;
	/** has ANT cadence sensor; either stand-alone speed or part of speed-cadence */
	boolean hasCadence = false;
	/** has ANT cadence sensor from crank power meter */
	boolean hasPowerCadence = false;
	/** has ANT power monitor */
	boolean hasPower = false;
	/** calibrated speed sensor; either stand-alone speed or part of speed-cadence */
	boolean hasSpeed = false;
	/** uncalibrated speed sensor; either stand-alone speed or part of speed-cadence. We can use this for trainer mode, or before we have GPS signals */
	boolean hasSpeedSensor = false;
	/** calibrated power meter speed sensor like from PowerTap */
	boolean hasPowerSpeed = false;
	/** uncalibrated power meter speed sensor like from PowerTap. We can use this for trainer mode, or before we have GPS signals*/
	boolean hasPowerSpeedSensor = false;
	private int prevRawPower;	
	private int rawPower;
	private int instantaneousCrankCadence;
	private int prevInstantaneousCrankCadence;
	private int instantaneousCrankCadenceEST;
	private Context myContext;
	/** this will be the tcx log file */
	TCXLogFile tcxLog;
	/** this will be the fit log file */
	FITLogFile fitLog;
    private int speedColor;
	private int powerCadence;
	private int pedalCadence;

	/**
	 * BikeStat contains all bike related information: trip distance, time,
	 * speeds, and control access to the log file
	 * @param context is the main activity context
	 */
	BikeStat(Context context) {
		gpsRideTime = 0.4;
		tcxLog = new TCXLogFile(context);
		fitLog = new FITLogFile(context);
		myContext = context;
    }
	
	/** the newLocation Handler should call this routine with the new position data.
	 *  If this is the first location of this Trip, put the new Location in both lastGoodWP & prevGoodWP
	 * @param locationCurrent true if the location is not too old
	 * @param firstLocation true if this is the first location of the trip
	 * @param myPlace the new Location data
	 * */
	void setLastGoodWP(Location myPlace, boolean firstLocation, boolean locationCurrent) {
		if (firstLocation) {
			lastGoodWP.setLatitude(myPlace.getLatitude());
			lastGoodWP.setLongitude(myPlace.getLongitude());
			lastGoodWP.setBearing(myPlace.getBearing());
			lastGoodWP.setAltitude(myPlace.getAltitude());
			lastGoodWP.setAccuracy(myPlace.getAccuracy());
			lastGoodWP.setTime(myPlace.getTime() + 2);
			prevGoodWP.setLatitude(myPlace.getLatitude());
			prevGoodWP.setLongitude(myPlace.getLongitude());
			prevGoodWP.setBearing(myPlace.getBearing());
			prevGoodWP.setAltitude(myPlace.getAltitude());
			prevGoodWP.setAccuracy(myPlace.getAccuracy());
			prevGoodWP.setTime(myPlace.getTime() + 1);
		} else {
			// swap lastGoodWP into prevGoodWP and add newLocation to lastGoodWP
			prevGoodWP.setLatitude(lastGoodWP.getLatitude());
			prevGoodWP.setLongitude(lastGoodWP.getLongitude());
			prevGoodWP.setBearing(lastGoodWP.getBearing());
			prevGoodWP.setAltitude(lastGoodWP.getAltitude());
			prevGoodWP.setAccuracy(lastGoodWP.getAccuracy());
			prevGoodWP.setTime(lastGoodWP.getTime());
			lastGoodWP.setLatitude(myPlace.getLatitude());
			lastGoodWP.setLongitude(myPlace.getLongitude());
			lastGoodWP.setBearing(myPlace.getBearing());
			lastGoodWP.setAltitude(myPlace.getAltitude());
			lastGoodWP.setAccuracy(myPlace.getAccuracy());
			lastGoodWP.setTime(myPlace.getTime());
			// if Location sensor uses milliseconds, convert to seconds!
			calcTripDistSpeed(locationCurrent);
		}
		gpsSpeed  = myPlace.getSpeed();
        gpsSpeedCurrent = true;
	}
	
/** given a new, valid Location re-calculate all the fields affected by the new location measurement; time is in seconds
 * @param locationCurrent if the location is not more than 3 seconds old
 * */
private void calcTripDistSpeed(boolean locationCurrent) {
		float[] results = {0};
		//distanceBetween returns in meters
		Location.distanceBetween(lastGoodWP.getLatitude(), lastGoodWP.getLongitude(),
				prevGoodWP.getLatitude(), prevGoodWP.getLongitude(), results);
		double deltaDistance = (double) results[0];

		double deltaTime = Math.abs(lastGoodWP.getTime() - prevGoodWP.getTime()) / msecPerSec;
		if (deltaTime < 0.001)
			deltaTime = .05;
		if (!isPaused()) {
			gpsTripDistance += deltaDistance;
			gpsRideTime += deltaTime;
			//save in sharedPrefs
			SharedPreferences settings = myContext.getSharedPreferences(PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(TRIP_TIME, Double.toString(getGPSRideTime()));
			editor.putString(TRIP_DISTANCE, Double.toString(getGPSTripDistance()));
			editor.putString(MAX_SPEED, Double.toString(getMaxSpeed()));
			editor.apply();
		}
		avgSpeed = gpsTripDistance / gpsRideTime;
		// rideTime was initialized to .4 sec to prevent / zero errors
	}

	public void reset() {
		gpsTripDistance = 0.0;
		gpsRideTime = 0.1;
		maxSpeed = 0.0;
		avgSpeed = 0.0;
		speed = 0;
		maxCadence = 0;
		maxHeartRate  = 0;
		maxPower = 0;
		spoofWheelTripDistance = 0.0;
		prevSpoofWheelTripDistance = 0.0;
		prevWheelTripDistance = 0.0;
		wheelTripDistance = 0.0;
		wheelRideTime = 0.1;
		powerWheelTripDistance = 0.0;
		powerWheelRideTime = 0.1;
	}

    /**
     * Called when a speed value is received from a speed sensor, or GPS.
     * Depending on what sensors are available. Speed is used in display and writing track
     *
     * outside of trainer mode
     * 1) if a calibrated speedSensor available use that
     * 2) if a calibrated powerTap available use that
     * 3) if location not current use either speed sensor, or PowerTap
     * 4) use GPS if location is current
     * 5) set -1 if no speed sensors and no GPS. refreshSpeed wil set display to "XX.X"
     *
     * In trainerMode
     * 1) if speedSensor available, use sensorSpeed
     * 2) if powerSpeedSensor available use powerSpeed
     */
    public void setSpeed(boolean trainerMode){

        speedColor = ContextCompat.getColor(myContext, R.color.white);
        boolean hiViz = PreferenceManager.getDefaultSharedPreferences(myContext).getBoolean(HI_VIZ, false);
        if (hiViz){
            speedColor = ContextCompat.getColor(myContext, R.color.texthiviz);
        }
        if (trainerMode){
            if (hasSpeedSensor && sensorSpeedCurrent){
//				Log.wtf(this.getClass().getName(), "setSpeed TM-  hasSpeedSensor & current");
                speed = sensorSpeed;
            } else if (hasPowerSpeedSensor && powerSpeedCurrent){
//				Log.wtf(this.getClass().getName(), "setSpeed TM- hasPowerSpeedSensor & current");
                speed = powerSpeed;
            } else {
//				Log.wtf(this.getClass().getName(), "setSpeed TM- no sensors");
                speed = -1;// don't have sensors
            }
        } else {
            if (hasSpeed && sensorSpeedCurrent){
//				Log.wtf(this.getClass().getName(), "setSpeed - hasSpeed & current");
                speed = sensorSpeed;
                speedColor = ContextCompat.getColor(myContext, R.color.cal_speed);
            } else if (hasPowerSpeed && powerSpeedCurrent){
//				Log.wtf(this.getClass().getName(), "setSpeed -  hasPowerSpeed & current");
                speed = powerSpeed;
                speedColor = ContextCompat.getColor(myContext, R.color.cal_speed);
            } else if (gpsSpeedCurrent){
//				Log.wtf(this.getClass().getName(), "setSpeed -  gpsSpeedCurrent");
                speed = gpsSpeed;
            } else if (hasSpeedSensor && sensorSpeedCurrent){
//				Log.wtf(this.getClass().getName(), "setSpeed -  hasSpeedSensor & current");
                speed = sensorSpeed;
                speedColor = ContextCompat.getColor(myContext, R.color.uncal_speed);
            } else if (hasPowerSpeedSensor && powerSpeedCurrent){
//				Log.wtf(this.getClass().getName(), "setSpeed -  hasPowerSpeedSensor & current");
                speed = powerSpeed;
                speedColor = ContextCompat.getColor(myContext, R.color.uncal_speed);
            } else{
//				Log.wtf(this.getClass().getName(), "setSpeed - no sensors");
                speed = -1;//don't have sensors or gps
            }
        }
        if (paused && speed != -1){
            speed = 0;
        }
        if (speed > maxSpeed){
            maxSpeed = speed;
        }
    }

    /**
     * Based on the source for speed, set the speedometer display color
     * called from refreshSpeed()
     * @return color value for speed display
     */
    int getSpeedColor() {
        return speedColor;
    }

	double getGPSTripDistance() {
		return gpsTripDistance;
	}

	double getAvgSpeed() {
		return avgSpeed;
	}

	double getMaxSpeed() {
		return maxSpeed;
	}

	Location getLastGoodWP() {
		return lastGoodWP; // a pointer to this Location
	}

	double getGPSRideTime() {
		return gpsRideTime;
	}

	/** method to display trip time as a string hours:minutes:seconds */
	@SuppressLint("DefaultLocale")
	String getTripTimeStr(double time) {
		int day = (int) TimeUnit.SECONDS.toDays((long) time);
		long hours = TimeUnit.SECONDS.toHours((long) time)
				- (day * 24);
		long minutes = TimeUnit.SECONDS.toMinutes((long) time)
				- (TimeUnit.SECONDS.toHours((long) time) * 60);
		long seconds = TimeUnit.SECONDS.toSeconds((long) time)
				- (TimeUnit.SECONDS.toMinutes((long) time) * 60);
		return String.format("%02d", hours) + ":"
				+ String.format("%02d", minutes) + ":"
				+ String.format("%02d", seconds);
	}

	void setGPSTripTime(double d) {
		this.gpsRideTime = d;
	}

	void setGPSTripDistance(double d) {
		this.gpsTripDistance = d;
	}

	boolean isPaused() {
		return paused;
	}

	void setPaused(boolean paused) {
		this.paused = paused;
	}

	void setHR(int heartRate) {
		this.heartRate = heartRate;
	}
	int getHR() {
		return heartRate;
	}

	public void setPower(int power) {
		this.power = power;
	}

	public int getPower() {
		return power;
	}

	void setRawPower(int power) {
		this.rawPower = power;
	}

	int getRawPower() {
		return rawPower;
	}

	int getAvgHeartRate() {
		return avgHeartRate;
	}

	void setAvgHeartRate(int avgHeartRate) {
		this.avgHeartRate = avgHeartRate;
	}

	int getAvgPower() {
		return avgPower;
	}

	void setAvgPower(int avgPower) {
		this.avgPower = avgPower;
	}

	int getMaxHeartRate() {
		return maxHeartRate;
	}

	void setMaxHeartRate(int maxHeartRate) {
		this.maxHeartRate = maxHeartRate;
	}

	int getMaxPower() {
		return maxPower;
	}

	void setMaxPower(int maxPower) {
		this.maxPower = maxPower;
	}

	public int getCadence() {
		return cadence;
	}
	public void setCadence(int cadence) {
		this.cadence = cadence;
	}
	void setPedalCadence(int cadence) {
		this.pedalCadence = cadence;
		if (hasCadence){
			this.cadence = pedalCadence;
		} else{
			this.cadence = powerCadence;
		}
	}
	void setPowerCadence(int powerCadence) {
		this.powerCadence = powerCadence;
		if (hasCadence){
			this.cadence = pedalCadence;
		} else{
			this.cadence = powerCadence;
		}
	}
	public int getPowerCadence(){return powerCadence;}
	int getMaxCadence() {
		return maxCadence;
	}

	void setMaxCadence(int maxCadence) {
		this.maxCadence = maxCadence;
	}

	int getAvgCadence() {
		return avgCadence;
	}

	void setAvgCadence(int avgCadence) {
		this.avgCadence = avgCadence;
	}

	double getWheelTripDistance() {
		return wheelTripDistance;
	}

	void setWheelTripDistance(double wheelTripDistance) {
		this.wheelTripDistance = wheelTripDistance;
	}

	double getPowerWheelTripDistance() {
		return powerWheelTripDistance;
	}

	void setPowerWheelTripDistance(double powerWheelTripDistance) {
		this.powerWheelTripDistance = powerWheelTripDistance;
	}

	int getPrevRawPower() {
		return prevRawPower;
	}

	void setPrevRawPower(int prevRawPower) {
		this.prevRawPower = prevRawPower;
	}

	int getPrevPower() {
		return prevPower;
	}

	void setPrevPower(int prevPower) {
		this.prevPower = prevPower;
	}

	double getPrevWheelTripDistance() {
		return prevWheelTripDistance;
	}

	void setPrevWheelTripDistance(double prevWheelTripDistance) {
		this.prevWheelTripDistance = prevWheelTripDistance;
	}

	double getSpoofWheelTripDistance() {
		return spoofWheelTripDistance;
	}

	void setSpoofWheelTripDistance(double spoofWheelTripDistance) {
		this.spoofWheelTripDistance = spoofWheelTripDistance;
	}

	double getPrevSpoofWheelTripDistance() {
		return prevSpoofWheelTripDistance;
	}

	void setPrevSpoofWheelTripDistance(double prevSpoofWheelTripDistance) {
		this.prevSpoofWheelTripDistance = prevSpoofWheelTripDistance;
	}
	Location getLastLocation() {
		return lastGoodWP;
	}

	int getInstantaneousCrankCadence() {
		return instantaneousCrankCadence;
	}

	void setInstantaneousCrankCadence(int instantaneousCrankCadence) {
		this.instantaneousCrankCadence = instantaneousCrankCadence;
	}

	int getPrevInstantaneousCrankCadence() {
		return prevInstantaneousCrankCadence;
	}

	void setPrevInstantaneousCrankCadence(
			int prevInstantaneousCrankCadence) {
		this.prevInstantaneousCrankCadence = prevInstantaneousCrankCadence;
	}

	int getInstantaneousCrankCadenceEST() {
		return instantaneousCrankCadenceEST;
	}

	void setInstantaneousCrankCadenceEST(int instantaneousCrankCadenceEST) {
		this.instantaneousCrankCadenceEST = instantaneousCrankCadenceEST;
	}

	double getPowerWheelRideTime() {
		return powerWheelRideTime;
	}

	void setPowerWheelRideTime(double powerWheelRideTime) {
		this.powerWheelRideTime = powerWheelRideTime;
	}

	double getWheelRideTime() {
		return wheelRideTime;
	}

	void setWheelRideTime(double wheelRideTime) {
		this.wheelRideTime = wheelRideTime;
	}

	public double getSpeed() {
		return speed;
	}

	void setSensorSpeed(double sensorSpeed) {
		this.sensorSpeed = sensorSpeed;
	}

	void setPowerSpeed(double powerSpeed) {
		this.powerSpeed = powerSpeed;
	}

	void setSensorSpeedCurrent(boolean sensorSpeedCurrent) {
		this.sensorSpeedCurrent = sensorSpeedCurrent;
	}

	void setPowerSpeedCurrent(boolean powerSpeedCurrent) {
		this.powerSpeedCurrent = powerSpeedCurrent;
	}

    void setMaxSpeed(Double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    double getSensorSpeed() {
        return sensorSpeed;
    }

    double getGpsSpeed() {
        return gpsSpeed;
    }

    double getPowerSpeed() {
        return powerSpeed;
    }

}
