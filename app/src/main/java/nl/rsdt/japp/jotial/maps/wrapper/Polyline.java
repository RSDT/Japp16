package nl.rsdt.japp.jotial.maps.wrapper;

import com.google.android.gms.maps.model.LatLng;

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
    private List<LatLng> points;

    public Polyline(com.google.android.gms.maps.model.Polyline polyline) {
        googlePolyline  = polyline;
        polylineType = GOOGLE_POLYLINE;
    }

    public void remove() {
        if (polylineType == GOOGLE_POLYLINE){
            googlePolyline.remove();
        }
    }

    public List<LatLng> getPoints() {
        if (polylineType == GOOGLE_POLYLINE){
            return googlePolyline.getPoints();
        }else {
            return new ArrayList<>();
        }
    }

    public void setPoints(List<LatLng> points) {
        if (polylineType == GOOGLE_POLYLINE){
            googlePolyline.setPoints(points);
        }
    }
}
