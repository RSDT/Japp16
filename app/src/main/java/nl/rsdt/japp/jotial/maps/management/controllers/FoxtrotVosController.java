package nl.rsdt.japp.jotial.maps.management.controllers;

import com.android.internal.util.Predicate;
import com.rsdt.anl.WebRequest;
import com.rsdt.anl.WebRequestMethod;
import com.rsdt.anl.WebResponse;

import nl.rsdt.japp.jotial.net.ApiUrlBuilder;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Description...
 */
public class FoxtrotVosController extends VosController {

    public static final String CONTROLLER_ID = "FoxtrotVosController";

    public static final String STORAGE_ID = "STORAGE_VOS_F";

    public static final String BUNDLE_ID = "VOS_F";

    public static final String REQUEST_ID = "REQUEST_VOS_F";

    public FoxtrotVosController() {
        this.condtion = new Predicate<WebResponse>() {
            @Override
            public boolean apply(WebResponse response) {
                return response.getRequest().getId().equals(REQUEST_ID) &&
                        response.getResponseCode() == 200;
            }
        };
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
    public WebRequest update(String mode) {
        switch (mode){
            case MODE_ALL:
                return new WebRequest.Builder()
                        .setId(REQUEST_ID)
                        .setMethod(WebRequestMethod.GET)
                        .setUrl(new ApiUrlBuilder().append("vos").append("f").append("all").build())
                        .create();
            case MODE_LATEST:
                return new WebRequest.Builder()
                        .setId(REQUEST_ID)
                        .setMethod(WebRequestMethod.GET)
                        .setUrl(new ApiUrlBuilder().append("vos").append("f").append("all").append(lastUpdate.toString()).build())
                        .create();
            default:
                return null;
        }
    }
}

