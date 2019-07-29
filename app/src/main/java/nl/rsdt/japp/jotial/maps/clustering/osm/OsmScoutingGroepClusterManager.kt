package nl.rsdt.japp.jotial.maps.clustering.osm

import nl.rsdt.japp.jotial.data.structures.area348.ScoutingGroepInfo
import nl.rsdt.japp.jotial.maps.clustering.ClusterManagerInterface
import nl.rsdt.japp.jotial.maps.wrapper.osm.OsmJotiMap
import java.util.*

/**
 * Created by mattijn on 10/08/17.
 */

class OsmScoutingGroepClusterManager(jotiMap: OsmJotiMap) : ClusterManagerInterface {

    private val infos: MutableCollection<ScoutingGroepInfo>
    private val markers: OsmMarkerContainer

    override val items: Collection<ScoutingGroepInfo>
        get() = infos

    init {
        if (jotiMap !is OsmJotiMap) {
            throw RuntimeException("this class can only be usd with osm")
        }
        infos = ArrayList()
        markers = OsmMarkerContainer(jotiMap)
    }

    override fun addItems(buffer: ArrayList<ScoutingGroepInfo>) {
        for (info in buffer) {
            infos.add(info)
            markers.add(info)
        }
        markers.showMarkers()
    }

    override fun cluster() {

    }

    override fun clearItems() {
        infos.clear()
        markers.clear()
    }
}
