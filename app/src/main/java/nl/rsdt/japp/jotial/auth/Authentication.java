package nl.rsdt.japp.jotial.auth;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;


import com.google.gson.annotations.SerializedName;

import nl.rsdt.japp.application.Japp;
import nl.rsdt.japp.application.JappPreferences;
import nl.rsdt.japp.application.activities.LoginActivity;
import nl.rsdt.japp.jotial.data.structures.area348.UserInfo;
import nl.rsdt.japp.jotial.net.apis.AuthApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 13-7-2016
 * Description...
 */
public class Authentication implements Callback<Authentication.KeyObject> {

    public static final String REQUEST_TAG = "Authentication";

    private OnAuthenticationCompletedCallback callback;

    private String username;

    private String password;

    public void executeAsync() {
        AuthApi api = Japp.getApi(AuthApi.class);
        api.login(new LoginBody(username, password)).enqueue(this);
    }

    @Override
    public void onResponse(Call<KeyObject> call, retrofit2.Response<KeyObject> response) {
        if(response.code() == 200) {

            String key = response.body().key;

            /**
             * Change the key in the release_preferences.
             * */
            SharedPreferences.Editor pEditor = JappPreferences.getVisiblePreferences().edit();
            pEditor.putString(JappPreferences.ACCOUNT_KEY, key);
            pEditor.apply();

            if(callback != null)
            {
                callback.onAuthenticationCompleted(new AuthenticationResult(key, 200, "Succesvol ingelogd!"));
            }
        } else {
            String message;
            switch (response.code()) {
                case 404:
                    message = "Verkeerde gegevens";
                    break;
                default:
                    message = "Er is een fout opgetreden tijdens het inloggen";
                    break;
            }
            if(callback != null)
            {
                callback.onAuthenticationCompleted(new AuthenticationResult("", response.code(), message));
            }
        }
    }

    @Override
    public void onFailure(Call<KeyObject> call, Throwable t) {
        if(callback != null) {
            callback.onAuthenticationCompleted(new AuthenticationResult("", 0, "Er is een fout opgetreden tijdens het inloggen"));
        }
    }


    public static class AuthenticationResult
    {
        private String key;

        private int code;

        private String message;

        private AuthenticationResult(String key, int code, String message)
        {
            this.key = key;
            this.code = code;
            this.message = message;
        }

        public boolean isSucceeded() {
            return (key != null && !key.isEmpty());
        }

        public String getKey() {
            return key;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }

    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 2-9-2016
     * Description...
     */
    public class LoginBody {

        private String gebruiker;

        private String ww;

        public LoginBody(String user, String text) {
            this.gebruiker = user;
            this.ww = text;
        }
    }

    public class KeyObject {
        @SerializedName("SLEUTEL")
        private String key;
    }

    public class ValidateObject {
        @SerializedName("exists")
        private boolean exists;

        public boolean exists() {
            return exists;
        }
    }

    public static class Builder
    {
        Authentication buffer = new Authentication();

        public Builder setCallback(OnAuthenticationCompletedCallback callback)
        {
            buffer.callback = callback;
            return this;
        }

        public Builder setUsername(String username) {
            buffer.username = username;
            return this;
        }

        public Builder setPassword(String password) {
            buffer.password = AeSimpleSHA1.trySHA1(password);
            return this;
        }

        public Authentication create()
        {
            return buffer;
        }
    }

    public interface OnAuthenticationCompletedCallback
    {
        void onAuthenticationCompleted(AuthenticationResult result);
    }

    /**
     * TODO: don't use final here
     * */
    public static void validate(final Activity activity) {
        AuthApi api = Japp.getApi(AuthApi.class);


    }

    public static void startLoginActivity(Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

}
