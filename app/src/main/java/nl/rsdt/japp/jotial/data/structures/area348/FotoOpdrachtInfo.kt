package nl.rsdt.japp.jotial.data.structures.area348

import android.os.Parcel
import android.os.Parcelable
import android.util.Log

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.stream.JsonReader

import nl.rsdt.japp.R

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 20-10-2015
 * Class that servers as deserialization object for the FotoOpdrachtInfo.
 */
class FotoOpdrachtInfo protected constructor(`in`: Parcel) : BaseInfo(`in`), Parcelable {

    /**
     * The name of the FotoOpdrachtInfo.
     */
    var foto_nr: Int = 0

    /**
     * The info of the FotoOpdrachtInfo.
     */
    var info: String? = null

    /**
     * The extra of the FotoOpdrachtInfo.
     */
    var extra: String? = null

    /**
     * The value indicating if FotoOpdrachtInfo is completed or not.
     */
    var klaar: Int = 0

    val associatedDrawable: Int
        get() = getAssociatedDrawable(klaar)

    init {
        foto_nr = `in`.readInt()
        info = `in`.readString()
        extra = `in`.readString()
        klaar = `in`.readInt()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
        dest.writeInt(foto_nr)
        dest.writeString(info)
        dest.writeString(extra)
        dest.writeInt(klaar)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR: Parcelable.Creator<FotoOpdrachtInfo>{

            override fun createFromParcel(`in`: Parcel): FotoOpdrachtInfo {
                return FotoOpdrachtInfo(`in`)
            }

            override fun newArray(size: Int): Array<FotoOpdrachtInfo?> {
                return arrayOfNulls(size)
            }


        /**
         * Deserializes a FotoOpdrachtInfo from the JSON.
         *
         * @param json The JSON where the FotoOpdrachtInfo should be deserialized from.
         * @return A FotoOpdrachtInfo.
         */
        fun fromJson(json: String): FotoOpdrachtInfo? {
            try {
                val jsonReader = JsonReader(java.io.StringReader(json))
                jsonReader.isLenient = true
                return Gson().fromJson<FotoOpdrachtInfo>(jsonReader, FotoOpdrachtInfo::class.java)
            } catch (e: JsonParseException) {
                Log.e("FotoOpdrachtInfo", e.message, e)
            }

            return null
        }

        /**
         * Deserializes a array of FotoOpdrachtInfo from the JSON.
         *
         * @param json The JSON where the array of FotoOpdrachtInfo should be deserialized from.
         * @return A array of FotoOpdrachtInfo.
         */
        fun fromJsonArray(json: String): Array<FotoOpdrachtInfo>? {
            try {
                val jsonReader = JsonReader(java.io.StringReader(json))
                jsonReader.isLenient = true
                return Gson().fromJson<Array<FotoOpdrachtInfo>>(jsonReader, Array<FotoOpdrachtInfo>::class.java)
            } catch (e: JsonParseException) {
                Log.e("FotoOpdrachtInfo", e.message, e)
            }

            return null
        }

        fun getAssociatedDrawable(klaar: Int): Int {
            return if (klaar == 1) {
                R.drawable.camera_20x20_klaar
            } else R.drawable.camera_20x20
        }
    }

}
