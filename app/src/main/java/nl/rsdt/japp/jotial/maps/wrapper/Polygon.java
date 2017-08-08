package nl.rsdt.japp.jotial.maps.wrapper;

/**
 * Created by mattijn on 08/08/17.
 */

public class Polygon {
    public static final int GOOGLE_POLYGON = 0;
    public static final int OSM_POLYGON = 1;
    private final com.google.android.gms.maps.model.Polygon googlePolygon;
    private final int polygonType;
    public Polygon(com.google.android.gms.maps.model.Polygon polygon) {
        polygonType = GOOGLE_POLYGON;
        googlePolygon = polygon;
    }

    public void remove() {
        if (polygonType == GOOGLE_POLYGON) {
            googlePolygon.remove();
        }
    }

    public void setStrokeWidth(int strokeWidth) {
        if (polygonType == GOOGLE_POLYGON){
            googlePolygon.setStrokeWidth(strokeWidth);
        }
    }

    public void setFillColor(int color) {
        if (polygonType == GOOGLE_POLYGON){
            googlePolygon.setFillColor(color);
        }
    }
}
