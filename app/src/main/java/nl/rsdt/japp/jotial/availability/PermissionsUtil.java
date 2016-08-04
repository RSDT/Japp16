package nl.rsdt.japp.jotial.availability;

        import android.content.Context;
        import android.content.pm.PackageManager;
        import android.os.Build;
        import android.support.v4.content.ContextCompat;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 4-8-2016
 * Description...
 */
public class PermissionsUtil {

    /**
     * Determines if the permissions should be requested, depending on the API level.
     *
     * @return The value indicating if permissions should be requested.
     * */
    public static boolean shouldAskForPermission() {
        return (Build.VERSION.SDK_INT >= 23);
    }

    /**
     * Checks if the given Context has the given permission.
     *
     * @param context The context to check for the permission
     * @param permission The permission to check.
     * @return PackageManager constant to indicate the result.
     * */
    public static int hasPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission);
    }

    public static boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if(grantResults.length < 1){
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

}
