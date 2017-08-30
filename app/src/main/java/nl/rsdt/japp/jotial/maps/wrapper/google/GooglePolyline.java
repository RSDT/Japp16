package nl.rsdt.japp.jotial.maps.wrapper.google;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.List;

import nl.rsdt.japp.jotial.maps.wrapper.IPolygon;
import nl.rsdt.japp.jotial.maps.wrapper.IPolyline;

/**
 * Created by mattijn on 08/08/17.
 */

public class GooglePolyline implements IPolyline {
    private final com.google.android.gms.maps.model.Polyline googlePolyline;

    public GooglePolyline(com.google.android.gms.maps.model.Polyline polyline) {
        googlePolyline  = polyline;
    }

    @Override
    public void remove() {
        googlePolyline.remove();
    }

    @Override
    public List<LatLng> getPoints() {
        return googlePolyline.getPoints();
    }

    @Override
    public void setPoints(List<LatLng> points) {
        googlePolyline.setPoints(points);
    }
}
