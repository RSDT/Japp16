package nl.rsdt.japp.jotial.maps.wrapper

/**
 * Created by mattijn on 09/08/17.
 */

interface IUiSettings {

    fun setAllGesturesEnabled(allesturesEnabled: Boolean)

    fun setCompassEnabled(compassEnabled: Boolean)

    fun setZoomControlsEnabled(zoomControlsEnabled: Boolean)

    fun setIndoorLevelPickerEnabled(indoorLevelPickerEnabled: Boolean)

    fun setMapToolbarEnabled(mapToolbarEnabled: Boolean)
}
