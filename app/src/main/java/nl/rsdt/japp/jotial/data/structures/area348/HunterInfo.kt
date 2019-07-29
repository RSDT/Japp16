package nl.rsdt.japp.jotial.data.structures.area348

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader
import nl.rsdt.japp.R

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 20-10-2015
 * Class that servers as deserialization object for the HunterInfo.
 */
class HunterInfo
/**
 * Initializes a new instance of HunterInfo from the parcel.
 *
 * @param in The parcel where the instance should be created from.
 */
protected constructor(`in`: Parcel) : BaseInfo(`in`), Parcelable {

    /**
     * The dateTime the HunterInfo was created.
     */
    var datetime: String? = null

    /**
     * The user of the HunterInfo.
     */
    var hunter: String? = null

    /**
     * The icon of the HunterInfo.
     */
    var icon: Int = 0

    val associatedDrawable: Int
        get() = getAssociatedDrawable(icon)

    init {
        datetime = `in`.readString()
        hunter = `in`.readString()
        icon = `in`.readInt()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
        dest.writeString(datetime)
        dest.writeString(hunter)
        dest.writeInt(icon)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR: Parcelable.Creator<HunterInfo>{

            override fun createFromParcel(`in`: Parcel): HunterInfo {
                return HunterInfo(`in`)
            }

            override fun newArray(size: Int): Array<HunterInfo?> {
                return arrayOfNulls(size)
            }


        fun getAssociatedDrawable(icon: Int): Int {
            when (icon) {
                0 -> return R.drawable.hunter_0
                1 -> return R.drawable.hunter_1
                2 -> return R.drawable.hunter_2
                3 -> return R.drawable.hunter_3
                4 -> return R.drawable.hunter_4
                5 -> return R.drawable.hunter_5
                6 -> return R.drawable.hunter_6
                7 -> return R.drawable.hunter_7
                8 -> return R.drawable.hunter_8
                9 -> return R.drawable.hunter_9
                10 -> return R.drawable.hunter_10
                11 -> return R.drawable.hunter_11
                12 -> return R.drawable.hunter_12
                13 -> return R.drawable.hunter_13
                14 -> return R.drawable.hunter_14
                15 -> return R.drawable.hunter_15
                else -> return R.drawable.hunter_0
            }
        }

        /**
         * Deserializes a HunterInfo from JSON.
         *
         * @param json The JSON where the HunterInfo should be deserialized from.
         * @return A HunterInfo.
         */
        fun fromJson(json: String): HunterInfo? {
            try {
                val jsonReader = JsonReader(java.io.StringReader(json))
                jsonReader.isLenient = true
                return Gson().fromJson<HunterInfo>(jsonReader, HunterInfo::class.java)
            } catch (e: JsonParseException) {
                Log.e("HunterInfo", e.message, e)
            }

            return null
        }

        /**
         * Deserializes a array of HunterInfo from JSON.
         *
         * @param json The JSON where the array of HunterInfo should be deserialized from.
         * @return A array of HunterInfo.
         */
        fun fromJsonArray(json: String): Array<HunterInfo>? {
            try {
                val jsonReader = JsonReader(java.io.StringReader(json))
                jsonReader.isLenient = true
                return Gson().fromJson<Array<HunterInfo>>(jsonReader, Array<HunterInfo>::class.java)
            } catch (e: JsonParseException) {
                Log.e("HunterInfo", e.message, e)
            }

            return null
        }

        /**
         * Deserializes a 2D array of HunterInfo from JSON.
         *
         * @param json The JSON where the 2D array of HunterInfo should be deserialized from.
         * @return A 2D array of HunterInfo.
         */
        fun formJsonArray2D(json: String): Array<Array<HunterInfo>?> {
            try {
                val jsonReader = JsonReader(java.io.StringReader(json))
                jsonReader.isLenient = true
                val parser = JsonParser()
                val `object` = parser.parse(jsonReader) as JsonObject
                val buffer = arrayOfNulls<Array<HunterInfo>>(`object`.entrySet().size)
                var count = 0
                for ((_, value) in `object`.entrySet()) {
                    buffer[count] = fromJsonArray(value.toString())
                    count++
                }
                return buffer
            } catch (e: JsonParseException) {
                Log.e("HunterInfo", e.message, e)
            }

            return emptyArray()
        }
    }
}
