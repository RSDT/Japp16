package nl.rsdt.japp.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.util.ArrayList;

import nl.rsdt.japp.application.activities.MainActivity;
import nl.rsdt.japp.jotial.maps.locations.LocationProviderService;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 14-8-2016
 * Description...
 */
public class LocationServiceManager implements ServiceConnection {

    protected ArrayList<OnLocationBinderGivenCallback> callbacks = new ArrayList<>();

    protected boolean isBound = false;

    public void add(OnLocationBinderGivenCallback callback) {
        this.callbacks.add(callback);
    }

    public void remove(OnLocationBinderGivenCallback callback) {
        this.callbacks.remove(callback);
    }

    public void bind(MainActivity mainActivity) {

        /**
         * Start the LocationService.
         * */
        Intent intent = new Intent(mainActivity, LocationService.class);
        mainActivity.startService(intent);
        mainActivity.bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        isBound = true;
        OnLocationBinderGivenCallback callback;
        for(int i = 0; i < callbacks.size(); i++) {
            callback = callbacks.get(i);
            if(callback != null) {
                callback.onBinderGiven((LocationProviderService.LocationBinder) iBinder);
            }
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        isBound = false;
    }

    public void unbind(MainActivity mainActivity) {
        mainActivity.unbindService(this);
    }

    public interface OnLocationBinderGivenCallback {
        void onBinderGiven(LocationProviderService.LocationBinder binder);
    }
}
