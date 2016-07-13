package nl.rsdt.japp.jotial.data.builders;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;

import nl.rsdt.japp.application.JappPreferences;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 10-7-2016
 * Description...
 */
public class VosPostDataBuilder {

    private String sleutel;

    private String hunter;

    private LatLng latLng;

    private String team;

    private String info;

    private int icon;

    public VosPostDataBuilder setKey(String key)
    {
        this.sleutel = key;
        return this;
    }

    public VosPostDataBuilder setName(String name)
    {
        this.hunter = name;
        return this;
    }

    public VosPostDataBuilder setLatLng(LatLng latLng)
    {
        this.latLng = latLng;
        return this;
    }
    public VosPostDataBuilder setTeam(String team)
    {
        this.team = team;
        return this;
    }
    public VosPostDataBuilder setInfo(String info)
    {
        this.info = info;
        return this;
    }

    public VosPostDataBuilder setIcon(int icon)
    {
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
        object.addProperty("team", team);
        object.addProperty("info", info);
        object.addProperty("icon", icon);

        return object.toString();
    }

    public static VosPostDataBuilder getDefault()
    {
        VosPostDataBuilder builder = new VosPostDataBuilder();
        builder.setName(JappPreferences.getAccountUsername());
        builder.setKey(JappPreferences.getAccountKey());
        return builder;
    }


}
