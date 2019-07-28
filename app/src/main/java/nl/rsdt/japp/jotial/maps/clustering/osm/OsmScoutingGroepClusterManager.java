package nl.rsdt.japp.jotial.maps.clustering.osm;

import java.util.ArrayList;
import java.util.Collection;

import nl.rsdt.japp.jotial.data.structures.area348.ScoutingGroepInfo;
import nl.rsdt.japp.jotial.maps.clustering.ClusterManagerInterface;
import nl.rsdt.japp.jotial.maps.wrapper.osm.OsmJotiMap;

/**
 * Created by mattijn on 10/08/17.
 */

public class OsmScoutingGroepClusterManager implements ClusterManagerInterface {

    private final Collection<ScoutingGroepInfo> infos;
    private final OsmMarkerContainer markers;

    public OsmScoutingGroepClusterManager(OsmJotiMap jotiMap) {
        if (!(jotiMap instanceof OsmJotiMap)){
            throw new RuntimeException("this class can only be usd with osm");
        }
        infos = new ArrayList<>();
        markers = new OsmMarkerContainer(jotiMap);
    }

    @Override
    public void addItems(final ArrayList<ScoutingGroepInfo> buffer) {
        for (final ScoutingGroepInfo info : buffer){
            infos.add(info);
            markers.add(info);
        }
        markers.showMarkers();
    }

    @Override
    public void cluster() {

    }

    @Override
    public Collection<ScoutingGroepInfo> getItems() {
        return infos;
    }

    @Override
    public void clearItems() {
        infos.clear();
        markers.clear();
    }
}
