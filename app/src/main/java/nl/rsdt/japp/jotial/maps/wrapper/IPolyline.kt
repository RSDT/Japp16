package nl.rsdt.japp.jotial.maps.wrapper

import com.google.android.gms.maps.model.LatLng

/**
 * Created by mattijn on 08/08/17.
 */

interface IPolyline {

    var points: MutableList<LatLng>

    fun remove()

    fun setVisible(visible: Boolean)
}
