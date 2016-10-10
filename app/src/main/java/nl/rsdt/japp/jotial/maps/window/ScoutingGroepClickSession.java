package nl.rsdt.japp.jotial.maps.window;

import android.graphics.Color;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;

import nl.rsdt.japp.jotial.maps.management.MarkerIdentifier;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 18-9-2016
 * Description...
 */
public class ScoutingGroepClickSession {

    public static final String TAG = "ScoutingGroepClickSession";

    private GoogleMap googleMap;

    private Marker marker;

    private Circle circle;

    public String getType() {
        if(marker != null) {
            return new Gson().fromJson(marker.getTitle(), MarkerIdentifier.class).getType();
        }
        return "none";
    }

    public void start() {
        if(marker != null) {
            MarkerIdentifier identifier = new Gson().fromJson(marker.getTitle(), MarkerIdentifier.class);
            switch (identifier.getType()) {
                case MarkerIdentifier.TYPE_SC:
                    circle = googleMap.addCircle(new CircleOptions()
                            .center(marker.getPosition())
                            .radius(500)
                            .fillColor(Color.parseColor("#663300"))
                            .strokeWidth(0));
                    break;
            }
        }
    }


    public void end() {
        onDestroy();
    }

    private void onDestroy() {
        if(circle != null) {
            circle.remove();
            circle = null;
        }

        marker = null;

        googleMap = null;
    }


    public static class Builder {

        ScoutingGroepClickSession buffer = new ScoutingGroepClickSession();

        public Builder setMarker(Marker marker) {
            buffer.marker = marker;
            return this;
        }

        public Builder setGoogleMap(GoogleMap googleMap) {
            buffer.googleMap = googleMap;
            return this;
        }

        public ScoutingGroepClickSession create() {
            return buffer;
        }
    }

}
