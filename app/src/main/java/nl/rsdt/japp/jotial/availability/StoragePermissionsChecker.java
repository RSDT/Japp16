package nl.rsdt.japp.jotial.availability;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;

/**
 * Created by mattijn on 10/08/17.
 */

public class StoragePermissionsChecker {
    private static final int PERMISSIONS_REQUEST_REQUIRED = 100;

    private static final int PERMISSIONS_REQUEST_NOT_REQUIRED = 101;

    /**
     * Defines the permission request code for the LOCATION.
     * */
    private static final int PERMISSION_GROUP_STORAGE = 102;

    public static int check(Activity activity) {
        if(PermissionsUtil.shouldAskForPermission()) {
            if(PermissionsUtil.hasPermission(activity, Manifest.permission_group.STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE } , PERMISSION_GROUP_STORAGE);
                }
                return PERMISSIONS_REQUEST_REQUIRED;
            }
        }
        return PERMISSIONS_REQUEST_NOT_REQUIRED;
    }

    /**
     * Checks if the permission is granted, of a permission request result
     *
     * @param requestCode The request code of the request.
     * @param grantResults The permissions granted.
     * @return The value indicating if the permission is grantend.
     */
    public static boolean hasPermissionOfPermissionRequestResult(int requestCode, int[] grantResults)
    {
        if (requestCode == PERMISSION_GROUP_STORAGE) {
            return grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }
}
