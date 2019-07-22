package nl.rsdt.japp.jotial.maps.wrapper.osm;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.util.Pair;

import com.google.android.gms.maps.GoogleMap;
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
import nl.rsdt.japp.jotial.maps.window.CustomInfoWindowAdapter;
import nl.rsdt.japp.jotial.maps.wrapper.ICameraPosition;
import nl.rsdt.japp.jotial.maps.wrapper.ICircle;
import nl.rsdt.japp.jotial.maps.wrapper.IJotiMap;
import nl.rsdt.japp.jotial.maps.wrapper.IMarker;
import nl.rsdt.japp.jotial.maps.wrapper.IPolygon;
import nl.rsdt.japp.jotial.maps.wrapper.IPolyline;
import nl.rsdt.japp.jotial.maps.wrapper.IUiSettings;


/**
 * Created by mattijn on 07/08/17.
 */

public class OsmJotiMap implements IJotiMap{

    private final MapView osmMap; //todo fix type

    private static Map<MapView,OsmJotiMap> osm_instances = new HashMap<>();

    private MapEventsOverlay eventsOverlay;

    private GeoPoint previousCameraPosition;

    private int previousZoom;

    private float previousRoation;


    private OsmJotiMap(MapView map){
        osmMap  = map;

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


    public static OsmJotiMap getJotiMapInstance(MapView map){
        if (!osm_instances.containsKey(map)) {
            OsmJotiMap jm = new OsmJotiMap(map);
            osm_instances.put(map,jm);
        }
        return osm_instances.get(map);
    }

    @Override
    public void setPreviousCameraPosition(double latitude, double longitude) {
        previousCameraPosition = new GeoPoint(latitude, longitude);
    }

    @Override
    public void setPreviousZoom(int previousZoom) {
        this.previousZoom = previousZoom;
    }

    @Override
    public void setPreviousRotation(float rotation) {
        this.previousRoation = rotation;
    }

    public void delete() {
        if (osm_instances.containsValue(this)) {
            osm_instances.remove(this.getOSMMap());
        }
    }

    public void setInfoWindowAdapter(CustomInfoWindowAdapter infoWindowAdapter) {
        //// TODO: 30/08/17
    }

    public void setGMapType(int mapType) {
        //// TODO: 30/08/17
    }

    public boolean setMapStyle(MapStyleOptions mapStyleOptions) {
        return false; //// TODO: 30/08/17
    }

    public IUiSettings getUiSettings() {
        return new OsmUiSettings(osmMap);
    }
    public void animateCamera(LatLng latLng, int zoom)  {
        osmMap.getController().animateTo(new GeoPoint(latLng.latitude, latLng.longitude));
        osmMap.getController().zoomTo(zoom);//// TODO: 07/08/17 controleren of de zoomlevels van googlemaps en osm enigzins overeenkomen.
    }

    public IMarker addMarker(Pair<MarkerOptions, Bitmap> markerOptions) {
        return new OsmMarker(markerOptions, osmMap);
    }

    public IPolyline addPolyline(PolylineOptions polylineOptions) {
        return new OsmPolyline(polylineOptions, osmMap);
    }

    public IPolygon addPolygon(PolygonOptions polygonOptions) {
        return new OsmPolygon(polygonOptions, osmMap);
    }

    public ICircle addCircle(CircleOptions circleOptions) {
        return new OsmCircle(circleOptions, osmMap);
    }

    public MapView getOSMMap() {
        return osmMap;
    }

    public void setOnMapClickListener(final OnMapClickListener onMapClickListener) {
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
    }

    public ICameraPosition getPreviousCameraPosition() {
        return new OsmCameraPosition(osmMap);
    }

    public void snapshot(final IJotiMap.SnapshotReadyCallback snapshotReadyCallback) {
        this.osmMap.setDrawingCacheEnabled(true);
        this.osmMap.buildDrawingCache();
        Bitmap bm = this.osmMap.getDrawingCache();
        snapshotReadyCallback.onSnapshotReady(bm);
    }

    public void animateCamera(LatLng latLng,int zoom, final IJotiMap.CancelableCallback cancelableCallback) {
        osmMap.getController().setCenter(new GeoPoint(latLng.latitude,latLng.longitude));
        osmMap.getController().setZoom(zoom);
        cancelableCallback.onFinish();
    }

    public void setOnCameraMoveStartedListener(final GoogleMap.OnCameraMoveStartedListener onCameraMoveStartedListener) {
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
    }

    public void cameraToLocation(boolean b, Location location, float zoom, float aoa, float bearing) {
        osmMap.getController().animateTo(new GeoPoint(location.getLatitude(), location.getLongitude()));
    }

    public void clear() {
        osmMap.getOverlays().clear();
    }

    public void setOnInfoWindowLongClickListener(GoogleMap.OnInfoWindowLongClickListener onInfoWindowLongClickListener) {
        //// TODO: 30/08/17
    }

    @Override
    public void setMarkerOnClickListener(OnMarkerClickListener listener) {
        OsmMarker.setAllOnClickLister(listener);
    }

    @Override
    public void getMapAsync(OnMapReadyCallback callback) {
        callback.onMapReady(this);
    }
}
