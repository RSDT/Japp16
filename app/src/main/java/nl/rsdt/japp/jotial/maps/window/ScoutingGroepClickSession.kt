package nl.rsdt.japp.jotial.maps.window

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.Marker
import com.google.gson.Gson

import nl.rsdt.japp.jotial.data.structures.area348.VosInfo
import nl.rsdt.japp.jotial.maps.management.MarkerIdentifier

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 18-9-2016
 * Description...
 */
class ScoutingGroepClickSession {

    private var googleMap: GoogleMap? = null

    private var marker: Marker? = null

    private var circle: Circle? = null

    val type: String?
        get() = if (marker != null) {
            Gson().fromJson(marker!!.title, MarkerIdentifier::class.java).type
        } else "none"

    fun start() {
        if (marker != null) {
            val identifier = Gson().fromJson(marker!!.title, MarkerIdentifier::class.java)
            if (MarkerIdentifier.TYPE_SC == identifier.type) {
                val team = identifier.properties["team"]
                circle = googleMap!!.addCircle(CircleOptions()
                        .center(marker!!.position)
                        .radius(500.0)
                        .fillColor(VosInfo.getAssociatedColor(team, 100))
                        .strokeWidth(0f))
            }
        }
    }


    fun end() {
        onDestroy()
    }

    private fun onDestroy() {
        if (circle != null) {
            circle!!.remove()
            circle = null
        }

        marker = null

        googleMap = null
    }


    class Builder {

        internal var buffer = ScoutingGroepClickSession()

        fun setMarker(marker: Marker): Builder {
            buffer.marker = marker
            return this
        }

        fun setGoogleMap(googleMap: GoogleMap): Builder {
            buffer.googleMap = googleMap
            return this
        }

        fun create(): ScoutingGroepClickSession {
            return buffer
        }
    }

    companion object {

        val TAG = "ScoutingGroepClickSession"
    }

}
