package nl.rsdt.japp.jotial.data.bodies

import com.google.gson.annotations.SerializedName

import nl.rsdt.japp.application.JappPreferences

class AutoPostBody {

    @SerializedName("SLEUTEL")
    private var SLEUTEL: String? = null

    @SerializedName("gebruikersID")
    private var id: String? = null

    @SerializedName("gebruikersNaam")
    private var gebruikersNaam: String? = null

    @SerializedName("autoEigenaar")
    private var autoEigenaar: String? = null

    @SerializedName("rol")
    private var rol: String? = null

    @SerializedName("taak")
    private var taak: String? = null

    fun setSleutel(sleutel: String?) {
        this.SLEUTEL = sleutel
    }

    fun setId(id: Int) {
        this.id = id.toString()
    }

    fun setGebruikersNaam(gebruikersNaam: String?) {
        this.gebruikersNaam = gebruikersNaam
    }

    fun setAutoEigenaar(autoEigenaar: String?) {
        this.autoEigenaar = autoEigenaar
    }

    fun setRol(rol: String) {
        this.rol = rol
    }

    fun setTaak(taak: String) {
        this.taak = taak
    }

    companion object {

        val default: AutoPostBody
            get() {
                val builder = AutoPostBody()
                builder.setGebruikersNaam(JappPreferences.accountUsername)
                builder.setId(JappPreferences.accountId)
                builder.setRol("rol")
                builder.setTaak("onbekend")
                builder.setAutoEigenaar(JappPreferences.accountUsername)
                builder.setSleutel(JappPreferences.accountKey)
                return builder
            }
    }
}
