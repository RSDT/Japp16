package nl.rsdt.japp.application;

import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.multidex.MultiDexApplication;

import com.google.android.gms.maps.MapsInitializer;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.GsonBuilder;


import nl.rsdt.japp.jotial.io.AppData;
import nl.rsdt.japp.jotial.maps.deelgebied.Deelgebied;
import nl.rsdt.japp.jotial.net.API;
import nl.rsdt.japp.service.cloud.messaging.MessageManager;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 8-7-2016
 * Description...
 */
public final class Japp extends MultiDexApplication {

    private static Japp instance;
    private static Location lastLocation = null;

    public static Japp getInstance() { return instance; }

    private FirebaseAnalytics analytics;

    public static FirebaseAnalytics getAnalytics() {
        return instance.analytics;
    }

    private MessageManager messageManager = new MessageManager();

    public static MessageManager getUpdateManager() {
        return instance.messageManager;
    }

    private Interceptor interceptor;

    public static Interceptor getInterceptor() { return instance.interceptor; }

    public static void setInterceptor(Interceptor value) { instance.interceptor = value; }

    public static <T> T getApi(Class api) {
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        if(instance.interceptor != null) {
            client.addInterceptor(instance.interceptor);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API.API_V2_ROOT)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .client(client.build())
                .build();

        return (T) retrofit.create(api);
    }

    public static Resources getAppResources() { return instance.getApplicationContext().getResources(); }

    public static boolean hasInternetConnection()
    {
        ConnectivityManager cm = (ConnectivityManager)instance.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

    public static void setLastLocation(Location lastLocation) {
        Japp.lastLocation = lastLocation;
    }

    public static Location getLastLocation() {
        return lastLocation;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        if (!FirebaseApp.getApps(this).isEmpty()) {
            analytics = FirebaseAnalytics.getInstance(this);
        }

        MapsInitializer.initialize(this);

        AppData.initialize(this.getFilesDir());
    }
}
