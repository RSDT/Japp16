package nl.rsdt.japp.jotial.maps.wrapper;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by mattijn on 08/08/17.
 */

public class Polygon {
    public static final int GOOGLE_POLYGON = 0;
    public static final int OSM_POLYGON = 1;
    private final com.google.android.gms.maps.model.Polygon googlePolygon;
    private final int polygonType;
    private final org.osmdroid.views.overlay.Polygon osmPolygon;
    private final MapView osmMap;

    public Polygon(com.google.android.gms.maps.model.Polygon polygon) {
        polygonType = GOOGLE_POLYGON;
        googlePolygon = polygon;
        osmPolygon = null;
        osmMap = null;
    }

    public Polygon(PolygonOptions polygonOptions, MapView  osmMap) {
        polygonType = OSM_POLYGON;
        googlePolygon = null;
        osmPolygon = new org.osmdroid.views.overlay.Polygon();
        osmPolygon.setFillColor(polygonOptions.getFillColor());
        osmPolygon.setStrokeColor(polygonOptions.getStrokeColor());
        osmPolygon.setStrokeWidth(polygonOptions.getStrokeWidth());
        ArrayList<GeoPoint> points = new ArrayList<>();
        for (LatLng ll: polygonOptions.getPoints()){
            points.add(new GeoPoint(ll.latitude,ll.longitude));
        }
        osmPolygon.setPoints(points);
        this.osmMap = osmMap;
        osmMap.getOverlays().add(osmPolygon);
    }

    public void remove() {
        switch (polygonType){
            case GOOGLE_POLYGON:
                googlePolygon.remove();
                break;
            case OSM_POLYGON:
                osmMap.getOverlays().remove(osmPolygon);
        }
    }

    public void setStrokeWidth(int strokeWidth) {
        switch (polygonType){
            case GOOGLE_POLYGON:
                googlePolygon.setStrokeWidth(strokeWidth);
                break;
            case OSM_POLYGON:
                osmPolygon.setStrokeWidth(strokeWidth);
                break;
            default:
                break;
        }
    }

    public void setFillColor(int color) {
        switch (polygonType){
            case GOOGLE_POLYGON:
                googlePolygon.setFillColor(color);
                break;
            case OSM_POLYGON:
                osmPolygon.setFillColor(color);
                break;
            default:
        }
    }
}
