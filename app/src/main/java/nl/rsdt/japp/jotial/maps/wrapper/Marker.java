package nl.rsdt.japp.jotial.maps.wrapper;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Pair;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import nl.rsdt.japp.application.Japp;

/**
 * Created by mattijn on 08/08/17.
 */

public class Marker {
    public static final int GOOGLEMARKER = 0;
    public static final int OSMMARKER = 1;
    private final int markerType;
    private final com.google.android.gms.maps.model.Marker googleMarker;
    private final org.osmdroid.views.overlay.Marker osmMarker;

    public Marker(com.google.android.gms.maps.model.Marker marker) {
        this.googleMarker = marker;
        markerType = GOOGLEMARKER;
        osmMarker = null;
    }

    public Marker(Pair<MarkerOptions,Bitmap> markerOptionsPair, MapView osmMap) {
        googleMarker = null;
        markerType = OSMMARKER;
        osmMarker = new org.osmdroid.views.overlay.Marker(osmMap);
        MarkerOptions markerOptions = markerOptionsPair.first;
        osmMarker.setIcon(new BitmapDrawable(Japp.getInstance().getResources(), markerOptionsPair.second));
        osmMarker.setPosition(new GeoPoint(markerOptions.getPosition().latitude,markerOptions.getPosition().longitude));
        osmMap.getOverlays().add(osmMarker);
        osmMap.invalidate();
    }

    public void remove() {
        switch (markerType){
            case GOOGLEMARKER:
                this.googleMarker.remove();
                break;
            case OSMMARKER:
                //// TODO: 09/08/17 implement this
                break;
            default:
                break;
        }
    }

    public String getTitle() {
        switch (markerType){
            case GOOGLEMARKER:
                return this.googleMarker.getTitle();
            case OSMMARKER:
                return osmMarker.getTitle();
            default:
                return "";
        }
    }

    public LatLng getPosition() {
        switch (markerType) {
            case GOOGLEMARKER:
                return this.googleMarker.getPosition();
            case OSMMARKER:
                return new LatLng(osmMarker.getPosition().getLatitude(),osmMarker.getPosition().getLongitude());
            default:
                return null;
        }
    }

    public void setPosition(LatLng latLng) {
        switch (markerType){
            case GOOGLEMARKER:
                this.googleMarker.setPosition(latLng);
                break;
            case OSMMARKER:
                this.osmMarker.setPosition(new GeoPoint(latLng.latitude,latLng.longitude));
                break;
            default:
                break;
        }
    }


    public void setIcon(int drawableHunt) {
        switch (markerType){
            case GOOGLEMARKER:
                this.googleMarker.setIcon(BitmapDescriptorFactory.fromResource(drawableHunt));
                break;
            case OSMMARKER:
                //// TODO: 09/08/17 implemnt this
                break;
            default:
                break;
        }
    }

    public void setTitle(String title) {
        switch (markerType){
            case GOOGLEMARKER:
                googleMarker.setTitle(title);
                break;
            case OSMMARKER:
                osmMarker.setTitle(title);
            default:
                break;
        }
    }

    public boolean isVisible() {
        switch (markerType) {
            case GOOGLEMARKER:
                return this.googleMarker.isVisible();
            case OSMMARKER:
                return this.osmMarker.isEnabled(); //// TODO: 09/08/17 is dit hetzelfde?
            default:
                return false;
        }
    }

    public void setVisible(boolean visible) {
        switch (markerType){
            case GOOGLEMARKER:
                googleMarker.setVisible(visible);
                break;
            case OSMMARKER:
                osmMarker.setEnabled(visible); //// TODO: 09/08/17 is dit hetzelfde?
        }
    }

    public void setRotation(float rotation) {
        switch (markerType) {
            case GOOGLEMARKER:
                googleMarker.setRotation(rotation);
                break;
            case OSMMARKER:
                osmMarker.setRotation(rotation);
                break;
            default:
                break;
        }
    }

    public String getId() {
        switch (markerType) {
            case GOOGLEMARKER:
                return googleMarker.getId();
            case OSMMARKER:
                return "1";// // TODO: 09/08/17 implement this
            default:
                return null;
        }
    }
}
