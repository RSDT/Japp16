package nl.rsdt.japp.jotial.maps.kml;

import java.util.LinkedList;
import java.util.List;


class KmlDocument {


    private String name;
    private List<KmlFolder> folders = new LinkedList<>();
    private List<KmlStyle> styles = new LinkedList<>();

    public void setName(String name) {
        this.name = name;
    }

    public void addFolder(KmlFolder folder) {
        this.folders.add(folder);
    }

    public void addStyle(KmlStyle style) {
        this.styles.add(style);
    }

    public KmlFile toKmlFile() {
        KmlScoutingGroep organisatie = null;
        List<KmlScoutingGroep> groepen = new LinkedList<>();
        List<KmlStyleBase> styles = new LinkedList<>();
        List<KmlDeelgebied> deelgebieden = new LinkedList<>();
        for (KmlFolder folder : folders){
            switch (folder.getType()){
                case Organisatie:
                    organisatie = folder.toKmlOrganisatie();
                    break;
                case Groepen:
                    groepen = folder.toKmlGroepen();
                    break;
                case Deelgebieden:
                    deelgebieden = folder.toKmlDeelgebieden();
                    break;
            }
        }
        for (KmlStyle style : this.styles){
            KmlStyleBase styleBase = style.toStyleBase();
            if (styleBase instanceof KmlGroepStyle) {
                KmlGroepStyle groepStyle = (KmlGroepStyle) styleBase;
                for (KmlScoutingGroep groep : groepen) {
                    if (groep.getStyleUrl().equals(groepStyle.getId())) {
                        groep.setStyle(groepStyle);
                    }
                }
            }
            if (styleBase instanceof KmlDeelgebiedStyle) {
                KmlDeelgebiedStyle deelgebiedStyle = (KmlDeelgebiedStyle) styleBase;
                for (KmlDeelgebied deelgebied : deelgebieden) {
                    if (deelgebied.getStyleId().equals(deelgebiedStyle.getId())) {
                        deelgebied.setStyle(deelgebiedStyle);
                    }
                }
            }
        }

        return new KmlFile(this.name,organisatie, groepen, styles, deelgebieden);
    }
}
