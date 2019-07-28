package nl.rsdt.japp.jotial.maps.kml;

public class KmlStyleBase {
    private final String id;

    protected KmlStyleBase(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
