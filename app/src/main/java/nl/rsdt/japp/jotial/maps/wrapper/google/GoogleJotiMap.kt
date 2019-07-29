package nl.rsdt.japp.jotial.maps.wrapper.google

import android.graphics.Bitmap
import android.location.Location
import android.util.Pair
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.*
import nl.rsdt.japp.jotial.maps.misc.CameraUtils
import nl.rsdt.japp.jotial.maps.window.CustomInfoWindowAdapter
import nl.rsdt.japp.jotial.maps.wrapper.*
import java.util.*

/**
 * Created by mattijn on 07/08/17.
 */

class GoogleJotiMap private constructor(private val view: MapView) : IJotiMap {

    var googleMap: GoogleMap? = null
        private set
    override val previousCameraPosition:ICameraPosition
        get() {
            return GoogleCameraPosition(googleMap!!.cameraPosition)
        }
    private var previousCameraPositionLatLng: LatLng? = null

    private var previousZoom: Int = 0

    private var previousRotation: Float = 0.toFloat()

    override val uiSettings: IUiSettings
        get() = GoogleUiSettings(googleMap!!.uiSettings)

    override fun delete() {
        for (entry in google_instances){
            if (entry.value == this){
                google_instances.remove(entry.key)
            }
        }
    }

    override fun setPreviousCameraPosition(latitude: Double, longitude: Double) {
        previousCameraPositionLatLng = LatLng(latitude, longitude)
    }

    override fun setPreviousZoom(zoom: Int) {
        previousZoom = zoom
    }

    override fun setPreviousRotation(rotation: Float) {
        this.previousRotation = rotation
    }

    override fun setInfoWindowAdapter(infoWindowAdapter: CustomInfoWindowAdapter) {
        googleMap!!.setInfoWindowAdapter(infoWindowAdapter)
    }

    override fun setGMapType(mapType: Int) {
        googleMap!!.mapType = mapType
    }

    override fun setMapStyle(mapStyleOptions: MapStyleOptions): Boolean {
        return googleMap!!.setMapStyle(mapStyleOptions)
    }

    override fun animateCamera(latLng: LatLng, zoom: Int) {
        googleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom.toFloat()))
    }

    override fun addMarker(markerOptions: Pair<MarkerOptions, Bitmap?>): IMarker {
        if (markerOptions.second != null) {
            markerOptions.first.icon(BitmapDescriptorFactory.fromBitmap(markerOptions.second))
        } else {
            markerOptions.first.icon(BitmapDescriptorFactory.defaultMarker())
        }
        return GoogleMarker(googleMap!!.addMarker(markerOptions.first))

    }

    override fun addPolyline(polylineOptions: PolylineOptions): IPolyline {
        return GooglePolyline(googleMap!!.addPolyline(polylineOptions))
    }

    override fun addPolygon(polygonOptions: PolygonOptions): IPolygon {
        return GooglePolygon(googleMap!!.addPolygon(polygonOptions))
    }

    override fun addCircle(circleOptions: CircleOptions): ICircle {
        return GoogleCircle(googleMap!!.addCircle(circleOptions))

    }

    override fun setOnMapClickListener(onMapClickListener: IJotiMap.OnMapClickListener?) {
        if (onMapClickListener == null) {
            googleMap!!.setOnMapClickListener(null)
        } else {
            googleMap!!.setOnMapClickListener { latLng -> onMapClickListener.onMapClick(latLng) }
        }
    }

    override fun snapshot(snapshotReadyCallback: IJotiMap.SnapshotReadyCallback?) {
        if (snapshotReadyCallback != null) {
            googleMap!!.snapshot { bitmap -> snapshotReadyCallback.onSnapshotReady(bitmap) }
        } else {
            googleMap!!.snapshot(null)
        }
    }

    override fun animateCamera(latLng: LatLng, zoom: Int, cancelableCallback: IJotiMap.CancelableCallback?) {
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom.toFloat())
        if (cancelableCallback != null) {
            googleMap!!.animateCamera(cameraUpdate, object : GoogleMap.CancelableCallback {
                override fun onFinish() {
                    cancelableCallback.onFinish()
                }

                override fun onCancel() {
                    cancelableCallback.onCancel()
                }
            })
        } else {
            googleMap!!.animateCamera(cameraUpdate, null)
        }
    }

    override fun setOnCameraMoveStartedListener(onCameraMoveStartedListener: GoogleMap.OnCameraMoveStartedListener?) {
        if (onCameraMoveStartedListener != null) {
            googleMap!!.setOnCameraMoveStartedListener(onCameraMoveStartedListener)
        } else {
            googleMap!!.setOnCameraMoveStartedListener(null)
        }
    }

    override fun cameraToLocation(b: Boolean, location: Location, zoom: Float, aoa: Float, bearing: Float) {
        CameraUtils.cameraToLocation(b, googleMap, location, zoom, aoa, bearing)
    }

    override fun clear() {
        googleMap!!.clear()
    }

    override fun setOnInfoWindowLongClickListener(onInfoWindowLongClickListener: GoogleMap.OnInfoWindowLongClickListener?) {
        googleMap!!.setOnInfoWindowLongClickListener(onInfoWindowLongClickListener)
    }

    override fun setMarkerOnClickListener(listener: IJotiMap.OnMarkerClickListener?) {
        GoogleMarker.setAllOnClickLister(listener)
    }

    override fun getMapAsync(callback: IJotiMap.OnMapReadyCallback) {
        val t = this
        view.getMapAsync { map ->
            googleMap = map
            if (previousCameraPositionLatLng != null) {
                map.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition(previousCameraPositionLatLng!!, previousZoom.toFloat(), previousRotation, 0f)))
            }
            callback.onMapReady(t)
        }
    }

    companion object {

        private val google_instances = HashMap<MapView, GoogleJotiMap>()


        fun getJotiMapInstance(map: MapView): GoogleJotiMap? {
            if (!google_instances.containsKey(map)) {
                val jm = GoogleJotiMap(map)
                google_instances[map] = jm
            }
            return google_instances[map]
        }
    }
}
