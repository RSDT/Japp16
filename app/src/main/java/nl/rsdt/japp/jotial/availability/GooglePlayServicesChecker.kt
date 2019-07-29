package nl.rsdt.japp.jotial.availability

import android.app.Activity

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 4-8-2016
 * Description...
 */
object GooglePlayServicesChecker {

    private val PLAY_SERVICES_RESOLUTION_REQUEST = 9000

    val FAILURE = 0

    val SUCCESS = 1

    fun check(activity: Activity): Int {
        val availability = GoogleApiAvailability.getInstance()
        val code = availability.isGooglePlayServicesAvailable(activity)
        if (code == ConnectionResult.SUCCESS) {
            return SUCCESS
        } else {
            availability.getErrorDialog(activity, code, PLAY_SERVICES_RESOLUTION_REQUEST).show()
            return FAILURE
        }
    }

}
