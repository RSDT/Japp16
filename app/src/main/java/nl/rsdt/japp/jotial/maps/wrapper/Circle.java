package nl.rsdt.japp.jotial.maps.wrapper;

import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polygon;

import java.util.ArrayList;

/**
 * Created by mattijn on 08/08/17.
 */

public class Circle {
    public static final int GOOGLE_CIRCLE= 0;
    public static final int OSM_CIRCLE = 1;
    private final com.google.android.gms.maps.model.Circle googleCircle;
    private final int circleType;
    private final MapView osmMap;
    private final Polygon osmCircle;
    private LatLng centre;


    public Circle(com.google.android.gms.maps.model.Circle circle) {
        googleCircle = circle;
        circleType = GOOGLE_CIRCLE;
        osmMap = null;
        osmCircle = null;
    }

    public Circle(CircleOptions circleOptions, MapView osmMap) {
        this.osmMap = osmMap;
        googleCircle = null;
        circleType = OSM_CIRCLE;
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
        switch (circleType){
            case GOOGLE_CIRCLE:
                googleCircle.remove();
                break;
            case OSM_CIRCLE:
                osmMap.getOverlays().remove(osmCircle);
                break;
            default:
                break;
        }
    }

    public void setRadius(float radius) {
        switch (circleType) {
            case GOOGLE_CIRCLE:
                googleCircle.setRadius(radius);
                break;
            case OSM_CIRCLE:
                int nrOfPoints = 360;
                ArrayList<GeoPoint> circlePoints = new ArrayList<GeoPoint>();
                for (float f = 0; f < nrOfPoints; f ++){
                    circlePoints.add(new GeoPoint(centre.latitude , centre.longitude ).destinationPoint(radius, f));
                }
                osmCircle.setPoints(circlePoints);
                break;
            default:
                break;
        }

    }
}
