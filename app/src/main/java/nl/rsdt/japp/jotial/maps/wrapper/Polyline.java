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

    public Polyline(com.google.android.gms.maps.model.Polyline polyline) {
        googlePolyline  = polyline;
        polylineType = GOOGLE_POLYLINE;
        osmPolyline = null;
    }

    public Polyline(PolylineOptions polylineOptions, MapView osmMap) {
        polylineType = OSM_POLYLINE;
        googlePolyline = null;
        org.osmdroid.views.overlay.Polyline polyline = new org.osmdroid.views.overlay.Polyline();
        polyline.setColor(polyline.getColor());
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
        if (polylineType == GOOGLE_POLYLINE){
            googlePolyline.remove();
        }else if (polylineType == OSM_POLYLINE){
            osmPolyline.setVisible(false); //todo dit is heel lelijk
        }
    }

    public List<LatLng> getPoints() {
        if (polylineType == GOOGLE_POLYLINE) {
            return googlePolyline.getPoints();
        }else if(polylineType == OSM_POLYLINE){
            ArrayList<LatLng> r = new ArrayList<>();
            for (GeoPoint p : osmPolyline.getPoints()){
                r.add(new LatLng(p.getLatitude(),p.getLongitude()));
            }
            return r;
        }else {
            return new ArrayList<>();
        }
    }

    public void setPoints(List<LatLng> points) {
        if (polylineType == GOOGLE_POLYLINE){
            googlePolyline.setPoints(points);
        }else if (polylineType == OSM_POLYLINE){
            ArrayList<GeoPoint> r = new ArrayList<>();
            for (LatLng p : points){
                r.add(new GeoPoint(p.latitude,p.longitude));
            }
            osmPolyline.setPoints(r);
        }
    }
}
