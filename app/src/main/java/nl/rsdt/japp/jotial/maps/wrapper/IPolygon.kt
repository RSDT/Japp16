package nl.rsdt.japp.jotial.maps.wrapper

/**
 * Created by mattijn on 08/08/17.
 */

interface IPolygon {


    fun remove()

    fun setStrokeWidth(strokeWidth: Int)

    fun setFillColor(color: Int)

    fun setVisible(visible: Boolean)
}
