package nl.rsdt.japp.jotial.maps;




import java.util.ArrayList;

import nl.rsdt.japp.jotial.maps.wrapper.ICircle;
import nl.rsdt.japp.jotial.maps.wrapper.IMarker;
import nl.rsdt.japp.jotial.maps.wrapper.IPolygon;
import nl.rsdt.japp.jotial.maps.wrapper.IPolyline;


/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 24-7-2016
 * Description...
 */
public interface MapItemHolder {

    ArrayList<IMarker> getMarkers();

    ArrayList<IPolyline> getPolylines();

    ArrayList<IPolygon> getPolygons();

    ArrayList<ICircle> getCircles();

}
