package nl.rsdt.japp.jotial.maps.clustering;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.maps.android.clustering.ClusterManager;

import java.util.Collection;
import nl.rsdt.japp.jotial.data.structures.area348.ScoutingGroepInfo;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 29-8-2016
 * Description...
 */
public class ScoutingGroepClusterManager extends ClusterManager<ScoutingGroepInfo> {

    /**
     * The algorithm used for the clustering.
     * */
    ScoutingGroepAlgorithm algorithm = new ScoutingGroepAlgorithm();

    /**
     * The renderer used for the clustering.
     * */
    ScoutingGroepRenderer renderer;

    /**
     * Initializes a new instance of ScoutingGroepClusterManager.
     * */
    public ScoutingGroepClusterManager(Context context, GoogleMap map) {
        super(context, map);
        setAlgorithm(algorithm);
        renderer = new ScoutingGroepRenderer(context, map, this);
        setRenderer(renderer);
        map.setOnCameraIdleListener(this);
        map.setOnMarkerClickListener(this);
    }

    /**
     * Gets the items.
     * */
    public Collection<ScoutingGroepInfo> getItems() {
        return algorithm.getItems();
    }

}

