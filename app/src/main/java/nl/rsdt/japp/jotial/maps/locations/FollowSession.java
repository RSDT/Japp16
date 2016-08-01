package nl.rsdt.japp.jotial.maps.locations;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import nl.rsdt.japp.R;
import nl.rsdt.japp.jotial.maps.misc.AnimateMarkerTool;
import nl.rsdt.japp.jotial.maps.misc.CameraUtils;
import nl.rsdt.japp.jotial.maps.misc.LatLngInterpolator;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 25-7-2016
 * Description...
 */
public class FollowSession extends LocationProvider {

    private GoogleMap googleMap;

    private Marker marker;

    private float zoom = 20f;

    private float aoa = 45f;

    private float bearing;

    private CameraPosition before;

    private Location lastLocation;

    protected FollowSession() {
        request = new LocationRequest()
                .setInterval(700)
                .setFastestInterval(100)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void intialize()
    {
        marker = googleMap.addMarker(
                new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.me))
                        .position(new LatLng(0, 0))
                        .visible(false)
                        .flat(true));
    }

    public void start() {

    }

    @Override
    public void onLocationChanged(Location location) {

        if(lastLocation != null)
        {
            bearing = lastLocation.bearingTo(location);
        }

        /**
         * Animate the camera to the new position
         * */
        CameraUtils.cameraToLocation(true, googleMap, location, zoom, aoa, bearing);

        /**
         * Animate the marker to the new position
         * */
        AnimateMarkerTool.animateMarkerToICS(marker, new LatLng(location.getLatitude(), location.getLongitude()), new LatLngInterpolator.Linear(), 100);
        marker.setRotation(bearing);

        /**
         * Make the marker visible
         * */
        if(!marker.isVisible()) marker.setVisible(true);

        lastLocation = location;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        super.onConnected(bundle);
        startLocationUpdates();
    }

    public void end() {

        /**
         * Move the camera back to the begin position
         * */
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(before));

        /**
         * Destroy
         * */
        onDestroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        googleMap = null;

        if(marker != null)
        {
            marker.remove();
            marker = null;
        }

        before = null;

        lastLocation = null;

    }

    public static class Builder
    {
        FollowSession buffer = new FollowSession();

        public Builder setGoogleMap(GoogleMap googleMap)
        {
            buffer.googleMap = googleMap;
            return this;
        }

        public Builder setZoom(float zoom)
        {
            buffer.zoom = zoom;
            return this;
        }

        public Builder setAngleOfAttack(float aoa)
        {
            buffer.aoa = aoa;
            return this;
        }

        public Builder setBearing(float bearing)
        {
            buffer.bearing = bearing;
            return this;
        }

        public Builder setBeforeSessionCameraPosition(CameraPosition position)
        {
            buffer.before = position;
            return this;
        }

        public FollowSession create()
        {
            buffer.intialize();
            return buffer;
        }

    }
}
