package nl.rsdt.japp.jotial.maps.wrapper;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.HashMap;
import java.util.Map;

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
    private final Object osmMap; //todo fix type

    private JotiMap(GoogleMap map){
        mapType = GOOGLEMAPTYPE;
        googleMap = map;
        osmMap = null;
    }

    public static JotiMap getJotiMapInstance(GoogleMap map){
        if (!google_instances.containsKey(map)) {
            JotiMap jm = new JotiMap(map);
            google_instances.put(map,jm);
        }
        return google_instances.get(map);
    }


    public void setInfoWindowAdapter(CustomInfoWindowAdapter infoWindowAdapter) {
        if (mapType == GOOGLEMAPTYPE){
            googleMap.setInfoWindowAdapter(infoWindowAdapter);
        }else{
            throw new RuntimeException("only supported for googleMaps");
        }
    }

    public void setGMapType(int mapType) {
        if (mapType == GOOGLEMAPTYPE){
            googleMap.setMapType(mapType);
        }else{
            throw new RuntimeException("only supported for googleMaps");
        }
    }

    public boolean setMapStyle(MapStyleOptions mapStyleOptions) {
        if (mapType == GOOGLEMAPTYPE){
            return googleMap.setMapStyle(mapStyleOptions);
        }else{
            throw new RuntimeException("only supported for googleMaps");
        }
    }

    public UiSettings getUiSettings() {
        if (mapType == GOOGLEMAPTYPE){
            return googleMap.getUiSettings();
        }else{
            throw new RuntimeException("only supported for googleMaps");
        }
    }

    public void animateCamera(CameraUpdate cameraUpdate) {
        if (mapType == GOOGLEMAPTYPE){
            googleMap.animateCamera(cameraUpdate);
        }else{
            throw new RuntimeException("only supported for googleMaps");
        }
    }

    public Marker addMarker(MarkerOptions markerOptions) {
        if (mapType == GOOGLEMAPTYPE){
            return googleMap.addMarker(markerOptions);
        }else{
            throw new RuntimeException("only supported for googleMaps");
        }
    }

    public Polyline addPolyline(PolylineOptions polylineOptions) {
        if (mapType == GOOGLEMAPTYPE){
            return googleMap.addPolyline(polylineOptions);
        }else{
            throw new RuntimeException("only supported for googleMaps");
        }
    }

    public Polygon addPolygon(PolygonOptions polygonOptions) {
        if (mapType == GOOGLEMAPTYPE){
            return googleMap.addPolygon(polygonOptions);
        }else{
            throw new RuntimeException("only supported for googleMaps");
        }
    }

    public Circle addCircle(CircleOptions circleOptions) {
        if (mapType == GOOGLEMAPTYPE){
            return googleMap.addCircle(circleOptions);
        }else{
            throw new RuntimeException("only supported for googleMaps");
        }
    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    public int getMapType() {
        return mapType;
    }
}
