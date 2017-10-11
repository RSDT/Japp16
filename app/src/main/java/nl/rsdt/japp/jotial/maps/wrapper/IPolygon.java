package nl.rsdt.japp.jotial.maps.wrapper;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;

/**
 * Created by mattijn on 08/08/17.
 */

public interface IPolygon {


    public void remove();

    public void setStrokeWidth(int strokeWidth);

    public void setFillColor(int color);

    void setVisible(boolean visible);
}
