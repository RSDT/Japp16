package nl.rsdt.japp.service.cloud.messaging;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import nl.rsdt.japp.application.Japp;
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
public class JappFirebaseInstanceIdService extends FirebaseInstanceIdService {

    public static final String TAG = "InstanceIdService";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        FcmApi api = Japp.getApi(FcmApi.class);
        api.postToken(FcmPostBody.getDefault().setToken(refreshedToken)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i(TAG, "Refreshed token to the server: " + call.request().body());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, t.toString(), t);
            }
        });
    }

}
