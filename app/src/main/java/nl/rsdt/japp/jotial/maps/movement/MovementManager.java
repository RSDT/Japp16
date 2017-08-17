package nl.rsdt.japp.jotial.maps.movement;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.support.design.widget.Snackbar;
import android.util.Pair;
import android.view.View;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import nl.rsdt.japp.R;
import nl.rsdt.japp.application.Japp;
import nl.rsdt.japp.application.JappPreferences;
import nl.rsdt.japp.jotial.io.AppData;
import nl.rsdt.japp.jotial.maps.deelgebied.Deelgebied;
import nl.rsdt.japp.jotial.maps.management.MarkerIdentifier;
import nl.rsdt.japp.jotial.maps.misc.AnimateMarkerTool;
import nl.rsdt.japp.jotial.maps.misc.LatLngInterpolator;
import nl.rsdt.japp.jotial.maps.wrapper.CameraPosition;
import nl.rsdt.japp.jotial.maps.wrapper.JotiMap;
import nl.rsdt.japp.jotial.maps.wrapper.Marker;
import nl.rsdt.japp.jotial.maps.wrapper.Polyline;
import nl.rsdt.japp.service.LocationService;
import nl.rsdt.japp.service.ServiceManager;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 2-8-2016
 * Description...
 */
public class MovementManager implements ServiceManager.OnBindCallback<LocationService.LocationBinder>, LocationListener {

    private static final String STORAGE_KEY = "TAIL";

    private static final String BUNDLE_KEY = "MovementManager";

    private LocationService service;

    private JotiMap jotiMap;

    private Marker marker;

    private Polyline tail;

    private float bearing;

    private Location lastLocation;

    private FollowSession activeSession;

    private Deelgebied deelgebied;

    private View snackBarView;

    public void setSnackBarView(View snackBarView) {
        this.snackBarView = snackBarView;
    }

    public FollowSession newSession(CameraPosition before, float zoom, float aoa) {
        if(activeSession != null) {
            activeSession.end();
            activeSession = null;
        }
        activeSession = new FollowSession(before, zoom, aoa);
        return activeSession;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (marker != null) {
            if (lastLocation != null) {
                bearing = lastLocation.bearingTo(location);

                /**
                 * Animate the marker to the new position
                 * */
                AnimateMarkerTool.animateMarkerToICS(marker,new LatLng(location.getLatitude(), location.getLongitude()), new LatLngInterpolator.Linear(), 1000);
                marker.setRotation(bearing);
            } else {
                marker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
            }

            List<LatLng> points = tail.getPoints();
            if (points.size() > 150) {
                points = points.subList(points.size() / 3, points.size());
            }
            points.add(new LatLng(location.getLatitude(), location.getLongitude()));
            tail.setPoints(points);
        }
        boolean refresh = false;
        if(deelgebied != null) {
            if(!deelgebied.containsLocation(location)) {
                refresh = true;

                /**
                 * Unsubscribe from the current deelgebied messages
                 * */
                FirebaseMessaging.getInstance().unsubscribeFromTopic(deelgebied.getName());
            }
        } else {
            refresh = true;
        }

        if(refresh) {
            deelgebied = Deelgebied.resolveOnLocation(location);
            if(deelgebied != null && snackBarView != null) {
                Snackbar.make(snackBarView, "Welkom in deelgebied " + deelgebied.getName(), Snackbar.LENGTH_LONG).show();

                /**
                 * Subscribe to the new deelgebied messages.
                 * */
                FirebaseMessaging.getInstance().subscribeToTopic(deelgebied.getName());
            }
        }
        if (marker  != null) {
            /**
             * Make the marker visible
             * */
            if (!marker.isVisible()) {
                marker.setVisible(true);
            }
        }
        if(activeSession != null) {
            activeSession.onLocationChanged(location);
        }

        lastLocation = location;
    }

    @Override
    public void onBind(LocationService.LocationBinder binder) {
        service = binder.getInstance();
        service.add(this);
        service.setRequest(new LocationRequest()
                .setInterval(700)
                .setFastestInterval(100)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY));
    }

    public void onMapReady(JotiMap jotiMap) {
        this.jotiMap = jotiMap;

        MarkerIdentifier identifier = new MarkerIdentifier.Builder()
                .setType(MarkerIdentifier.TYPE_ME)
                .add("icon", String.valueOf(R.drawable.me))
                .create();

        marker = jotiMap.addMarker(new Pair<MarkerOptions, Bitmap>(
                new MarkerOptions()
                        .position(new LatLng(52.021818, 6.059603))
                        .visible(false)
                        .flat(true)
                        .title(new Gson().toJson(identifier)), BitmapFactory.decodeResource(Japp.getInstance().getResources(), R.drawable.me)));

        tail = jotiMap.addPolyline(
                new PolylineOptions()
                        .width(3)
                        .color(Color.BLUE));

        /**
         * TODO: use the onSaveInstane way instead of loading the list of locations each time the fragment gets recreated.
         * */
        List<LatLng> list = AppData.getObject(STORAGE_KEY, new TypeToken<List<LatLng>>() {}.getType());
        if(list != null && !list.isEmpty()) {
            tail.setPoints(list);

            int last;
            if(list.size() > 1) {
                last = list.size() - 1;
            } else {
                last = 0;
            }
            marker.setPosition(list.get(last));
            marker.setVisible(true);
        }
    }

    public class FollowSession implements LocationSource.OnLocationChangedListener {

        private float zoom = 19f;

        private float aoa = 45f;

        private CameraPosition before;

        public FollowSession(CameraPosition before, float zoom, float aoa) {
            this.before = before;
            this.zoom = zoom;
            this.aoa = aoa;

            /**
             * Enable controls.
             * */
            jotiMap.getUiSettings().setAllGesturesEnabled(true);
            jotiMap.getUiSettings().setCompassEnabled(true);

            jotiMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
                @Override
                public void onCameraMoveStarted(int i) {
                    switch (i) {
                        case REASON_GESTURE:
                            CameraPosition position = jotiMap.getCameraPosition();
                            setZoom(position.getZoom());
                            setAngleOfAttack(position.getTilt());
                            break;
                    }
                }
            });

        }

        public void setZoom(float zoom) {
            this.zoom = zoom;
        }

        public void setAngleOfAttack(float aoa) {
            this.aoa = aoa;
        }

        @Override
        public void onLocationChanged(Location location) {
            /**
             * Animate the camera to the new position
             * */

            jotiMap.cameraToLocation(true, location, zoom, aoa, bearing);
        }

        public void end() {

            /**
             * Save the settings of the session to the release_preferences
             * */
            JappPreferences.setFollowZoom(zoom);
            JappPreferences.setFollowAoa(aoa);

            /**
             * Disable controls
             * */
            jotiMap.getUiSettings().setCompassEnabled(false);

            /**
             * Remove callback
             * */
            jotiMap.setOnCameraMoveStartedListener(null);

            /**
             * Move the camera to the before position
             * */
            //googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(before));

            activeSession = null;
        }
    }

    public void onResume() {
        if(service != null) {
            service.setRequest(new LocationRequest()
                    .setInterval(700)
                    .setFastestInterval(100)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY));
        }
    }

    public void onPause() {
        if(service != null) {
            service.setRequest(service.getStandard());
        }
    }

    private void save(boolean background) {
        if(tail != null) {
            if(background) {
                AppData.saveObjectAsJsonInBackground(tail.getPoints(), STORAGE_KEY);
            } else {
                AppData.saveObjectAsJson(tail.getPoints(), STORAGE_KEY);
            }
        }
    }

    public void onDestroy() {

        if(marker != null) {
            marker.remove();
            marker = null;
        }

        if(tail != null) {
            save(false);
            tail.remove();
            tail = null;
        }

        if(jotiMap != null) {
            jotiMap = null;
        }

        if(marker != null) {
            marker.remove();
            marker = null;
        }

        if(lastLocation != null) {
            lastLocation = null;
        }

        if(activeSession != null) {
            activeSession = null;
        }

        if(service != null) {
            service.remove(this);
            service = null;
        }

    }

}
