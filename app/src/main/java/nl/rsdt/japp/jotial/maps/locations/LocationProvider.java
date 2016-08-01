package nl.rsdt.japp.jotial.maps.locations;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
public abstract class LocationProvider implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {

    protected GoogleApiClient client;

    protected LocationRequest request = new LocationRequest();

    public LocationProvider()
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

    }


    public void removeLocationUpdates()
    {
        LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
    }

    public void restartLocationUpdates()
    {
        removeLocationUpdates();
        startLocationUpdates();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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

}
