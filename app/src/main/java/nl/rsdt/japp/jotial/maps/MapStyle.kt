package nl.rsdt.japp.jotial.maps

import nl.rsdt.japp.R

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 28-9-2016
 * Description...
 */

object MapStyle {

    val AUTO = 1

    val DAY = 2

    val NIGHT = 3


    fun getAssociatedRaw(style: Int): Int {
        when (style) {
            MapStyle.AUTO -> {
            }
            MapStyle.DAY -> return R.raw.map_style_day
            MapStyle.NIGHT -> return R.raw.map_style_night
        }
        return R.raw.map_style_day
    }
}
