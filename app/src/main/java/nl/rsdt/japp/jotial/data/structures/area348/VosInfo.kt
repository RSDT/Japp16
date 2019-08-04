package nl.rsdt.japp.jotial.data.structures.area348

import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import android.util.Log

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.annotations.SerializedName
import com.google.gson.stream.JsonReader

import nl.rsdt.japp.R
import nl.rsdt.japp.application.JappPreferences
import nl.rsdt.japp.jotial.maps.sighting.SightingIcon

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 20-10-2015
 * Class that servers as deserialization object for the VosInfo.
 */
class VosInfo
/**
 * Initializes a new instance of VosInfo from the parcel.
 *
 * @param in The parcel where the instance should be created from.
 */
protected constructor(`in`: Parcel) : BaseInfo(`in`), Parcelable {

    @SerializedName("datetime")
            /**
             * The dateTime the vosInfo was created.
             */
    val datetime: String?

    @SerializedName("team")
            /**
             * The team of the VosInfo as a char.
             */
    val team: String?

    @SerializedName("team_naam")
            /**
             * The team of the VosInfo as a whole name.
             */
    val teamName: String?

    @SerializedName("opmerking")
            /**
             * A extra of the VosInfo.
             */
    val note: String?

    @SerializedName("extra")
            /**
             * The user of the VosInfo.
             */
    val extra: String?

    @SerializedName("hint_nr")
            /**
             * The hint number of the VosInfo.
             */
    val hintNr: Int

    @SerializedName("icon")
            /**
             * The icon of the VosInfo.
             */
    var icon: Int = 0

    val associatedDrawable: Int
        get() = VosInfo.getAssociatedDrawable(this)

    val associatedColor: Int
        get() = VosInfo.getAssociatedColor(this)

    init {
        datetime = `in`.readString()
        team = `in`.readString()
        teamName = `in`.readString()
        note = `in`.readString()
        extra = `in`.readString()
        hintNr = `in`.readInt()
        icon = `in`.readInt()
    }

    fun getAssociatedColor(alpha: Int): Int {
        return VosInfo.getAssociatedColor(this, alpha)
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
        dest.writeString(datetime)
        dest.writeString(team)
        dest.writeString(teamName)
        dest.writeString(note)
        dest.writeString(extra)
        dest.writeInt(hintNr)
        dest.writeInt(icon)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {

        val CREATOR: Parcelable.Creator<VosInfo> = object : Parcelable.Creator<VosInfo> {
            override fun createFromParcel(`in`: Parcel): VosInfo {
                return VosInfo(`in`)
            }

            override fun newArray(size: Int): Array<VosInfo?> {
                return arrayOfNulls(size)
            }
        }

        /**
         * Deserializes a VosInfo from JSON.
         *
         * @param json The JSON where the VosInfo should be deserialized from.
         * @return The VosInfo deserialized from the JSON.
         */
        fun fromJson(json: String): VosInfo? {
            try {
                val jsonReader = JsonReader(java.io.StringReader(json))
                jsonReader.isLenient = true
                return Gson().fromJson<VosInfo>(jsonReader, VosInfo::class.java)
            } catch (e: JsonParseException) {
                Log.e("VosInfo", e.message, e)
            }

            return null
        }

        /**
         * Deserializes a VosInfo array from JSON.
         *
         * @param json The JSON where the array should be deserialized from.
         * @return The array of VosInfo deserialized from the JSON.
         */
        fun fromJsonArray(json: String): Array<VosInfo>? {
            try {
                val jsonReader = JsonReader(java.io.StringReader(json))
                jsonReader.isLenient = true
                return Gson().fromJson<Array<VosInfo>>(jsonReader, Array<VosInfo>::class.java)
            } catch (e: JsonParseException) {
                Log.e("VosInfo", e.message, e)
            }

            return null
        }

        /**
         * Gets the Drawable associated with the given VosInfo.
         *
         * @param info The VosInfo to get the associated icon from.
         * @return A int pointing to the associated drawable.
         */
        fun getAssociatedDrawable(info: VosInfo): Int {
            val color = MetaColorInfo.ColorNameInfo.DeelgebiedColor.valueOf(JappPreferences.getColorName(info.team?:"x"))
            return when (info.icon) {
                SightingIcon.DEFAULT -> when (color) {
                    MetaColorInfo.ColorNameInfo.DeelgebiedColor.Groen -> R.drawable.vos_groen_2
                    MetaColorInfo.ColorNameInfo.DeelgebiedColor.Rood -> R.drawable.vos_rood_2
                    MetaColorInfo.ColorNameInfo.DeelgebiedColor.Paars -> R.drawable.vos_paars_2
                    MetaColorInfo.ColorNameInfo.DeelgebiedColor.Oranje -> R.drawable.vos_oranje_2
                    MetaColorInfo.ColorNameInfo.DeelgebiedColor.Blauw -> R.drawable.vos_blauw_2
                    MetaColorInfo.ColorNameInfo.DeelgebiedColor.Zwart -> R.drawable.vos_zwart_2
                    MetaColorInfo.ColorNameInfo.DeelgebiedColor.Turquoise -> R.drawable.vos_turquoise_2
                    MetaColorInfo.ColorNameInfo.DeelgebiedColor.Onbekend -> R.drawable.vos_zwart_2
                }
                SightingIcon.HUNT -> when (color) {
                    MetaColorInfo.ColorNameInfo.DeelgebiedColor.Groen -> R.drawable.vos_groen_4
                    MetaColorInfo.ColorNameInfo.DeelgebiedColor.Rood -> R.drawable.vos_rood_4
                    MetaColorInfo.ColorNameInfo.DeelgebiedColor.Paars -> R.drawable.vos_paars_4
                    MetaColorInfo.ColorNameInfo.DeelgebiedColor.Oranje -> R.drawable.vos_oranje_4
                    MetaColorInfo.ColorNameInfo.DeelgebiedColor.Blauw -> R.drawable.vos_blauw_4
                    MetaColorInfo.ColorNameInfo.DeelgebiedColor.Zwart -> R.drawable.vos_zwart_4
                    MetaColorInfo.ColorNameInfo.DeelgebiedColor.Turquoise -> R.drawable.vos_turquoise_4
                    MetaColorInfo.ColorNameInfo.DeelgebiedColor.Onbekend -> R.drawable.vos_zwart_4
                }
                SightingIcon.SPOT -> when (color) {
                    MetaColorInfo.ColorNameInfo.DeelgebiedColor.Groen -> R.drawable.vos_groen_3
                    MetaColorInfo.ColorNameInfo.DeelgebiedColor.Rood -> R.drawable.vos_rood_3
                    MetaColorInfo.ColorNameInfo.DeelgebiedColor.Paars -> R.drawable.vos_paars_3
                    MetaColorInfo.ColorNameInfo.DeelgebiedColor.Oranje -> R.drawable.vos_oranje_3
                    MetaColorInfo.ColorNameInfo.DeelgebiedColor.Blauw -> R.drawable.vos_blauw_3
                    MetaColorInfo.ColorNameInfo.DeelgebiedColor.Zwart -> R.drawable.vos_zwart_3
                    MetaColorInfo.ColorNameInfo.DeelgebiedColor.Turquoise -> R.drawable.vos_turquoise_3
                    MetaColorInfo.ColorNameInfo.DeelgebiedColor.Onbekend -> R.drawable.vos_zwart_3
                }
                SightingIcon.LAST_LOCATION -> when (color) {
                    MetaColorInfo.ColorNameInfo.DeelgebiedColor.Groen -> R.drawable.vos_groen_1
                    MetaColorInfo.ColorNameInfo.DeelgebiedColor.Rood -> R.drawable.vos_rood_1
                    MetaColorInfo.ColorNameInfo.DeelgebiedColor.Paars -> R.drawable.vos_paars_1
                    MetaColorInfo.ColorNameInfo.DeelgebiedColor.Oranje -> R.drawable.vos_oranje_1
                    MetaColorInfo.ColorNameInfo.DeelgebiedColor.Blauw -> R.drawable.vos_blauw_1
                    MetaColorInfo.ColorNameInfo.DeelgebiedColor.Zwart -> R.drawable.vos_zwart_1
                    MetaColorInfo.ColorNameInfo.DeelgebiedColor.Turquoise -> R.drawable.vos_turquoise_1
                    MetaColorInfo.ColorNameInfo.DeelgebiedColor.Onbekend -> R.drawable.vos_zwart_1
                }
                else -> when (color) {
                    MetaColorInfo.ColorNameInfo.DeelgebiedColor.Groen -> R.drawable.vos_groen_2
                    MetaColorInfo.ColorNameInfo.DeelgebiedColor.Rood -> R.drawable.vos_rood_2
                    MetaColorInfo.ColorNameInfo.DeelgebiedColor.Paars -> R.drawable.vos_paars_2
                    MetaColorInfo.ColorNameInfo.DeelgebiedColor.Oranje -> R.drawable.vos_oranje_2
                    MetaColorInfo.ColorNameInfo.DeelgebiedColor.Blauw -> R.drawable.vos_blauw_2
                    MetaColorInfo.ColorNameInfo.DeelgebiedColor.Zwart -> R.drawable.vos_zwart_2
                    MetaColorInfo.ColorNameInfo.DeelgebiedColor.Turquoise -> R.drawable.vos_turquoise_2
                    MetaColorInfo.ColorNameInfo.DeelgebiedColor.Onbekend -> R.drawable.vos_zwart_2
                }
            }
        }


        fun getAssociatedColor(info: VosInfo): Int {
            return getAssociatedColor(info.team)
        }

        fun getAssociatedColor(info: VosInfo, alpha: Int): Int {
            return getAssociatedColor(info.team, alpha)
        }

        @JvmOverloads
        fun getAssociatedColor(team: String?, alpha: Int = 255): Int {
            val color = MetaColorInfo.ColorNameInfo.DeelgebiedColor.valueOf(JappPreferences.getColorName(team?:"x"))
            when (color) {
                MetaColorInfo.ColorNameInfo.DeelgebiedColor.Rood -> return Color.argb(alpha, 255, 0, 0)
                MetaColorInfo.ColorNameInfo.DeelgebiedColor.Groen -> return Color.argb(alpha, 0, 255, 0)
                MetaColorInfo.ColorNameInfo.DeelgebiedColor.Blauw -> return Color.argb(alpha, 0, 0, 255)
                MetaColorInfo.ColorNameInfo.DeelgebiedColor.Turquoise -> return Color.argb(alpha, 0, 255, 255)
                MetaColorInfo.ColorNameInfo.DeelgebiedColor.Paars -> return Color.argb(alpha, 255, 0, 255)
                MetaColorInfo.ColorNameInfo.DeelgebiedColor.Oranje -> return Color.argb(alpha, 255, 162, 0)
                MetaColorInfo.ColorNameInfo.DeelgebiedColor.Zwart -> return Color.argb(alpha, 0, 0, 0)
                MetaColorInfo.ColorNameInfo.DeelgebiedColor.Onbekend -> return Color.WHITE
                else -> return Color.WHITE
            }
        }
    }


}
