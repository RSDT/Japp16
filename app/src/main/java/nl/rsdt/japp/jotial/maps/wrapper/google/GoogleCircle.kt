package nl.rsdt.japp.jotial.maps.wrapper.google

import nl.rsdt.japp.jotial.maps.wrapper.ICircle

/**
 * Created by mattijn on 08/08/17.
 */

class GoogleCircle(private val googleCircle: com.google.android.gms.maps.model.Circle) : ICircle {

    override var fillColor: Int
        get() = googleCircle.fillColor
        set(color) {
            googleCircle.fillColor = color
        }

    override fun remove() {
        googleCircle.remove()
    }

    override fun setRadius(radius: Float) {
        googleCircle.radius = radius.toDouble()
    }

    override fun setVisible(visible: Boolean) {
        googleCircle.isVisible = visible
    }
}
