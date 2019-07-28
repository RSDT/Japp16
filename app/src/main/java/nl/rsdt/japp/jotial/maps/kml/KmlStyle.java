package nl.rsdt.japp.jotial.maps.kml;

import android.graphics.Color;

public class KmlStyle {
    private String id;
    private int iconColor;
    private Double iconScale;
    private String iconUrl;
    private int balloonBgColor;
    private String balloonText;
    private int lineColor;
    private double lineWidth;
    private int polyLineColor;
    private boolean polyLineFill;
    private int polyLineOutline;

    public void setId(String id) {
        this.id = id;
    }

    public void setIconColor(String iconColor) {
        if (!iconColor.startsWith("#")){
            iconColor = "#" + iconColor;
        }
        this.iconColor = Color.parseColor(iconColor);
    }

    public void setIconScale(double iconScale) {
        this.iconScale = iconScale;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public void setBalloonBgColor(String balloonBgColor) {
        if (!balloonBgColor.startsWith("#")){
            balloonBgColor = "#" + balloonBgColor;
        }
        this.balloonBgColor = Color.parseColor(balloonBgColor);
    }

    public void setBalloonText(String balloonText) {
        this.balloonText = balloonText;
    }

    public void setLineColor(String lineColor) {
        if (!lineColor.startsWith("#")){
            lineColor = "#" + lineColor;
        }
        this.lineColor = Color.parseColor(lineColor);
    }

    public void setLineWidth(double lineWidth) {
        this.lineWidth = lineWidth;

    }

    public void setPolyLineColor(String polyLineColor) {
        if (!polyLineColor.startsWith("#")){
            polyLineColor = "#" + polyLineColor;
        }
        this.polyLineColor = Color.parseColor(polyLineColor);
    }

    public void setPolyLineFill(int polyLineFill) {
        this.polyLineFill = polyLineFill != 0;
    }

    public void setPolyLineOutline(int polyLineOutline) {
        this.polyLineOutline = polyLineOutline;
    }

    public KmlStyleBase toStyleBase() {
        if (this.id.equals("groep")){
            return new KmlGroepStyle(id,iconColor,iconScale, iconUrl,balloonBgColor,balloonText);
        }else{
            return new KmlDeelgebiedStyle(id, lineColor,lineWidth,polyLineColor,polyLineFill, polyLineOutline);
        }
    }
}
