package nl.rsdt.japp.jotial.maps.management

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import com.google.android.gms.maps.model.CircleOptions
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
import kotlin.collections.HashMap

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Description...
 */
abstract class MapItemController<I, O : AbstractTransducer.Result>(protected val jotiMap: IJotiMap) : MapItemDateControl(), Recreatable, MapItemHolder, MapItemUpdatable<I>, Transducable<I, O>, Identifiable, StorageIdentifiable, BundleIdentifiable, AsyncTransduceTask.OnTransduceCompletedCallback<O>, IntentCreatable, Searchable, Callback<I>, Mergable<O>, SharedPreferences.OnSharedPreferenceChangeListener {


    override var markers: MutableList<IMarker> = ArrayList()
        protected set

    override var polylines: MutableList<IPolyline> = ArrayList()
        protected set

    override var polygons: MutableList<IPolygon> = ArrayList()
        protected set

    var circlesController: MutableMap<ICircle, Int> = HashMap()

    override val circles: List<ICircle>
        get() {
            return circlesController.keys.toList()
        }

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

    init{
        JappPreferences.visiblePreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onIntentCreate(bundle: Bundle) {
        val result = bundle.getParcelable<O>(bundleId)
        if (result != null) {
            processResult(result)
        }
    }

    override fun onUpdateInvoked() {
        val call = update(MapItemUpdatable.MODE_ALL, this)
    }

    override fun onUpdateMessage(info: UpdateInfo) {
        val call: Call<I>?
        when (info.action) {
            UpdateInfo.ACTION_NEW -> update(MapItemUpdatable.MODE_ALL,this)
            UpdateInfo.ACTION_UPDATE -> update(MapItemUpdatable.MODE_ALL, this)
            else -> call = null
        }
    }

    override fun onResponse(call: Call<I>, response: Response<I>) {
        val body = response.body()
            if (body != null){
                transducer.enqueue(body, this)
            }else{
                Log.println(Log.ERROR, TAG, "{}".format(response.message()))
            }
    }

    override fun onFailure(call: Call<I>, t: Throwable) {
        Log.e(TAG, t.toString(), t)
    }

    override fun onTransduceCompleted(result: O) {
        if (markers.isNotEmpty() || polylines.isNotEmpty() || polygons.isNotEmpty()) {
            merge(result)
        } else {
            processResult(result)
        }
    }

    protected open fun processResult(result: O) {
        val markers = result.markers
        var marker: IMarker
        for (m in markers.indices) {
            marker = jotiMap.addMarker(markers[m])
            marker.isVisible = visiblity
            this.markers.add(marker)
        }

        val polylines = result.polylines
        var polyline: IPolyline
        for (p in polylines.indices) {
            polyline = jotiMap.addPolyline(polylines[p])
            polyline.setVisible(visiblity)
            this.polylines.add(polyline)
        }

        val polygons = result.polygons
        var polygon: IPolygon
        for (g in polygons.indices) {
            polygon = jotiMap.addPolygon(polygons[g])
            polygon.setVisible(visiblity)
            this.polygons.add(polygon)
        }

        val circles = result.circles ?: emptyList<CircleOptions>()
        for (c in circles.indices) {

                val circle = jotiMap.addCircle(circles[c])
                circle.setVisible(visiblity)
                this.circlesController[circle] = circle.fillColor
                if (!JappPreferences.fillCircles()) {
                    circle.fillColor = Color.TRANSPARENT
                }
        }
    }

    protected open fun clear() {
        for (i in markers.indices) {
            markers[i].remove()
        }
        markers.clear()

        for (i in polylines.indices) {
            polylines[i].remove()
        }
        polylines.clear()

        for (i in polygons.indices) {
            polygons[i].remove()
        }
        polygons.clear()
        val circles = ArrayList(this.circlesController.keys)
        for (i in circles.indices) {
            circles[i].remove()
        }
        circles.clear()

    }


    override fun searchFor(query: String): IMarker? {
        var marker: IMarker
        for (i in markers.indices) {
            marker = markers[i]
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

        }
}
