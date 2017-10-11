package nl.rsdt.japp.jotial.maps.clustering;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.Set;

import nl.rsdt.japp.R;
import nl.rsdt.japp.application.JappPreferences;
import nl.rsdt.japp.jotial.data.structures.area348.ScoutingGroepInfo;
import nl.rsdt.japp.jotial.maps.deelgebied.Deelgebied;
import nl.rsdt.japp.jotial.maps.management.MarkerIdentifier;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 26-10-2015
 * The (cluster)renderer for the ScoutingGroepInfo.
 */
public class ScoutingGroepRenderer extends DefaultClusterRenderer<ScoutingGroepInfo> {

    public ScoutingGroepRenderer(Context context, GoogleMap map, ClusterManager<ScoutingGroepInfo> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(ScoutingGroepInfo item, MarkerOptions markerOptions) {
        markerOptions.visible(false);
        Set<String> enabled = JappPreferences.getAreasEnabled();
        for(String area : enabled) {
            if(item.team.toLowerCase().equals(area.substring(0, 1))) {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.scouting_groep_icon_30x22));
                markerOptions.anchor(0.5f, 0.5f);

                MarkerIdentifier identifier  = new MarkerIdentifier.Builder()
                        .setType(MarkerIdentifier.TYPE_SC)
                        .add("name", item.naam)
                        .add("adres", item.adres)
                        .add("team", item.team)
                        .create();

                markerOptions.title(new Gson().toJson(identifier));
                markerOptions.visible(true);
            }
        }
    }

    @Override
    protected void onClusterItemRendered(ScoutingGroepInfo clusterItem, Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<ScoutingGroepInfo> cluster, MarkerOptions markerOptions) {
        Deelgebied deelgebied = Deelgebied.resolveOnLocation(cluster.getPosition());
        if(deelgebied != null) {
            Set<String> enabled = JappPreferences.getAreasEnabled();
            if(!enabled.contains(deelgebied.getName())) {
                markerOptions.visible(false);
            }
        }

        MarkerIdentifier identifier  = new MarkerIdentifier.Builder()
                .setType(MarkerIdentifier.TYPE_SC_CLUSTER)
                .add("size", String.valueOf(cluster.getSize()))
                .create();
        markerOptions.title(new Gson().toJson(identifier));
        super.onBeforeClusterRendered(cluster, markerOptions);
    }

    @Override
    protected void onClusterRendered(Cluster<ScoutingGroepInfo> cluster, Marker marker) {
        super.onClusterRendered(cluster, marker);
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<ScoutingGroepInfo> cluster) {
        return super.shouldRenderAsCluster(cluster);
    }

    @Override
    protected String getClusterText(int bucket) {
        return super.getClusterText(bucket);
    }
}