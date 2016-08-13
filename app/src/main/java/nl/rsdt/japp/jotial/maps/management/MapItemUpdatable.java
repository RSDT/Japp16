package nl.rsdt.japp.jotial.maps.management;

import com.rsdt.anl.RequestPool;
import com.rsdt.anl.WebRequest;

import nl.rsdt.japp.service.cloud.data.UpdateInfo;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Description...
 */
public interface MapItemUpdatable {

    String MODE_ALL = "ALL";

    String MODE_LATEST = "LATEST";

    WebRequest update(String mode);

    void onUpdateInvoked(RequestPool pool);

    void onUpdateMessage(RequestPool pool, UpdateInfo info);
}
