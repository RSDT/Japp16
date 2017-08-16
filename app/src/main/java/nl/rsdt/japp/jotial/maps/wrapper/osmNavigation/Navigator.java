package nl.rsdt.japp.jotial.maps.wrapper.osmNavigation;

import android.os.Handler;
import android.os.Message;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;


import nl.rsdt.japp.application.Japp;
import nl.rsdt.japp.jotial.maps.wrapper.JotiMap;

/**
 * Created by mattijn on 16/08/17.
 */

public class Navigator extends Thread {
    private Handler handler;
    private GeoPoint endLocation;
    private GeoPoint startLocation;

    public Navigator(final JotiMap map){
        startLocation = null;
        endLocation = startLocation;
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                Polyline polyline = (Polyline) msg.obj;
                PolylineOptions options = new PolylineOptions()
                        .color(polyline.getColor())
                        .visible(polyline.isVisible())
                        .width(polyline.getWidth())
                        ;
                for (GeoPoint p : polyline.getPoints()){
                    options.add(new LatLng(p.getLatitude(), p.getLongitude()));
                }
                map.addPolyline(options);

            }
        };
    }
    public void setEndLocation(GeoPoint end){
        startLocation = endLocation;
        endLocation = end;
    }
    @Override
    public void run(){
        if (startLocation == null ||  endLocation == null){
            startLocation = new GeoPoint(Japp.getLastLocation());
            endLocation = startLocation;
        }
        RoadManager roadManager = new OSRMRoadManager(Japp.getInstance().getApplicationContext());
        ArrayList<GeoPoint> waypoints = new ArrayList<>();
        waypoints.add(startLocation);
        waypoints.add(endLocation);
        Road road = roadManager.getRoad(waypoints);
        Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
        handler.sendMessage(Message.obtain(handler,0, roadOverlay));
    }
}
