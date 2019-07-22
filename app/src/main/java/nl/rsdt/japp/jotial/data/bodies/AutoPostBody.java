package nl.rsdt.japp.jotial.data.bodies;

import com.google.gson.annotations.SerializedName;

import nl.rsdt.japp.application.JappPreferences;

public class AutoPostBody {

    @SerializedName("SLEUTEL")
    private String SLEUTEL;

    @SerializedName("gebruikersID")
    private String id;

    @SerializedName("gebruikersNaam")
    private String gebruikersNaam;

    @SerializedName("autoEigenaar")
    private String autoEigenaar;

    @SerializedName("rol")
    private String rol;

    @SerializedName("taak")
    private String taak;

    public void setSleutel(String sleutel) {
        this.SLEUTEL = sleutel;
    }

    public void setId(int id) {
        this.id = String.valueOf(id);
    }

    public void setGebruikersNaam(String gebruikersNaam) {
        this.gebruikersNaam = gebruikersNaam;
    }

    public void setAutoEigenaar(String autoEigenaar) {
        this.autoEigenaar = autoEigenaar;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public void setTaak(String taak) {
        this.taak = taak;
    }

    public static AutoPostBody getDefault()
    {
        AutoPostBody builder = new AutoPostBody();
        builder.setGebruikersNaam(JappPreferences.getAccountUsername());
        builder.setId(JappPreferences.getAccountId());
        builder.setRol("rol");
        builder.setTaak("taak");
        builder.setAutoEigenaar(JappPreferences.getAccountUsername());
        builder.setSleutel(JappPreferences.getAccountKey());
        return builder;
    }
}
