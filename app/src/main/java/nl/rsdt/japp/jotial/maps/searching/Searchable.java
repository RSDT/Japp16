package nl.rsdt.japp.jotial.maps.searching;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

import nl.rsdt.japp.jotial.data.structures.area348.BaseInfo;

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
