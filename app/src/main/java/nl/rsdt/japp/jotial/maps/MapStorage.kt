package nl.rsdt.japp.jotial.maps

import android.os.Bundle
import nl.rsdt.japp.jotial.maps.clustering.ScoutingGroepController
import nl.rsdt.japp.jotial.maps.management.MapItemController
import nl.rsdt.japp.jotial.maps.management.transformation.async.AsyncBundleTransduceTask
import java.util.*

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 19-9-2017
 * Description...
 */

class MapStorage : AsyncBundleTransduceTask.OnBundleTransduceCompletedCallback {

    var data: Bundle? = Bundle()
        private set

    private val callbacks = ArrayList<OnMapDataLoadedCallback>()

    fun add(callback: OnMapDataLoadedCallback) {
        callbacks.add(callback)
    }

    fun remove(callback: OnMapDataLoadedCallback) {
        callbacks.remove(callback)
    }

    fun load() {
        /**
         * Load in the map data.
         */
        AsyncBundleTransduceTask(this).execute(*MapItemController.all)
    }

    fun clear() {
        if (data != null) {
            data!!.clear()
            data = null
        }
    }

    override fun onTransduceCompleted(bundle: Bundle) {
        data = bundle
        ScoutingGroepController.loadAndPutToBundle(bundle)
        for (callback in callbacks) {
            callback.onMapDataLoaded()
        }
    }

    interface OnMapDataLoadedCallback {
        fun onMapDataLoaded()
    }

    companion object {

        val instance = MapStorage()
    }
}
