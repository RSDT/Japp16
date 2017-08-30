package nl.rsdt.japp.jotial.maps.wrapper;

import org.osmdroid.views.MapView;

/**
 * Created by mattijn on 09/08/17.
 */

public interface IUiSettings {

    public void setAllGesturesEnabled(boolean allesturesEnabled) ;

    public void setCompassEnabled(boolean compassEnabled) ;

    public void setZoomControlsEnabled(boolean zoomControlsEnabled) ;

    public void setIndoorLevelPickerEnabled(boolean indoorLevelPickerEnabled) ;

    public void setMapToolbarEnabled(boolean mapToolbarEnabled) ;
}
