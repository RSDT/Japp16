package nl.rsdt.japp.jotial.data.structures.area348

import android.os.Parcel
import android.os.Parcelable

class DeletedInfo protected constructor(`in`: Parcel) : Parcelable {
    var verwijderd: Boolean = false

    init {
        verwijderd = true
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, fags: Int) {

    }

    companion object {
        val CREATOR: Parcelable.Creator<DeletedInfo> = object : Parcelable.Creator<DeletedInfo> {
            override fun createFromParcel(`in`: Parcel): DeletedInfo {
                return DeletedInfo(`in`)
            }

            override fun newArray(size: Int): Array<DeletedInfo> {
                return arrayOfNulls(size)
            }
        }
    }
}
