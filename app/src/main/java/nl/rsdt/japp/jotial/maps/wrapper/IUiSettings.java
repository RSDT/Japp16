package nl.rsdt.japp.jotial.maps.wrapper;

/**
 * Created by mattijn on 09/08/17.
 */

public interface IUiSettings {

    void setAllGesturesEnabled(boolean allesturesEnabled);

    void setCompassEnabled(boolean compassEnabled);

    void setZoomControlsEnabled(boolean zoomControlsEnabled);

    void setIndoorLevelPickerEnabled(boolean indoorLevelPickerEnabled);

    void setMapToolbarEnabled(boolean mapToolbarEnabled);
}
