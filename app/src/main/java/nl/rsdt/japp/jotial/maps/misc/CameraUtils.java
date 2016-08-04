package nl.rsdt.japp.jotial.maps.misc;

import android.location.Location;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 25-7-2016
 * Description...
 */
public class CameraUtils {

    /**
     * Moves the camera to given location.
     */
    public static void cameraToLocation(boolean animate, GoogleMap googleMap, Location location, float zoom, float aoa, float bearing)
    {
        if(location != null) {
            if (animate) {

                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(location.getLatitude(), location.getLongitude()), zoom, aoa, bearing)));
            }
            else
            {
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(location.getLatitude(), location.getLongitude()), zoom, aoa, bearing)));
            }
        }

    }

}
