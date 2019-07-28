package nl.rsdt.japp.jotial.maps.kml;

import java.util.List;

public class KmlFile {
    private final KmlScoutingGroep organisatie;
    private final List<KmlScoutingGroep> groepen;
    private final List<KmlStyleBase> styles;
    private final String name;
    private final KmlDeelgebied alpha;
    private final KmlDeelgebied bravo;
    private final KmlDeelgebied charlie;
    private final KmlDeelgebied delta;
    private final KmlDeelgebied echo;
    private final KmlDeelgebied foxtrot;


    KmlFile(String name, KmlScoutingGroep organisatie, List<KmlScoutingGroep> groepen, List<KmlStyleBase> styles, List<KmlDeelgebied> deelgebieden) {
        this.organisatie = organisatie;
        this.groepen = groepen;
        this.styles = styles;
        this.name = name;
        KmlDeelgebied alpha = null;
        KmlDeelgebied bravo = null;
        KmlDeelgebied charlie = null;
        KmlDeelgebied delta = null;
        KmlDeelgebied echo = null;
        KmlDeelgebied foxtrot = null;
        for (KmlDeelgebied dg : deelgebieden){


            switch (dg.getName().toLowerCase().charAt(0)){
                case 'a':
                    alpha = dg;
                    break;
                case 'b':
                    bravo = dg;
                    break;
                case 'c':
                    charlie = dg;
                    break;
                case 'd':
                    delta = dg;
                    break;
                case 'e':
                    echo = dg;
                    break;
                case 'f':
                    foxtrot = dg;
                    break;
            }
        }
        this.alpha =alpha;
        this.bravo = bravo;
        this.charlie = charlie;
        this.delta = delta;
        this.echo = echo;
        this.foxtrot = foxtrot;
    }

    public KmlScoutingGroep getOrganisatie() {
        return organisatie;
    }

    public List<KmlScoutingGroep> getGroepen() {
        return groepen;
    }

    public List<KmlStyleBase> getStyles() {
        return styles;
    }

    public String getName() {
        return name;
    }

    public KmlDeelgebied getAlpha() {
        return alpha;
    }

    public KmlDeelgebied getBravo() {
        return bravo;
    }

    public KmlDeelgebied getCharlie() {
        return charlie;
    }

    public KmlDeelgebied getDelta() {
        return delta;
    }

    public KmlDeelgebied getEcho() {
        return echo;
    }

    public KmlDeelgebied getFoxtrot() {
        return foxtrot;
    }
}
