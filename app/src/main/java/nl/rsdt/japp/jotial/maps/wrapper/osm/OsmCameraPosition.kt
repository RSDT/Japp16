package nl.rsdt.japp.jotial.maps.wrapper.osm

import nl.rsdt.japp.jotial.maps.wrapper.ICameraPosition
import org.osmdroid.views.MapView

/**
 * Created by mattijn on 17/08/17.
 */

class OsmCameraPosition(private val osmMap: MapView) : ICameraPosition {
    override val zoom: Float
        get() = osmMap.zoomLevel.toFloat()

    override val tilt: Float
        get() = 90f
}
