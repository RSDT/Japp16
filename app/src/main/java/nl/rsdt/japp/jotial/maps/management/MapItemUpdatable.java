package nl.rsdt.japp.jotial.maps.management;

import com.rsdt.anl.RequestPool;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Description...
 */
public interface MapItemUpdatable {

    void onUpdateInvoked(RequestPool pool, boolean userInvoked);

}
