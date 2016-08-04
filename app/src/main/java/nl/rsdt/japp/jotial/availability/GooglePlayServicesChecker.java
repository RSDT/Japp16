package nl.rsdt.japp.jotial.availability;

import android.app.Activity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 4-8-2016
 * Description...
 */
public class GooglePlayServicesChecker {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static final int FAILURE = 0;

    public static final int SUCCESS = 1;

    public static int check(Activity activity) {
        GoogleApiAvailability availability = GoogleApiAvailability.getInstance();
        int code = availability.isGooglePlayServicesAvailable(activity);
        if(code == ConnectionResult.SUCCESS) {
            return SUCCESS;
        }
        else
        {
            availability.getErrorDialog(activity, code, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            return FAILURE;
        }
    }

}
