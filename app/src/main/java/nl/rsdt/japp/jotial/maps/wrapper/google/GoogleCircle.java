package nl.rsdt.japp.jotial.maps.wrapper.google;

import com.google.android.gms.maps.GoogleMap;
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

public class GoogleCircle implements ICircle{
    private final com.google.android.gms.maps.model.Circle googleCircle;


    public GoogleCircle(com.google.android.gms.maps.model.Circle circle) {
        googleCircle = circle;
    }

    @Override
    public void remove() {
        googleCircle.remove();
    }

    @Override
    public void setRadius(float radius) {
        googleCircle.setRadius(radius);
    }
}
