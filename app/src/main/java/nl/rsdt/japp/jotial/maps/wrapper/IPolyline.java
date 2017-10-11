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

public interface IPolyline {

    public void remove();

    public List<LatLng> getPoints();

    public void setPoints(List<LatLng> points) ;

    void setVisible(boolean visible);
}
