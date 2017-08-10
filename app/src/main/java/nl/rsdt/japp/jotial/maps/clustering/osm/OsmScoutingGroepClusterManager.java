package nl.rsdt.japp.jotial.maps.clustering.osm;

import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.rsdt.japp.R;
import nl.rsdt.japp.application.Japp;
import nl.rsdt.japp.jotial.data.structures.area348.ScoutingGroepInfo;
import nl.rsdt.japp.jotial.maps.clustering.ClusterManagerInterface;
import nl.rsdt.japp.jotial.maps.clustering.ScoutingGroepRenderer;
import nl.rsdt.japp.jotial.maps.wrapper.Circle;
import nl.rsdt.japp.jotial.maps.wrapper.JotiMap;

/**
 * Created by mattijn on 10/08/17.
 */

public class OsmScoutingGroepClusterManager implements ClusterManagerInterface {


    private RadiusMarkerClusterer scoutingGroepMarkers;
    private final Collection<ScoutingGroepInfo> infos;
    private final Collection<Marker> markers;
    private final MapView osmMap;

    public OsmScoutingGroepClusterManager(JotiMap jotiMap) {
        scoutingGroepMarkers = new RadiusMarkerClusterer(Japp.getInstance().getApplicationContext());
        if (jotiMap.getMapType() != JotiMap.OSMMAPTYPE){
            throw new RuntimeException("this class can only be usd with osm");
        }
        this.osmMap = jotiMap.getOSMMap();
        infos = new ArrayList<>();
        markers = new ArrayList<>();
        osmMap.getOverlays().add(scoutingGroepMarkers);
    }

    @Override
    public void addItems(final ArrayList<ScoutingGroepInfo> buffer) {
        for (final ScoutingGroepInfo info : buffer){
            Marker marker = new Marker(osmMap);
            marker.setIcon(Japp.getInstance().getResources().getDrawable( R.drawable.scouting_groep_icon_30x22 ));
            marker.setPosition(new GeoPoint(info.getPosition().latitude,info.getPosition().longitude));
            StringBuilder buff = new StringBuilder();
            buff.append(info.naam).append("\n");
            buff.append(info.team).append("\n");
            buff.append(info.adres).append("\n");
            marker.setTitle(buff.toString());
            marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                Circle circle = null;
                boolean visible = false;
                @Override
                public boolean onMarkerClick(Marker marker, MapView mapView) {
                    if (circle == null) {
                        circle = new Circle(new CircleOptions()
                                .center(new LatLng(info.getPosition().latitude, info.getPosition().longitude))
                                .radius(500)
                                .fillColor(Color.argb(80, 200, 200, 200))
                                , mapView);
                        visible = true;
                    }else {
                        if (visible){
                            circle.setRadius(0);
                            visible = false;
                        }else {
                            circle.setRadius(500);
                            visible = true;
                        }
                    }
                    marker.showInfoWindow();
                    return true;
                }
            });
            scoutingGroepMarkers.add(marker);
            infos.add(info);
            markers.add(marker);
        }
    }

    @Override
    public void cluster() {

    }

    @Override
    public Collection<ScoutingGroepInfo> getItems() {
        return infos;
    }

    @Override
    public void clearItems() {
        osmMap.getOverlays().remove(scoutingGroepMarkers);
        infos.clear();
        markers.clear();
        scoutingGroepMarkers = new RadiusMarkerClusterer(Japp.getInstance().getApplicationContext());
        osmMap.getOverlays().add(scoutingGroepMarkers);
    }
}
