package nl.rsdt.japp.jotial.maps.misc

import android.location.Location

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 25-7-2016
 * Description...
 */
object CameraUtils {

    /**
     * Moves the camera to given location.
     */
    fun cameraToLocation(animate: Boolean, googleMap: GoogleMap, location: Location?, zoom: Float, aoa: Float, bearing: Float) {
        if (location != null) {
            if (animate) {

                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition(LatLng(location.latitude, location.longitude), zoom, aoa, bearing)))
            } else {
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition(LatLng(location.latitude, location.longitude), zoom, aoa, bearing)))
            }
        }
    }

    /**
     * Moves the camera to given location.
     */
    fun cameraToLocation(animate: Boolean, googleMap: GoogleMap, location: LatLng?, zoom: Float, aoa: Float, bearing: Float) {
        if (location != null) {
            if (animate) {

                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition(location, zoom, aoa, bearing)))
            } else {
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition(location, zoom, aoa, bearing)))
            }
        }

    }

}
