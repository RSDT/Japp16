package nl.rsdt.japp.jotial.data.structures.area348

import android.os.Parcel
import android.os.Parcelable

class MetaColorInfo protected constructor(`in`: Parcel) : BaseInfo(`in`), Parcelable {
    /**
     * Initializes a new instance of BaseInfo from the parcel.
     *
     * @param in
     */
    var ColorName: ColorNameInfo? = null
    var ColorCode: ColorHexInfo? = null

    inner class ColorHexInfo {
        var a: String? = null
        var b: String? = null
        var c: String? = null
        var d: String? = null
        var e: String? = null
        var f: String? = null
        var x: String? = null

    }

    class ColorNameInfo {
        var a: String? = null
        var b: String? = null
        var c: String? = null
        var d: String? = null
        var e: String? = null
        var f: String? = null
        var x: String? = null

        enum class DeelgebiedColor {
            Groen,
            Rood,
            Paars,
            Oranje,
            Blauw,
            Zwart,
            Turquoise,
            Onbekend

        }
    }
}
