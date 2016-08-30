package nl.rsdt.japp.jotial.maps.management.controllers;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Description...
 */

import com.android.internal.util.Predicate;
import com.rsdt.anl.WebRequest;
import com.rsdt.anl.WebRequestMethod;
import com.rsdt.anl.WebResponse;

import java.util.ArrayList;

import nl.rsdt.japp.jotial.data.structures.area348.BaseInfo;
import nl.rsdt.japp.jotial.net.ApiUrlBuilder;

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

    @Override
    public String getUrlByAssociatedMode(String mode) {
        switch (mode) {
            case MODE_ALL:
                return new ApiUrlBuilder().append("vos").append("b").append("all").buildAsString();

            case MODE_LATEST:
                return new ApiUrlBuilder().append("vos").append("b").append("all").append(lastUpdate.toString()).buildAsString();
            default:
                return null;
        }
    }
}

