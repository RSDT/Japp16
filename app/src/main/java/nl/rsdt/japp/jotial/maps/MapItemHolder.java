package nl.rsdt.japp.jotial.maps;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 24-7-2016
 * Description...
 */
public interface MapItemHolder<T> {

    ArrayList<T> getItems();

    ArrayList<Marker> getMarkers();

    ArrayList<Polyline> getPolylines();

    ArrayList<Polygon> getPolygons();

}
