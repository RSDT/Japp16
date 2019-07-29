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
            override fun size(): Int {
                return 0
            }

            override fun isEmpty(): Boolean {
                return true
            }

            override operator fun contains(o: Any): Boolean {
                return false
            }

            override fun iterator(): Iterator<ScoutingGroepInfo> {
                return object : Iterator<ScoutingGroepInfo> {
                    override fun hasNext(): Boolean {
                        return false
                    }

                    override fun next(): ScoutingGroepInfo? {
                        return null
                    }
                }
            }

            override fun toArray(): Array<Any> {
                return arrayOfNulls(0)
            }

            override fun <T> toArray(a: Array<T>): Array<T> {
                return a
            }

            override fun add(scoutingGroepInfo: ScoutingGroepInfo): Boolean {
                return false
            }

            override fun remove(o: Any): Boolean {
                return false
            }

            override fun containsAll(c: Collection<*>): Boolean {
                return false
            }

            override fun addAll(c: Collection<ScoutingGroepInfo>): Boolean {
                return false
            }

            override fun removeAll(c: Collection<*>): Boolean {
                return false
            }

            override fun retainAll(c: Collection<*>): Boolean {
                return false
            }

            override fun clear() {

            }
        }

    override fun addItems(buffer: ArrayList<ScoutingGroepInfo>) {

    }

    override fun cluster() {

    }

    override fun clearItems() {

    }
}
