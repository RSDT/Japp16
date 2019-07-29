package nl.rsdt.japp.jotial.data.structures.area348

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by mattijn on 30/09/17.
 */

class AutoInzittendeInfo
/**
 * Initializes a new instance of BaseInfo from the parcel.
 *
 * @param in
 */
protected constructor(`in`: Parcel) : Parcelable {

    var id: Int = 0
    var datetime: String? = null
    var gebruikersNaam: String? = null
    var gebruikersID: Int = 0
    var autoEigenaar: String? = null
    var rol: String? = null

    init {
        id = `in`.readInt()
        datetime = `in`.readString()
        gebruikersNaam = `in`.readString()
        gebruikersID = `in`.readInt()
        autoEigenaar = `in`.readString()
        rol = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, fags: Int) {
        dest.writeInt(id)
        dest.writeString(datetime)
        dest.writeString(gebruikersNaam)
        dest.writeInt(gebruikersID)
        dest.writeString(autoEigenaar)
        dest.writeString(rol)
    }

    companion object {
        val CREATOR: Parcelable.Creator<AutoInzittendeInfo> = object : Parcelable.Creator<AutoInzittendeInfo> {
            override fun createFromParcel(`in`: Parcel): AutoInzittendeInfo {
                return AutoInzittendeInfo(`in`)
            }

            override fun newArray(size: Int): Array<AutoInzittendeInfo> {
                return arrayOfNulls(size)
            }
        }
    }
}
