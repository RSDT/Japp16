package nl.rsdt.japp.jotial.maps.management.controllers;



/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Description...
 */
public class DeltaVosController extends VosController {

    public static final String CONTROLLER_ID = "DeltaVosController";

    public static final String STORAGE_ID = "STORAGE_VOS_D";

    public static final String BUNDLE_ID = "VOS_D";

    public static final String REQUEST_ID = "REQUEST_VOS_D";

    public DeltaVosController() {

    }

    @Override
    public String getTeam() {
        return "d";
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

