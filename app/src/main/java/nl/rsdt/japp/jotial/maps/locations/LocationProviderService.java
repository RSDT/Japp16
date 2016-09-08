package nl.rsdt.japp.jotial.maps.locations;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

import nl.rsdt.japp.application.Japp;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 25-7-2016
 * Description...
 */
public abstract class LocationProviderService<B extends Binder> extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {

    public static final String TAG = "LocationProviderService";

    protected GoogleApiClient client;

    protected LocationRequest request = new LocationRequest();

    protected ArrayList<LocationListener> listeners = new ArrayList<>();

    protected Location lastLocation;

    protected boolean isRequesting = false;

    public LocationProviderService()
    {
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

    public void startLocationUpdates()
    {
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
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "Connection suspended : " + i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, connectionResult.getErrorMessage());
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

