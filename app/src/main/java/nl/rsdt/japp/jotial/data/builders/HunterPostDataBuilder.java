package nl.rsdt.japp.jotial.data.builders;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import nl.rsdt.japp.application.JappPreferences;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 14-8-2016
 * Description...
 */
public class HunterPostDataBuilder {

    private String sleutel;

    private String hunter;

    private LatLng latLng;

    private int icon;

    public HunterPostDataBuilder setKey(String key) {
        this.sleutel = key;
        return this;
    }

    public HunterPostDataBuilder setName(String name)
    {
        this.hunter = name;
        return this;
    }

    public HunterPostDataBuilder setLatLng(LatLng latLng)
    {
        this.latLng = latLng;
        return this;
    }

    public HunterPostDataBuilder setIcon(int icon) {
        this.icon = icon;
        return this;
    }

    public String buildAsJson()
    {
        JsonObject object = new JsonObject();
        object.addProperty("SLEUTEL", sleutel);
        object.addProperty("hunter", hunter);
        object.addProperty("latitude", Double.toString(latLng.latitude));
        object.addProperty("longitude", Double.toString(latLng.longitude));
        object.addProperty("icon", Integer.toString(icon));
        return object.toString();
    }

    public JSONObject buildAsJSONObject() {
        HashMap<String, String> map = new HashMap<>();
        map.put("SLEUTEL", sleutel);
        map.put("hunter", hunter);
        map.put("latitude", Double.toString(latLng.latitude));
        map.put("longitude", Double.toString(latLng.longitude));
        map.put("icon", Integer.toString(icon));
        return new JSONObject(map);
    }

    public static HunterPostDataBuilder getDefault() {
        HunterPostDataBuilder builder = new HunterPostDataBuilder();

        String huntname = JappPreferences.getHuntname();
        if(!huntname.isEmpty()) {
            builder.setName(huntname);
        } else {
            builder.setName(JappPreferences.getAccountUsername());
        }

        builder.setKey(JappPreferences.getAccountKey());
        builder.setIcon(JappPreferences.getAccountIcon());
        return builder;
    }




}
