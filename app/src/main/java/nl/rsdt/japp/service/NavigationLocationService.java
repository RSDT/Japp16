package nl.rsdt.japp.service;

import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.widget.Toast;

import nl.rsdt.japp.application.JappPreferences;
import nl.rsdt.japp.jotial.maps.NavigationLocationManager;

public class NavigationLocationService extends Service {
    private final NavigationLocationManager locationManager;
    Binder binder = new NavigationLocationBinder();


    public NavigationLocationService() {
        locationManager = new NavigationLocationManager();
        locationManager.setCallback(new NavigationLocationManager.OnNewLocation() {
            @Override
            public void onNewLocation(nl.rsdt.japp.jotial.data.firebase.Location location) {
                if (JappPreferences.isNavigationPhone()) {
                    try {
                        String mesg = "Japp: locatie ontvangen, navigeren naar: " +location.lat+ ", " +location.lon;
                        showToast(mesg, Toast.LENGTH_SHORT);
                        switch (JappPreferences.navigationApp()) {
                            case GoogleMaps:
                                String uristr = "google.navigation:q=" + Double.toString(location.lat) + "," + Double.toString(location.lon);
                                Uri gmmIntentUri = Uri.parse(uristr);
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mapIntent.setPackage("com.google.android.apps.maps");
                                startActivity(mapIntent);
                                break;
                            case Waze:
                                String uri = "waze://?ll=" + Double.toString(location.lat) + "," + Double.toString(location.lon) + "&navigate=yes";
                                Intent wazeIntent =new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                wazeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(wazeIntent);
                                break;
                        }
                    } catch (ActivityNotFoundException e) {
                        System.out.println(e.toString());
                        String mesg = "Japp: De App: " + JappPreferences.navigationApp().toString() + " is niet geinstaleerd.";
                        showToast(mesg, Toast.LENGTH_SHORT);
                    }
                }
            }

            @Override
            public void onNotInCar() {
                String mesg = "Japp: Fout: Zet jezelf eerst in een auto via menu->auto.";
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
