package nl.rsdt.japp.jotial.data.bodies;

import com.google.gson.annotations.SerializedName;

import nl.rsdt.japp.application.JappPreferences;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 7-9-2016
 * Description...
 */
public class FcmPostBody {

    public FcmPostBody(String key, String user, String token) {
        this.key = key;
        this.user = user;
        this.token = token;
    }

    @SerializedName("SLEUTEL")
    private String key;

    @SerializedName("gebruiker")
    private String user;

    @SerializedName("token")
    private String token;

    public String getKey() {
        return key;
    }

    public String getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }

    public FcmPostBody setKey(String key) {
        this.key = key;
        return this;
    }

    public FcmPostBody setUser(String user) {
        this.user = user;
        return this;
    }

    public FcmPostBody setToken(String token) {
        this.token = token;
        return this;
    }

    public static FcmPostBody getDefault() {
        return new FcmPostBody(JappPreferences.getAccountKey(), JappPreferences.getAccountUsername(), "");
    }

}
