package nl.rsdt.japp.jotial.maps.kml

class KmlPlaceMark {
    var name: String? = null
    private var styleUrl: String? = null
    private var coordinates: List<KmlLocation>? = null

    fun setStyleUrl(styleUrl: String) {
        this.styleUrl = styleUrl
    }

    fun setCoordinates(coordinates: List<KmlLocation>) {
        this.coordinates = coordinates
    }

    fun toKmlScoutingGroep(): KmlScoutingGroep {
        if (coordinates!!.size != 1) throw AssertionError()
        return KmlScoutingGroep(name, coordinates!![0], styleUrl)
    }

    fun toKmlDeelgebied(): KmlDeelgebied {
        return KmlDeelgebied(styleUrl, name, coordinates)
    }
}
