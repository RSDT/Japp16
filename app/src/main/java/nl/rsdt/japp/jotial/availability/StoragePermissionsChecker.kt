package nl.rsdt.japp.jotial.availability

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build

import androidx.core.app.ActivityCompat

/**
 * Created by mattijn on 10/08/17.
 */

object StoragePermissionsChecker {
    internal val PERMISSIONS_REQUEST_REQUIRED = 100

    private val PERMISSIONS_REQUEST_NOT_REQUIRED = 101

    /**
     * Defines the permission request code for the STORAGE.
     */
    private val PERMISSION_GROUP_STORAGE = 102

    fun check(activity: Activity): Int {
        if (PermissionsUtil.shouldAskForPermission()) {
            if (PermissionsUtil.hasPermission(activity, Manifest.permission_group.STORAGE) != PackageManager.PERMISSION_GRANTED|| PermissionsUtil.hasPermission(activity, Manifest.permission_group.STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        PERMISSION_GROUP_STORAGE)
                return PERMISSIONS_REQUEST_REQUIRED
            }
        }
        return PERMISSIONS_REQUEST_NOT_REQUIRED
    }

    /**
     * Checks if the permission is granted, of a permission request result
     *
     * @param requestCode The request code of the request.
     * @param grantResults The permissions granted.
     * @return The value indicating if the permission is grantend.
     */
    fun hasPermissionOfPermissionRequestResult(requestCode: Int, grantResults: IntArray): Boolean {
        return if (requestCode == PERMISSION_GROUP_STORAGE) {
            grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else false
    }
}
