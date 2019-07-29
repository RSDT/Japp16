package nl.rsdt.japp.jotial.data.structures.area348

import android.os.Parcel
import android.os.Parcelable

import com.google.android.gms.maps.model.LatLng

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 20-10-2015
 * Class that servers as a deserialization object for the most abstract Info.
 */
open class BaseInfo
/**
 * Initializes a new instance of BaseInfo from the parcel.
 */
protected constructor(`in`: Parcel) : Parcelable {

    /**
     * The id of the Info.
     */
    var id: Int = 0

    /**
     * The latitude of the Info.
     */
    var latitude: Double = 0.toDouble()

    /**
     * The longitude of the Info.
     */
    var longitude: Double = 0.toDouble()

    val latLng: LatLng
        get() = LatLng(latitude, longitude)

    init {
        id = `in`.readInt()
        latitude = `in`.readDouble()
        longitude = `in`.readDouble()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
        dest.writeDouble(latitude)
        dest.writeDouble(longitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR: Parcelable.Creator<BaseInfo>{
        override fun createFromParcel(`in`: Parcel): BaseInfo {
            return BaseInfo(`in`)
        }

        override fun newArray(size: Int): Array<BaseInfo?> {
            return arrayOfNulls(size)
        }
    }
}
