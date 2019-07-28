package nl.rsdt.japp.service;

import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import nl.rsdt.japp.R;
import nl.rsdt.japp.application.JappPreferences;
import nl.rsdt.japp.jotial.maps.NavigationLocationManager;

public class NavigationLocationService extends Service {
    Binder binder = new NavigationLocationBinder();


    public NavigationLocationService() {
        NavigationLocationManager locationManager = new NavigationLocationManager();
        locationManager.setCallback(new NavigationLocationManager.OnNewLocation() {
            @Override
            public void onNewLocation(nl.rsdt.japp.jotial.data.firebase.Location location) {
                if (JappPreferences.isNavigationPhone()) {
                    try {
                        String mesg = getString(R.string.location_received, location.createdBy, location.lat, location.lon);
                        showToast(mesg, Toast.LENGTH_SHORT);
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
                            case OSMAndWalk:
                                String osmuriwalk = getString(R.string.osmandwalk_uri, Double.toString(location.lat),Double.toString(location.lon));
                                Intent osmWalkIntent =new Intent(Intent.ACTION_VIEW, Uri.parse(osmuriwalk));
                                osmWalkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(osmWalkIntent);
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
                showToast(mesg, Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return binder;
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

    class NavigationLocationBinder extends Binder {
        public NavigationLocationService getInstance() {
            return NavigationLocationService.this;
        }
    }
}
