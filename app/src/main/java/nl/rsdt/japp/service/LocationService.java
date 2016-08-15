package nl.rsdt.japp.service;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;
import com.rsdt.anl.WebRequest;
import com.rsdt.anl.WebRequestMethod;
import com.rsdt.anl.WebResponse;

import java.util.Calendar;

import nl.rsdt.japp.jotial.data.builders.HunterPostDataBuilder;
import nl.rsdt.japp.jotial.maps.locations.LocationProviderService;
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
                .setInterval(1000)
                .setFastestInterval(1000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);

        long dif = Calendar.getInstance().getTimeInMillis() - lastUpdate.getTimeInMillis();
        if(dif >= (1000 * 60)) {
            HunterPostDataBuilder builder = HunterPostDataBuilder.getDefault();
            builder.setLatLng(new LatLng(location.getLatitude(), location.getLongitude()));

            WebRequest request = new WebRequest.Builder()
                    .setUrl(new ApiUrlBuilder(false).append("hunter").build())
                    .setMethod(WebRequestMethod.POST)
                    .setData(builder.build())
                    .create();
            request.executeAsync(new WebRequest.OnWebRequestCompletedCallback() {
                @Override
                public void onWebRequestCompleted(WebResponse response) {
                    switch (response.getResponseCode()){
                        case 200:
                            Log.i(TAG, "Location successfully sent");
                            break;
                        default:
                            Log.e(TAG, "Error occured while sending location: " + response.getResponseCode());
                            break;
                    }
                }
            });
            lastUpdate = Calendar.getInstance();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        super.onConnected(bundle);
    }


}
