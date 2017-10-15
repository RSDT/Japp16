package nl.rsdt.japp.jotial.maps.clustering.osm;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Pair;

import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.osmdroid.bonuspack.clustering.MarkerClusterer;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import java.util.ArrayList;
import java.util.Collection;

import nl.rsdt.japp.R;
import nl.rsdt.japp.application.Japp;
import nl.rsdt.japp.application.fragments.JappMapFragment;
import nl.rsdt.japp.jotial.data.structures.area348.ScoutingGroepInfo;
import nl.rsdt.japp.jotial.maps.clustering.ClusterManagerInterface;
import nl.rsdt.japp.jotial.maps.wrapper.ICircle;
import nl.rsdt.japp.jotial.maps.wrapper.IJotiMap;
import nl.rsdt.japp.jotial.maps.wrapper.IMarker;
import nl.rsdt.japp.jotial.maps.wrapper.osm.OsmJotiMap;
import nl.rsdt.japp.jotial.maps.wrapper.osm.OsmMarker;

/**
 * Created by mattijn on 10/08/17.
 */

public class OsmScoutingGroepClusterManager implements ClusterManagerInterface {

    private final Collection<ScoutingGroepInfo> infos;
    private final MapView osmMap;
    private final OsmMarkerContainer markers;

    public OsmScoutingGroepClusterManager(OsmJotiMap jotiMap) {
        if (!(jotiMap instanceof OsmJotiMap)){
            throw new RuntimeException("this class can only be usd with osm");
        }
        OsmJotiMap osmJotiMap = (OsmJotiMap) jotiMap;
        this.osmMap = osmJotiMap.getOSMMap();
        infos = new ArrayList<>();
        markers = new OsmMarkerContainer(osmJotiMap);
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
