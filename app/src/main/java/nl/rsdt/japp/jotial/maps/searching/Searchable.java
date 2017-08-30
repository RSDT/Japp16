package nl.rsdt.japp.jotial.maps.searching;



import java.util.List;

import nl.rsdt.japp.jotial.maps.wrapper.IMarker;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 29-7-2016
 * Description...
 */
public interface Searchable {

    List<String> provide();

    IMarker searchFor(String query);

}
