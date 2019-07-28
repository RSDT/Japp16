package nl.rsdt.japp.jotial.maps.kml;


import android.graphics.Color;

public class KmlGroepStyle extends KmlStyleBase {
    private final int iconColor;
    private final double iconScale;
    private final String iconUrl;
    private final int balloonBgColor;
    private final String balloonText;
    public KmlGroepStyle(String id, int iconColor, double iconScale, String iconUrl, int balloonBgColor, String balloonText) {
        super(id);
        this.iconColor = iconColor;
        this.iconScale = iconScale;
        this.iconUrl = iconUrl;
        this.balloonBgColor = balloonBgColor;
        this.balloonText = balloonText;
    }

    public int getIconColor() {
        return iconColor;
    }

    public double getIconScale() {
        return iconScale;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public int getBalloonBgColor() {
        return balloonBgColor;
    }

    public String getBalloonText() {
        return balloonText;
    }
}
