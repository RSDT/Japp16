package nl.rsdt.japp.jotial.maps.kml;

import java.util.List;

public class KmlDeelgebied  {
    private final String styleId;
    private KmlDeelgebiedStyle style;
    private final String name;
    private final List<KmlLocation> boundry;

    public KmlDeelgebied(String styleName, String name, List<KmlLocation> boundry) {
        this.styleId = styleName;
        this.name = name;
        this.boundry = boundry;
    }

    public KmlDeelgebiedStyle getStyle() {
        return style;
    }

    public void setStyle(KmlDeelgebiedStyle style) {
        if (style.getId().equals(styleId)) {
            this.style = style;
        }
    }

    public String getName() {
        return name;
    }
    public String getStyleId() {
        return styleId;
    }
    public List<KmlLocation> getBoundry() {
        return boundry;
    }
}
