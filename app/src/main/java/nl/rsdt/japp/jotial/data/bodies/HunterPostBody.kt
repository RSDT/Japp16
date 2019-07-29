package nl.rsdt.japp.jotial.data.bodies

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName

import nl.rsdt.japp.application.JappPreferences

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 14-8-2016
 * Description...
 */
class HunterPostBody {

    @SerializedName("SLEUTEL")
    private var sleutel: String? = null

    @SerializedName("hunter")
    private var hunter: String? = null

    @SerializedName("latitude")
    private var latitude: String? = null

    @SerializedName("longitude")
    private var longitude: String? = null

    @SerializedName("icon")
    private var icon: String? = null

    fun setKey(key: String?): HunterPostBody {
        this.sleutel = key
        return this
    }

    fun setName(name: String?): HunterPostBody {
        this.hunter = name
        return this
    }

    fun setLatLng(latLng: LatLng): HunterPostBody {
        this.latitude = java.lang.Double.toString(latLng.latitude)
        this.longitude = java.lang.Double.toString(latLng.longitude)
        return this
    }

    fun setIcon(icon: Int): HunterPostBody {
        this.icon = Integer.toString(icon)
        return this
    }

    companion object {

        val default: HunterPostBody
            get() {
                val builder = HunterPostBody()

                val huntname = JappPreferences.huntname
                if (!huntname!!.isEmpty()) {
                    builder.setName(huntname)
                } else {
                    builder.setName(JappPreferences.accountUsername)
                }

                builder.setKey(JappPreferences.accountKey)
                builder.setIcon(JappPreferences.accountIcon)
                return builder
            }
    }


}
