package nl.rsdt.japp.jotial.maps.management.controllers;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Description...
 */


import java.util.ArrayList;

import nl.rsdt.japp.jotial.data.structures.area348.BaseInfo;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Description...
 */
public class BravoVosController extends VosController {

    public static final String CONTROLLER_ID = "BravoVosController";

    public static final String STORAGE_ID = "STORAGE_VOS_B";

    public static final String BUNDLE_ID = "VOS_B";

    public static final String REQUEST_ID = "REQUEST_VOS_B";

    public BravoVosController() {

    }

    @Override
    public String getTeam() {
        return "b";
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
    public ArrayList<BaseInfo> searchFor(String query) {
        return super.searchFor(query);
    }
}

