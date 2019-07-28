package nl.rsdt.japp.jotial.maps.wrapper;

import android.graphics.Bitmap;
import android.location.Location;
import android.util.Pair;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import nl.rsdt.japp.jotial.maps.window.CustomInfoWindowAdapter;

/**
 * Created by mattijn on 07/08/17.
 */

public interface IJotiMap {

    void delete();

    void setInfoWindowAdapter(CustomInfoWindowAdapter infoWindowAdapter);

    void setGMapType(int mapType);

    boolean setMapStyle(MapStyleOptions mapStyleOptions);

    IUiSettings getUiSettings();

    void animateCamera(LatLng latLng, int zoom);

    IMarker addMarker(Pair<MarkerOptions, Bitmap> markerOptions);

    IPolyline addPolyline(PolylineOptions polylineOptions);

    IPolygon addPolygon(PolygonOptions polygonOptions);

    ICircle addCircle(CircleOptions circleOptions);

    void setOnMapClickListener(final OnMapClickListener onMapClickListener);

    ICameraPosition getPreviousCameraPosition();

    void snapshot(final IJotiMap.SnapshotReadyCallback snapshotReadyCallback);

    void animateCamera(LatLng latLng, int zoom, final IJotiMap.CancelableCallback cancelableCallback);

    void setOnCameraMoveStartedListener(final GoogleMap.OnCameraMoveStartedListener onCameraMoveStartedListener);

    void cameraToLocation(boolean b, Location location, float zoom, float aoa, float bearing);

    void clear();

    void setOnInfoWindowLongClickListener(GoogleMap.OnInfoWindowLongClickListener onInfoWindowLongClickListener);

    void setMarkerOnClickListener(IJotiMap.OnMarkerClickListener listener);

    void getMapAsync(IJotiMap.OnMapReadyCallback callback);

    void setPreviousCameraPosition(double latitude, double longitude);

    void setPreviousZoom(int zoom);

    void setPreviousRotation(float rotation);

    interface OnMapReadyCallback {
        void onMapReady(IJotiMap map);
    }

    interface OnMarkerClickListener {
        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        boolean OnClick(IMarker m);
    }

    interface OnMapClickListener {

        boolean onMapClick(LatLng latLng);
    }

    interface CancelableCallback {
        void onFinish();

        void onCancel();
    }

    interface SnapshotReadyCallback {
        void onSnapshotReady(Bitmap var1);
    }
}
