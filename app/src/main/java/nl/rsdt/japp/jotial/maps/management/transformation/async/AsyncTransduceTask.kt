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

    protected var transducer: AbstractTransducer<I, O>

    protected var data: I

    protected var callback: OnTransduceCompletedCallback<O>? = null

    fun setTransducer(transducer: AbstractTransducer<I, O>) {
        this.transducer = transducer
    }

    fun setData(data: I) {
        this.data = data
    }

    fun setCallback(callback: OnTransduceCompletedCallback<O>) {
        this.callback = callback
    }

    protected override fun doInBackground(vararg integers: Int): O {
        return transducer.generate(data)
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
