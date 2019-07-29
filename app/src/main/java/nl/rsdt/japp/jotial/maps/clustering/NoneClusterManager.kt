package nl.rsdt.japp.jotial.maps.clustering

import nl.rsdt.japp.jotial.data.structures.area348.ScoutingGroepInfo
import java.util.*

/**
 * Created by mattijn on 07/08/17.
 * this clustermanager does absolutely nothing
 * necessary for maptypes that do not support clustering
 */
class NoneClusterManager : ClusterManagerInterface {

    override val items: Collection<ScoutingGroepInfo>
        get() = object : Collection<ScoutingGroepInfo> {
            override fun containsAll(elements: Collection<ScoutingGroepInfo>): Boolean {
                return false
            }

            override val size: Int
                get() = 0

            override fun isEmpty(): Boolean {
                return true
            }

            override fun contains(o: ScoutingGroepInfo): Boolean {
                return false
            }

            override fun iterator(): Iterator<ScoutingGroepInfo> {
                return object : Iterator<ScoutingGroepInfo> {
                    override fun hasNext(): Boolean {
                        return false
                    }

                    override fun next(): ScoutingGroepInfo {
                        return null!!
                    }
                }
            }
        }

    override fun addItems(buffer: ArrayList<ScoutingGroepInfo>) {

    }

    override fun cluster() {

    }

    override fun clearItems() {

    }
}
