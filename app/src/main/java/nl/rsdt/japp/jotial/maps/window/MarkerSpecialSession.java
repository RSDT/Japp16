package nl.rsdt.japp.jotial.maps.window;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import nl.rsdt.japp.application.JappPreferences;
import nl.rsdt.japp.jotial.maps.management.MarkerIdentifier;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 18-9-2016
 * Description...
 */
public class MarkerSpecialSession implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String TAG = "MarkerSpecialSession";

    /**
     * Handler for updating the Hunters.
     * */
    private Handler handler = new Handler();

    private VosCircleUpdateRunnable runnable;

    private GoogleMap googleMap;

    private Marker marker;

    private Circle circle;

    public String getType() {
        if(marker != null) {
            return new Gson().fromJson(marker.getTitle(), MarkerIdentifier.class).getType();
        }
        return "none";
    }

    public MarkerSpecialSession() {
        JappPreferences.getVisiblePreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case JappPreferences.AUTO_ENLARGMENT:
                if(runnable != null) {
                    runnable.run();
                }
                break;
        }
    }

    public void start() {
        if(marker != null) {
            MarkerIdentifier identifier = new Gson().fromJson(marker.getTitle(), MarkerIdentifier.class);
            switch (identifier.getType()) {
                case MarkerIdentifier.TYPE_SC:
                    circle = googleMap.addCircle(new CircleOptions()
                            .center(marker.getPosition())
                            .radius(500)
                            .fillColor(Color.argb(120, 255, 255, 255))
                            .strokeWidth(0));
                    break;
                case MarkerIdentifier.TYPE_VOS:
                    runnable = new VosCircleUpdateRunnable();
                    runnable.run();
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

        if(handler != null) {
            if(runnable != null) {
                handler.removeCallbacks(runnable);
                runnable = null;
            }
            handler = null;
        }

        marker = null;

        googleMap = null;

        JappPreferences.getVisiblePreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    private class VosCircleUpdateRunnable implements Runnable {

        @Override
        public void run() {
            MarkerIdentifier identifier = new Gson().fromJson(marker.getTitle(), MarkerIdentifier.class);
            if(identifier.getType().equals(MarkerIdentifier.TYPE_VOS)) {
                HashMap<String, String> properties = identifier.getProperties();
                String time = properties.get("time");
                Date date = null;
                try {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT);
                    date = format.parse(time);
                } catch (ParseException e) {
                    Log.e(TAG, e.toString(), e);
                }
                float speed = JappPreferences.getWalkSpeed();
                float size = 500;

                if(date != null) {
                    float diffMs = new Date().getTime() - date.getTime();
                    float diffS = diffMs / 1000;
                    size = diffS * (speed / 3.6f );
                }
                if(circle != null) {
                    circle.setRadius(size);
                } else {
                    circle = googleMap.addCircle(new CircleOptions()
                            .center(marker.getPosition())
                            .radius(size)
                            .fillColor(Integer.parseInt(properties.get("color")))
                            .strokeColor(Color.argb(90, 255, 153, 0))
                            .strokeWidth(5));
                }
                if(JappPreferences.isAutoEnlargementEnabled()) {
                    handler.postDelayed(this, (long)JappPreferences.getAutoEnlargementIntervalInMs());
                }
            }
        }
    }

    public static class Builder {

        MarkerSpecialSession buffer = new MarkerSpecialSession();

        public Builder setMarker(Marker marker) {
            buffer.marker = marker;
            return this;
        }

        public Builder setGoogleMap(GoogleMap googleMap) {
            buffer.googleMap = googleMap;
            return this;
        }

        public MarkerSpecialSession create() {
            return buffer;
        }
    }

}
