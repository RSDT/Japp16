package nl.rsdt.japp.jotial.data.structures.area348

import android.os.Parcel
import android.os.Parcelable
import android.util.Log

import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.stream.JsonReader
import com.google.maps.android.clustering.ClusterItem

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 20-10-2015
 * Class that servers as deserialization object for the ScoutingGroepInfo.
 * NOTE: This class implements ClusterItem and thereby can be clustered.
 */
class ScoutingGroepInfo
/**
 * Initializes a new instance of ScoutingGroepInfo from the parcel.
 *
 * @param in The parcel where the instance should be created from.
 */
protected constructor(`in`: Parcel) : BaseInfo(`in`), Parcelable, ClusterItem {

    /**
     * The name of the ScoutingGroepInfo.
     */
    var naam: String? = null

    /**
     * The address of the ScoutingGroepInfo.
     */
    var adres: String? = null

    /**
     * The area where the ScoutingGroepInfo is located.
     */
    var team: String? = null

    init {
        naam = `in`.readString()
        adres = `in`.readString()
        team = `in`.readString()
    }
    /**
     * Cluster item implementation.
     */
    override fun getPosition(): LatLng {
        return LatLng(super.latitude, super.longitude)
    }


    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
        dest.writeString(naam)
        dest.writeString(adres)
        dest.writeString(team)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {

        val CREATOR: Parcelable.Creator<ScoutingGroepInfo> = object : Parcelable.Creator<ScoutingGroepInfo> {
            override fun createFromParcel(`in`: Parcel): ScoutingGroepInfo {
                return ScoutingGroepInfo(`in`)
            }

            override fun newArray(size: Int): Array<ScoutingGroepInfo?> {
                return arrayOfNulls(size)
            }
        }

        /**
         * Deserializes a ScoutingGroepInfo from JSON.
         *
         * @param json The JSON where the ScoutingGroepInfo should be deserialized from.
         * @return The deserialized ScoutingGroepInfo.
         */
        fun fromJson(json: String): ScoutingGroepInfo? {
            try {
                val jsonReader = JsonReader(java.io.StringReader(json))
                jsonReader.isLenient = true
                return Gson().fromJson<ScoutingGroepInfo>(jsonReader, ScoutingGroepInfo::class.java)
            } catch (e: JsonParseException) {
                Log.e("ScoutingGroepInfo", e.message, e)
            }

            return null
        }

        /**
         * Deserializes a array of ScoutingGroepInfo from JSON.
         *
         * @param json The JSON where the array of ScoutingGroepInfo should be deserialized from.
         * @return The deserialized array of ScoutingGroepInfo.
         */
        fun fromJsonArray(json: String): Array<ScoutingGroepInfo>? {
            try {
                val jsonReader = JsonReader(java.io.StringReader(json))
                jsonReader.isLenient = true
                return Gson().fromJson<Array<ScoutingGroepInfo>>(jsonReader, Array<ScoutingGroepInfo>::class.java)
            } catch (e: JsonParseException) {
                Log.e("ScoutingGroepInfo", e.message, e)
            }

            return null
        }
    }
}
