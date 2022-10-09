package nl.rsdt.japp.jotial.data.nav
import com.google.android.gms.maps.model.LatLng

/**
 * Created by mattijn on 30/09/17.
 */

class Location(navigateTo: LatLng, auto: String ,username: String) {
    val auto: String
    val username: String
    val createdOn: Long
    val latitude: Double
    val longitude: Double
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
