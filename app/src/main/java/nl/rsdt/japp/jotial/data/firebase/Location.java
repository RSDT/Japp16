package nl.rsdt.japp.jotial.data.firebase;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.vision.face.Face;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.Map;

/**
 * Created by mattijn on 30/09/17.
 */

public class Location {
    public Long createdOn;
    public double lat;
    public double lon;
    public String createdBy;


    public Location () {} // speciaal voor firebase
    public Location(LatLng navigateTo, String createdBy) {
        this.lat = navigateTo.latitude;
        this.lon = navigateTo.longitude;
        this.createdBy = createdBy;
        createdOn = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object other){
        if (other == null) {
            return false;
        }
        if (other instanceof Location){
            Location l = (Location) other;
            return l.lon == this.lon && l.lat == this.lat;
        }
        else{
            return false;
        }
    }
}
