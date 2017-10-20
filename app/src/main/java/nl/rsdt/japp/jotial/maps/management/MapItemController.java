package nl.rsdt.japp.jotial.maps.management;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nl.rsdt.japp.application.Japp;
import nl.rsdt.japp.application.JappPreferences;
import nl.rsdt.japp.jotial.BundleIdentifiable;
import nl.rsdt.japp.jotial.Identifiable;
import nl.rsdt.japp.jotial.IntentCreatable;
import nl.rsdt.japp.jotial.Recreatable;
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
import nl.rsdt.japp.jotial.maps.wrapper.ICircle;
import nl.rsdt.japp.jotial.maps.wrapper.IJotiMap;
import nl.rsdt.japp.jotial.maps.wrapper.IMarker;
import nl.rsdt.japp.jotial.maps.wrapper.IPolygon;
import nl.rsdt.japp.jotial.maps.wrapper.IPolyline;
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
        MapItemHolder, MapItemUpdatable<I>, Transducable<I, O>, Identifiable, StorageIdentifiable, BundleIdentifiable,
        AsyncTransduceTask.OnTransduceCompletedCallback<O>, IntentCreatable, Searchable, Callback<I>, Mergable<O>, SharedPreferences.OnSharedPreferenceChangeListener{

    public static final String TAG = "MapItemController";

    protected IJotiMap jotiMap;

    protected ArrayList<IMarker> markers = new ArrayList<>();

    @Override
    public ArrayList<IMarker> getMarkers() {
        return markers;
    }

    protected ArrayList<IPolyline> polylines = new ArrayList<>();

    public ArrayList<IPolyline> getPolylines() {
        return polylines;
    }

    protected ArrayList<IPolygon> polygons = new ArrayList<>();

    @Override
    public ArrayList<IPolygon> getPolygons() {
        return polygons;
    }

    public Map<ICircle, Integer> circles = new HashMap<>();

    @Override
    public ArrayList<ICircle> getCircles() {
        return new ArrayList<>(circles.keySet());
    }

    protected O buffer;

    protected boolean visiblity = true;

    @Override
    public void onIntentCreate(Bundle bundle) {
        if(bundle != null) {
            O result = bundle.getParcelable(getBundleId());
            if(result != null) {
                if(jotiMap != null) {
                    processResult(result);
                } else {
                    buffer = result;
                }
            }
        }
    }


    public void onMapReady(IJotiMap jotiMap) {
        this.jotiMap = jotiMap;
        if(buffer != null)
        {
            if(!markers.isEmpty() || !polylines.isEmpty() || !polygons.isEmpty()) {
                merge(buffer);
            } else {
                processResult(buffer);
            }
            buffer = null;
        }
        JappPreferences.getVisiblePreferences().registerOnSharedPreferenceChangeListener(this);
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
        if(jotiMap != null)
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
        ArrayList<Pair<MarkerOptions, Bitmap>> markers = result.getMarkers();
        IMarker marker;
        for(int m = 0; m < markers.size(); m++)
        {
            marker = jotiMap.addMarker(markers.get(m));
            marker.setVisible(visiblity);
            this.markers.add(marker);
        }

        ArrayList<PolylineOptions> polylines = result.getPolylines();
        IPolyline polyline;
        for(int p = 0; p < polylines.size(); p++)
        {
            polyline = jotiMap.addPolyline(polylines.get(p));
            polyline.setVisible(visiblity);
            this.polylines.add(polyline);
        }

        ArrayList<PolygonOptions> polygons = result.getPolygons();
        IPolygon polygon;
        for(int g = 0; g < polygons.size(); g++)
        {
            polygon = jotiMap.addPolygon(polygons.get(g));
            polygon.setVisible(visiblity);
            this.polygons.add(polygon);
        }

        ArrayList<CircleOptions> circles = result.getCircles();
        for(int c = 0; c < circles.size(); c++)
        {
            ICircle circle = jotiMap.addCircle(circles.get(c));
            circle.setVisible(visiblity);
            this.circles.put(circle, circle.getFillColor());
            if (!JappPreferences.fillCircles()){
                circle.setFillColor(Color.TRANSPARENT);
            }
        }
    }

    public void setVisiblity(boolean visiblity) {
        this.visiblity = visiblity;

        for(int i = 0; i < markers.size(); i++) {
            markers.get(i).setVisible(visiblity);
        }

        for(int i = 0; i < polylines.size(); i++) {
            polylines.get(i).setVisible(visiblity);
        }

        for(int i = 0; i < polygons.size(); i++) {
            polygons.get(i).setVisible(visiblity);
        }
        ArrayList<ICircle> circles = new ArrayList(this.circles.keySet());
        for(int i = 0; i < circles.size(); i++) {
            circles.get(i).setVisible(visiblity);
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
        ArrayList<ICircle> circles = new ArrayList(this.circles.keySet());
        for(int i = 0; i < circles.size(); i++) {
            circles.get(i).remove();
        }
        circles.clear();

    }


    @Override
    public IMarker searchFor(String query) {
        IMarker marker;
        for(int i = 0; i < markers.size(); i++) {
            marker = markers.get(i);
            if(marker.getTitle().equals(String.valueOf(query))) {
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

        jotiMap = null;

    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key){
        if (key.equals(JappPreferences.FILL_CIRCLES)){
            if (JappPreferences.fillCircles()){
                for (Map.Entry<ICircle, Integer> entry: circles.entrySet()){
                    entry.getKey().setFillColor(entry.getValue());
                }
            }else{
                for (Map.Entry<ICircle, Integer> entry: circles.entrySet()){
                    entry.getKey().setFillColor(Color.TRANSPARENT);
                }
            }
        }
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
