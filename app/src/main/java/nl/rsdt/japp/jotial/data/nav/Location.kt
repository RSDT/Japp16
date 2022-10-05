package nl.rsdt.japp.jotial.data.nav
import com.google.android.gms.maps.model.LatLng

/**
 * Created by mattijn on 30/09/17.
 */

class Location(navigateTo: LatLng, auto: String ,username: String) {
    private var auto: String
    var username: String
    var createdOn: Long
    var latitude: Double = 0.toDouble()
    var longitude: Double = 0.toDouble()
    val lat: Double
        get() = latitude
    val lon: Double
        get() = longitude
    init {
        this.latitude = navigateTo.latitude
        this.longitude = navigateTo.longitude
        this.username = username
        this.auto = auto
        createdOn = System.currentTimeMillis()
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Location) {
            other.longitude == this.longitude && other.latitude == this.latitude
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = auto.hashCode()
        result = 31 * result + latitude.hashCode()
        result = 31 * result + longitude.hashCode()
        return result
    }


}
