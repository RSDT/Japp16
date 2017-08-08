package nl.rsdt.japp.jotial.maps;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;

import nl.rsdt.japp.jotial.maps.wrapper.Marker;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 24-7-2016
 * Description...
 */
public interface MapItemHolder {

    ArrayList<Marker> getMarkers();

    ArrayList<Polyline> getPolylines();

    ArrayList<Polygon> getPolygons();

    ArrayList<Circle> getCircles();

}
