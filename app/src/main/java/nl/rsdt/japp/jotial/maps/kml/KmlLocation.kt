package nl.rsdt.japp.jotial.maps.kml

import java.util.*

class KmlLocation(val lon: Double?, val lat: Double?, val alt: Double?) {
    companion object {

        fun readCoordinates(coordinates: String): List<KmlLocation> {
            val result = ArrayList<KmlLocation>()
            for (c in coordinates.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                c = c.trim { it <= ' ' }
                val coordinate = c.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (coordinate.size == 3) {
                    result.add(KmlLocation(
                            java.lang.Double.valueOf(coordinate[0]),
                            java.lang.Double.valueOf(coordinate[1]),
                            java.lang.Double.valueOf(coordinate[2])
                    ))
                }
            }
            return result
        }
    }
}
