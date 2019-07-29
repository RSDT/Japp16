package nl.rsdt.japp.jotial.maps.clustering

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import nl.rsdt.japp.R
import nl.rsdt.japp.application.JappPreferences
import nl.rsdt.japp.jotial.data.structures.area348.ScoutingGroepInfo
import nl.rsdt.japp.jotial.maps.deelgebied.Deelgebied
import nl.rsdt.japp.jotial.maps.management.MarkerIdentifier

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 26-10-2015
 * The (cluster)renderer for the ScoutingGroepInfo.
 */
class ScoutingGroepRenderer(context: Context, map: GoogleMap, clusterManager: ClusterManager<ScoutingGroepInfo>) : DefaultClusterRenderer<ScoutingGroepInfo>(context, map, clusterManager) {

    override fun onBeforeClusterItemRendered(item: ScoutingGroepInfo?, markerOptions: MarkerOptions?) {
        markerOptions!!.visible(false)
        val enabled = JappPreferences.areasEnabled
        for (area in enabled!!) {
            if (item!!.team!!.toLowerCase() == area.substring(0, 1)) {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.scouting_groep_icon_30x22))
                markerOptions.anchor(0.5f, 0.5f)

                val identifier = MarkerIdentifier.Builder()
                        .setType(MarkerIdentifier.TYPE_SC)
                        .add("name", item.naam)
                        .add("adres", item.adres)
                        .add("team", item.team)
                        .create()

                markerOptions.title(Gson().toJson(identifier))
                markerOptions.visible(true)
            }
        }
    }

    fun update() {
        onClustersChanged(null)
    }

    override fun onBeforeClusterRendered(cluster: Cluster<ScoutingGroepInfo>, markerOptions: MarkerOptions) {
        val deelgebied = Deelgebied.resolveOnLocation(cluster.position)
        if (deelgebied != null) {
            val enabled = JappPreferences.areasEnabled
            if (!enabled!!.contains(deelgebied.name)) {
                markerOptions.visible(false)
            }
        }

        val identifier = MarkerIdentifier.Builder()
                .setType(MarkerIdentifier.TYPE_SC_CLUSTER)
                .add("size", cluster.size.toString())
                .create()
        markerOptions.title(Gson().toJson(identifier))
        super.onBeforeClusterRendered(cluster, markerOptions)
    }

    override fun shouldRenderAsCluster(cluster: Cluster<ScoutingGroepInfo>): Boolean {
        val deelgebied = Deelgebied.resolveOnLocation(cluster.position)
        if (deelgebied != null) {
            val enabled = JappPreferences.areasEnabled
            if (!enabled!!.contains(deelgebied.name)) {
                return false
            }
        }
        return super.shouldRenderAsCluster(cluster)
    }

}