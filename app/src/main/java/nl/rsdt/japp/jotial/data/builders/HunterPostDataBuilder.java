package nl.rsdt.japp.jotial.data.builders;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;

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

    public String build()
    {
        JsonObject object = new JsonObject();
        object.addProperty("SLEUTEL", sleutel);
        object.addProperty("hunter", hunter);
        object.addProperty("latitude", latLng.latitude);
        object.addProperty("longitude", latLng.longitude);
        object.addProperty("icon", icon);

        return object.toString();
    }

    public static HunterPostDataBuilder getDefault() {
        HunterPostDataBuilder builder = new HunterPostDataBuilder();
        builder.setName(JappPreferences.getAccountUsername());
        builder.setKey(JappPreferences.getAccountKey());
        builder.setIcon(JappPreferences.getAccountIcon());
        return builder;
    }




}
