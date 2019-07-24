package nl.rsdt.japp.jotial.maps.locations;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.ArrayList;

import nl.rsdt.japp.application.Japp;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 25-7-2016
 * Description...
 */
public abstract class LocationProviderService<B extends Binder> extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<LocationSettingsResult> {

    public static final String TAG = "LocationProviderService";

    protected GoogleApiClient client;

    protected LocationRequest request = new LocationRequest();

    protected ArrayList<LocationListener> listeners = new ArrayList<>();

    protected Location lastLocation;

    protected boolean isRequesting = false;

    public LocationProviderService() {
        buildGoogleApiClient();
    }

    /**
     * Builds the GoogleApiClient.
     * */
    protected synchronized void buildGoogleApiClient() {
        client = new GoogleApiClient.Builder(Japp.getInstance())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        client.connect();
    }

    public void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);
        isRequesting = true;
    }


    public void removeLocationUpdates()
    {
        LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
        isRequesting = false;
    }

    public void restartLocationUpdates()
    {
        removeLocationUpdates();
        startLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        LocationListener listener;
        for(int i = 0; i < listeners.size(); i++) {
            listener = listeners.get(i);
            if(listener != null) {
                listener.onLocationChanged(location);
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
       startLocationUpdates();
        //// TODO: 20/10/17 quickfix #127 de bug is erin gekomen in df4e994826ede9b014335c573deb30b7b9fd3455
        // checkLocationSettings();
    }

    public void checkLocationSettings() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(request);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(client,
                        builder.build());
        result.setResultCallback(this);
    }

    @Override
    public void onResult(LocationSettingsResult result) {
        final Status status = result.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                // All location settings are satisfied. The client can
                // initialize location requests here.
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                // Location settings are not satisfied. However, we have no way
                // to fix the settings so we won't show the dialog.

                break;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "Connection suspended : " + i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        String error = connectionResult.getErrorMessage();
        if(error == null) error = connectionResult.toString();
        Log.e(TAG, error);
    }

    public void onDestroy() {
        if(client != null)
        {
            removeLocationUpdates();
            client.disconnect();
            client = null;
        }
        request = null;
    }

    public void add(LocationListener listener) {
        this.listeners.add(listener);
    }

    public void remove(LocationListener listener) {
        this.listeners.remove(listener);
    }

    public void setRequest(LocationRequest request) {
        LocationProviderService.this.request = request;
        if(isRequesting && client.isConnected()) {
            restartLocationUpdates();
        }
    }

    public Location getLastLocation() {
        return lastLocation;
    }

}

