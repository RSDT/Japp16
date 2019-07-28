package nl.rsdt.japp.jotial.maps.kml;

public class KmlDeelgebiedStyle extends KmlStyleBase {

    private final int lineColor;
    private final double lineWidth;
    private final int polyColor;
    private final boolean polyFill;
    private final int polyOutline;

    public KmlDeelgebiedStyle(String id, int lineColor, double lineWidth, int polyColor, boolean polyFill, int polyOutline) {
        super(id);
        this.lineColor = lineColor;
        this.lineWidth = lineWidth;
        this.polyColor = polyColor;
        this.polyFill = polyFill;
        this.polyOutline = polyOutline;
    }

    public int getLineColor() {
        return lineColor;
    }

    public double getLineWidth() {
        return lineWidth;
    }

    public int getPolyColor() {
        return polyColor;
    }

    public boolean getPolyFill() {
        return polyFill;
    }

    public int getPolyOutline() {
        return polyOutline;
    }
}
