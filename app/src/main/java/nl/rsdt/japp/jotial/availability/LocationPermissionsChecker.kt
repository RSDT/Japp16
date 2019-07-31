package nl.rsdt.japp.jotial.availability

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager

import androidx.core.app.ActivityCompat

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 4-8-2016
 * Description...
 */
object LocationPermissionsChecker {

    val PERMISSIONS_REQUEST_REQUIRED = 0

    val PERMISSIONS_REQUEST_NOT_REQUIRED = 1

    /**
     * Defines the permission request code for the LOCATION.
     */
    val PERMISSION_GROUP_LOCATION = 2

    fun check(activity: Activity): Int {
        if (PermissionsUtil.shouldAskForPermission()) {
            if (PermissionsUtil.hasPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || PermissionsUtil.hasPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION),
                        PERMISSION_GROUP_LOCATION)
                return PERMISSIONS_REQUEST_REQUIRED
            }
        }
        return PERMISSIONS_REQUEST_NOT_REQUIRED
    }

    /**
     * Checks if the permission is granted, of a permission request result
     *
     * @param requestCode The request code of the request.
     * @param permissions The permissions requested.
     * @param grantResults The permissions granted.
     * @return The value indicating if the permission is grantend.
     */
    fun hasPermissionOfPermissionRequestResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray): Boolean {
        return if (requestCode == PERMISSION_GROUP_LOCATION) {
            grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else false
    }

    fun permissionRequestResultContainsLocation(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (permission == Manifest.permission.ACCESS_FINE_LOCATION || permission == Manifest.permission.ACCESS_COARSE_LOCATION) {
                return true
            }
        }
        return false
    }


}
