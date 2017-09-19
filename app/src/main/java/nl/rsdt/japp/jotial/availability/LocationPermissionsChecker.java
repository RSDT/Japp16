package nl.rsdt.japp.jotial.availability;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import nl.rsdt.japp.application.Japp;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 4-8-2016
 * Description...
 */
public final class LocationPermissionsChecker {

    public static final int PERMISSIONS_REQUEST_REQUIRED = 0;

    public static final int PERMISSIONS_REQUEST_NOT_REQUIRED = 1;

    /**
     * Defines the permission request code for the LOCATION.
     * */
    public static final int PERMISSION_GROUP_LOCATION = 2;

    public static int check(Activity activity) {
        if(PermissionsUtil.shouldAskForPermission()) {
            if(PermissionsUtil.hasPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    PermissionsUtil.hasPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION } , PERMISSION_GROUP_LOCATION);
                return PERMISSIONS_REQUEST_REQUIRED;
            }
        }
        return PERMISSIONS_REQUEST_NOT_REQUIRED;
    }

    /**
     * Checks if the permission is granted, of a permission request result
     *
     * @param requestCode The request code of the request.
     * @param permissions The permissions requested.
     * @param grantResults The permissions granted.
     * @return The value indicating if the permission is grantend.
     */
    public static boolean hasPermissionOfPermissionRequestResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case PERMISSION_GROUP_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return true;
                }
                else
                {
                    return false;
                }
        }
        return false;
    }

    public static boolean permissionRequestResultContainsLocation(String permissions[]) {
        for(String permission : permissions) {
            if(permission.equals(Manifest.permission.ACCESS_FINE_LOCATION) || permission.equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                return true;
            }
        }
        return false;
    }




}
