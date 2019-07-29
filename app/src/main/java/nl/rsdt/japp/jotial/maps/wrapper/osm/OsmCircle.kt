package nl.rsdt.japp.jotial.maps.wrapper.osm

import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import nl.rsdt.japp.jotial.maps.wrapper.ICircle
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polygon
import java.util.*

/**
 * Created by mattijn on 08/08/17.
 */

class OsmCircle(circleOptions: CircleOptions, private val osmMap: MapView) : ICircle {
    private val osmCircle: Polygon
    private val centre: LatLng

    override var fillColor: Int
        get() = osmCircle.fillColor
        set(color) {
            osmCircle.fillColor = color
        }

    init {
        osmCircle = Polygon()

        osmCircle.strokeWidth = circleOptions.strokeWidth
        osmCircle.fillColor = circleOptions.fillColor
        osmCircle.strokeColor = circleOptions.strokeColor

        val radius = circleOptions.radius
        this.centre = circleOptions.center
        this.setRadius(radius.toFloat())
        osmMap.overlays.add(osmCircle)
    }

    override fun remove() {
        osmMap.overlays.remove(osmCircle)
    }

    override fun setRadius(radius: Float) {
        val nrOfPoints = 360
        val circlePoints = ArrayList<GeoPoint>()
        for (f in 0 until nrOfPoints) {
            circlePoints.add(GeoPoint(centre.latitude, centre.longitude).destinationPoint(radius.toDouble(), f.toFloat()))
        }
        osmCircle.points = circlePoints
    }

    override fun setVisible(visible: Boolean) {
        osmCircle.isVisible = visible
    }
}
