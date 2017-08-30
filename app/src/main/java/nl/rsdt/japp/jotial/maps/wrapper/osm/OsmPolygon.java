package nl.rsdt.japp.jotial.maps.wrapper.osm;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;

import nl.rsdt.japp.jotial.maps.wrapper.IPolygon;

/**
 * Created by mattijn on 08/08/17.
 */

public class OsmPolygon  implements IPolygon{
    private final org.osmdroid.views.overlay.Polygon osmPolygon;
    private final MapView osmMap;

    public OsmPolygon(PolygonOptions polygonOptions, MapView  osmMap) {
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
        osmMap.getOverlays().remove(osmPolygon);
    }

    public void setStrokeWidth(int strokeWidth) {
        osmPolygon.setStrokeWidth(strokeWidth);
    }

    public void setFillColor(int color) {
        osmPolygon.setFillColor(color);
    }
}
