package nl.rsdt.japp.jotial.data.nav
import com.google.android.gms.maps.model.LatLng

/**
 * Created by mattijn on 30/09/17.
 */

class Location(navigateTo: LatLng, auto: String ,username: String) {
    private var auto: String
    var username: String
    var createdOn: Long
    var lat: Double = 0.toDouble()
    var lon: Double = 0.toDouble()

    init {
        this.lat = navigateTo.latitude
        this.lon = navigateTo.longitude
        this.username = username
        this.auto = auto
        createdOn = System.currentTimeMillis()
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Location) {
            other.lon == this.lon && other.lat == this.lat
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = auto.hashCode()
        result = 31 * result + lat.hashCode()
        result = 31 * result + lon.hashCode()
        return result
    }


}
