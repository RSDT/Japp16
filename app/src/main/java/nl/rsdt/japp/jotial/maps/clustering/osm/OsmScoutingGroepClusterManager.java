package nl.rsdt.japp.jotial.maps.clustering.osm;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Pair;

import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.osmdroid.bonuspack.clustering.MarkerClusterer;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import java.util.ArrayList;
import java.util.Collection;

import nl.rsdt.japp.R;
import nl.rsdt.japp.application.Japp;
import nl.rsdt.japp.jotial.data.structures.area348.ScoutingGroepInfo;
import nl.rsdt.japp.jotial.maps.clustering.ClusterManagerInterface;
import nl.rsdt.japp.jotial.maps.wrapper.ICircle;
import nl.rsdt.japp.jotial.maps.wrapper.IJotiMap;
import nl.rsdt.japp.jotial.maps.wrapper.IMarker;
import nl.rsdt.japp.jotial.maps.wrapper.osm.OsmJotiMap;
import nl.rsdt.japp.jotial.maps.wrapper.osm.OsmMarker;

/**
 * Created by mattijn on 10/08/17.
 */

public class OsmScoutingGroepClusterManager implements ClusterManagerInterface {


    private MarkerClusterer scoutingGroepMarkers;
    private final Collection<ScoutingGroepInfo> infos;
    private final Collection<IMarker> markers;
    private final MapView osmMap;

    public OsmScoutingGroepClusterManager(IJotiMap jotiMap) {
        scoutingGroepMarkers = new RadiusMarkerClusterer(Japp.getInstance().getApplicationContext());
        if (!(jotiMap instanceof OsmJotiMap)){
            throw new RuntimeException("this class can only be usd with osm");
        }
        OsmJotiMap osmJotiMap = (OsmJotiMap) jotiMap;
        this.osmMap = osmJotiMap.getOSMMap();
        infos = new ArrayList<>();
        markers = new ArrayList<>();
        osmMap.getOverlays().add(scoutingGroepMarkers);
    }

    @Override
    public void addItems(final ArrayList<ScoutingGroepInfo> buffer) {
        for (final ScoutingGroepInfo info : buffer){
            MarkerOptions options = new MarkerOptions();
            Bitmap bm = BitmapFactory.decodeResource(Japp.getAppResources(), R.drawable.scouting_groep_icon_30x22 );
            options.position(info.getPosition());
            StringBuilder buff = new StringBuilder();
            buff.append(info.naam).append("\n");
            buff.append(info.team).append("\n");
            buff.append(info.adres).append("\n");
            options.title(buff.toString());
            final IJotiMap jm = OsmJotiMap.getJotiMapInstance(osmMap);
            IMarker marker = jm.addMarker(new Pair<MarkerOptions, Bitmap>(options, bm));
            marker.remove();
            marker.setOnClickListener(new IMarker.OnClickListener() {
                @Override
                public boolean OnClick(IMarker m) {
                    if (circle == null) {
                        circle = jm.addCircle(new CircleOptions()
                                .center(new LatLng(info.getPosition().latitude, info.getPosition().longitude))
                                .radius(500)
                                .fillColor(Color.argb(80, 200, 200, 200)));
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
                    m.showInfoWindow();
                    return true;
                }

                ICircle circle = null;
                boolean visible = false;
            });
            scoutingGroepMarkers.add(((OsmMarker) marker).getOSMMarker());
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
