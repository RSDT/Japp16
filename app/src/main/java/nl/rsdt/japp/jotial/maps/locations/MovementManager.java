package nl.rsdt.japp.jotial.maps.locations;

import android.location.Location;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
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
 * @since 2-8-2016
 * Description...
 */
public class MovementManager extends LocationProvider implements OnMapReadyCallback {

    private GoogleMap googleMap;

    private Marker marker;

    private float bearing;

    private Location lastLocation;

    private FollowSession activeSession;

    public MovementManager() {}

    public FollowSession newSession(CameraPosition before, float zoom, float aoa) {
        if(activeSession != null) {
            activeSession.end();
            activeSession = null;
        }
        activeSession = new FollowSession();
        return activeSession;
    }

    @Override
    public void onLocationChanged(Location location) {

        if(lastLocation != null)
        {
            bearing = lastLocation.bearingTo(location);
        }

        if(activeSession != null) {
            activeSession.onLocationChanged(location);
        }

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
    public void onMapReady(GoogleMap googleMap) {
        marker = googleMap.addMarker(
                new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.me))
                        .position(new LatLng(0, 0))
                        .visible(false)
                        .flat(true));
    }


    public class FollowSession implements LocationSource.OnLocationChangedListener {

        private float zoom = 20f;

        private float aoa = 45f;

        private CameraPosition before;

        @Override
        public void onLocationChanged(Location location) {
            /**
             * Animate the camera to the new position
             * */
            CameraUtils.cameraToLocation(true, googleMap, location, zoom, aoa, bearing);
        }

        public void end() {

            /**
             * Move the camera to the before position
             * */
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(before));
        }
    }

}
