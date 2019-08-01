package nl.rsdt.japp.jotial.maps.clustering.osm

import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Pair
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import nl.rsdt.japp.R
import nl.rsdt.japp.application.Japp
import nl.rsdt.japp.application.JappPreferences
import nl.rsdt.japp.jotial.data.structures.area348.ScoutingGroepInfo
import nl.rsdt.japp.jotial.maps.deelgebied.Deelgebied
import nl.rsdt.japp.jotial.maps.management.MarkerIdentifier
import nl.rsdt.japp.jotial.maps.wrapper.ICircle
import nl.rsdt.japp.jotial.maps.wrapper.IMarker
import nl.rsdt.japp.jotial.maps.wrapper.osm.OsmJotiMap
import nl.rsdt.japp.jotial.maps.wrapper.osm.OsmMarker
import org.osmdroid.bonuspack.clustering.MarkerClusterer
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer
import org.osmdroid.views.overlay.Marker
import java.util.*

/**
 * Created by mattijn on 01/10/17.
 */

class OsmMarkerContainer(private val map: OsmJotiMap) : SharedPreferences.OnSharedPreferenceChangeListener {
    private val markers: MutableMap<Deelgebied, MarkerClusterer>

    init {
        markers = HashMap()
        JappPreferences.visiblePreferences.registerOnSharedPreferenceChangeListener(this)
    }

    fun add(info: ScoutingGroepInfo): IMarker {
        val options = MarkerOptions()
        val bm = BitmapFactory.decodeResource(Japp.appResources, R.drawable.scouting_groep_icon_30x22)
        options.position(info.position)

        val identifier = MarkerIdentifier.Builder()
                .setType(MarkerIdentifier.TYPE_SC)
                .add("name", info.naam)
                .add("adres", info.adres)
                .add("team", info.team)
                .create()
        options.title(Gson().toJson(identifier))

        val marker = map.addMarker(Pair(options, bm))
        marker.remove()
        marker.setOnClickListener(object : IMarker.OnClickListener {

            var circle: ICircle? = null
            var visible = false
            override fun OnClick(m: IMarker): Boolean {
                if (circle == null) {
                    circle = map.addCircle(CircleOptions()
                            .center(LatLng(info.position.latitude, info.position.longitude))
                            .radius(500.0)
                            .fillColor(Color.argb(80, 200, 200, 200)))
                    visible = true
                } else {
                    visible = if (visible) {
                        circle!!.setRadius(0f)
                        false
                    } else {
                        circle!!.setRadius(500f)
                        true
                    }
                }
                m.showInfoWindow()
                return true
            }
        })
        this.add(Deelgebied.parse(info.team!!), (marker as OsmMarker).osmMarker)
        return marker
    }

    private fun add(key: Deelgebied?, osmMarker: Marker) {
        if (!markers.containsKey(key)) {
            markers[key!!] = RadiusMarkerClusterer(Japp.instance!!.applicationContext)
            map.osmMap.overlays.add(markers[key])
        }
        markers[key]!!.add(osmMarker)
    }

    fun clear() {
        for (markerClusterer in markers.values) {
            map.osmMap.overlays.remove(markerClusterer)
        }
        markers.clear()
    }

    fun showMarkers() {
        for (d in markers.keys) {
            if (JappPreferences.areasEnabled!!.contains(d.name)) {
                if (!this.map.osmMap.overlays.contains(markers[d])) {
                    this.map.osmMap.overlays.add(markers[d])
                }
            } else {
                this.map.osmMap.overlays.remove(markers[d])
            }
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == JappPreferences.AREAS) {
            showMarkers()
        }
    }
}
