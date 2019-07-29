package nl.rsdt.japp.jotial.maps.wrapper

import android.graphics.Bitmap
import android.location.Location
import android.util.Pair
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import nl.rsdt.japp.jotial.maps.window.CustomInfoWindowAdapter

/**
 * Created by mattijn on 07/08/17.
 */

interface IJotiMap {

    val uiSettings: IUiSettings

    val previousCameraPosition: ICameraPosition

    fun delete()

    fun setInfoWindowAdapter(infoWindowAdapter: CustomInfoWindowAdapter)

    fun setGMapType(mapType: Int)

    fun setMapStyle(mapStyleOptions: MapStyleOptions): Boolean

    fun animateCamera(latLng: LatLng, zoom: Int)

    fun addMarker(markerOptions: Pair<MarkerOptions, Bitmap?>): IMarker

    fun addPolyline(polylineOptions: PolylineOptions): IPolyline

    fun addPolygon(polygonOptions: PolygonOptions): IPolygon

    fun addCircle(circleOptions: CircleOptions): ICircle

    fun setOnMapClickListener(onMapClickListener: OnMapClickListener?)

    fun snapshot(snapshotReadyCallback: IJotiMap.SnapshotReadyCallback?)

    fun animateCamera(latLng: LatLng, zoom: Int, cancelableCallback: IJotiMap.CancelableCallback?)

    fun setOnCameraMoveStartedListener(onCameraMoveStartedListener: GoogleMap.OnCameraMoveStartedListener?)

    fun cameraToLocation(b: Boolean, location: Location, zoom: Float, aoa: Float, bearing: Float)

    fun clear()

    fun setOnInfoWindowLongClickListener(onInfoWindowLongClickListener: GoogleMap.OnInfoWindowLongClickListener?)

    fun setMarkerOnClickListener(listener: IJotiMap.OnMarkerClickListener?)

    fun getMapAsync(callback: IJotiMap.OnMapReadyCallback)

    fun setPreviousCameraPosition(latitude: Double, longitude: Double)

    fun setPreviousZoom(zoom: Int)

    fun setPreviousRotation(rotation: Float)

    interface OnMapReadyCallback {
        fun onMapReady(map: IJotiMap)
    }

    interface OnMarkerClickListener {
        fun OnClick(m: IMarker): Boolean
    }

    interface OnMapClickListener {

        fun onMapClick(latLng: LatLng): Boolean
    }

    interface CancelableCallback {
        fun onFinish()

        fun onCancel()
    }

    interface SnapshotReadyCallback {
        fun onSnapshotReady(var1: Bitmap)
    }
}
