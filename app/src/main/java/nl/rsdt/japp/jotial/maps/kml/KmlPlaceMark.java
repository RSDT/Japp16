package nl.rsdt.japp.jotial.maps.kml;

import java.util.List;

public class KmlPlaceMark {
    private String name = null;
    private String styleUrl;
    private List<KmlLocation> coordinates;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setStyleUrl(String styleUrl) {
        this.styleUrl = styleUrl;
    }

    public void setCoordinates(List<KmlLocation> coordinates) {
        this.coordinates = coordinates;
    }

    public KmlScoutingGroep toKmlScoutingGroep() {
        if (coordinates.size() != 1) throw new AssertionError();
        return new KmlScoutingGroep(name,coordinates.get(0), styleUrl);
    }

    public KmlDeelgebied toKmlDeelgebied() {
        return new KmlDeelgebied(styleUrl,name,coordinates);
    }
}
