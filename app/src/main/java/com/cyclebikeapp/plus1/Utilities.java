package com.cyclebikeapp.plus1;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import static com.cyclebikeapp.plus1.Constants.MOBILE_DATA_SETTING_KEY;
import static com.cyclebikeapp.plus1.Constants.ZERO;

/**
 * Created by TommyD on 4/18/2016.
 *
 */
class Utilities {

    static boolean hasInternetConnection(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean hasInternetPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED;
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting() && hasInternetPermission;
    }

    static boolean hasMobileDataPermission(SharedPreferences settings) {
        return Integer.parseInt(settings.getString(MOBILE_DATA_SETTING_KEY, ZERO)) == 1;
    }

    static boolean hasMobileInternetPermission(SharedPreferences settings) {
        return Integer.parseInt(settings.getString(MOBILE_DATA_SETTING_KEY, ZERO)) == 1;
    }

    static boolean hasWifiInternetConnection(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean hasInternetPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED;
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting() && hasInternetPermission
                && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
    }

    static boolean isGPSLocationEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (SecurityException ignored) {
        }
        return gps_enabled;
    }
    static boolean hasGPSPermission (Context context) {

        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }
    static boolean isNetworkLocationEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean network_enabled = false;
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (SecurityException ignored) {
        }
        return network_enabled;
    }
    static boolean areLocationServicesAvailable(Context context) {
        return isGPSLocationEnabled(context);
    }
    /* Checks if external storage is available for read and write */
    static boolean isExternalStorageWritable(Context context) {
        String state = Environment.getExternalStorageState();
        boolean hasWritePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return Environment.MEDIA_MOUNTED.equals(state) && hasWritePermission;
    }

     static boolean hasLocationPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    static boolean hasFineLocationPermission(Context context) {
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    static boolean hasStoragePermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

}
