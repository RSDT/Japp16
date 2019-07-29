package nl.rsdt.japp.jotial.maps.management

import android.os.Bundle
import android.os.Parcelable
import nl.rsdt.japp.jotial.maps.management.transformation.AbstractTransducer
import java.util.*

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 4-9-2016
 * Description...
 */
abstract class StandardMapItemController<I : Parcelable, O : AbstractTransducer.StandardResult<I>> : MapItemController<ArrayList<I>, O>() {

    protected var items: ArrayList<I>? = ArrayList()

    override fun onIntentCreate(bundle: Bundle) {
        super.onIntentCreate(bundle)
        val result = bundle.getParcelable<O>(bundleId)
        if (result != null) {
            this.items = result.items
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState?.containsKey(bundleId)== true) {
            this.items = savedInstanceState.getParcelableArrayList(bundleId)
            val result = transducer.generate(items!!)
            if (jotiMap != null) {
                processResult(result)
            } else {
                buffer = result
            }
        }
    }

    override fun onSaveInstanceState(saveInstanceState: Bundle?) {
        saveInstanceState?.putParcelableArrayList(bundleId, items)
    }

    override fun merge(other: O) {
        clear()
        processResult(other)
    }

    override fun processResult(result: O) {
        super.processResult(result)
        this.items!!.addAll(result.items)
    }

    override fun clear() {
        super.clear()
        items!!.clear()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (items != null) {
            /**
             * Do not clear the list or the parcelable list inside the bundle will be empty as well
             */
            items = null
        }
    }
}
