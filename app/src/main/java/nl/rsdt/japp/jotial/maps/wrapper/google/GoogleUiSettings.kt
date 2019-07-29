package nl.rsdt.japp.jotial.maps.wrapper.google

import nl.rsdt.japp.jotial.maps.wrapper.IUiSettings

/**
 * Created by mattijn on 09/08/17.
 */

class GoogleUiSettings(private val googleUiSettings: com.google.android.gms.maps.UiSettings) : IUiSettings {

    override fun setAllGesturesEnabled(allesturesEnabled: Boolean) {
        googleUiSettings.setAllGesturesEnabled(allesturesEnabled)
    }

    override fun setCompassEnabled(compassEnabled: Boolean) {
        googleUiSettings.isCompassEnabled = compassEnabled
    }

    override fun setZoomControlsEnabled(zoomControlsEnabled: Boolean) {
        googleUiSettings.isZoomControlsEnabled = zoomControlsEnabled
    }

    override fun setIndoorLevelPickerEnabled(indoorLevelPickerEnabled: Boolean) {
        googleUiSettings.isIndoorLevelPickerEnabled = indoorLevelPickerEnabled
    }

    override fun setMapToolbarEnabled(mapToolbarEnabled: Boolean) {
        googleUiSettings.isMapToolbarEnabled = mapToolbarEnabled
    }
}
