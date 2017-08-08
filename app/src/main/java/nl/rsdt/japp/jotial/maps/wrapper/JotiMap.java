package nl.rsdt.japp.jotial.maps.wrapper;

import android.graphics.drawable.Drawable;
import android.location.Location;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.HashMap;
import java.util.Map;

import nl.rsdt.japp.jotial.maps.misc.CameraUtils;
import nl.rsdt.japp.jotial.maps.pinning.PinningManager;
import nl.rsdt.japp.jotial.maps.pinning.PinningSession;
import nl.rsdt.japp.jotial.maps.sighting.SightingSession;
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
        if (mapType == GOOGLEMAPTYPE){
            googleMap.setInfoWindowAdapter(infoWindowAdapter);
        }else{
            //throw new RuntimeException("only supported for googleMaps");
        }
    }

    public void setGMapType(int mapType) {
        if (this.mapType == GOOGLEMAPTYPE){
            googleMap.setMapType(mapType);
        }else{
            //throw new RuntimeException("only supported for googleMaps");
        }
    }

    public boolean setMapStyle(MapStyleOptions mapStyleOptions) {
        if (mapType == GOOGLEMAPTYPE){
            return googleMap.setMapStyle(mapStyleOptions);
        }else{
            return false;
            //throw new RuntimeException("only supported for googleMaps");
        }
    }

    public UiSettings getUiSettings() {
        if (mapType == GOOGLEMAPTYPE){
            UiSettings sett = googleMap.getUiSettings();
            return sett;
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

    public Marker addMarker(MarkerOptions markerOptions) {
        if (mapType == GOOGLEMAPTYPE) {
            return new Marker(googleMap.addMarker(markerOptions));
        }else if(mapType == OSMMAPTYPE){
            return null;

        }else{
            return null;
            //throw new RuntimeException("only supported for googleMaps");
        }
    }

    public Polyline addPolyline(PolylineOptions polylineOptions) {
        if (mapType == GOOGLEMAPTYPE){
            return googleMap.addPolyline(polylineOptions);
        }else{
            return null;
            //throw new RuntimeException("only supported for googleMaps");
        }
    }

    public Polygon addPolygon(PolygonOptions polygonOptions) {
        if (mapType == GOOGLEMAPTYPE){
            return googleMap.addPolygon(polygonOptions);
        }else{
            return null;
            //throw new RuntimeException("only supported for googleMaps");
        }
    }

    public Circle addCircle(CircleOptions circleOptions) {
        if (mapType == GOOGLEMAPTYPE){
            return googleMap.addCircle(circleOptions);
        }else{
            return null;
            //throw new RuntimeException("only supported for googleMaps");
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
        if (mapType == GOOGLEMAPTYPE){
            return googleMap.getCameraPosition();
        }else{
            return null;
            //throw new RuntimeException("only supported for googleMaps");
        }
    }

    public void snapshot(GoogleMap.SnapshotReadyCallback snapshotReadyCallback) {
        if (mapType == GOOGLEMAPTYPE){
            googleMap.snapshot(snapshotReadyCallback);
        }else{
            return;
            //throw new RuntimeException("only supported for googleMaps");
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
        if (mapType == GOOGLEMAPTYPE) {
            CameraUtils.cameraToLocation(b, googleMap, location, zoom, aoa, bearing);
        }else {

        }
    }

    public void clear() {
        if (mapType == GOOGLEMAPTYPE){
            googleMap.clear();
        }else{
            return;
            //throw new RuntimeException("only supported for googleMaps");
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
