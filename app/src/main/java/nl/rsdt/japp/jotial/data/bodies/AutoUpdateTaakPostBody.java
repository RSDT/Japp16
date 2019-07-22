package nl.rsdt.japp.jotial.data.bodies;

import com.google.gson.annotations.SerializedName;

import nl.rsdt.japp.application.JappPreferences;

public class AutoUpdateTaakPostBody {
    @SerializedName("taak")
    private String taak;

    @SerializedName("gebruikersID")
    private int id;

    @SerializedName("SLEUTEL")
    private String sleutel;

    public void setTaak(String taak) {
        this.taak = taak;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSleutel(String sleutel) {
        this.sleutel = sleutel;
    }

    public static AutoUpdateTaakPostBody getDefault()
    {
        AutoUpdateTaakPostBody builder = new AutoUpdateTaakPostBody();
        builder.setId(JappPreferences.getAccountId());
        builder.setSleutel(JappPreferences.getAccountKey());
        return builder;
    }
}
