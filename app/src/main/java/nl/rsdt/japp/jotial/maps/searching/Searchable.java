package nl.rsdt.japp.jotial.maps.searching;



import java.util.List;

import nl.rsdt.japp.jotial.maps.wrapper.Marker;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 29-7-2016
 * Description...
 */
public interface Searchable {

    List<String> provide();

    Marker searchFor(String query);

}
