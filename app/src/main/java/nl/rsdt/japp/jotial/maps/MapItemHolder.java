package nl.rsdt.japp.jotial.maps;




import java.util.ArrayList;

import nl.rsdt.japp.jotial.maps.wrapper.Circle;
import nl.rsdt.japp.jotial.maps.wrapper.Marker;
import nl.rsdt.japp.jotial.maps.wrapper.Polygon;
import nl.rsdt.japp.jotial.maps.wrapper.Polyline;


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
