package nl.rsdt.japp.application;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.multidex.MultiDexApplication;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.MapsInitializer;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;

import nl.rsdt.japp.jotial.io.AppData;
import nl.rsdt.japp.jotial.maps.deelgebied.Deelgebied;
import nl.rsdt.japp.service.cloud.messaging.UpdateManager;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 8-7-2016
 * Description...
 */
public final class Japp extends MultiDexApplication {

    private static Japp instance;

    public static Japp getInstance() { return instance; }

    private FirebaseAnalytics analytics;

    public static FirebaseAnalytics getAnalytics() {
        return instance.analytics;
    }

    private UpdateManager updateManager = new UpdateManager();

    public static UpdateManager getUpdateManager() {
        return instance.updateManager;
    }

    private RequestQueue requestQueue;

    public static RequestQueue getRequestQueue() {
        return instance.requestQueue;
    }

    public static Resources getAppResources() { return instance.getApplicationContext().getResources(); }

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

        requestQueue = Volley.newRequestQueue(this);

        if (!FirebaseApp.getApps(this).isEmpty()) {
            analytics = FirebaseAnalytics.getInstance(this);
        }

        MapsInitializer.initialize(this);

        Deelgebied.initialize(this.getResources());

        AppData.initialize(this.getFilesDir());
    }
}
