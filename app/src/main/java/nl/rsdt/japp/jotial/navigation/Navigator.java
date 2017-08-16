package nl.rsdt.japp.jotial.navigation;

import android.os.Handler;
import android.os.Looper;
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
import nl.rsdt.japp.jotial.maps.wrapper.JotiMap;

/**
 * Created by mattijn on 16/08/17.
 */

public class Navigator extends Thread {
    private final Handler polylineHandler;
    private Handler endPointHandler;
    private nl.rsdt.japp.jotial.maps.wrapper.Polyline oldPolyline;
    public Navigator(final JotiMap map){
        polylineHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                Polyline newPolyline = (Polyline) msg.obj;
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
        };
    }

    public void setEndLocation(LatLng end){
        if (endPointHandler != null) {
            this.endPointHandler.sendMessage(Message.obtain(endPointHandler, 0, end));
        }
    }

    @Override
    public void run() {
        Looper.prepare();
        endPointHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                LatLng end = (LatLng) msg.obj;
                if (end == null) {
                    polylineHandler.sendMessage(Message.obtain(polylineHandler, 0, null));
                } else {
                    RoadManager roadManager = new OSRMRoadManager(Japp.getInstance().getApplicationContext());
                    ArrayList<GeoPoint> waypoints = new ArrayList<>();

                    waypoints.add(new GeoPoint(Japp.getLastLocation()));
                    waypoints.add(new GeoPoint(end.latitude,end.longitude));
                    Road road = roadManager.getRoad(waypoints);
                    Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
                    polylineHandler.sendMessage(Message.obtain(polylineHandler, 0, roadOverlay));
                }
            }
        };

    }
}
