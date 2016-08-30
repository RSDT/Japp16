package nl.rsdt.japp.jotial.maps;

import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 29-8-2016
 * Description...
 */
public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {

    @Override
    public View getInfoWindow(Marker marker) {
        String title = marker.getTitle();
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

}
