package nl.rsdt.japp.jotial.data.bodies

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName

import nl.rsdt.japp.application.JappPreferences

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 10-7-2016
 * Description...
 */
class VosPostBody {

    @SerializedName("SLEUTEL")
    private var sleutel: String? = null

    @SerializedName("hunter")
    private var hunter: String? = null

    @SerializedName("latitude")
    private var latitude: Double = 0.toDouble()

    @SerializedName("longitude")
    private var longitude: Double = 0.toDouble()

    @SerializedName("team")
    private var team: String? = null

    @SerializedName("info")
    private var info: String? = null

    @SerializedName("icon")
    private var icon: Int = 0

    fun setKey(key: String?): VosPostBody {
        this.sleutel = key
        return this
    }

    fun setName(name: String?): VosPostBody {
        this.hunter = name
        return this
    }

    fun setLatLng(latLng: LatLng): VosPostBody {
        this.latitude = latLng.latitude
        this.longitude = latLng.longitude
        return this
    }

    fun setTeam(team: String): VosPostBody {
        this.team = team
        return this
    }

    fun setInfo(info: String?): VosPostBody {
        this.info = info
        return this
    }

    fun setIcon(icon: Int): VosPostBody {
        this.icon = icon
        return this
    }

    companion object {

        val default: VosPostBody
            get() {
                val builder = VosPostBody()
                builder.setName(JappPreferences.accountUsername)
                builder.setKey(JappPreferences.accountKey)
                return builder
            }
    }

}
