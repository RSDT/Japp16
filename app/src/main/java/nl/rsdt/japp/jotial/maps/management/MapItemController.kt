package nl.rsdt.japp.jotial.maps.management

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import nl.rsdt.japp.application.JappPreferences
import nl.rsdt.japp.jotial.BundleIdentifiable
import nl.rsdt.japp.jotial.Identifiable
import nl.rsdt.japp.jotial.IntentCreatable
import nl.rsdt.japp.jotial.Recreatable
import nl.rsdt.japp.jotial.io.StorageIdentifiable
import nl.rsdt.japp.jotial.maps.MapItemHolder
import nl.rsdt.japp.jotial.maps.Mergable
import nl.rsdt.japp.jotial.maps.management.controllers.*
import nl.rsdt.japp.jotial.maps.management.transformation.AbstractTransducer
import nl.rsdt.japp.jotial.maps.management.transformation.Transducable
import nl.rsdt.japp.jotial.maps.management.transformation.async.AsyncTransduceTask
import nl.rsdt.japp.jotial.maps.searching.Searchable
import nl.rsdt.japp.jotial.maps.wrapper.*
import nl.rsdt.japp.service.cloud.data.UpdateInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Description...
 */
abstract class MapItemController<I, O : AbstractTransducer.Result> : MapItemDateControl(), Recreatable, MapItemHolder, MapItemUpdatable<I>, Transducable<I, O>, Identifiable, StorageIdentifiable, BundleIdentifiable, AsyncTransduceTask.OnTransduceCompletedCallback<O>, IntentCreatable, Searchable, Callback<I>, Mergable<O>, SharedPreferences.OnSharedPreferenceChangeListener {

    protected var jotiMap: IJotiMap? = null

    override var markers: ArrayList<IMarker> = ArrayList()
        protected set

    override var polylines: ArrayList<IPolyline> = ArrayList()
        protected set

    override var polygons: ArrayList<IPolygon> = ArrayList()
        protected set

    var circlesController: MutableMap<ICircle, Int> = HashMap()

    override val circles: ArrayList<ICircle>
        get() {
            return ArrayList(circlesController.keys)
        }

    protected var buffer: O? = null

    var visiblity = true
        set(value) {
            field= value

            for (i in markers.indices) {
                markers[i].isVisible = visiblity
            }

            for (i in polylines.indices) {
                polylines[i].setVisible(visiblity)
            }

            for (i in polygons.indices) {
                polygons[i].setVisible(visiblity)
            }
            val circles = ArrayList(this.circlesController.keys)
            for (i in circles.indices) {
                circles[i].setVisible(visiblity)
            }
        }

    override fun onIntentCreate(bundle: Bundle) {
        val result = bundle.getParcelable<O>(bundleId)
        if (result != null) {
            if (jotiMap != null) {
                processResult(result)
            } else {
                buffer = result
            }
        }
    }


    fun onMapReady(jotiMap: IJotiMap) {
        this.jotiMap = jotiMap
        if (buffer != null) {
            if (!markers!!.isEmpty() || !polylines!!.isEmpty() || !polygons!!.isEmpty()) {
                merge(buffer!!)
            } else {
                processResult(buffer!!)
            }
            buffer = null
        }
        JappPreferences.visiblePreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onUpdateInvoked() {
        val call = update(MapItemUpdatable.MODE_ALL)
        call?.enqueue(this)
    }

    override fun onUpdateMessage(info: UpdateInfo) {
        val call: Call<I>?
        when (info.action) {
            UpdateInfo.ACTION_NEW -> call = update(MapItemUpdatable.MODE_ALL)
            UpdateInfo.ACTION_UPDATE -> call = update(MapItemUpdatable.MODE_ALL)
            else -> call = null
        }
        call?.enqueue(this)
    }

    override fun onResponse(call: Call<I>, response: Response<I>) {
        val body = response.body()
            if (body != null){
                transducer.enqueue(body, this)
            }else{
                Log.println(Log.ERROR, TAG,response.message())
            }
    }

    override fun onFailure(call: Call<I>, t: Throwable) {
        Log.e(TAG, t.toString(), t)
    }

    override fun onTransduceCompleted(result: O) {
        if (jotiMap != null) {
            if (!markers!!.isEmpty() || !polylines!!.isEmpty() || !polygons!!.isEmpty()) {
                merge(result)
            } else {
                processResult(result)
            }
        } else {
            buffer = result
        }
    }

    protected open fun processResult(result: O) {
        val markers = result.markers
        var marker: IMarker
        for (m in markers.indices) {
            marker = jotiMap!!.addMarker(markers[m])
            marker.isVisible = visiblity
            this.markers!!.add(marker)
        }

        val polylines = result.polylines
        var polyline: IPolyline
        for (p in polylines!!.indices) {
            polyline = jotiMap!!.addPolyline(polylines[p])
            polyline.setVisible(visiblity)
            this.polylines!!.add(polyline)
        }

        val polygons = result.polygons
        var polygon: IPolygon
        for (g in polygons!!.indices) {
            polygon = jotiMap!!.addPolygon(polygons[g])
            polygon.setVisible(visiblity)
            this.polygons!!.add(polygon)
        }

        val circles = result.circles
        for (c in circles!!.indices) {
            val circle = jotiMap!!.addCircle(circles[c])
            circle.setVisible(visiblity)
            this.circlesController[circle] = circle.fillColor
            if (!JappPreferences.fillCircles()) {
                circle.fillColor = Color.TRANSPARENT
            }
        }
    }

    protected open fun clear() {
        for (i in markers!!.indices) {
            markers!![i].remove()
        }
        markers!!.clear()

        for (i in polylines!!.indices) {
            polylines!![i].remove()
        }
        polylines!!.clear()

        for (i in polygons!!.indices) {
            polygons!![i].remove()
        }
        polygons!!.clear()
        val circles = ArrayList(this.circlesController.keys)
        for (i in circles.indices) {
            circles[i].remove()
        }
        circles.clear()

    }


    override fun searchFor(query: String): IMarker? {
        var marker: IMarker
        for (i in markers!!.indices) {
            marker = markers!![i]
            if (marker.title == query) {
                return marker
            }
        }
        return null
    }


    override fun onDestroy() {
            markers.clear()

            polylines.clear()
            polygons.clear()
            polygons

        buffer = null

        jotiMap = null

    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == JappPreferences.FILL_CIRCLES) {
            if (JappPreferences.fillCircles()) {
                for ((key1, value) in circlesController) {
                    key1.fillColor = value
                }
            } else {
                for ((key1) in circlesController) {
                    key1.fillColor = Color.TRANSPARENT
                }
            }
        }
    }

    companion object {

        val TAG = "MapItemController"

        val all: Array<MapItemController<*, *>>
            get() = arrayOf(FotoOpdrachtController(), HunterController(), AlphaVosController(), BravoVosController(), CharlieVosController(), DeltaVosController(), EchoVosController(), FoxtrotVosController(), XrayVosController())
    }
}
