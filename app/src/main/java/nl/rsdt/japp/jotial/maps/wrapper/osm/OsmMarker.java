package nl.rsdt.japp.jotial.maps.wrapper.osm;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.HashMap;

import nl.rsdt.japp.application.Japp;
import nl.rsdt.japp.jotial.maps.management.MarkerIdentifier;
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

        if(markerOptions.getTitle() != null && !markerOptions.getTitle().isEmpty()) {
            StringBuilder buff = new StringBuilder();
            MarkerIdentifier identifier = null;
            try {
                identifier = new Gson().fromJson(markerOptions.getTitle(), MarkerIdentifier.class);
            }
            catch(Exception e) {
                Log.e("OsmMarker", e.toString());
            }
            if(identifier != null) {
                HashMap<String, String> properties = identifier.getProperties();
                switch (identifier.getType()) {
                    case MarkerIdentifier.TYPE_VOS:
                        buff.append(properties.get("extra")).append("\n");
                        buff.append(properties.get("time")).append("\n");
                        buff.append(properties.get("note")).append("\n");
                        buff.append(properties.get("team")).append("\n");
                        break;
                    case MarkerIdentifier.TYPE_HUNTER:
                        buff.append(properties.get("hunter")).append("\n");
                        buff.append(properties.get("time")).append("\n");
                        break;
                    case MarkerIdentifier.TYPE_SC:
                        buff.append(properties.get("name")).append("\n");
                        buff.append(properties.get("adres")).append("\n");
                        buff.append(properties.get("team")).append("\n");
                        break;
                    case MarkerIdentifier.TYPE_NAVIGATE_CAR:
                        buff.append("Dit is waar de navigatie telefoon naartoe naar navigeerd");
                        buff.append("\n");
                        buff.append("geplaatst door: ");
                        buff.append(properties.get("addedBy"));
                        //todo voeg leuk icoon toe
                        break;
                    case MarkerIdentifier.TYPE_NAVIGATE:
                        buff.append("Hier naar zal je genavigeerd worden!");
                        break;
                }
                osmMarker.setTitle(buff.toString());
            } else {
                osmMarker.setTitle(markerOptions.getTitle());
            }
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
        return true; }

    public void setVisible(boolean visible) {
        osmMarker.setEnabled(visible);
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
