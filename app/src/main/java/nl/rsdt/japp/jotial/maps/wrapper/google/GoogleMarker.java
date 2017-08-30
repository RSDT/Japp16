package nl.rsdt.japp.jotial.maps.wrapper.google;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import nl.rsdt.japp.application.Japp;
import nl.rsdt.japp.jotial.maps.wrapper.IJotiMap;
import nl.rsdt.japp.jotial.maps.wrapper.IMarker;

/**
 * Created by mattijn on 08/08/17.
 */

public class GoogleMarker implements IMarker {
    private static IJotiMap.OnMarkerClickListener allOnClickLister;
    private final com.google.android.gms.maps.model.Marker googleMarker;
    private IMarker.OnClickListener onClickListener;

    public GoogleMarker(com.google.android.gms.maps.model.Marker marker) {
        this.googleMarker = marker;
    }

    public static void setAllOnClickLister(IJotiMap.OnMarkerClickListener onClickListener){
        allOnClickLister = onClickListener;
    }

    private boolean onClick(){
        if (allOnClickLister != null){
            if (!allOnClickLister.OnClick(this)){
                if (this.onClickListener == null) {
                    showInfoWindow();
                    return false;
                }else{
                    return this.onClickListener.OnClick(this);
                }
            }
        }else {
            if (this.onClickListener == null) {
                showInfoWindow();
                return false;
            }else{
                return this.onClickListener.OnClick(this);
            }
        }
        return false;
    }

    @Override
    public void showInfoWindow(){
        googleMarker.showInfoWindow();
    }

    @Override
    public void remove() {
        this.googleMarker.remove();
    }

    @Override
    public String getTitle() {
        return this.googleMarker.getTitle();
    }

    @Override
    public LatLng getPosition() {
        return this.googleMarker.getPosition();
    }

    @Override
    public void setPosition(LatLng latLng) {
        this.googleMarker.setPosition(latLng);
    }

    @Override
    public void setOnClickListener(IMarker.OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }

    @Override
    public void setIcon(int drawableHunt) {
        this.setIcon(BitmapFactory.decodeResource(Japp.getAppResources(),drawableHunt));
    }

    @Override
    public void setIcon(Bitmap bitmap) {
        this.googleMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
    }

    @Override
    public void setTitle(String title) {
        googleMarker.setTitle(title);
    }

    @Override
    public boolean isVisible() {
        return this.googleMarker.isVisible();
    }

    @Override
    public void setVisible(boolean visible) {
        googleMarker.setVisible(visible);
    }

    @Override
    public void setRotation(float rotation) {
        googleMarker.setRotation(rotation);
    }

    @Override
    public String getId() {
        return googleMarker.getId();
    }
}
