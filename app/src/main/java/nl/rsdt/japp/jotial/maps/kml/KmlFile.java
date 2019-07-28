package nl.rsdt.japp.jotial.maps.kml;

import java.util.List;

public class KmlFile {
    private final KmlScoutingGroep organisatie;
    private final List<KmlScoutingGroep> groepen;
    private final List<KmlStyleBase> styles;
    private final List<KmlDeelgebied> deelgebieden;
    private final String name;


    public KmlFile(String name, KmlScoutingGroep organisatie, List<KmlScoutingGroep> groepen, List<KmlStyleBase> styles, List<KmlDeelgebied> deelgebieden) {
        this.organisatie = organisatie;
        this.groepen = groepen;
        this.styles = styles;
        this.name = name;
        this.deelgebieden = deelgebieden;
    }
}
