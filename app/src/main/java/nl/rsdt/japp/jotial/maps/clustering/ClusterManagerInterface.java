package nl.rsdt.japp.jotial.maps.clustering;

import java.util.ArrayList;
import java.util.Collection;

import nl.rsdt.japp.jotial.data.structures.area348.ScoutingGroepInfo;

/**
 * Created by mattijn on 07/08/17.
 */

public interface ClusterManagerInterface {
    void addItems(ArrayList<ScoutingGroepInfo> buffer);

    void cluster();

    Collection<ScoutingGroepInfo> getItems();

    void clearItems();
}
