package nl.rsdt.japp.jotial.maps.kml;

import java.util.ArrayList;
import java.util.List;

public class KmlLocation {
    public final double lat;
    public final double lon;
    public final double alt;

    public KmlLocation(Double lat, Double lon, Double alt) {
        this.lat = lat;
        this.lon = lon;
        this.alt = alt;
    }

    public static List<KmlLocation> readCoordinates(String coordinates) {
        List<KmlLocation> result = new ArrayList<>();
        for (String c : coordinates.split("\n")) {
            c = c.trim();
            String[] coordinate = c.split(",");
            if (coordinate.length != 3) throw new RuntimeException("string is not an coordinate");
            result.add(new KmlLocation(
                    Double.valueOf(coordinate[0]),
                    Double.valueOf(coordinate[1]),
                    Double.valueOf(coordinate[2])
            ));
        }
        return result;
    }
}
