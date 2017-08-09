package nl.rsdt.japp.jotial.maps.wrapper;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mattijn on 08/08/17.
 */

public class Polyline {
    public static final int GOOGLE_POLYLINE = 0;
    public static final int OSM_POLYLINE = 1;

    private final int polylineType;
    private final com.google.android.gms.maps.model.Polyline googlePolyline;
    private final org.osmdroid.views.overlay.Polyline osmPolyline;
    private final MapView osmMap;

    public Polyline(com.google.android.gms.maps.model.Polyline polyline) {
        googlePolyline  = polyline;
        polylineType = GOOGLE_POLYLINE;
        osmPolyline = null;
        osmMap = null;
    }

    public Polyline(PolylineOptions polylineOptions, MapView osmMap) {
        polylineType = OSM_POLYLINE;
        googlePolyline = null;
        this.osmMap = osmMap;
        org.osmdroid.views.overlay.Polyline polyline = new org.osmdroid.views.overlay.Polyline();
        polyline.setColor(polylineOptions.getColor());
        polyline.setVisible(polylineOptions.isVisible());
        ArrayList<GeoPoint> points = new ArrayList<>();
        for (LatLng latlng : polylineOptions.getPoints()){
            points.add(new GeoPoint(latlng.latitude,latlng.longitude));
        }
        polyline.setPoints(points);
        polyline.setWidth(polylineOptions.getWidth());
        osmMap.getOverlayManager().add(polyline);
        osmMap.invalidate();
        osmPolyline = polyline;
    }

    public void remove() {
        switch (polylineType){
            case GOOGLE_POLYLINE:
                googlePolyline.remove();
                break;
            case OSM_POLYLINE:
                osmMap.getOverlays().remove(osmPolyline);
                break;
            default:
                break;
        }
    }

    public List<LatLng> getPoints() {
        switch (polylineType){
            case GOOGLE_POLYLINE:
                return googlePolyline.getPoints();
            case OSM_POLYLINE:
                ArrayList<LatLng> r = new ArrayList<>();
                for (GeoPoint p : osmPolyline.getPoints()){
                    r.add(new LatLng(p.getLatitude(),p.getLongitude()));
                }
                return r;
            default:
                return new ArrayList<>();
        }
    }

    public void setPoints(List<LatLng> points) {
        switch (polylineType){
            case GOOGLE_POLYLINE:
                googlePolyline.setPoints(points);
                break;
            case OSM_POLYLINE:
                ArrayList<GeoPoint> r = new ArrayList<>();
                for (LatLng p : points){
                    r.add(new GeoPoint(p.latitude,p.longitude));
                }
                osmPolyline.setPoints(r);
                break;
            default:
                break;
        }
    }
}
