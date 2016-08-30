package nl.rsdt.japp.jotial.auth;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.rsdt.anl.WebRequest;
import com.rsdt.anl.WebRequestMethod;
import com.rsdt.anl.WebResponse;

import org.json.JSONObject;

import java.io.StringReader;
import java.util.HashMap;

import nl.rsdt.japp.application.Japp;
import nl.rsdt.japp.application.JappPreferences;
import nl.rsdt.japp.application.activities.LoginActivity;
import nl.rsdt.japp.jotial.data.builders.LoginPostDataBuilder;
import nl.rsdt.japp.jotial.net.ApiPostRequest;
import nl.rsdt.japp.jotial.net.ApiUrlBuilder;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 13-7-2016
 * Description...
 */
public class Authentication implements Response.Listener<JSONObject>, Response.ErrorListener {

    public static final String REQUEST_TAG = "Authentication";

    private OnAuthenticationCompletedCallback callback;

    private String username;

    private String password;

    public void executeAsync() {
        ApiPostRequest request = new ApiPostRequest(new ApiUrlBuilder(false).append("login").buildAsString(),
                this, this, new LoginPostDataBuilder().setUsername(username).setPassword(password).buildAsParams());
        request.setTag(REQUEST_TAG);
        Japp.getRequestQueue().add(request);
    }

    @Override
    public void onResponse(JSONObject response) {

        /**
         * Extract key from Json.
         * */
        JsonParser parser = new JsonParser();
        JsonReader reader = new JsonReader(new StringReader(response.toString()));
        reader.setLenient(true);
        JsonObject jsonObject = parser.parse(reader).getAsJsonObject();
        String key = jsonObject.get("SLEUTEL").getAsString();

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
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        AuthenticationResult result = null;
        if(error.networkResponse != null) {
            String message;
            switch (error.networkResponse.statusCode)
            {
                case 404:
                    message = "Verkeerde gegevens";
                    break;
                default:
                    message = "Er is een fout opgetreden tijdens het inloggen";
                    break;
            }
            result = new AuthenticationResult(null, error.networkResponse.statusCode, message);
        } else {
            result = new AuthenticationResult(null, 0, "Er is een fout opgetreden tijdens het inloggen");
        }
        error.printStackTrace();
        if(callback != null && result != null)
        {
            callback.onAuthenticationCompleted(result);
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

    public static void startLoginActivity(Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

}
