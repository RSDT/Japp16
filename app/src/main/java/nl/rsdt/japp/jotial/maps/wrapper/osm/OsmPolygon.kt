package nl.rsdt.japp.jotial.maps.wrapper.osm

import com.google.android.gms.maps.model.PolygonOptions
import nl.rsdt.japp.jotial.maps.wrapper.IPolygon
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import java.util.*

/**
 * Created by mattijn on 08/08/17.
 */

class OsmPolygon(polygonOptions: PolygonOptions, private val osmMap: MapView) : IPolygon {
    private val osmPolygon: org.osmdroid.views.overlay.Polygon

    init {
        osmPolygon = org.osmdroid.views.overlay.Polygon()
        osmPolygon.fillColor = polygonOptions.fillColor
        osmPolygon.strokeColor = polygonOptions.strokeColor
        osmPolygon.strokeWidth = polygonOptions.strokeWidth
        val points = ArrayList<GeoPoint>()
        for (ll in polygonOptions.points) {
            points.add(GeoPoint(ll.latitude, ll.longitude))
        }
        osmPolygon.points = points
        osmMap.overlays.add(osmPolygon)
    }

    override fun remove() {
        osmMap.overlays.remove(osmPolygon)
    }

    override fun setStrokeWidth(strokeWidth: Int) {
        osmPolygon.strokeWidth = strokeWidth.toFloat()
    }

    override fun setFillColor(color: Int) {
        osmPolygon.fillColor = color
    }

    override fun setVisible(visible: Boolean) {
        osmPolygon.isVisible = visible
    }

}
