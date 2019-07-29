package nl.rsdt.japp.jotial.maps.wrapper.google

import com.google.android.gms.maps.model.LatLng

import nl.rsdt.japp.jotial.maps.wrapper.IPolyline

/**
 * Created by mattijn on 08/08/17.
 */

class GooglePolyline(private val googlePolyline: com.google.android.gms.maps.model.Polyline) : IPolyline {

    override var points: MutableList<LatLng>
        get() = googlePolyline.points
        set(points) {
            googlePolyline.points = points
        }

    override fun remove() {
        googlePolyline.remove()
    }

    override fun setVisible(visible: Boolean) {
        googlePolyline.isVisible = visible
    }
}

