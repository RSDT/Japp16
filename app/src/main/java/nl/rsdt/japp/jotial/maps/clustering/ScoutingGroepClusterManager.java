package nl.rsdt.japp.jotial.maps.clustering;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.Collection;

import nl.rsdt.japp.jotial.data.structures.area348.ScoutingGroepInfo;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 29-8-2016
 * Description...
 */
public class ScoutingGroepClusterManager extends ClusterManager<ScoutingGroepInfo> implements ClusterManagerInterface {


    protected Context context;

    protected GoogleMap map;

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
        this.context = context;
        this.map = map;
        setAlgorithm(algorithm);
        renderer = new ScoutingGroepRenderer(context, map, this);
        setRenderer(renderer);
        map.setOnCameraIdleListener(this);
        map.setOnMarkerClickListener(this);
    }

    public void reRender() {
        renderer = new ScoutingGroepRenderer(context, map, this);
        setRenderer(renderer);
    }


    @Override
    public void addItems(ArrayList<ScoutingGroepInfo> buffer) {
        super.addItems(buffer);
    }

    /**
     * Gets the items.
     * */
    public Collection<ScoutingGroepInfo> getItems() {
        return algorithm.getItems();
    }

}

