package nl.rsdt.japp.jotial.data.bodies;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.HashMap;

import nl.rsdt.japp.application.JappPreferences;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 14-8-2016
 * Description...
 */
public class HunterPostBody {

    @SerializedName("SLEUTEL")
    private String sleutel;

    @SerializedName("hunter")
    private String hunter;

    @SerializedName("latitude")
    private String latitude;

    @SerializedName("longitude")
    private String longitude;

    @SerializedName("icon")
    private String icon;

    public HunterPostBody setKey(String key) {
        this.sleutel = key;
        return this;
    }

    public HunterPostBody setName(String name)
    {
        this.hunter = name;
        return this;
    }

    public HunterPostBody setLatLng(LatLng latLng)
    {
        this.latitude = Double.toString(latLng.latitude);
        this.longitude = Double.toString(latLng.longitude);
        return this;
    }

    public HunterPostBody setIcon(int icon) {
        this.icon = Integer.toString(icon);
        return this;
    }

    public static HunterPostBody getDefault() {
        HunterPostBody builder = new HunterPostBody();

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
