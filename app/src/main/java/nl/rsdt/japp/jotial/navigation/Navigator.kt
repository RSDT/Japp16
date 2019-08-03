package nl.rsdt.japp.jotial.navigation

import android.content.Context
import android.os.AsyncTask
import android.os.Handler
import android.os.Message
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import nl.rsdt.japp.R
import nl.rsdt.japp.application.Japp
import nl.rsdt.japp.application.JappPreferences
import nl.rsdt.japp.jotial.maps.wrapper.IJotiMap
import nl.rsdt.japp.jotial.maps.wrapper.IPolyline
import org.osmdroid.bonuspack.routing.*
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline
import java.util.*

/**
 * Created by mattijn on 16/08/17.
 */

class Navigator(private val map: IJotiMap?) {
    private val onFinishedHandler: Handler
    private var oldPolyline: IPolyline? = null

    init {
        onFinishedHandler = NavHandler(this)
    }

    internal class NavHandler(private val navigator: Navigator) : Handler() {

        override fun handleMessage(msg: Message) {
            if (msg.obj is Polyline) {
                navigator.setPolyline(msg.obj as Polyline)
            }
        }
    }

    fun setEndLocation(end: LatLng?, context: Context?) {
        if (Japp.lastLocation != null && end != null) {
            val r = RouteCalculator(this, context)
            val start = Japp.lastLocation
            if (start != null) {
                r.execute(LatLng(start.latitude, start.longitude), end)
            }
        }
    }

    fun clear() {
        oldPolyline?.remove()
    }

    fun setPolyline(newPolyline: Polyline?) {
        oldPolyline?.remove()
        if (newPolyline == null) {
            return
        }

        val options = PolylineOptions()
                .color(newPolyline.color)
                .visible(newPolyline.isVisible)
                .width(newPolyline.width)
        for (p in newPolyline.points) {
            options.add(LatLng(p.latitude, p.longitude))
        }
        oldPolyline = map?.addPolyline(options)
    }

    fun onFinished(newPolyline: Polyline) {
        onFinishedHandler.sendMessage(Message.obtain(onFinishedHandler, 0, newPolyline))
    }

    internal class RouteCalculator(private val callback: Navigator, private val context: Context?) : AsyncTask<LatLng, Void, Polyline>() {
        override fun doInBackground(vararg params: LatLng): Polyline {
            val roadManager: RoadManager = when (JappPreferences.roadManager){
                JappPreferences.RoadManager.MapQuest -> MapQuestRoadManager(Japp.getString(R.string.map_quest_key))
                JappPreferences.RoadManager.Google ->  GoogleRoadManager()
                JappPreferences.RoadManager.OSRM -> OSRMRoadManager(context)
                JappPreferences.RoadManager.GraphHopper -> GraphHopperRoadManager(Japp.getString(R.string.graphhopper_key),false)
            }

            val waypoints = ArrayList<GeoPoint>()
            for (p in params) {
                waypoints.add(GeoPoint(p.latitude, p.longitude))
            }

            val road = roadManager.getRoad(waypoints)
            val roadOverlay = RoadManager.buildRoadOverlay(road)
            callback.onFinished(roadOverlay)
            return roadOverlay
        }

        internal interface OnFinished {
            fun onFinished(p: Polyline)
        }
    }
}
