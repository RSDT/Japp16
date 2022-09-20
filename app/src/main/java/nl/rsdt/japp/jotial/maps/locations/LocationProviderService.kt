package nl.rsdt.japp.jotial.maps.locations

import android.Manifest
import android.app.Service
import android.content.pm.PackageManager
import android.location.Location
import android.os.Binder
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
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

    private var client: GoogleApiClient? = null

    private var requests: MutableList<LocationProviderService.LocationRequest> = LinkedList()
    private fun updateRequest(){
        if (requests.isEmpty()){
            client?.let{
                LocationServices.FusedLocationApi.removeLocationUpdates(it, this)
                isRequesting = false
                return
            }
        }
        var fastestInterval: Long = Long.MAX_VALUE
        var interval: Long = Long.MAX_VALUE
        var accuracy = Int.MAX_VALUE
        for (request in requests){
            if (request.fastestInterval < fastestInterval){
                fastestInterval = request.fastestInterval
            }
            if (request.interval < interval){
                interval = request.interval
            }
            if (request.accuracy < accuracy){
                accuracy = request.accuracy
            }
        }
        val request = com.google.android.gms.location.LocationRequest()
            .setInterval(interval)
            .setPriority(accuracy)
            .setFastestInterval(fastestInterval)
        client?.let{
            LocationServices.FusedLocationApi.removeLocationUpdates(it, this)
        }

        client?.let{
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(it, request, this)
            isRequesting = true
        }
    }
    fun addRequest(request: LocationProviderService.LocationRequest): Boolean{
        val index = requests.indexOf(request)
        if (requests[index] === request){
            return false;
        }
        requests.add(request)
        updateRequest()
        return true;
    }

    fun removeRequest(request: LocationProviderService.LocationRequest) : Boolean {
        val index = requests.indexOf(request)
        if (requests[index] !== request){
            return false;
        }
        if( requests.remove(request) ){
            updateRequest()
            return true;
        }
        return false;
    }

    private var listeners = ArrayList<LocationListener>()

    var lastLocation: Location? = null
        protected set

    private var isRequesting = false

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


    fun restartLocationUpdates() {
        updateRequest()
    }

    override fun onLocationChanged(location: Location) {
        lastLocation = location
        for (listener in listeners) {
            listener.onLocationChanged(location)
        }
    }

    override fun onConnected(bundle: Bundle?) {
        updateRequest()
        //// TODO: 20/10/17 quickfix #127 de bug is erin gekomen in df4e994826ede9b014335c573deb30b7b9fd3455
        // checkLocationSettings();
    }

    override fun onResult(result: LocationSettingsResult) {
        val status = result.status
        when (status.statusCode) {
            LocationSettingsStatusCodes.SUCCESS ->
                // All location settings are satisfied. The client can
                // initialize location requests here.
                updateRequest()
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
            requests.removeAll(requests)
            updateRequest()
            client!!.disconnect()
            client = null
        }
    }

    fun add(listener: LocationListener) {
        this.listeners.add(listener)
    }

    fun remove(listener: LocationListener) {
        this.listeners.remove(listener)
    }
    data class LocationRequest(val accuracy: Int, val fastestInterval:Long, val interval :Long)
    companion object {

        val TAG = "LocationProviderService"
    }

}

