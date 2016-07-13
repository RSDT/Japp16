package nl.rsdt.japp.application;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import nl.rsdt.japp.jotial.io.AppData;
import nl.rsdt.japp.jotial.maps.deelgebied.Deelgebied;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 8-7-2016
 * Description...
 */
public class Japp extends Application {

    private static Japp instance;

    public static Japp getInstance() { return instance; }

    public static SharedPreferences getPreferences() { return PreferenceManager.getDefaultSharedPreferences(instance); }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        Deelgebied.initialize(this.getResources());

        AppData.initialize(this.getFilesDir());
    }
}
