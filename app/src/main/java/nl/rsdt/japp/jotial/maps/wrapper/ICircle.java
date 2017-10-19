package nl.rsdt.japp.jotial.maps.wrapper;

import android.graphics.Color;

import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polygon;

import java.util.ArrayList;

/**
 * Created by mattijn on 08/08/17.
 */

public interface ICircle {

    public void remove();

    public void setRadius(float radius);

    void setVisible(boolean visible);

    int getFillColor();

    void setFillColor(int color);
}
