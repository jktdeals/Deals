package com.jktdeals.deals.helpers;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import static android.support.v4.content.PermissionChecker.checkSelfPermission;

/**
 * Created by kartikkulkarni on 3/5/16.
 */
public class GPSHelper implements LocationListener {

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1; // 1 minute
    private static String TAG = "GPS_HELPER";
    private Context context;
    // flag for GPS Status
    private boolean isGPSEnabled = false;
    // flag for network status
    private boolean isNetworkEnabled = false;
    private LocationManager locationManager;
    private Location location;
    private double latitude;
    private double longitude;


    public GPSHelper(Context context) {
        this.context = context;

        locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);

        if (checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            Log.e(TAG, "no location permissions");
            return;
        }
        Log.d(TAG, "yes location permissions");

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
    }

    public void getMyLocation() {
        List<String> providers = locationManager.getProviders(true);

        Location l = null;
        Log.d("GPS getMyLocation: ", "providersL :" + providers.size());

        for (int i = 0; i < providers.size(); i++) {

            if (checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
            l = locationManager.getLastKnownLocation(providers.get(i));

            if (l != null)
                break;
        }

        if (l != null) {
            Log.d("GPSHelper", "setting lt/lng: " + latitude + "/" + longitude);

            latitude = l.getLatitude();
            longitude = l.getLongitude();
        }
    }

    public boolean isGPSenabled() {
        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        return (isGPSEnabled || isNetworkEnabled);
    }

    /**
     * Function to get latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Function to get longitude
     */
    public double getLongitude() {
        return longitude;
    }

    public LatLng getLatLng() {
        getMyLocation();
        LatLng ret = new LatLng(latitude, longitude);
        Log.d("GPSHelper", ret.toString());
        Log.d("GPSHelper", "isGPSEnabled: " + isGPSenabled());

        return ret;
    }

    public void onLocationChanged(final Location location) {
        //your code here
        Log.d(TAG, "location_update: " + location.toString());

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}