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
        switch (uisettingsType){
            case GOOGLE_UISETTINGS:
                googleUiSettings.setAllGesturesEnabled(allesturesEnabled);
                break;
            case OSM_UISETTINGS:
                //// TODO: 09/08/17
                break;
            default:
                break;
        }
    }

    public void setCompassEnabled(boolean compassEnabled) {
        switch (uisettingsType){
            case GOOGLE_UISETTINGS:
                googleUiSettings.setCompassEnabled(compassEnabled);
                break;
            case OSM_UISETTINGS:
                //// TODO: 09/08/17
                break;
            default:
                break;
        }
    }

    public void setZoomControlsEnabled(boolean zoomControlsEnabled) {
        switch (uisettingsType){
            case GOOGLE_UISETTINGS:
                googleUiSettings.setZoomControlsEnabled(zoomControlsEnabled);
                break;
            case OSM_UISETTINGS:
                //// TODO: 09/08/17
                break;
            default:
                break;
        }
    }

    public void setIndoorLevelPickerEnabled(boolean indoorLevelPickerEnabled) {
        switch (uisettingsType){
            case GOOGLE_UISETTINGS:
                googleUiSettings.setIndoorLevelPickerEnabled(indoorLevelPickerEnabled);
                break;
            case OSM_UISETTINGS:
                //// TODO: 09/08/17
                break;
            default:
                break;
        }
    }

    public void setMapToolbarEnabled(boolean mapToolbarEnabled) {
        switch (uisettingsType){
            case GOOGLE_UISETTINGS:
                googleUiSettings.setMapToolbarEnabled(mapToolbarEnabled);
                break;
            case OSM_UISETTINGS:
                //// TODO: 09/08/17
                break;
            default:
                break;
        }
    }
}
