package nl.rsdt.japp.jotial.maps.locations

import android.app.Service
import android.location.Location
import android.os.Binder
import android.os.Bundle
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.location.*
import nl.rsdt.japp.application.Japp
import java.util.*

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 25-7-2016
 * Description...
 */
abstract class LocationProviderService<B : Binder> : Service(), LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<LocationSettingsResult> {

    protected var client: GoogleApiClient? = null

    var request: LocationRequest? = LocationRequest()
        set(value) {
            field = value
            if (isRequesting && client!!.isConnected) {
                restartLocationUpdates()
            }
        }

    protected var listeners = ArrayList<LocationListener>()

    var lastLocation: Location? = null
        protected set

    protected var isRequesting = false

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

    @Throws(SecurityException::class)
    fun startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this)
        isRequesting = true
    }


    fun removeLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(client, this)
        isRequesting = false
    }

    fun restartLocationUpdates() {
        removeLocationUpdates()
        startLocationUpdates()
    }

    override fun onLocationChanged(location: Location) {
        lastLocation = location
        var listener: LocationListener?
        for (i in listeners.indices) {
            listener = listeners[i]
            listener.onLocationChanged(location)
        }
    }

    override fun onConnected(bundle: Bundle?) {
        startLocationUpdates()
        //// TODO: 20/10/17 quickfix #127 de bug is erin gekomen in df4e994826ede9b014335c573deb30b7b9fd3455
        // checkLocationSettings();
    }

    fun checkLocationSettings() {
        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(request!!)
        val result = LocationServices.SettingsApi.checkLocationSettings(client,
                builder.build())
        result.setResultCallback(this)
    }

    override fun onResult(result: LocationSettingsResult) {
        val status = result.status
        when (status.statusCode) {
            LocationSettingsStatusCodes.SUCCESS ->
                // All location settings are satisfied. The client can
                // initialize location requests here.
                startLocationUpdates()
            LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
            }
            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
            }
        }// Location settings are not satisfied, but this can be fixed
        // by showing the user a dialog.
        // Location settings are not satisfied. However, we have no way
        // to fix the settings so we won't show the dialog.
    }

    override fun onConnectionSuspended(i: Int) {
        Log.e(TAG, "Connection suspended : $i")
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        var error = connectionResult.errorMessage
        if (error == null) error = connectionResult.toString()
        Log.e(TAG, error)
    }

    override fun onDestroy() {
        if (client != null) {
            removeLocationUpdates()
            client!!.disconnect()
            client = null
        }
        request = null
    }

    fun add(listener: LocationListener) {
        this.listeners.add(listener)
    }

    fun remove(listener: LocationListener) {
        this.listeners.remove(listener)
    }

    companion object {

        val TAG = "LocationProviderService"
    }

}

