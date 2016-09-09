package nl.rsdt.japp.jotial.maps.management;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


import java.util.ArrayList;
import nl.rsdt.japp.jotial.BundleIdentifiable;
import nl.rsdt.japp.jotial.Identifiable;
import nl.rsdt.japp.jotial.IntentCreatable;
import nl.rsdt.japp.jotial.Recreatable;
import nl.rsdt.japp.jotial.data.structures.area348.BaseInfo;
import nl.rsdt.japp.jotial.io.StorageIdentifiable;
import nl.rsdt.japp.jotial.maps.MapItemHolder;
import nl.rsdt.japp.jotial.maps.Mergable;
import nl.rsdt.japp.jotial.maps.management.controllers.AlphaVosController;
import nl.rsdt.japp.jotial.maps.management.controllers.BravoVosController;
import nl.rsdt.japp.jotial.maps.management.controllers.CharlieVosController;
import nl.rsdt.japp.jotial.maps.management.controllers.DeltaVosController;
import nl.rsdt.japp.jotial.maps.management.controllers.EchoVosController;
import nl.rsdt.japp.jotial.maps.management.controllers.FotoOpdrachtController;
import nl.rsdt.japp.jotial.maps.management.controllers.FoxtrotVosController;
import nl.rsdt.japp.jotial.maps.management.controllers.HunterController;
import nl.rsdt.japp.jotial.maps.management.controllers.XrayVosController;
import nl.rsdt.japp.jotial.maps.management.transformation.AbstractTransducer;
import nl.rsdt.japp.jotial.maps.management.transformation.Transducable;
import nl.rsdt.japp.jotial.maps.management.transformation.async.AsyncTransduceTask;
import nl.rsdt.japp.jotial.maps.searching.Searchable;
import nl.rsdt.japp.service.cloud.data.UpdateInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Description...
 */
public abstract class MapItemController<I, O extends AbstractTransducer.Result> extends MapItemDateControl implements Recreatable,
        MapItemHolder, MapItemUpdatable<I>, Transducable<I, O>, Identifiable, StorageIdentifiable, BundleIdentifiable, OnMapReadyCallback,
        AsyncTransduceTask.OnTransduceCompletedCallback<O>, IntentCreatable, Searchable, Callback<I>, Mergable<O> {

    public static final String TAG = "MapItemController";

    protected GoogleMap googleMap;

    protected ArrayList<Marker> markers = new ArrayList<>();

    @Override
    public ArrayList<Marker> getMarkers() {
        return markers;
    }

    protected ArrayList<Polyline> polylines = new ArrayList<>();

    @Override
    public ArrayList<Polyline> getPolylines() {
        return polylines;
    }

    protected ArrayList<Polygon> polygons = new ArrayList<>();

    @Override
    public ArrayList<Polygon> getPolygons() {
        return polygons;
    }

    protected O buffer;

    @Override
    public void onIntentCreate(Bundle bundle) {
        if(bundle != null) {
            O result = bundle.getParcelable(getBundleId());
            if(result != null) {
                if(googleMap != null) {
                    processResult(result);
                } else {
                    buffer = result;
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if(buffer != null)
        {
            if(!markers.isEmpty() || !polylines.isEmpty() || !polygons.isEmpty()) {
                merge(buffer);
            } else {
                processResult(buffer);
            }
            buffer = null;
        }
    }

    @Override
    public void onUpdateInvoked() {
        Call<I> call = update(MODE_ALL);
        if(call != null) {
            call.enqueue(this);
        }
    }

    @Override
    public void onUpdateMessage(UpdateInfo info) {
        Call<I> call;
        switch (info.action) {
            case UpdateInfo.ACTION_NEW:
                call = update(MODE_ALL);
                break;
            case UpdateInfo.ACTION_UPDATE:
                call = update(MODE_ALL);
                break;
            default:
                call = null;
                break;
        }
        if(call != null) {
            call.enqueue(this);
        }
    }

    @Override
    public void onResponse(Call<I> call, Response<I> response) {
        getTransducer().enqueue(response.body(), this);
    }

    @Override
    public void onFailure(Call<I> call, Throwable t) {
        Log.e(TAG, t.toString(), t);
    }

    @Override
    public void onTransduceCompleted(O result) {
        if(googleMap != null)
        {
            if(!markers.isEmpty() || !polylines.isEmpty() || !polygons.isEmpty()) {merge(result);
            } else {
                processResult(result);
            }
        }
        else
        {
            buffer = result;
        }
    }

    protected void processResult(O result)
    {
        ArrayList<MarkerOptions> markers = result.getMarkers();
        for(int m = 0; m < markers.size(); m++)
        {
            this.markers.add(googleMap.addMarker(markers.get(m)));
        }

        ArrayList<PolylineOptions> polylines = result.getPolylines();
        for(int p = 0; p < polylines.size(); p++)
        {
            this.polylines.add(googleMap.addPolyline(polylines.get(p)));
        }

        ArrayList<PolygonOptions> polygons = result.getPolygons();
        for(int g = 0; g < polygons.size(); g++)
        {
            this.polygons.add(googleMap.addPolygon(polygons.get(g)));
        }
    }

    protected void clear() {
        for(int i = 0; i < markers.size(); i++) {
            markers.get(i).remove();
        }
        markers.clear();

        for(int i = 0; i < polylines.size(); i++) {
            polylines.get(i).remove();
        }
        polylines.clear();

        for(int i = 0; i < polygons.size(); i++) {
            polygons.get(i).remove();
        }
        polygons.clear();
    }


    @Override
    public Marker getAssociatedMarker(BaseInfo info) {
        Marker marker;
        for(int i = 0; i < markers.size(); i++) {
            marker = markers.get(i);
            if(marker.getTitle().equals(String.valueOf(info.id))) {
                return marker;
            }
        }
        return null;
    }


    @Override
    public void onDestroy() {

        if(markers != null)
        {
            markers.clear();
            markers = null;
        }

        if(polylines != null)
        {
            polylines.clear();
            polylines = null;
        }

        if(polygons != null)
        {
            polygons.clear();
            polygons = null;
        }

        buffer = null;

        googleMap = null;

    }

    public static MapItemController[] getAll() {
        return new MapItemController[] {
                new FotoOpdrachtController(),
                new HunterController(),
                new AlphaVosController(),
                new BravoVosController(),
                new CharlieVosController(),
                new DeltaVosController(),
                new EchoVosController(),
                new FoxtrotVosController(),
                new XrayVosController()
        };
    }
}
