package nl.rsdt.japp.jotial.maps.wrapper.google;

import nl.rsdt.japp.jotial.maps.wrapper.ICameraPosition;

/**
 * Created by mattijn on 17/08/17.
 */

public class GoogleCameraPosition implements ICameraPosition {

    private com.google.android.gms.maps.model.CameraPosition googlePos;

    public GoogleCameraPosition(com.google.android.gms.maps.model.CameraPosition cameraPosition) {
        googlePos = cameraPosition;
    }

    @Override
    public float getZoom() {
        return googlePos.zoom;
    }

    @Override
    public float getTilt() {
        return googlePos.tilt;
    }
}
