package nl.rsdt.japp.jotial.maps.management.transformation.async

import android.os.AsyncTask
import android.os.Bundle

import nl.rsdt.japp.jotial.maps.management.transformation.AbstractTransducer
import nl.rsdt.japp.jotial.maps.management.transformation.Transducable

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 4-9-2016
 * Description...
 */
class AsyncBundleTransduceTask(protected var callback: OnBundleTransduceCompletedCallback?) : AsyncTask<Transducable<*, *>, Int, Bundle?>() {

    override fun doInBackground(vararg transducables: Transducable<*, *>): Bundle {
        val buffer = Bundle()
        var transducer: AbstractTransducer<*, *>
        for (i in transducables.indices) {
            transducer = transducables[i].transducer
            if (transducer != null) {
                transducer.isSaveEnabled = false
                transducer.transduceToBundle(buffer)
            }

        }
        return buffer
    }

    override fun onPostExecute(bundle: Bundle?) {
        super.onPostExecute(bundle)
        callback?.onTransduceCompleted(bundle!!)
    }

    interface OnBundleTransduceCompletedCallback {
        fun onTransduceCompleted(bundle: Bundle)
    }

}
