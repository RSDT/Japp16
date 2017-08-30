package nl.rsdt.japp.jotial.navigation;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;

import nl.rsdt.japp.application.Japp;
import nl.rsdt.japp.jotial.maps.wrapper.IJotiMap;
import nl.rsdt.japp.jotial.maps.wrapper.IPolyline;

/**
 * Created by mattijn on 16/08/17.
 */

public class Navigator {
    private final IJotiMap map;
    private Handler onFinishedHandler;
    private IPolyline oldPolyline;
    public Navigator(final IJotiMap map){
        this.map = map;
        onFinishedHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                if (msg.obj instanceof Polyline){
                    setPolyline((Polyline) msg.obj);
                }
            }
        };
    }

    public void setEndLocation(LatLng end){
        if (Japp.getLastLocation() != null && end != null){
            RouteCalculator r = new RouteCalculator(this);
            Location start = Japp.getLastLocation();
            AsyncTask<LatLng, Void, Polyline> t = r.execute(new LatLng(start.getLatitude(), start.getLongitude()), end);
        }
    }
    public void clear(){
        oldPolyline.remove();
    }
    public void setPolyline(Polyline newPolyline){
        if (oldPolyline != null){
            oldPolyline.remove();
        }
        if (newPolyline == null){
            return;
        }

        PolylineOptions options = new PolylineOptions()
                .color(newPolyline.getColor())
                .visible(newPolyline.isVisible())
                .width(newPolyline.getWidth())
                ;
        for (GeoPoint p : newPolyline.getPoints()){
            options.add(new LatLng(p.getLatitude(), p.getLongitude()));
        }
        oldPolyline = map.addPolyline(options);
    }

    public void onFinished(Polyline newPolyline) {
        onFinishedHandler.sendMessage(Message.obtain(onFinishedHandler, 0, newPolyline));
    }

    static class RouteCalculator extends AsyncTask<LatLng,Void, Polyline>{

        private Navigator callback;

        public RouteCalculator(Navigator onFinished){
            callback= onFinished;
        }
        @Override
        protected Polyline doInBackground(LatLng... params) {
            RoadManager roadManager = new OSRMRoadManager(Japp.getInstance().getApplicationContext());
            ArrayList<GeoPoint> waypoints = new ArrayList<>();
            for (LatLng p : params){
                waypoints.add(new GeoPoint(p.latitude,p.longitude));
            }
            Road road = roadManager.getRoad(waypoints);
            Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
            callback.onFinished(roadOverlay);
            return roadOverlay;
        }

        interface OnFinished {
            void onFinished(Polyline p);
        }
    }
}
