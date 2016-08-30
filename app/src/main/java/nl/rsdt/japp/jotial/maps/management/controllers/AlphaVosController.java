package nl.rsdt.japp.jotial.maps.management.controllers;

import nl.rsdt.japp.jotial.net.ApiUrlBuilder;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Description...
 */
public class AlphaVosController extends VosController {

    public static final String CONTROLLER_ID = "AlphaVosController";

    public static final String STORAGE_ID = "STORAGE_VOS_A";

    public static final String BUNDLE_ID = "VOS_A";

    public static final String REQUEST_ID = "REQUEST_VOS_A";

    public AlphaVosController() {
    }

    @Override
    public String getId() {
        return CONTROLLER_ID;
    }

    @Override
    public String getStorageId() {
        return STORAGE_ID;
    }

    @Override
    public String getBundleId() {
        return BUNDLE_ID;
    }

    @Override
    public String getUrlByAssociatedMode(String mode) {
        switch (mode) {
            case MODE_ALL:
                return new ApiUrlBuilder().append("vos").append("a").append("all").buildAsString();

            case MODE_LATEST:
                return new ApiUrlBuilder().append("vos").append("a").append("all").append(lastUpdate.toString()).buildAsString();
            default:
                return null;
        }
    }
}
