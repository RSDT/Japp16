package nl.rsdt.japp.jotial.maps.kml

class KmlFile internal constructor(val name: String, val organisatie: KmlScoutingGroep?, val groepen: List<KmlScoutingGroep>, val styles: List<KmlStyleBase>, deelgebieden: List<KmlDeelgebied>) {
    val alpha: KmlDeelgebied?
    val bravo: KmlDeelgebied?
    val charlie: KmlDeelgebied?
    val delta: KmlDeelgebied?
    val echo: KmlDeelgebied?
    val foxtrot: KmlDeelgebied?


    init {
        var alpha: KmlDeelgebied? = null
        var bravo: KmlDeelgebied? = null
        var charlie: KmlDeelgebied? = null
        var delta: KmlDeelgebied? = null
        var echo: KmlDeelgebied? = null
        var foxtrot: KmlDeelgebied? = null
        for (dg in deelgebieden) {


            when (dg.name.toLowerCase()[0]) {
                'a' -> alpha = dg
                'b' -> bravo = dg
                'c' -> charlie = dg
                'd' -> delta = dg
                'e' -> echo = dg
                'f' -> foxtrot = dg
            }
        }
        this.alpha = alpha
        this.bravo = bravo
        this.charlie = charlie
        this.delta = delta
        this.echo = echo
        this.foxtrot = foxtrot
    }
}
