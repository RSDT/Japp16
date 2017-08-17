package nl.rsdt.japp.jotial.maps.wrapper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.util.Pair;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;

import java.util.HashMap;
import java.util.Map;

import nl.rsdt.japp.R;
import nl.rsdt.japp.application.Japp;
import nl.rsdt.japp.jotial.maps.misc.CameraUtils;
import nl.rsdt.japp.jotial.maps.window.CustomInfoWindowAdapter;
import nl.rsdt.japp.jotial.navigation.Navigator;

/**
 * Created by mattijn on 07/08/17.
 */

public class JotiMap {
    private static Map<GoogleMap,JotiMap> google_instances= new HashMap<>();
    public static final int GOOGLEMAPTYPE = 0;
    public static final int OSMMAPTYPE = 1;
    private final int mapType;

    private final GoogleMap googleMap;
    private final MapView osmMap; //todo fix type
    private static Map<MapView,JotiMap> osm_instances = new HashMap<>();
    private MapEventsOverlay eventsOverlay;

    private JotiMap(GoogleMap map){
        mapType = GOOGLEMAPTYPE;
        googleMap = map;
        osmMap = null;
        eventsOverlay = null;
    }

    private JotiMap(MapView map){
        mapType = OSMMAPTYPE;
        osmMap  = map;
        googleMap = null;
        eventsOverlay = new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        });
        osmMap.getOverlays().add(0,eventsOverlay);
    }

    public static JotiMap getJotiMapInstance(GoogleMap map){
        if (!google_instances.containsKey(map)) {
            JotiMap jm = new JotiMap(map);
            google_instances.put(map,jm);
        }
        return google_instances.get(map);
    }

    public static JotiMap getJotiMapInstance(MapView map){
        if (!osm_instances.containsKey(map)) {
            JotiMap jm = new JotiMap(map);
            osm_instances.put(map,jm);
        }
        return osm_instances.get(map);
    }

    public void delete() {
        if (google_instances.containsValue(this)) {
            google_instances.remove(this.getGoogleMap());
        } else if (osm_instances.containsValue(this)) {
            osm_instances.remove(this.getOSMMap());
        }
    }

    public void setInfoWindowAdapter(CustomInfoWindowAdapter infoWindowAdapter) {
        switch (mapType){
            case GOOGLEMAPTYPE:
                googleMap.setInfoWindowAdapter(infoWindowAdapter);
                break;
            case OSMMAPTYPE:
                //// TODO: 09/08/17 implement this
                break;
            default:
                break;
        }
    }

    public void setGMapType(int mapType) {
        switch (this.mapType) {
            case GOOGLEMAPTYPE:
                googleMap.setMapType(mapType);
                break;
            case OSMMAPTYPE:
                break;
            default:
                break;
        }
    }

    public boolean setMapStyle(MapStyleOptions mapStyleOptions) {
        switch (mapType){
            case GOOGLEMAPTYPE:
                return googleMap.setMapStyle(mapStyleOptions);
            case OSMMAPTYPE:
                return false; //// TODO: 09/08/17 moet dit iets doen?
            default:
                return false;
            //throw new RuntimeException("only supported for googleMaps");
        }
    }

    public UiSettings getUiSettings() {
         switch (mapType){
            case GOOGLEMAPTYPE:
                return new UiSettings(googleMap.getUiSettings());
            case OSMMAPTYPE:
                return new UiSettings(osmMap);
            default:
                return null;
        }
    }
    public void animateCamera(LatLng latLng, int zoom)  {
        switch (mapType) {
            case GOOGLEMAPTYPE:
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
                break;
            case OSMMAPTYPE:
                osmMap.getController().animateTo(new GeoPoint(latLng.latitude, latLng.longitude));
                osmMap.getController().zoomTo(zoom);//// TODO: 07/08/17 controleren of de zoomlevels van googlemaps en osm enigzins overeenkomen.
            default:
                break;
        }
    }

    public Marker addMarker(Pair<MarkerOptions, Bitmap> markerOptions) {
        switch (mapType){
            case GOOGLEMAPTYPE:
                if (markerOptions.second != null){
                    markerOptions.first.icon(BitmapDescriptorFactory.fromBitmap(markerOptions.second));
                }else{
                    markerOptions.first.icon(BitmapDescriptorFactory.defaultMarker());
                }
                return new Marker(googleMap.addMarker(markerOptions.first));
            case OSMMAPTYPE:
                return new Marker(markerOptions, osmMap);
            default:
                return null;
            //throw new RuntimeException("only supported for googleMaps");
        }
    }

    public Polyline addPolyline(PolylineOptions polylineOptions) {
        switch (mapType){
            case GOOGLEMAPTYPE:
                return new Polyline(googleMap.addPolyline(polylineOptions));
            case OSMMAPTYPE:
                return new Polyline(polylineOptions, osmMap);
            default:
                return null;
        }
    }

    public Polygon addPolygon(PolygonOptions polygonOptions) {
        switch (mapType) {
            case GOOGLEMAPTYPE:
                return new Polygon(googleMap.addPolygon(polygonOptions));
            case OSMMAPTYPE:
                return new Polygon(polygonOptions, osmMap);
            default:
                return null;
        }
    }

    public Circle addCircle(CircleOptions circleOptions) {
        switch (mapType){
            case GOOGLEMAPTYPE:
                return new Circle(googleMap.addCircle(circleOptions));
            case OSMMAPTYPE:
                return new Circle(circleOptions, osmMap);
            default:
                return null;
        }
    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    public int getMapType() {
        return mapType;
    }

    public MapView getOSMMap() {
        return osmMap;
    }

    public void setOnMapClickListener(final OnMapClickListener onMapClickListener) {
        switch (mapType){
            case GOOGLEMAPTYPE:
                if (onMapClickListener == null){
                    googleMap.setOnMapClickListener(null);
                }else {
                    googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            onMapClickListener.onMapClick(latLng);
                        }
                    });
                }
                break;
            case OSMMAPTYPE:
                osmMap.getOverlays().remove(eventsOverlay);
                eventsOverlay =new MapEventsOverlay(new MapEventsReceiver() {
                    @Override
                    public boolean singleTapConfirmedHelper(GeoPoint p) {
                        if (onMapClickListener != null) {
                            return onMapClickListener.onMapClick(new LatLng(p.getLatitude(), p.getLongitude()));
                        }
                        return false;
                    }

                    @Override
                    public boolean longPressHelper(GeoPoint p) {
                        return false;
                    }
                });
                osmMap.getOverlays().add(0,eventsOverlay);
                break;
            default:
                break;
        }

    }

    public CameraPosition getCameraPosition() {
        switch (mapType){
            case GOOGLEMAPTYPE:
                return new CameraPosition(googleMap.getCameraPosition());
            case OSMMAPTYPE:
                return new CameraPosition(osmMap);
            default:
                return null;
        }
    }

    public void snapshot(final JotiMap.SnapshotReadyCallback snapshotReadyCallback) {
        switch (mapType){
            case GOOGLEMAPTYPE:
                if (snapshotReadyCallback != null) {
                    googleMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                        @Override
                        public void onSnapshotReady(Bitmap bitmap) {
                            snapshotReadyCallback.onSnapshotReady(bitmap);
                        }
                    });
                } else {
                    googleMap.snapshot(null);
                }

                break;
            case OSMMAPTYPE:
                    Bitmap bm = BitmapFactory.decodeResource(Japp.getAppResources(), R.drawable.about_bram);
                    snapshotReadyCallback.onSnapshotReady(bm);
                break;
            default:
                break;
        }
    }

    public void animateCamera(LatLng latLng,int zoom, final JotiMap.CancelableCallback cancelableCallback) {
        switch (mapType) {
            case GOOGLEMAPTYPE:
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
                if (cancelableCallback != null) {
                    googleMap.animateCamera(cameraUpdate, new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            cancelableCallback.onFinish();
                        }

                        @Override
                        public void onCancel() {
                            cancelableCallback.onCancel();
                        }
                    });
                } else {
                    googleMap.animateCamera(cameraUpdate, null);
                }
                break;
            case OSMMAPTYPE:
                osmMap.getController().setCenter(new GeoPoint(latLng.latitude,latLng.longitude));
                osmMap.getController().setZoom(zoom);
                cancelableCallback.onFinish();
                break;
            default:
                break;
        }
    }

    public void setOnCameraMoveStartedListener(final GoogleMap.OnCameraMoveStartedListener onCameraMoveStartedListener) {
        switch (mapType){
            case GOOGLEMAPTYPE:
                if (onCameraMoveStartedListener != null) {
                    googleMap.setOnCameraMoveStartedListener(onCameraMoveStartedListener);
                }else{
                    googleMap.setOnCameraMoveStartedListener(null);
                }
                    break;
            case OSMMAPTYPE:
                if (onCameraMoveStartedListener != null) {
                    osmMap.setMapListener(new MapListener() {
                        @Override
                        public boolean onScroll(ScrollEvent event) {
                            onCameraMoveStartedListener.onCameraMoveStarted(GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE);
                            return false;
                        }

                        @Override
                        public boolean onZoom(ZoomEvent event) {
                            onCameraMoveStartedListener.onCameraMoveStarted(GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE);
                            return false;
                        }
                    });
                }else{
                    osmMap.setMapListener(null);
                }
                break;
            default:
                break;
        }
    }

    public void cameraToLocation(boolean b, Location location, float zoom, float aoa, float bearing) {
        switch (mapType) {
            case GOOGLEMAPTYPE:
                CameraUtils.cameraToLocation(b, googleMap, location, zoom, aoa, bearing);
                break;
            case OSMMAPTYPE:
                osmMap.getController().animateTo(new GeoPoint(location.getLatitude(), location.getLongitude()));
                break;
            default:
                break;

        }
    }

    public void clear() {
        switch (mapType){
            case GOOGLEMAPTYPE:
                googleMap.clear();
                break;
            case OSMMAPTYPE:
                osmMap.getOverlays().clear();
                break;
            default:
        }
    }

    public void setOnInfoWindowLongClickListener(GoogleMap.OnInfoWindowLongClickListener onInfoWindowLongClickListener) {
        switch (mapType){
            case GOOGLEMAPTYPE:
                if (onInfoWindowLongClickListener == null){
                    googleMap.setOnInfoWindowLongClickListener(null);
                }else {
                    googleMap.setOnInfoWindowLongClickListener(onInfoWindowLongClickListener);
                }
                break;
            case OSMMAPTYPE:
                //// TODO: 09/08/17
                break;
            default:
                break;
        }
    }
    public interface OnMapClickListener{

        boolean onMapClick(LatLng latLng);
    }

    public interface CancelableCallback {
        void onFinish();

        void onCancel();
    }
    public interface SnapshotReadyCallback {
        void onSnapshotReady(Bitmap var1);
    }
}
