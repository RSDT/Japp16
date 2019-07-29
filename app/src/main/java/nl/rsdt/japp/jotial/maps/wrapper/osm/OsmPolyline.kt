package nl.rsdt.japp.jotial.maps.wrapper.osm

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import nl.rsdt.japp.jotial.maps.wrapper.IPolyline
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import java.util.*

/**
 * Created by mattijn on 08/08/17.
 */

class OsmPolyline(polylineOptions: PolylineOptions, private val osmMap: MapView) : IPolyline {
    private val osmPolyline: org.osmdroid.views.overlay.Polyline

    override var points: MutableList<LatLng>
        get() {
            val r = ArrayList<LatLng>()
            for (p in osmPolyline.points) {
                r.add(LatLng(p.latitude, p.longitude))
            }
            return r
        }
        set(points) {
            val r = ArrayList<GeoPoint>()
            for (p in points) {
                r.add(GeoPoint(p.latitude, p.longitude))
            }
            osmPolyline.points = r
        }

    init {
        val polyline = org.osmdroid.views.overlay.Polyline()
        polyline.color = polylineOptions.color
        polyline.isVisible = polylineOptions.isVisible
        val points = ArrayList<GeoPoint>()
        for (latlng in polylineOptions.points) {
            points.add(GeoPoint(latlng.latitude, latlng.longitude))
        }
        polyline.points = points
        polyline.width = polylineOptions.width
        osmMap.overlayManager.add(polyline)
        osmMap.invalidate()
        osmPolyline = polyline
    }

    override fun remove() {
        osmMap.overlays.remove(osmPolyline)
    }

    override fun setVisible(visible: Boolean) {
        osmPolyline.isVisible = visible
    }
}
