package nl.rsdt.japp.service;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;

import nl.rsdt.japp.R;
import nl.rsdt.japp.application.Japp;
import nl.rsdt.japp.application.JappPreferences;
import nl.rsdt.japp.application.activities.MainActivity;
import nl.rsdt.japp.application.fragments.JappMapFragment;
import nl.rsdt.japp.jotial.data.bodies.HunterPostBody;
import nl.rsdt.japp.jotial.maps.NavigationLocationManager;
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
public class LocationService extends LocationProviderService implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String TAG = "LocationService";

    public static final String ACTION_REQUEST_LOCATION_SETTING = "ACTION_REQUEST_LOCATION_SETTING";

    private final LocationBinder binder = new LocationBinder();

    private boolean wasSending = false;

    private OnResolutionRequiredListener listener;

    Calendar lastUpdate = Calendar.getInstance();

    private NavigationLocationManager locationManager;

    private BroadcastReceiver locationSettingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
                // Make an action or refresh an already managed state.
                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if(!gps) {
                    Intent notificationIntent = new Intent(LocationService.this, MainActivity.class);
                    notificationIntent.setAction(ACTION_REQUEST_LOCATION_SETTING);
                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent pendingIntent = PendingIntent.getActivity(LocationService.this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    showLocationNotification(getString(R.string.japp_not_sending_location), getString(R.string.torn_on_gps), Color.rgb(244, 66, 66), pendingIntent);
                }
            }
        }
    };

    public void setListener(OnResolutionRequiredListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        JappPreferences.getVisiblePreferences().registerOnSharedPreferenceChangeListener(this);

        registerReceiver(locationSettingReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));

        locationManager = new NavigationLocationManager();
        locationManager.setCallback(new NavigationLocationManager.OnNewLocation() {
            @Override
            public void onNewLocation(nl.rsdt.japp.jotial.data.firebase.Location location) {
                if (JappPreferences.isNavigationPhone()) {
                    try {
                        String mesg = getString(R.string.location_received, location.createdBy, location.lat, location.lon);
                        showToast(mesg, Toast.LENGTH_LONG);
                        switch (JappPreferences.navigationApp()) {
                            case GoogleMaps:
                                String uristr = getString(R.string.google_uri, Double.toString(location.lat),Double.toString(location.lon));
                                Uri gmmIntentUri = Uri.parse(uristr);
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mapIntent.setPackage("com.google.android.apps.maps");
                                startActivity(mapIntent);
                                break;
                            case Waze:
                                String uri = getString(R.string.waze_uri, Double.toString(location.lat),Double.toString(location.lon));
                                Intent wazeIntent =new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                wazeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(wazeIntent);
                                break;
                            case OSMAnd:
                                String osmUri = getString(R.string.osmand_uri, Double.toString(location.lat),Double.toString(location.lon));
                                Intent osmIntent =new Intent(Intent.ACTION_VIEW, Uri.parse(osmUri));
                                osmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(osmIntent);
                                break;
                            case Geo:
                                String geoUri = getString(R.string.geo_uri, Double.toString(location.lat),Double.toString(location.lon));
                                Intent geoIntent =new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                                geoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(geoIntent);
                                break;
                        }
                    } catch (ActivityNotFoundException e) {
                        System.out.println(e.toString());
                        String mesg = getString(R.string.navigation_app_not_installed, JappPreferences.navigationApp().toString());
                        showToast(mesg, Toast.LENGTH_SHORT);
                    }
                }
            }

            @Override
            public void onNotInCar() {
                String mesg = getString(R.string.fout_not_in_car);
                showToast(mesg, Toast.LENGTH_LONG);
            }
        });
    }
    private void showToast(final String message, final int length){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),
                        message,
                        length).show();
            }
        });
    }
    public void showLocationNotification(String title, int color) {
        showLocationNotification(title, getString(R.string.open_japp), color);
    }
    public void showLocationNotification(String title, String description, int color) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        showLocationNotification(title, description, color, intent);
    }

    public void showLocationNotification(String title, String description, int color, PendingIntent intent) {
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(description)
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
                title = getString(R.string.japp_sends_location);
                color = Color.rgb(113, 244, 66);
            } else {
                title = getString(R.string.japp_not_sending_location);
                color = Color.rgb(244, 66, 66);
            }
            showLocationNotification(title, color);
        }

        if(shouldSend) {
            if(dif >= Math.round(JappPreferences.getLocationUpdateIntervalInMs())) {
                sendLocation(location);
            }
        }
    }

    @Override
    public void onResult(LocationSettingsResult result) {
        Status status = result.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                wasSending = JappPreferences.isUpdatingLocationToServer();
                if (!wasSending) {
                    showLocationNotification(getString(R.string.japp_sends_location), Color.rgb(244, 66, 66));
                } else {
                    showLocationNotification(getString(R.string.japp_not_sending_location), Color.rgb(113, 244, 66));
                }
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                if(listener != null) {
                    listener.onResolutionRequired(status);
                }
                break;
        }
    }

    public void handleResolutionResult(int code) {
        switch (code) {
            case Activity.RESULT_OK:
                startLocationUpdates();
                boolean wasSending = JappPreferences.isUpdatingLocationToServer();
                if (!wasSending) {
                    showLocationNotification(getString(R.string.japp_not_sending_location), getString(R.string.turn_on_location_in_app), Color.rgb(244, 66, 66));
                } else {
                    showLocationNotification(getString(R.string.japp_sends_location), Color.rgb(113, 244, 66));
                }
                break;
            case Activity.RESULT_CANCELED:
                Intent notificationIntent = new Intent(this, MainActivity.class);
                notificationIntent.setAction(ACTION_REQUEST_LOCATION_SETTING);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent intent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                showLocationNotification(getString(R.string.japp_not_sending_location), getString(R.string.click_to_activate_location_setting), Color.rgb(244, 66, 66), intent);
                break;
        }
    }

    private void sendLocation(Location location) {
        HunterPostBody builder = HunterPostBody.getDefault();
        builder.setLatLng(new LatLng(location.getLatitude(), location.getLongitude()));

        HunterApi api = Japp.getApi(HunterApi.class);
        api.post(builder).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i(TAG, getString(R.string.location_sent));
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, t.toString(), t);
            }
        });
        lastUpdate = Calendar.getInstance();
        wasSending = true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        switch (s) {
            case JappPreferences.UPDATE_LOCATION:
                boolean shouldSend = JappPreferences.isUpdatingLocationToServer();
                String title;
                int color;
                if(shouldSend) {
                    title = getString(R.string.japp_sends_location);
                    color = Color.rgb(113, 244, 66);
                } else {
                    title = getString(R.string.japp_not_sending_location);
                    color = Color.rgb(244, 66, 66);
                }
                showLocationNotification(title, color);

                break;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        JappPreferences.getVisiblePreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    public class LocationBinder extends Binder {
        public LocationService getInstance() {
            return LocationService.this;
        }
    }

    public interface OnResolutionRequiredListener {
        void onResolutionRequired(Status status);
    }


}
