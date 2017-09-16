package nl.rsdt.japp.jotial.maps.wrapper.osm;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Pair;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import nl.rsdt.japp.application.Japp;
import nl.rsdt.japp.jotial.maps.wrapper.IJotiMap;
import nl.rsdt.japp.jotial.maps.wrapper.IMarker;

/**
 * Created by mattijn on 08/08/17.
 */

public class OsmMarker implements IMarker{
    private static IJotiMap.OnMarkerClickListener allOnClickLister = null;
    private final org.osmdroid.views.overlay.Marker osmMarker;
    private final MapView osmMap;
    private OnClickListener onClickListener = null;

    public OsmMarker(Pair<MarkerOptions,Bitmap> markerOptionsPair, MapView osmMap) {
        this.osmMap = osmMap;
        osmMarker = new org.osmdroid.views.overlay.Marker(osmMap);
        MarkerOptions markerOptions = markerOptionsPair.first;
        this.setIcon(markerOptionsPair.second);
        this.setPosition(markerOptions.getPosition());
        if (markerOptions.getTitle() == null){
            markerOptions.title("");
        }
        try {
            JSONObject mainObject = new JSONObject(markerOptions.getTitle());
            String type = mainObject.getString("type");
            JSONObject properties = mainObject.getJSONObject("properties");
            StringBuilder buff = new StringBuilder();
            if (type.equals("VOS")) {

                buff.append(properties.getString("extra")).append("\n");
                buff.append(properties.getString("time")).append("\n");
                buff.append(properties.getString("note")).append("\n");
                buff.append(properties.getString("team")).append("\n");
            }else if(type.equals("HUNTER")){
                buff.append(properties.getString("hunter")).append("\n");
                buff.append(properties.getString("time")).append("\n");
            }else {
                buff.append(markerOptions.getTitle());
            }
            osmMarker.setTitle(buff.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            osmMarker.setTitle(markerOptions.getTitle());
        }

        osmMarker.setOnMarkerClickListener(new org.osmdroid.views.overlay.Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(org.osmdroid.views.overlay.Marker marker, MapView mapView) {
                if (marker == osmMarker) {
                    onClick();
                }
                return false;
            }
        });
        osmMap.getOverlays().add(osmMarker);
        osmMap.invalidate();
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

    public void showInfoWindow(){
        osmMarker.showInfoWindow() ;
    }
    public void remove() {
        osmMap.getOverlays().remove(osmMarker);
        osmMap.invalidate();
    }

    public String getTitle() {
        return osmMarker.getTitle() ;
    }

    public LatLng getPosition() {
        return new LatLng(osmMarker.getPosition().getLatitude(),osmMarker.getPosition().getLongitude());
    }

    public void setPosition(LatLng latLng) {
        this.osmMarker.setPosition(new GeoPoint(latLng.latitude,latLng.longitude));
        osmMap.invalidate() ;
    }

    public void setOnClickListener(IMarker.OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }
    public void setIcon(int drawableHunt) {
        this.setIcon(BitmapFactory.decodeResource(Japp.getAppResources(),drawableHunt));

    }

    public void setIcon(Bitmap bitmap) {
        if (bitmap != null) {
            Drawable d = new BitmapDrawable(Japp.getAppResources(), bitmap);
            this.osmMarker.setIcon(d);
            osmMap.invalidate();
        }
    }

    public void setTitle(String title) {
        osmMarker.setTitle(title);
    }

    public boolean isVisible() {
        return true; //// TODO: 09/08/17 is dit hetzelfde?
    }

    public void setVisible(boolean visible) {
        //osmMarker.setEnabled(visible); //// TODO: 09/08/17 is dit hetzelfde?
    }

    public void setRotation(float rotation) {
        osmMarker.setRotation(rotation);
        osmMap.invalidate();
    }

    public String getId() {
        return "1";// // TODO: 09/08/17 implement this
    }

    public org.osmdroid.views.overlay.Marker getOSMMarker() {
        return osmMarker;
    }
}
