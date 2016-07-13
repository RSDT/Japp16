package nl.rsdt.japp.jotial.auth;

import android.content.SharedPreferences;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.rsdt.anl.WebRequest;
import com.rsdt.anl.WebRequestMethod;
import com.rsdt.anl.WebResponse;

import java.io.StringReader;

import nl.rsdt.japp.application.Japp;
import nl.rsdt.japp.application.JappPreferences;
import nl.rsdt.japp.jotial.data.builders.LoginPostDataBuilder;
import nl.rsdt.japp.jotial.net.ApiUrlBuilder;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 13-7-2016
 * Description...
 */
public class Authentication implements WebRequest.OnWebRequestCompletedCallback {

    private OnAuthenticationCompletedCallback callback;

    private String username;

    private String password;

    public void executeAsync()
    {
        WebRequest request = new WebRequest.Builder()
                .setUrl(new ApiUrlBuilder(false).append("login").build())
                .setMethod(WebRequestMethod.POST)
                .setData(new LoginPostDataBuilder()
                        .setUsername(username)
                        .setPassword(password)
                        .build())
                .create();
        request.executeAsync(this);
    }

    @Override
    public void onWebRequestCompleted(WebResponse response) {

        AuthenticationResult result;
        if(response.getResponseCode() == 200)
        {
            /**
             * Extract key from Json.
             * */
            JsonParser parser = new JsonParser();
            JsonReader reader = new JsonReader(new StringReader(response.getData()));
            reader.setLenient(true);
            JsonObject jsonObject = parser.parse(reader).getAsJsonObject();
            String key = jsonObject.get("SLEUTEL").getAsString();

            /**
             * Change the key in the preferences.
             * */
            SharedPreferences.Editor pEditor = Japp.getPreferences().edit();
            pEditor.putString(JappPreferences.ACCOUNT_KEY, key);
            pEditor.apply();

            result = new AuthenticationResult(key, response.getResponseCode(), "Succesvol ingelogd!");
        }
        else
        {
            String message;
            switch (response.getResponseCode())
            {
                case 404:
                    message = "Verkeerde gegevens";
                    break;
                default:
                    message = "Er is een fout opgetreden tijdens het inloggen";
                    break;
            }
            result = new AuthenticationResult(null, response.getResponseCode(), message);
        }

        if(callback != null)
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

}
