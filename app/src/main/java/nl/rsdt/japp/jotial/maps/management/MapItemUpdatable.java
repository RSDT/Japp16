package nl.rsdt.japp.jotial.maps.management;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
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

    String getUrlByAssociatedMode(String mode);

    void onUpdateInvoked(RequestQueue queue);

    void onUpdateMessage(RequestQueue queue, UpdateInfo info);
}
