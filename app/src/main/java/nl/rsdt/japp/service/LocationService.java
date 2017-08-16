package nl.rsdt.japp.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;

import nl.rsdt.japp.R;
import nl.rsdt.japp.application.Japp;
import nl.rsdt.japp.application.JappPreferences;
import nl.rsdt.japp.application.activities.MainActivity;
import nl.rsdt.japp.jotial.data.bodies.HunterPostBody;
import nl.rsdt.japp.jotial.maps.locations.LocationProviderService;
import nl.rsdt.japp.jotial.net.apis.HunterApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 8-7-2016
 * Description...
 */
public class LocationService extends LocationProviderService {

    public static final String TAG = "LocationService";

    private final LocationBinder binder = new LocationBinder();

    private boolean wasSending = false;

    Calendar lastUpdate = Calendar.getInstance();

    @Override
    public void onCreate() {
        super.onCreate();
        wasSending = JappPreferences.isUpdatingLocationToServer();
        if(!wasSending) {
            showLocationNotification("Japp verzendt je locatie niet!", Color.rgb(244, 66, 66));
        } else {
            showLocationNotification("Japp verzendt je locatie", Color.rgb(113, 244, 66));
        }
    }

    private void showLocationNotification(String title, int color) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText("Klik om Japp te openen")
                .setSmallIcon(R.drawable.ic_my_location_white_48dp)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setColor(color)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(intent)
                .build();
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(1923, notification);
    }

    public LocationRequest getStandard() {
        return new LocationRequest()
                .setInterval((long)JappPreferences.getLocationUpdateIntervalInMs())
                .setFastestInterval((long)JappPreferences.getLocationUpdateIntervalInMs())
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);
        Japp.setLastLocation(location);
        long dif = Calendar.getInstance().getTimeInMillis() - lastUpdate.getTimeInMillis();

        boolean shouldSend = JappPreferences.isUpdatingLocationToServer();

        if(shouldSend != wasSending) {

            String title;
            int color;
            if(shouldSend) {
                title = "Japp verzendt je locatie";
                color = Color.rgb(113, 244, 66);
            } else {
                title = "Japp verzendt je locatie niet!";
                color = Color.rgb(244, 66, 66);
            }
            showLocationNotification(title, color);
        }

        if(shouldSend) {
            if(dif >= Math.round(JappPreferences.getLocationUpdateIntervalInMs())) {
                HunterPostBody builder = HunterPostBody.getDefault();
                builder.setLatLng(new LatLng(location.getLatitude(), location.getLongitude()));

                HunterApi api = Japp.getApi(HunterApi.class);
                api.post(builder).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Log.i(TAG, "Location was sent!");
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e(TAG, t.toString(), t);
                    }
                });

                lastUpdate = Calendar.getInstance();
                wasSending = true;
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        super.onConnected(bundle);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocationBinder extends Binder {
        public LocationService getInstance() {
            return LocationService.this;
        }
    }

}
