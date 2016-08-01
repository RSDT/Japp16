package nl.rsdt.japp.application;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.multidex.MultiDexApplication;

import com.google.android.gms.maps.MapsInitializer;

import nl.rsdt.japp.jotial.io.AppData;
import nl.rsdt.japp.jotial.maps.deelgebied.Deelgebied;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 8-7-2016
 * Description...
 */
public class Japp extends MultiDexApplication {

    private static Japp instance;

    public static Japp getInstance() { return instance; }

    public static boolean hasInternetConnection()
    {
        ConnectivityManager cm = (ConnectivityManager)instance.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        MapsInitializer.initialize(this);

        Deelgebied.initialize(this.getResources());

        AppData.initialize(this.getFilesDir());
    }
}
