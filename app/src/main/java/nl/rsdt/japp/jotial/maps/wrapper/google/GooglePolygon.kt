package nl.rsdt.japp.jotial.maps.wrapper.google

import nl.rsdt.japp.jotial.maps.wrapper.IPolygon

/**
 * Created by mattijn on 08/08/17.
 */

class GooglePolygon(private val googlePolygon: com.google.android.gms.maps.model.Polygon) : IPolygon {

    override fun remove() {
        googlePolygon.remove()
    }

    override fun setStrokeWidth(strokeWidth: Int) {
        googlePolygon.strokeWidth = strokeWidth.toFloat()
    }

    override fun setFillColor(color: Int) {
        googlePolygon.fillColor = color
    }

    override fun setVisible(visible: Boolean) {
        googlePolygon.isVisible = visible
    }
}
