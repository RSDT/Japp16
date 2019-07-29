package nl.rsdt.japp.jotial.maps.locations

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices

import nl.rsdt.japp.application.Japp

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 25-7-2016
 * Description...
 */
abstract class LocationProvider : LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    protected var client: GoogleApiClient? = null

    protected var request: LocationRequest? = LocationRequest()

    init {
        buildGoogleApiClient()
    }

    /**
     * Builds the GoogleApiClient.
     */
    @Synchronized
    protected fun buildGoogleApiClient() {
        client = GoogleApiClient.Builder(Japp.instance!!)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
        client!!.connect()
    }

    fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(Japp.instance!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Japp.instance!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this)

    }


    fun removeLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(client, this)
    }

    fun restartLocationUpdates() {
        removeLocationUpdates()
        startLocationUpdates()
    }

    override fun onConnected(bundle: Bundle?) {}

    override fun onConnectionSuspended(i: Int) {

    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

    }

    fun onDestroy() {
        if (client != null) {
            removeLocationUpdates()
            client!!.disconnect()
            client = null
        }
        request = null
    }

}
