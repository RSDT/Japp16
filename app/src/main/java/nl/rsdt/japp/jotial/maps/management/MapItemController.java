package nl.rsdt.japp.jotial.maps.management;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import nl.rsdt.japp.application.activities.MainActivity;
import nl.rsdt.japp.jotial.BundleIdentifiable;
import nl.rsdt.japp.jotial.Identifiable;
import nl.rsdt.japp.jotial.IntentCreatable;
import nl.rsdt.japp.jotial.Recreatable;
import nl.rsdt.japp.jotial.data.structures.area348.BaseInfo;
import nl.rsdt.japp.jotial.io.StorageIdentifiable;
import nl.rsdt.japp.jotial.maps.MapItemHolder;
import nl.rsdt.japp.jotial.maps.management.controllers.AlphaVosController;
import nl.rsdt.japp.jotial.maps.management.controllers.BravoVosController;
import nl.rsdt.japp.jotial.maps.management.controllers.CharlieVosController;
import nl.rsdt.japp.jotial.maps.management.controllers.DeltaVosController;
import nl.rsdt.japp.jotial.maps.management.controllers.EchoVosController;
import nl.rsdt.japp.jotial.maps.management.controllers.FotoOpdrachtController;
import nl.rsdt.japp.jotial.maps.management.controllers.FoxtrotVosController;
import nl.rsdt.japp.jotial.maps.management.controllers.HunterController;
import nl.rsdt.japp.jotial.maps.management.controllers.XrayVosController;
import nl.rsdt.japp.jotial.maps.management.transformation.AbstractTransducerResult;
import nl.rsdt.japp.jotial.maps.management.transformation.Transducable;
import nl.rsdt.japp.jotial.maps.management.transformation.async.OnTransduceCompletedCallback;
import nl.rsdt.japp.jotial.maps.searching.Searchable;
import nl.rsdt.japp.jotial.net.ApiGetJsonArrayRequest;
import nl.rsdt.japp.service.cloud.data.UpdateInfo;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Description...
 */
public abstract class MapItemController<T> extends MapItemDateControl implements Recreatable,
        MapItemHolder<T>, MapItemUpdatable, Transducable<T>, Identifiable, StorageIdentifiable, BundleIdentifiable, OnMapReadyCallback,
        OnTransduceCompletedCallback<T>, IntentCreatable, Searchable, Response.Listener<JSONArray>, Response.ErrorListener {

    public static final String TAG = "MapItemController";

    protected GoogleMap googleMap;

    protected ArrayList<T> items = new ArrayList<>();

    @Override
    public ArrayList<T> getItems() {
        return items;
    }

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

    protected AbstractTransducerResult<T> buffer;

    @Override
    public void onIntentCreate(Bundle bundle) {
        if(bundle != null) {
            AbstractTransducerResult<T> result = bundle.getParcelable(getBundleId());
            if(result != null) {
                this.items = result.getItems();
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
            processResult(buffer);
            buffer = null;
        }
    }

    @Override
    public void onResponse(JSONArray response) {
        lastUpdate = Calendar.getInstance();
        getTransducer().executeAsync(response.toString(), this);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e(TAG, error.toString(), error);
    }

    @Override
    public void onTransduceCompleted(AbstractTransducerResult<T> result) {
        if(this.items.isEmpty()) {
            this.items = result.getItems();
        } else  {

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

            this.items.clear();
            this.items = result.getItems();
        }

        if(googleMap != null)
        {
            processResult(result);
        }
        else
        {
            buffer = result;
        }
    }

    protected void processResult(AbstractTransducerResult<T> result)
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
    public void onUpdateInvoked(RequestQueue queue) {
        queue.add(new ApiGetJsonArrayRequest(getUrlByAssociatedMode(MODE_ALL), this, this));
    }

    @Override
    public void onUpdateMessage(RequestQueue queue, UpdateInfo info) {
        /**
         * TODO: Discussion on MODE with @Area348
         * */
        Request request;
        String mode;
        switch (info.action) {
            case UpdateInfo.ACTION_NEW:
                mode = MODE_ALL;
                break;
            case UpdateInfo.ACTION_UPDATE:
                mode = MODE_ALL;
                break;
            default:
                mode = null;
                break;
        }

        if(mode != null) {
            request = new ApiGetJsonArrayRequest(getUrlByAssociatedMode(mode), this, this);
            request.setTag(MainActivity.TAG);
            queue.add(request);
        }

    }


    @Override
    public void onDestroy() {

        if(items != null)
        {
            /**
             * Do not clear the list or the parcelable list inside the bundle will be empty as well
             * */
            items = null;
        }

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
