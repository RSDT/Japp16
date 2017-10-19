package nl.rsdt.japp.jotial.maps.wrapper.osm;

import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polygon;

import java.util.ArrayList;

import nl.rsdt.japp.jotial.maps.wrapper.ICircle;

/**
 * Created by mattijn on 08/08/17.
 */

public class OsmCircle implements ICircle {
    private final MapView osmMap;
    private final Polygon osmCircle;
    private LatLng centre;

    public OsmCircle(CircleOptions circleOptions, MapView osmMap) {
        this.osmMap = osmMap;
        osmCircle = new Polygon();

        osmCircle.setStrokeWidth(circleOptions.getStrokeWidth());
        osmCircle.setFillColor(circleOptions.getFillColor());
        osmCircle.setStrokeColor(circleOptions.getStrokeColor());

        final double radius = circleOptions.getRadius();
        this.centre = circleOptions.getCenter();
        this.setRadius((float) radius);
        osmMap.getOverlays().add(osmCircle);
    }

    public void remove() {
        osmMap.getOverlays().remove(osmCircle) ;
    }

    public void setRadius(float radius) {
        int nrOfPoints = 360;
        ArrayList<GeoPoint> circlePoints = new ArrayList<GeoPoint>();
        for (float f = 0; f < nrOfPoints; f ++){
            circlePoints.add(new GeoPoint(centre.latitude , centre.longitude ).destinationPoint(radius, f));
        }
        osmCircle.setPoints(circlePoints);
    }

    @Override
    public void setVisible(boolean visible) {
        osmCircle.setVisible(visible);
    }

    @Override
    public int getFillColor() {
        return osmCircle.getFillColor();
    }

    @Override
    public void setFillColor(int color) {
        osmCircle.setFillColor(color);
    }
}
