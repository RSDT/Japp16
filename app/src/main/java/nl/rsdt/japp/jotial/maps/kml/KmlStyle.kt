package nl.rsdt.japp.jotial.maps.kml

import android.graphics.Color

class KmlStyle {
    private var id: String = "unkown"
    private var iconColor: Int = 0
    private var iconScale: Double? = null
    private var iconUrl: String = "unkown"
    private var balloonBgColor: Int = 0
    private var balloonText: String = ""
    private var lineColor: Int = 0
    private var lineWidth: Double = 0.toDouble()
    private var polyLineColor: Int = 0
    private var polyLineFill: Boolean = false
    private var polyLineOutline: Int = 0

    fun setId(id: String) {
        this.id = id
    }

    fun setIconColor(iconColor: String) {
        var iconColor = iconColor
        if (!iconColor.startsWith("#")) {
            iconColor = "#$iconColor"
        }
        this.iconColor = Color.parseColor(iconColor)
    }

    fun setIconScale(iconScale: Double) {
        this.iconScale = iconScale
    }

    fun setIconUrl(iconUrl: String) {
        this.iconUrl = iconUrl
    }

    fun setBalloonBgColor(balloonBgColor: String) {
        var balloonBgColor = balloonBgColor
        if (!balloonBgColor.startsWith("#")) {
            balloonBgColor = "#$balloonBgColor"
        }
        this.balloonBgColor = Color.parseColor(balloonBgColor)
    }

    fun setBalloonText(balloonText: String) {
        this.balloonText = balloonText
    }

    fun setLineColor(lineColor: String) {
        var lineColor = lineColor
        if (!lineColor.startsWith("#")) {
            lineColor = "#$lineColor"
        }
        this.lineColor = Color.parseColor(lineColor)
    }

    fun setLineWidth(lineWidth: Double) {
        this.lineWidth = lineWidth

    }

    fun setPolyLineColor(polyLineColor: String) {
        var polyLineColor = polyLineColor
        if (!polyLineColor.startsWith("#")) {
            polyLineColor = "#$polyLineColor"
        }
        this.polyLineColor = Color.parseColor(polyLineColor)
    }

    fun setPolyLineFill(polyLineFill: Int) {
        this.polyLineFill = polyLineFill != 0
    }

    fun setPolyLineOutline(polyLineOutline: Int) {
        this.polyLineOutline = polyLineOutline
    }

    fun toStyleBase(): KmlStyleBase {
        return if (this.id == "groep") {
            KmlGroepStyle(id, iconColor, iconScale!!, iconUrl, balloonBgColor, balloonText)
        } else {
            KmlDeelgebiedStyle(id, lineColor, lineWidth, polyLineColor, polyLineFill, polyLineOutline)
        }
    }
}
