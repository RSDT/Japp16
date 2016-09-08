package nl.rsdt.japp.application;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.multidex.MultiDexApplication;

import com.google.android.gms.maps.MapsInitializer;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;


import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import nl.rsdt.japp.jotial.io.AppData;
import nl.rsdt.japp.jotial.maps.deelgebied.Deelgebied;
import nl.rsdt.japp.jotial.net.API;
import nl.rsdt.japp.service.cloud.messaging.UpdateManager;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.CallAdapter;
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

    public static Japp getInstance() { return instance; }

    private FirebaseAnalytics analytics;

    public static FirebaseAnalytics getAnalytics() {
        return instance.analytics;
    }

    private UpdateManager updateManager = new UpdateManager();

    public static UpdateManager getUpdateManager() {
        return instance.updateManager;
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
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build();
        return (T)retrofit.create(api);
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

        if (!FirebaseApp.getApps(this).isEmpty()) {
            analytics = FirebaseAnalytics.getInstance(this);
        }

        MapsInitializer.initialize(this);

        Deelgebied.initialize(this.getResources());

        AppData.initialize(this.getFilesDir());
        File dir = this.getFilesDir();
        String file = dir.getAbsolutePath();
        dir.exists();
    }
}
