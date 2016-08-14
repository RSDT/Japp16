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
import com.rsdt.anl.RequestPool;
import com.rsdt.anl.WebResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;

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
import nl.rsdt.japp.service.cloud.data.UpdateInfo;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Description...
 */
public abstract class MapItemController<T> extends MapItemRequestListener implements Recreatable,
        MapItemHolder<T>, MapItemUpdatable, Transducable<T>, Identifiable, StorageIdentifiable, BundleIdentifiable, OnMapReadyCallback,
        OnTransduceCompletedCallback<T>, IntentCreatable, Searchable {

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

    public void initialize(RequestPool pool) {
        pool.addListener(this);
    }

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
    public void onWebRequestCompleted(WebResponse response) {
        getTransducer().executeAsync(response.getData(), this);
    }

    @Override
    public void onTransduceCompleted(AbstractTransducerResult<T> result) {
        if(this.items.isEmpty()) {
            this.items = result.getItems();
        } else  {

            ArrayList<Integer> ids = new ArrayList<>();
            BaseInfo info;
            for(int i = 0; i < result.getItems().size(); i++){
                info = (BaseInfo)result.getItems().get(i);

                BaseInfo buffer;
                for(int x = 0; x < this.items.size(); x++) {
                    buffer = (BaseInfo) items.get(x);
                    if(buffer.id == info.id) {
                        items.add(x, (T)info);
                        items.remove(buffer);
                        ids.add(info.id);
                    }
                }
            }

            int id;
            Marker marker;
            for(int i = 0; i < ids.size(); i++) {
                id = ids.get(i);
                Iterator<Marker> iterator = markers.iterator();
                while(iterator.hasNext()) {
                    marker = iterator.next();
                    if(marker.getTitle().equals(String.valueOf(id))) {
                        iterator.remove();
                        marker.remove();
                    }
                }
            }

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
    public void onUpdateInvoked(RequestPool pool) {
        /**
         * When the user invokes the update, all data should be fetched.
         * */
        pool.query(update(MODE_ALL));
    }

    @Override
    public void onUpdateMessage(RequestPool pool, UpdateInfo info) {
        Calendar cal = Calendar.getInstance();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ROOT);
            cal.setTime(sdf.parse(info.dateOfCreation));
        } catch (ParseException e) {
            Log.e(TAG, e.toString(), e); }

        /**
         * Check if the date of creation is before the date of the last update
         * */
        if(cal.before(lastUpdate)) {
            /**
             * This means we need to fetch all the data
             * */
            update(MODE_ALL);
        } else {

            /**
             * Fetch only the latest data, the new item should be among the data.
             * */
            update(MODE_LATEST);
        }
    }


    @Override
    public void onDestroy() {

        if(items != null)
        {
            items.clear();
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
