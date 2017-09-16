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

;

/**
 * Created by mattijn on 07/08/17.
 */

public interface IJotiMap {

    public void delete();

    public void setInfoWindowAdapter(CustomInfoWindowAdapter infoWindowAdapter);

    public void setGMapType(int mapType);

    public boolean setMapStyle(MapStyleOptions mapStyleOptions);

    public IUiSettings getUiSettings();

    public void animateCamera(LatLng latLng, int zoom) ;

    public IMarker addMarker(Pair<MarkerOptions, Bitmap> markerOptions) ;

    public IPolyline addPolyline(PolylineOptions polylineOptions) ;

    public IPolygon addPolygon(PolygonOptions polygonOptions) ;

    public ICircle addCircle(CircleOptions circleOptions) ;

    public void setOnMapClickListener(final OnMapClickListener onMapClickListener) ;

    public ICameraPosition getPreviousCameraPosition() ;

    public void snapshot(final IJotiMap.SnapshotReadyCallback snapshotReadyCallback) ;

    public void animateCamera(LatLng latLng,int zoom, final IJotiMap.CancelableCallback cancelableCallback) ;

    public void setOnCameraMoveStartedListener(final GoogleMap.OnCameraMoveStartedListener onCameraMoveStartedListener) ;

    public void cameraToLocation(boolean b, Location location, float zoom, float aoa, float bearing) ;

    public void clear() ;

    public void setOnInfoWindowLongClickListener(GoogleMap.OnInfoWindowLongClickListener onInfoWindowLongClickListener) ;

    public void setMarkerOnClickListener(IJotiMap.OnMarkerClickListener listener) ;

    public void getMapAsync(IJotiMap.OnMapReadyCallback callback) ;

    void setPreviousCameraPosition(double latitude, double longitude);

    void setPreviousZoom(int zoom);

    void setPreviousRotation(float rotation);

    public interface OnMapReadyCallback{
        public void onMapReady(IJotiMap map) ;
    }
    public interface OnMarkerClickListener {
        public boolean OnClick(IMarker m);
    }

    public interface OnMapClickListener{

        boolean onMapClick(LatLng latLng);
    }

    public interface CancelableCallback {
        void onFinish();

        void onCancel();
    }
    public interface SnapshotReadyCallback {
        void onSnapshotReady(Bitmap var1);
    }
}
