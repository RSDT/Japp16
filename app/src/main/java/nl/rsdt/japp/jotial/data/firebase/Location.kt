package nl.rsdt.japp.jotial.data.firebase

import com.google.android.gms.maps.model.LatLng

/**
 * Created by mattijn on 30/09/17.
 */

class Location {
    var createdOn: Long? = null
    var lat: Double = 0.toDouble()
    var lon: Double = 0.toDouble()
    var createdBy: String? =null


    constructor()  // speciaal voor firebase
    constructor(navigateTo: LatLng, createdBy: String?) {
        this.lat = navigateTo.latitude
        this.lon = navigateTo.longitude
        this.createdBy = createdBy
        createdOn = System.currentTimeMillis()
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        if (other is Location) {
            val l = other as Location?
            return l!!.lon == this.lon && l.lat == this.lat
        } else {
            return false
        }
    }
}
