package nl.rsdt.japp.jotial.maps.wrapper.google;

import org.osmdroid.views.MapView;

import nl.rsdt.japp.jotial.maps.wrapper.IUiSettings;

/**
 * Created by mattijn on 09/08/17.
 */

public class GoogleUiSettings implements IUiSettings{
    private final com.google.android.gms.maps.UiSettings googleUiSettings;

    public GoogleUiSettings(com.google.android.gms.maps.UiSettings uiSettings) {
        googleUiSettings = uiSettings;
    }

    @Override
    public void setAllGesturesEnabled(boolean allesturesEnabled) {
        googleUiSettings.setAllGesturesEnabled(allesturesEnabled);
    }

    @Override
    public void setCompassEnabled(boolean compassEnabled) {
        googleUiSettings.setCompassEnabled(compassEnabled);
    }

    @Override
    public void setZoomControlsEnabled(boolean zoomControlsEnabled) {
        googleUiSettings.setZoomControlsEnabled(zoomControlsEnabled);
    }

    @Override
    public void setIndoorLevelPickerEnabled(boolean indoorLevelPickerEnabled) {
        googleUiSettings.setIndoorLevelPickerEnabled(indoorLevelPickerEnabled);
    }

    @Override
    public void setMapToolbarEnabled(boolean mapToolbarEnabled) {
        googleUiSettings.setMapToolbarEnabled(mapToolbarEnabled);
    }
}
