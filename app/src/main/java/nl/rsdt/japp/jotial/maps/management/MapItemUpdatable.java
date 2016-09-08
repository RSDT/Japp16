package nl.rsdt.japp.jotial.maps.management;

import nl.rsdt.japp.service.cloud.data.UpdateInfo;
import retrofit2.Call;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Description...
 */
public interface MapItemUpdatable<I> {

    String MODE_ALL = "ALL";

    String MODE_LATEST = "LATEST";

    Call<I> update(String mode);

    void onUpdateInvoked();

    void onUpdateMessage(UpdateInfo info);
}
