package nl.rsdt.japp.jotial.maps.wrapper.google

import nl.rsdt.japp.jotial.maps.wrapper.ICameraPosition

/**
 * Created by mattijn on 17/08/17.
 */

class GoogleCameraPosition(private val googlePos: com.google.android.gms.maps.model.CameraPosition) : ICameraPosition {

    override val zoom: Float
        get() = googlePos.zoom

    override val tilt: Float
        get() = googlePos.tilt
}
