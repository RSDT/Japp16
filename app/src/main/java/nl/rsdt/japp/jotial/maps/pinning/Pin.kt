package nl.rsdt.japp.jotial.maps.pinning

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Parcel
import android.os.Parcelable
import android.util.Pair

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson

import nl.rsdt.japp.application.Japp
import nl.rsdt.japp.jotial.maps.management.MarkerIdentifier
import nl.rsdt.japp.jotial.maps.wrapper.IJotiMap
import nl.rsdt.japp.jotial.maps.wrapper.IMarker

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 8-9-2016
 * Description...
 */
class Pin {

    var marker: IMarker? = null

    var data: Data? = null

    class Data : Parcelable {

        var title: String? = null

        var description: String? = null

        var position: LatLng? = null

        var icon: Int = 0

        constructor()

        constructor(title: String, description: String, position: LatLng, icon: Int) {
            this.title = title
            this.description = description
            this.position = position
            this.icon = icon
        }

        protected constructor(`in`: Parcel) {
            title = `in`.readString()
            description = `in`.readString()
            position = `in`.readParcelable(LatLng::class.java.classLoader)
            icon = `in`.readInt()
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeString(title)
            dest.writeString(description)
            dest.writeParcelable(position, flags)
            dest.writeInt(icon)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR: Parcelable.Creator<Data>  {
                override fun createFromParcel(`in`: Parcel): Data {
                    return Data(`in`)
                }

                override fun newArray(size: Int): Array<Data?> {
                    return arrayOfNulls(size)
                }
            }
        }

    companion object {

        fun create(jotiMap: IJotiMap, data: Data): Pin {
            val buffer = Pin()

            val identifier = MarkerIdentifier.Builder()
                    .setType(MarkerIdentifier.TYPE_PIN)
                    .add("title", data.title)
                    .add("description", data.description)
                    .add("icon", data.icon.toString())
                    .create()

            buffer.marker = jotiMap.addMarker(Pair<MarkerOptions, Bitmap?>(MarkerOptions()
                    .title(Gson().toJson(identifier))
                    .position(data.position!!), BitmapFactory.decodeResource(Japp.instance!!.resources, data.icon)))
            buffer.data = data
            return buffer
        }
    }

}
