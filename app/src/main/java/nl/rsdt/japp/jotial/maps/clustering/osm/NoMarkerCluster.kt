package nl.rsdt.japp.jotial.maps.clustering.osm

import android.graphics.Canvas
import org.osmdroid.bonuspack.clustering.MarkerClusterer
import org.osmdroid.bonuspack.clustering.StaticCluster
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.util.ArrayList

class NoMarkerCluster : MarkerClusterer() {
    override fun clusterer(mapView: MapView?): ArrayList<StaticCluster> {
        TODO("Not yet implemented")
    }

    override fun buildClusterMarker(cluster: StaticCluster?, mapView: MapView?): Marker {
        TODO("Not yet implemented")
    }

    override fun renderer(clusters: ArrayList<StaticCluster>?, canvas: Canvas?, mapView: MapView?) {
        TODO("Not yet implemented")
    }
}