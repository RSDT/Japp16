package nl.rsdt.japp.jotial.maps.wrapper.osm;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.List;

import nl.rsdt.japp.jotial.maps.wrapper.IPolyline;

/**
 * Created by mattijn on 08/08/17.
 */

public class OsmPolyline implements IPolyline{
    private final org.osmdroid.views.overlay.Polyline osmPolyline;
    private final MapView osmMap;

    public OsmPolyline(PolylineOptions polylineOptions, MapView osmMap) {
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
        osmMap.getOverlays().remove(osmPolyline);
    }

    public List<LatLng> getPoints() {
        ArrayList<LatLng> r = new ArrayList<>();
        for (GeoPoint p : osmPolyline.getPoints()){
            r.add(new LatLng(p.getLatitude(),p.getLongitude()));
        }
        return r;
    }

    public void setPoints(List<LatLng> points) {
        ArrayList<GeoPoint> r = new ArrayList<>();
        for (LatLng p : points){
            r.add(new GeoPoint(p.latitude,p.longitude));
        }
        osmPolyline.setPoints(r);
    }

    @Override
    public void setVisible(boolean visible) {
        osmPolyline.setVisible(visible);
    }
}
