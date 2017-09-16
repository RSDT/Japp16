package nl.rsdt.japp.jotial.maps.wrapper.google;

import android.graphics.Bitmap;
import android.location.Location;
import android.util.Pair;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.HashMap;
import java.util.Map;

import nl.rsdt.japp.jotial.maps.misc.CameraUtils;
import nl.rsdt.japp.jotial.maps.window.CustomInfoWindowAdapter;
import nl.rsdt.japp.jotial.maps.wrapper.ICameraPosition;
import nl.rsdt.japp.jotial.maps.wrapper.ICircle;
import nl.rsdt.japp.jotial.maps.wrapper.IJotiMap;
import nl.rsdt.japp.jotial.maps.wrapper.IMarker;
import nl.rsdt.japp.jotial.maps.wrapper.IPolygon;
import nl.rsdt.japp.jotial.maps.wrapper.IPolyline;
import nl.rsdt.japp.jotial.maps.wrapper.IUiSettings;

;

/**
 * Created by mattijn on 07/08/17.
 */

public class GoogleJotiMap implements IJotiMap{
    private static Map<MapView, GoogleJotiMap> google_instances= new HashMap<>();
    private GoogleMap googleMap ;
    private final MapView view;

    private GoogleJotiMap(MapView map){
        view = map;
    }



    public static GoogleJotiMap getJotiMapInstance(MapView map){
        if (!google_instances.containsKey(map)) {
            GoogleJotiMap jm = new GoogleJotiMap(map);
            google_instances.put(map,jm);
        }
        return google_instances.get(map);
    }

    @Override
    public void delete() {
        google_instances.remove(this.getGoogleMap());
    }

    @Override
    public void setInfoWindowAdapter(CustomInfoWindowAdapter infoWindowAdapter) {
        googleMap.setInfoWindowAdapter(infoWindowAdapter) ;
    }

    @Override
    public void setGMapType(int mapType) {
        googleMap.setMapType(mapType);
    }

    @Override
    public boolean setMapStyle(MapStyleOptions mapStyleOptions) {
                return googleMap.setMapStyle(mapStyleOptions);
    }

    @Override
    public IUiSettings getUiSettings() {
        return new GoogleUiSettings(googleMap.getUiSettings()) ;
    }

    @Override
    public void animateCamera(LatLng latLng, int zoom)  {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom)) ;
    }

    @Override
    public IMarker addMarker(Pair<MarkerOptions, Bitmap> markerOptions) {
        if (markerOptions.second != null){
            markerOptions.first.icon(BitmapDescriptorFactory.fromBitmap(markerOptions.second));
        }else{
            markerOptions.first.icon(BitmapDescriptorFactory.defaultMarker());
        }
        return new GoogleMarker(googleMap.addMarker(markerOptions.first));

    }

    @Override
    public IPolyline addPolyline(PolylineOptions polylineOptions) {
        return new GooglePolyline(googleMap.addPolyline(polylineOptions));
    }

    @Override
    public IPolygon addPolygon(PolygonOptions polygonOptions) {
        return new GooglePolygon(googleMap.addPolygon(polygonOptions));
    }

    @Override
    public ICircle addCircle(CircleOptions circleOptions) {
        return new GoogleCircle(googleMap.addCircle(circleOptions));

    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    @Override
    public void setOnMapClickListener(final OnMapClickListener onMapClickListener) {
        if (onMapClickListener == null){
            googleMap.setOnMapClickListener(null);
        }else {
            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    onMapClickListener.onMapClick(latLng);
                }
            });
        }
    }

    @Override
    public ICameraPosition getCameraPosition() {
        return new GoogleCameraPosition(googleMap.getCameraPosition());
    }

    @Override
    public void snapshot(final IJotiMap.SnapshotReadyCallback snapshotReadyCallback) {
        if (snapshotReadyCallback != null) {
            googleMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                @Override
                public void onSnapshotReady(Bitmap bitmap) {
                    snapshotReadyCallback.onSnapshotReady(bitmap);
                }
            });
        } else {
            googleMap.snapshot(null);
        }
    }

    @Override
    public void animateCamera(LatLng latLng,int zoom, final IJotiMap.CancelableCallback cancelableCallback) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        if (cancelableCallback != null) {
            googleMap.animateCamera(cameraUpdate, new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    cancelableCallback.onFinish();
                }

                @Override
                public void onCancel() {
                    cancelableCallback.onCancel();
                }
            });
        } else {
            googleMap.animateCamera(cameraUpdate, null);
        }
    }

    @Override
    public void setOnCameraMoveStartedListener(final GoogleMap.OnCameraMoveStartedListener onCameraMoveStartedListener) {
        if (onCameraMoveStartedListener != null) {
            googleMap.setOnCameraMoveStartedListener(onCameraMoveStartedListener);
        }else{
            googleMap.setOnCameraMoveStartedListener(null);
        }
    }

    @Override
    public void cameraToLocation(boolean b, Location location, float zoom, float aoa, float bearing) {
        CameraUtils.cameraToLocation(b, googleMap, location, zoom, aoa, bearing) ;
    }

    @Override
    public void clear() {
        googleMap.clear();
    }

    @Override
    public void setOnInfoWindowLongClickListener(GoogleMap.OnInfoWindowLongClickListener onInfoWindowLongClickListener) {
        if (onInfoWindowLongClickListener == null){
            googleMap.setOnInfoWindowLongClickListener(null);
        }else {
            googleMap.setOnInfoWindowLongClickListener(onInfoWindowLongClickListener);
        }
    }

    @Override
    public void setMarkerOnClickListener(OnMarkerClickListener listener) {
        GoogleMarker.setAllOnClickLister(listener);
    }

    @Override
    public void getMapAsync(final OnMapReadyCallback callback) {
        final IJotiMap t = this;
        view.getMapAsync(new com.google.android.gms.maps.OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;
                callback.onMapReady(t);
            }
        });
    }
}
