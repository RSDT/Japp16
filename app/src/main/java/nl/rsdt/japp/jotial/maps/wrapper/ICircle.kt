package nl.rsdt.japp.jotial.maps.wrapper

/**
 * Created by mattijn on 08/08/17.
 */

interface ICircle {

    var fillColor: Int

    fun remove()

    fun setRadius(radius: Float)

    fun setVisible(visible: Boolean)
}
