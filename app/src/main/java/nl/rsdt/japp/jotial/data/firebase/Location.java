package nl.rsdt.japp.jotial.data.firebase;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by mattijn on 30/09/17.
 */

public class Location {
    public double lat;
    public double lon;

    public Location(LatLng navigateTo) {
        this.lat = navigateTo.latitude;
        this.lon = navigateTo.longitude;
    }
}
