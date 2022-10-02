package nl.rsdt.japp.jotial.net

import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.util.Log
import java.io.InputStream
import java.net.URL
import java.util.*

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 14-8-2016
 * Description...
 */
class DownloadDrawableTask(private val callback: OnDowloadDrawablesCompletedCallback?) : AsyncTask<URL, Int, ArrayList<Drawable>>() {

    override fun doInBackground(vararg params: URL): ArrayList<Drawable> {
        val drawables = ArrayList<Drawable>()
        var url: URL?
        for (i in params.indices) {
            url = params[i]
            if (url != null) {
                try {
                    val `is` = url.content as InputStream
                    drawables.add(Drawable.createFromStream(`is`, url.file)!!)
                } catch (e: Exception) {
                    Log.e("UserInfo", e.toString(), e)
                }

            }
        }
        return drawables
    }

    override fun onPostExecute(drawables: ArrayList<Drawable>) {
        super.onPostExecute(drawables)

        callback?.onDownloadDrawablesCompleted(drawables)
    }

    interface OnDowloadDrawablesCompletedCallback {
        fun onDownloadDrawablesCompleted(drawables: ArrayList<Drawable>)
    }
}
