package nl.rsdt.japp.jotial.maps.wrapper.osm

import android.graphics.Bitmap
import android.location.Location
import android.util.Pair
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import nl.rsdt.japp.jotial.maps.window.CustomInfoWindowAdapter
import nl.rsdt.japp.jotial.maps.wrapper.*
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import java.util.*


/**
 * Created by mattijn on 07/08/17.
 */

class OsmJotiMap private constructor(val osmMap: MapView //todo fix type
) : IJotiMap {

    private var eventsOverlay: MapEventsOverlay? = null

    override val uiSettings: IUiSettings
        get() = OsmUiSettings(osmMap)

    override val previousCameraPosition: ICameraPosition
        get() = OsmCameraPosition(osmMap)


    init {

        eventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                return false
            }

            override fun longPressHelper(p: GeoPoint): Boolean {
                return false
            }
        })
        osmMap.overlays.add(0, eventsOverlay)
    }

    override fun setPreviousCameraPosition(latitude: Double, longitude: Double) {
        val previousCameraPosition = GeoPoint(latitude, longitude)
    }

    override fun setPreviousZoom(previousZoom: Int) {
        //TODO
    }

    override fun setPreviousRotation(rotation: Float) {
        //TODO
    }

    override fun delete() {
        if (osm_instances.containsValue(this)) {
            osm_instances.remove(this.osmMap)
        }
    }

    override fun setInfoWindowAdapter(infoWindowAdapter: CustomInfoWindowAdapter) {
        //// TODO: 30/08/17
    }

    override fun setGMapType(mapType: Int) {
        //// TODO: 30/08/17
    }

    override fun setMapStyle(mapStyleOptions: MapStyleOptions): Boolean {
        return false //// TODO: 30/08/17
    }

    override fun animateCamera(latLng: LatLng, zoom: Int) {
        osmMap.controller.animateTo(GeoPoint(latLng.latitude, latLng.longitude))
        osmMap.controller.zoomTo(zoom)//// TODO: 07/08/17 controleren of de zoomlevels van googlemaps en osm enigzins overeenkomen.
    }

    override fun addMarker(markerOptions: Pair<MarkerOptions, Bitmap?>): IMarker {
        return OsmMarker(markerOptions, osmMap)
    }

    override fun addPolyline(polylineOptions: PolylineOptions): IPolyline {
        return OsmPolyline(polylineOptions, osmMap)
    }

    override fun addPolygon(polygonOptions: PolygonOptions): IPolygon {
        return OsmPolygon(polygonOptions, osmMap)
    }

    override fun addCircle(circleOptions: CircleOptions): ICircle {
        return OsmCircle(circleOptions, osmMap)
    }

    override fun setOnMapClickListener(onMapClickListener: IJotiMap.OnMapClickListener?) {
        osmMap.overlays.remove(eventsOverlay)
        eventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                return onMapClickListener?.onMapClick(LatLng(p.latitude, p.longitude)) ?: false
            }

            override fun longPressHelper(p: GeoPoint): Boolean {
                return false
            }
        })
        osmMap.overlays.add(0, eventsOverlay)
    }

    override fun snapshot(snapshotReadyCallback: IJotiMap.SnapshotReadyCallback?) {
        this.osmMap.isDrawingCacheEnabled = true
        this.osmMap.buildDrawingCache()
        val bm = this.osmMap.drawingCache
        snapshotReadyCallback?.onSnapshotReady(bm)
    }

    override fun animateCamera(latLng: LatLng, zoom: Int, cancelableCallback: IJotiMap.CancelableCallback?) {
        osmMap.controller.setCenter(GeoPoint(latLng.latitude, latLng.longitude))
        osmMap.controller.setZoom(zoom)
        cancelableCallback?.onFinish()
    }

    override fun setOnCameraMoveStartedListener(onCameraMoveStartedListener: GoogleMap.OnCameraMoveStartedListener?) {
        if (onCameraMoveStartedListener != null) {
            osmMap.setMapListener(object : MapListener {
                override fun onScroll(event: ScrollEvent): Boolean {
                    onCameraMoveStartedListener.onCameraMoveStarted(GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE)
                    return false
                }

                override fun onZoom(event: ZoomEvent): Boolean {
                    onCameraMoveStartedListener.onCameraMoveStarted(GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE)
                    return false
                }
            })
        } else {
            osmMap.setMapListener(null)
        }
    }

    override fun cameraToLocation(b: Boolean, location: Location, zoom: Float, aoa: Float, bearing: Float) {
        osmMap.controller.animateTo(GeoPoint(location.latitude, location.longitude))
    }

    override fun clear() {
        osmMap.overlays.clear()
    }

    override fun setOnInfoWindowLongClickListener(onInfoWindowLongClickListener: GoogleMap.OnInfoWindowLongClickListener?) {
        //// TODO: 30/08/17
    }

    override fun setMarkerOnClickListener(listener: IJotiMap.OnMarkerClickListener?) {
        OsmMarker.setAllOnClickLister(listener)
    }

    override fun getMapAsync(callback: IJotiMap.OnMapReadyCallback) {
        callback.onMapReady(this)
    }

    companion object {

        private val osm_instances = HashMap<MapView, OsmJotiMap>()


        fun getJotiMapInstance(map: MapView): OsmJotiMap? {
            if (!osm_instances.containsKey(map)) {
                val jm = OsmJotiMap(map)
                osm_instances[map] = jm
            }
            return osm_instances[map]
        }
    }
}
