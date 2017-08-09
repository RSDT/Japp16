package nl.rsdt.japp.jotial.maps.wrapper;

import android.graphics.Bitmap;
import android.location.Location;
import android.util.Pair;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nl.rsdt.japp.jotial.maps.misc.CameraUtils;
import nl.rsdt.japp.jotial.maps.window.CustomInfoWindowAdapter;

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

    private JotiMap(GoogleMap map){
        mapType = GOOGLEMAPTYPE;
        googleMap = map;

        osmMap = null;
    }
    private JotiMap(MapView map){
        mapType = OSMMAPTYPE;
        osmMap  = map;
        googleMap = null;

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
        if (mapType == GOOGLEMAPTYPE) {
            UiSettings sett = new UiSettings(googleMap.getUiSettings());
            return sett;
        } else if (mapType == OSMMAPTYPE){
            return new UiSettings(osmMap);
        }else{
            return null;
            //throw new RuntimeException("only supported for googleMaps");
        }
    }
    public void animateCamera(LatLng latLng, int zoom)  {
        if (mapType == GOOGLEMAPTYPE) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        } else  if (mapType == OSMMAPTYPE){
            osmMap.getController().animateTo(new GeoPoint(latLng.latitude,latLng.longitude));
            osmMap.getController().zoomTo(zoom);//// TODO: 07/08/17 controleren of de zoomlevels van googlemaps en osm enigzins overeenkomen.
        }else{
            //throw new RuntimeException("only supported for googleMaps&OSM");
        }
    }

    public Marker addMarker(Pair<MarkerOptions, Bitmap> markerOptions) {
        if (mapType == GOOGLEMAPTYPE) {
            if (markerOptions.second != null){
                markerOptions.first.icon(BitmapDescriptorFactory.fromBitmap(markerOptions.second));
            }else{
                markerOptions.first.icon(BitmapDescriptorFactory.defaultMarker());
            }
            return new Marker(googleMap.addMarker(markerOptions.first));
        }else if(mapType == OSMMAPTYPE){
            return new Marker(markerOptions, osmMap);
        }else{
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

    public void setOnMapClickListener(GoogleMap.OnMapClickListener onMapClickListener) {
        if (mapType == GOOGLEMAPTYPE){
            googleMap.setOnMapClickListener(onMapClickListener);
        }else{
            return;
            //throw new RuntimeException("only supported for googleMaps");
        }
    }

    public CameraPosition getCameraPosition() {
        switch (mapType){
            case GOOGLEMAPTYPE:
                return googleMap.getCameraPosition();
            case OSMMAPTYPE:
                return null;
            default:
                return null;
        }
    }

    public void snapshot(GoogleMap.SnapshotReadyCallback snapshotReadyCallback) {
        switch (mapType){
            case GOOGLEMAPTYPE:
                googleMap.snapshot(snapshotReadyCallback);
                break;
            case OSMMAPTYPE:
                //// TODO: 09/08/17 implement this?
                break;
            default:
                break;
        }
    }

    public void animateCamera(CameraUpdate cameraUpdate, GoogleMap.CancelableCallback cancelableCallback) {
        if (mapType == GOOGLEMAPTYPE){
            googleMap.animateCamera(cameraUpdate, cancelableCallback);
        }else{
            return;
            //throw new RuntimeException("only supported for googleMaps");
        }
    }

    public void setOnCameraMoveStartedListener(GoogleMap.OnCameraMoveStartedListener onCameraMoveStartedListener) {
        if (mapType == GOOGLEMAPTYPE){
            googleMap.setOnCameraMoveStartedListener(onCameraMoveStartedListener);
        }else{
            return;
            //throw new RuntimeException("only supported for googleMaps");
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
        if (mapType == GOOGLEMAPTYPE){
            googleMap.setOnInfoWindowLongClickListener(onInfoWindowLongClickListener);
        }else{
            return;
            //throw new RuntimeException("only supported for googleMaps");
        }
    }
}
