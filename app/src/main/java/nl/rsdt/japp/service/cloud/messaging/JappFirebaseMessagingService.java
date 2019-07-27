package nl.rsdt.japp.service.cloud.messaging;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import nl.rsdt.japp.application.Japp;
import nl.rsdt.japp.application.JappPreferences;
import nl.rsdt.japp.jotial.data.bodies.FcmPostBody;
import nl.rsdt.japp.jotial.net.apis.FcmApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 4-8-2016
 * Description...
 */
public class JappFirebaseMessagingService extends FirebaseMessagingService {
    public static final String TAG = "MessagingService";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Japp.getUpdateManager().onMessage(remoteMessage);
    }
    @Override
    public void onNewToken(String token) {
        JappPreferences.setFcmToken(token);

        if(!JappPreferences.isFirstRun()) {
            sendToken(token);
        }
    }

    public static void sendToken() {
        sendToken(JappPreferences.getFcmToken());
    }

    public static void sendToken(String token) {
        FcmApi api = Japp.getApi(FcmApi.class);
        api.postToken(FcmPostBody.getDefault().setToken(token)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i(TAG, "Refreshed token to the server");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, t.toString(), t);
            }
        });
    }
}
