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

import nl.rsdt.japp.application.Japp;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 25-7-2016
 * Description...
 */
public abstract class LocationProviderService extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {

    public static final String TAG = "LocationProviderService";

    private final LocationBinder binder = new LocationBinder();

    protected GoogleApiClient client;

    protected LocationRequest request = new LocationRequest();

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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
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

    public class LocationApiInterface {

        public Location getLastLocation() {
            return lastLocation;
        }

        public void setRequest(LocationRequest request) {
            LocationProviderService.this.request = request;
            if(isRequesting) {
                restartLocationUpdates();
            }
        }

        public boolean isRequesting() {
            return isRequesting;
        }

    }

    public class LocationBinder extends Binder {
        public LocationApiInterface getApi() {
            return new LocationApiInterface();
        }
    }

}

