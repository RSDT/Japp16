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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import nl.rsdt.japp.application.Japp;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 25-7-2016
 * Description...
 */
public class UserMovementFollower implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap googleMap;

    private GoogleApiClient client;

    private LocationRequest request;

    private float bearing;

    public float getBearing() {
        return bearing;
    }

    private float zoom;

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    public float getZoom() {
        return zoom;
    }

    private float aoa;

    public void setAngleOfAttack(float aoa) {
        this.aoa = aoa;
    }

    public float getAngleOfAttack() {
        return aoa;
    }

    private boolean follow = false;

    public boolean isFollowing() {
        return follow;
    }

    public void setFollow(boolean follow) {
        this.follow = follow;

        if(follow)
        {
            restartLocationUpdates();
        }
        else
        {
            removeLocationUpdates();
        }
    }

    public void onCreate() {
        buildGoogleApiClient();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
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

    /**
     * Moves the camera to given location.
     */
    private void cameraToLocation(Location location)
    {
        if(location != null)
        {
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(location.getLatitude(), location.getLongitude()), zoom, aoa, bearing)));
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(follow)
        {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        cameraToLocation(location);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void onDestroy() {

        if(client != null)
        {
            client.disconnect();
            client = null;
        }

        request = null;
    }

}
