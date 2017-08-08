package nl.rsdt.japp.jotial.maps.wrapper;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import java.util.Objects;

import nl.rsdt.japp.jotial.maps.misc.AnimateMarkerTool;
import nl.rsdt.japp.jotial.maps.misc.LatLngInterpolator;

/**
 * Created by mattijn on 08/08/17.
 */

public class Marker {
    public static final int GOOGLEMARKER = 0;
    public static final int OSMMARKER = 1;
    private final int markerType;
    private final com.google.android.gms.maps.model.Marker marker;

    public Marker(com.google.android.gms.maps.model.Marker marker) {
        this.marker = marker;
        markerType = GOOGLEMARKER;
    }

    public void remove() {
        if (markerType == GOOGLEMARKER){
            this.marker.remove();
        }else{
            return;
        }
    }

    public String getTitle() {
        if (markerType == GOOGLEMARKER){
            return this.marker.getTitle();
        }else{
            return "";
        }
    }

    public LatLng getPosition() {
        if (markerType == GOOGLEMARKER){
            return this.marker.getPosition();
        }else{
            return null;
        }
    }

    public void setPosition(LatLng latLng) {
        if (markerType == GOOGLEMARKER){
            this.marker.setPosition(latLng);
        }else{
            return;
        }
    }


    public void setIcon(int drawableHunt) {
        if (markerType == GOOGLEMARKER){
            this.marker.setIcon(BitmapDescriptorFactory.fromResource(drawableHunt));
        }else{
            return;
        }
    }

    public void setTitle(String title) {
        if (markerType == GOOGLEMARKER){
            this.marker.setTitle(title);
        }else{
            return;
        }
    }

    public boolean isVisible() {
        if (markerType == GOOGLEMARKER){
            return this.marker.isVisible();
        }else{
            return false;
        }
    }

    public void setVisible(boolean visible) {
        if (markerType == GOOGLEMARKER){
            marker.setVisible(visible);
        }else{
            return;
        }
    }

    public void setRotation(float rotation) {
        if (markerType == GOOGLEMARKER){
            marker.setRotation(rotation);
        }else{
            return;
        }
    }

    public void animateMarkerToICS(LatLng latLng, LatLngInterpolator.Linear linear, int duration) {
        if (markerType == GOOGLEMARKER){
            AnimateMarkerTool.animateMarkerToICS(marker,latLng,linear,duration);
        }else{
            return;
        }
    }

    public String getId() {
        if (markerType == GOOGLEMARKER){
            return marker.getId();
        }else{
            return null;
        }
    }
}
