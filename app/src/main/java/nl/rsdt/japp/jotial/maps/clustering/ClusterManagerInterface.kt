package nl.rsdt.japp.jotial.maps.clustering

import nl.rsdt.japp.jotial.data.structures.area348.ScoutingGroepInfo
import java.util.*

/**
 * Created by mattijn on 07/08/17.
 */

interface ClusterManagerInterface {

    val items: Collection<ScoutingGroepInfo>
    fun addItems(buffer: ArrayList<ScoutingGroepInfo>)

    fun cluster()

    fun clearItems()
}
