package nl.rsdt.japp.jotial.maps.wrapper;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by mattijn on 08/08/17.
 */

public interface IPolyline {

    void remove();

    List<LatLng> getPoints();

    void setPoints(List<LatLng> points);

    void setVisible(boolean visible);
}
