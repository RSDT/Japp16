package nl.rsdt.japp.jotial.data.bodies;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 13-7-2016
 * Description...
 */
public class LoginPostDataBuilder {

    private String user;

    private String password;

    public LoginPostDataBuilder setUsername(String username) {
        this.user = username;
        return this;
    }

    public LoginPostDataBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public String build()
    {
        JsonObject object = new JsonObject();
        object.addProperty("gebruiker", user);
        object.addProperty("ww", password);
        return object.toString();
    }

    public JSONObject buildAsParams() {
        HashMap<String, String> map =  new HashMap<>();
        map.put("gebruiker", user);
        map.put("ww", password);
        return new JSONObject(map);
    }

}
