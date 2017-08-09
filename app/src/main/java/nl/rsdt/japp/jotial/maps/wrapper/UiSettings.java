package nl.rsdt.japp.jotial.maps.wrapper;

import org.osmdroid.views.MapView;

/**
 * Created by mattijn on 09/08/17.
 */

public class UiSettings {
    public static final int GOOGLE_UISETTINGS = 0;
    public static final int OSM_UISETTINGS = 1;
    private final int uisettingsType;
    private final com.google.android.gms.maps.UiSettings googleUiSettings;
    private final MapView osmMap;

    public UiSettings(com.google.android.gms.maps.UiSettings uiSettings) {
        googleUiSettings = uiSettings;
        uisettingsType = GOOGLE_UISETTINGS;
        osmMap = null;
    }

    public UiSettings(MapView osmMap) {
        uisettingsType = OSM_UISETTINGS;
        googleUiSettings = null;
        this.osmMap = osmMap;
    }

    public void setAllGesturesEnabled(boolean allesturesEnabled) {
        if (uisettingsType  == GOOGLE_UISETTINGS){
            googleUiSettings.setAllGesturesEnabled(allesturesEnabled);
        }
    }

    public void setCompassEnabled(boolean compassEnabled) {
        if (uisettingsType  == GOOGLE_UISETTINGS){
            googleUiSettings.setCompassEnabled(compassEnabled);
        }
    }

    public void setZoomControlsEnabled(boolean zoomControlsEnabled) {
        if (uisettingsType  == GOOGLE_UISETTINGS){
            googleUiSettings.setZoomControlsEnabled(zoomControlsEnabled);
        }
    }

    public void setIndoorLevelPickerEnabled(boolean indoorLevelPickerEnabled) {
        if (uisettingsType  == GOOGLE_UISETTINGS){
            googleUiSettings.setIndoorLevelPickerEnabled(indoorLevelPickerEnabled);
        }
    }

    public void setMapToolbarEnabled(boolean mapToolbarEnabled) {
        if (uisettingsType  == GOOGLE_UISETTINGS){
            googleUiSettings.setMapToolbarEnabled(mapToolbarEnabled);
        }
    }
}
