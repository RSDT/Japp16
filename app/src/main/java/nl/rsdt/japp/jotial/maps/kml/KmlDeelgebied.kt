package nl.rsdt.japp.jotial.maps.kml

class KmlDeelgebied(val styleId: String, val name: String, val boundry: List<KmlLocation>) {
    var style: KmlDeelgebiedStyle? = null
        set(style) {
            if (style.id == styleId) {
                field = style
            }
        }
}
