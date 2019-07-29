package nl.rsdt.japp.jotial.data.structures.area348

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable

class AutoEigenaarInfo protected constructor(`in`: Parcel) : Parcelable {
    var autoEigenaar: String? = null

    init {
        autoEigenaar = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, fags: Int) {
        dest.writeString(autoEigenaar)
    }

    companion object {
        @SuppressLint("ParcelCreator")
        val CREATOR: Parcelable.Creator<AutoEigenaarInfo> = object : Parcelable.Creator<AutoEigenaarInfo> {
            override fun createFromParcel(`in`: Parcel): AutoEigenaarInfo {
                return AutoEigenaarInfo(`in`)
            }

            override fun newArray(size: Int): Array<AutoEigenaarInfo?> {
                return arrayOfNulls(size)
            }
        }
    }
}
