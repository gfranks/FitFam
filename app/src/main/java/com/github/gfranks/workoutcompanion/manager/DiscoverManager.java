package com.github.gfranks.workoutcompanion.manager;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;

import info.metadude.android.typedpreferences.DoublePreference;

public class DiscoverManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final int REQUEST_LOCATION_PERMISSION = 1;

    private static final String KEY_LAT = "lat";
    private static final String KEY_LNG = "lng";

    private GoogleApiClient mGoogleApiClient;
    private DoublePreference mLatitudePreference;
    private DoublePreference mLongitudePreference;

    public DiscoverManager(SharedPreferences prefs, Application app) {
        mLatitudePreference = new DoublePreference(prefs, KEY_LAT, 0.0);
        mLongitudePreference = new DoublePreference(prefs, KEY_LNG, 0.0);

        mGoogleApiClient = new GoogleApiClient
                .Builder(app)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }

    /**
     * ***********************************
     * GoogleApiClient.ConnectionCallbacks
     * ***********************************
     */
    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    /**
     * ******************************************
     * GoogleApiClient.OnConnectionFailedListener
     * ******************************************
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    public boolean connect(Activity activity, GoogleApiClient.ConnectionCallbacks onConnectedCallback,
                           GoogleApiClient.OnConnectionFailedListener onFailedToConnectListener) {
        if (hasLocationPermission(activity)) {
            connect(onConnectedCallback, onFailedToConnectListener);
            return true;
        } else {
            activity.requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
            return false;
        }
    }

    public boolean connect(Fragment fragment, GoogleApiClient.ConnectionCallbacks onConnectedCallback,
                           GoogleApiClient.OnConnectionFailedListener onFailedToConnectListener) {
        if (hasLocationPermission(fragment.getActivity())) {
            connect(onConnectedCallback, onFailedToConnectListener);
            return true;
        } else {
            fragment.requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
            return false;
        }
    }

    public void disconnect() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    public LatLng getLastKnownLocation() {
        if (mLatitudePreference.get() == 0.0 || mLongitudePreference.get() == 0.0) {
            LatLng latLng = new LatLng(33.744473, -84.389886);
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (location != null) {
                    return new LatLng(location.getLatitude(), location.getLongitude());
                }
            }

            mLatitudePreference.set(latLng.latitude);
            mLongitudePreference.set(latLng.longitude);
            return latLng;
        } else {
            return new LatLng(mLatitudePreference.get(), mLongitudePreference.get());
        }
    }

    public void setLastKnownLocation(LatLng latLng) {
        mLatitudePreference.set(latLng.latitude);
        mLongitudePreference.set(latLng.longitude);
    }

    public boolean hasLocationPermission(Activity activity) {
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void connect(GoogleApiClient.ConnectionCallbacks onConnectedCallback,
                         GoogleApiClient.OnConnectionFailedListener onFailedToConnectListener) {
        if (mGoogleApiClient != null) {
            if (onConnectedCallback != null) {
                mGoogleApiClient.registerConnectionCallbacks(onConnectedCallback);
            }
            if (onFailedToConnectListener != null) {
                mGoogleApiClient.registerConnectionFailedListener(onFailedToConnectListener);
            }
            mGoogleApiClient.connect();
        }
    }
}