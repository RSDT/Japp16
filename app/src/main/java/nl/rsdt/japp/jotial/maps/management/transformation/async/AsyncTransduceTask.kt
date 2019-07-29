package nl.rsdt.japp.jotial.maps.management.transformation.async

import android.os.AsyncTask

import nl.rsdt.japp.jotial.maps.management.transformation.AbstractTransducer

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 4-9-2016
 * Description...
 */
class AsyncTransduceTask<I, O : AbstractTransducer.Result> : AsyncTask<Int, Int, O>() {

    var transducer: AbstractTransducer<I, O>? = null

    var data: I? = null

    var callback: OnTransduceCompletedCallback<O>? = null

    protected override fun doInBackground(vararg integers: Int?): O? {
        return data?.let { transducer?.generate(it) }
    }

    override fun onPostExecute(output: O?) {
        if (output != null && callback != null) {
            callback!!.onTransduceCompleted(output)
        }
    }

    interface OnTransduceCompletedCallback<O : AbstractTransducer.Result> {
        fun onTransduceCompleted(result: O)
    }
}
