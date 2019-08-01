package nl.rsdt.japp.jotial.maps.wrapper.osm

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.util.Pair
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import nl.rsdt.japp.application.Japp
import nl.rsdt.japp.jotial.maps.management.MarkerIdentifier
import nl.rsdt.japp.jotial.maps.wrapper.IJotiMap
import nl.rsdt.japp.jotial.maps.wrapper.IMarker
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

/**
 * Created by mattijn on 08/08/17.
 */

class OsmMarker(markerOptionsPair: Pair<MarkerOptions, Bitmap?>, private val osmMap: MapView) : IMarker {
    val osmMarker: org.osmdroid.views.overlay.Marker = org.osmdroid.views.overlay.Marker(osmMap)
    private var onClickListener: IMarker.OnClickListener? = null

    override var title: String
        get() = osmMarker.title
        set(title) {
            osmMarker.title = title
        }

    override var position: LatLng
        get() = LatLng(osmMarker.position.latitude, osmMarker.position.longitude)
        set(latLng) {
            this.osmMarker.position = GeoPoint(latLng.latitude, latLng.longitude)
            osmMap.invalidate()
        }

    override var isVisible: Boolean
        get() = true
        set(visible) {
            osmMarker.isEnabled = visible
        }

    override// // TODO: 09/08/17 implement this
    val id: String
        get() = "1"

    init {
        val markerOptions = markerOptionsPair.first
        this.setIcon(markerOptionsPair.second)
        this.position = markerOptions.position

        if (markerOptions.title != null && markerOptions.title.isNotEmpty()) {
            val buff = StringBuilder()
            var identifier: MarkerIdentifier? = null
            try {
                identifier = Gson().fromJson(markerOptions.title, MarkerIdentifier::class.java)
            } catch (e: Exception) {
                Log.e("OsmMarker", e.toString())
            }

            if (identifier != null) {
                val properties = identifier.properties
                when (identifier.type) {
                    MarkerIdentifier.TYPE_VOS -> {
                        buff.append(properties["extra"]).append("\n")
                        buff.append(properties["time"]).append("\n")
                        buff.append(properties["note"]).append("\n")
                        buff.append(properties["team"]).append("\n")
                    }
                    MarkerIdentifier.TYPE_HUNTER -> {
                        buff.append(properties["hunter"]).append("\n")
                        buff.append(properties["time"]).append("\n")
                    }
                    MarkerIdentifier.TYPE_SC -> {
                        buff.append(properties["name"]).append("\n")
                        buff.append(properties["adres"]).append("\n")
                        buff.append(properties["team"]).append("\n")
                    }
                    MarkerIdentifier.TYPE_NAVIGATE_CAR -> {
                        buff.append("Dit is waar de navigatie telefoon naartoe naar navigeerd")
                        buff.append("\n")
                        buff.append("geplaatst door: ")
                        buff.append(properties["addedBy"])
                    }
                    MarkerIdentifier.TYPE_NAVIGATE -> buff.append("Hier naar zal je genavigeerd worden!")
                }//todo voeg leuk icoon toe
                osmMarker.title = buff.toString()
            } else {
                osmMarker.title = markerOptions.title
            }
        }



        osmMarker.setOnMarkerClickListener { marker, mapView ->
            if (marker === osmMarker) {
                onClick()
            }
            false
        }
        osmMap.overlays.add(osmMarker)
        osmMap.invalidate()
    }

    private fun onClick(): Boolean {
        if (allOnClickLister != null) {
            if (!allOnClickLister!!.OnClick(this)) {
                return if (this.onClickListener == null) {
                    showInfoWindow()
                    false
                } else {
                    this.onClickListener!!.OnClick(this)
                }
            }
        } else {
            return if (this.onClickListener == null) {
                showInfoWindow()
                false
            } else {
                this.onClickListener!!.OnClick(this)
            }
        }
        return false
    }

    override fun showInfoWindow() {
        osmMarker.showInfoWindow()
    }

    override fun remove() {
        osmMap.overlays.remove(osmMarker)
        osmMap.invalidate()
    }

    override fun setOnClickListener(onClickListener: IMarker.OnClickListener) {
        this.onClickListener = onClickListener
    }

    override fun setIcon(drawableHunt: Int) {
        this.setIcon(BitmapFactory.decodeResource(Japp.appResources, drawableHunt))

    }

    override fun setIcon(bitmap: Bitmap?) {
        val d = BitmapDrawable(Japp.appResources, bitmap)
        this.osmMarker.setIcon(d)
        osmMap.invalidate()
    }

    override fun setRotation(rotation: Float) {
        osmMarker.rotation = rotation
        osmMap.invalidate()
    }

    companion object {
        private var allOnClickLister: IJotiMap.OnMarkerClickListener? = null

        fun setAllOnClickLister(onClickListener: IJotiMap.OnMarkerClickListener?) {
            allOnClickLister = onClickListener
        }
    }
}
