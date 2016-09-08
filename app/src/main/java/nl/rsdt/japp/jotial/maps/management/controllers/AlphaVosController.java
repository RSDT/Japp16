package nl.rsdt.japp.jotial.maps.management.controllers;


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
    public String getTeam() {
        return "a";
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

}
