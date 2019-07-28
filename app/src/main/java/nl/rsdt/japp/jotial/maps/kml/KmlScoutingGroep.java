package nl.rsdt.japp.jotial.maps.kml;

public class KmlScoutingGroep {
    private final String name;
    private final KmlLocation coordinate;
    private final String styleUrl;
    private KmlGroepStyle style;

    public KmlScoutingGroep(String name, KmlLocation coordinate, String styleUrl) {
        this.name = name;
        this.coordinate = coordinate;
        this.styleUrl = styleUrl;
    }

    public String getName() {
        return name;
    }

    public KmlLocation getCoordinate() {
        return coordinate;
    }

    public KmlGroepStyle getStyle() {
        return style;
    }

    public void setStyle(KmlGroepStyle style) {
        this.style = style;
    }

    public String getStyleUrl() {
        return styleUrl;
    }
}
