package nl.rsdt.japp.jotial.data.bodies

import com.google.gson.annotations.SerializedName

import nl.rsdt.japp.application.JappPreferences

class AutoUpdateTaakPostBody {
    @SerializedName("taak")
    private var taak: String? = null

    @SerializedName("gebruikersID")
    private var id: Int = 0

    @SerializedName("SLEUTEL")
    private var sleutel: String? = null

    fun setTaak(taak: String) {
        this.taak = taak
    }

    fun setId(id: Int) {
        this.id = id
    }

    fun setSleutel(sleutel: String?) {
        this.sleutel = sleutel
    }

    companion object {

        val default: AutoUpdateTaakPostBody
            get() {
                val builder = AutoUpdateTaakPostBody()
                builder.setId(JappPreferences.accountId)
                builder.setSleutel(JappPreferences.accountKey)
                return builder
            }
    }
}
