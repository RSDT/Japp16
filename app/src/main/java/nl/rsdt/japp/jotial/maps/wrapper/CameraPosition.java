package nl.rsdt.japp.jotial.maps.wrapper;

import org.osmdroid.views.MapView;

/**
 * Created by mattijn on 17/08/17.
 */

public class CameraPosition {
    private final MapView osmMap;
    private float zoom;
    private float tilt;
    private final static int GOOGLE_CAMERAPOS= 0;
    private final static int OSM_CAMERAPOS= 1;
    private final int cameraPositionType;
    private com.google.android.gms.maps.model.CameraPosition googlePos;

    public CameraPosition(com.google.android.gms.maps.model.CameraPosition cameraPosition) {
        cameraPositionType = GOOGLE_CAMERAPOS;
        googlePos = cameraPosition;
        osmMap = null;
    }
    public CameraPosition(MapView osmMap){
        this.osmMap = osmMap;
        this.cameraPositionType = OSM_CAMERAPOS;
    }
    public float getZoom() {
        switch (cameraPositionType){
            case GOOGLE_CAMERAPOS:
                return googlePos.zoom;
            case OSM_CAMERAPOS:
                return osmMap.getZoomLevel();
            default:
                return 10;
        }
    }

    public float getTilt() {
        switch (cameraPositionType){
            case GOOGLE_CAMERAPOS:
                return googlePos.zoom;
            case OSM_CAMERAPOS:
                return 90;
            default:
                return 10;
        }
    }
}
