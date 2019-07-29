package nl.rsdt.japp.jotial.maps.kml

class KmlScoutingGroep(val name: String, val coordinate: KmlLocation, val styleUrl: String) {
    var style: KmlGroepStyle? = null
}
