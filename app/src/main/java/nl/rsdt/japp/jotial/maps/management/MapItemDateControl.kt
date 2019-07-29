package nl.rsdt.japp.jotial.maps.management

import nl.rsdt.japp.jotial.Destroyable
import java.util.*

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Description...
 */
abstract class MapItemDateControl : Destroyable {

    var lastUpdateDate: Calendar? = Calendar.getInstance()
        protected set

    protected fun isElapsedSinceLastUpdate(time: Long): Boolean {
        val now = Calendar.getInstance()
        val delta = now.timeInMillis - lastUpdateDate!!.timeInMillis
        return delta >= time
    }

    override fun onDestroy() {
        lastUpdateDate = null
    }
}
