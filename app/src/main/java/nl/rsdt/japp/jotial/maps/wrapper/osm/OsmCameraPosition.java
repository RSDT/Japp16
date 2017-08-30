package nl.rsdt.japp.jotial.maps.wrapper.osm;

import org.osmdroid.views.MapView;

import nl.rsdt.japp.jotial.maps.wrapper.ICameraPosition;

/**
 * Created by mattijn on 17/08/17.
 */

public class OsmCameraPosition implements ICameraPosition{
    private final MapView osmMap;

    public OsmCameraPosition(MapView osmMap){
        this.osmMap = osmMap;
    }
    public float getZoom() {
        return osmMap.getZoomLevel();
    }

    public float getTilt() {
        return 90;
    }
}
