package nl.rsdt.japp.jotial.data.bodies;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import nl.rsdt.japp.application.JappPreferences;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 10-7-2016
 * Description...
 */
public class VosPostBody {

    @SerializedName("SLEUTEL")
    private String sleutel;

    @SerializedName("hunter")
    private String hunter;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("team")
    private String team;

    @SerializedName("info")
    private String info;

    @SerializedName("icon")
    private int icon;

    public VosPostBody setKey(String key)
    {
        this.sleutel = key;
        return this;
    }

    public VosPostBody setName(String name)
    {
        this.hunter = name;
        return this;
    }

    public VosPostBody setLatLng(LatLng latLng)
    {
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
        return this;
    }
    public VosPostBody setTeam(String team)
    {
        this.team = team;
        return this;
    }
    public VosPostBody setInfo(String info)
    {
        this.info = info;
        return this;
    }

    public VosPostBody setIcon(int icon)
    {
        this.icon = icon;
        return this;
    }

    public static VosPostBody getDefault()
    {
        VosPostBody builder = new VosPostBody();
        builder.setName(JappPreferences.getAccountUsername());
        builder.setKey(JappPreferences.getAccountKey());
        return builder;
    }

}
