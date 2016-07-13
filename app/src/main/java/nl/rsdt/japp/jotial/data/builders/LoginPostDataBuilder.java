package nl.rsdt.japp.jotial.data.builders;

import com.google.gson.JsonObject;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 13-7-2016
 * Description...
 */
public class LoginPostDataBuilder {

    private String gebruiker;

    private String ww;

    public LoginPostDataBuilder setUsername(String username) {
        this.gebruiker = username;
        return this;
    }

    public LoginPostDataBuilder setPassword(String password) {
        this.ww = password;
        return this;
    }

    public String build()
    {
        JsonObject object = new JsonObject();
        object.addProperty("gebruiker", gebruiker);
        object.addProperty("ww", ww);
        return object.toString();
    }
}
