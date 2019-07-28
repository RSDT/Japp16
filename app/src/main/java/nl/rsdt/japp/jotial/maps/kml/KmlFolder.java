package nl.rsdt.japp.jotial.maps.kml;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class KmlFolder {
    private Type type;
    private List<KmlPlaceMark> placeMarkList = new ArrayList<>();

    public KmlScoutingGroep toKmlOrganisatie() {
        if (type != Type.Organisatie) throw new AssertionError();
        if (placeMarkList.size() != 1) throw new AssertionError();
        return placeMarkList.get(0).toKmlScoutingGroep();
    }

    public List<KmlScoutingGroep> toKmlGroepen() {
        if (type != Type.Groepen) throw new AssertionError();
        List<KmlScoutingGroep> groepen = new LinkedList<>();
        for (KmlPlaceMark placeMark : placeMarkList){
            groepen.add(placeMark.toKmlScoutingGroep());
        }
        return groepen;
    }

    public List<KmlDeelgebied> toKmlDeelgebieden() {
        if (type != Type.Deelgebieden) throw new AssertionError();
        List<KmlDeelgebied> deelgebieden = new LinkedList<>();
        for (KmlPlaceMark placeMark : placeMarkList){
            deelgebieden.add(placeMark.toKmlDeelgebied());
        }
        return deelgebieden;
    }


    public enum Type {
        Organisatie, Groepen, Deelgebieden, Unkown;

        public static Type parse(String s){
        switch (s.toLowerCase()){
            case "organisatie":
                return Organisatie;
            case "groepen":
                return Groepen;
            case "deelgebieden":
                return Deelgebieden;
                default:
                    return Unkown;
        }

        }
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
    public boolean addPlacemark(KmlPlaceMark placeMark){
        return placeMarkList.add(placeMark);
    }
}
