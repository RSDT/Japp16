package nl.rsdt.japp.service;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.rsdt.anl.WebRequest;
import com.rsdt.anl.WebRequestMethod;
import com.rsdt.anl.WebResponse;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Map;

import nl.rsdt.japp.application.Japp;
import nl.rsdt.japp.application.JappPreferences;
import nl.rsdt.japp.jotial.data.builders.HunterPostDataBuilder;
import nl.rsdt.japp.jotial.maps.locations.LocationProviderService;
import nl.rsdt.japp.jotial.net.ApiPostRequest;
import nl.rsdt.japp.jotial.net.ApiUrlBuilder;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 8-7-2016
 * Description...
 */
public class LocationService extends LocationProviderService {

    public static final String TAG = "LocationService";

    Calendar lastUpdate = Calendar.getInstance();

    @Override
    public void onCreate() {
        super.onCreate();
        request = new LocationRequest()
                .setInterval(Float.floatToIntBits(JappPreferences.getLocationUpdateIntervalInMs()))
                .setFastestInterval(Float.floatToIntBits(JappPreferences.getLocationUpdateIntervalInMs()))
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);

        long dif = Calendar.getInstance().getTimeInMillis() - lastUpdate.getTimeInMillis();
        if(dif >= Float.floatToIntBits(JappPreferences.getLocationUpdateIntervalInMs())) {
            HunterPostDataBuilder builder = HunterPostDataBuilder.getDefault();
            builder.setLatLng(new LatLng(location.getLatitude(), location.getLongitude()));

            ApiPostRequest request = new ApiPostRequest(
                    new ApiUrlBuilder(false).append("hunter").buildAsString(),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i(TAG, "Location was sent to the server");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, error.getMessage(), error);
                        }
                    }, builder.buildAsJSONObject());

            Japp.getRequestQueue().add(request);
            Japp.getRequestQueue().start();
            lastUpdate = Calendar.getInstance();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        super.onConnected(bundle);
    }


}
