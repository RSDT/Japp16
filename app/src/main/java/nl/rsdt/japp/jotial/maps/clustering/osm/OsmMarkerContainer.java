package nl.rsdt.japp.jotial.maps.clustering.osm;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Pair;

import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.osmdroid.bonuspack.clustering.MarkerClusterer;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.views.overlay.Marker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.rsdt.japp.R;
import nl.rsdt.japp.application.Japp;
import nl.rsdt.japp.jotial.data.structures.area348.ScoutingGroepInfo;
import nl.rsdt.japp.jotial.maps.management.MarkerIdentifier;
import nl.rsdt.japp.jotial.maps.wrapper.ICircle;
import nl.rsdt.japp.jotial.maps.wrapper.IJotiMap;
import nl.rsdt.japp.jotial.maps.wrapper.IMarker;
import nl.rsdt.japp.jotial.maps.wrapper.osm.OsmJotiMap;
import nl.rsdt.japp.jotial.maps.wrapper.osm.OsmMarker;

/**
 * Created by mattijn on 01/10/17.
 */

public class OsmMarkerContainer {
    private final OsmJotiMap map;
    private final Map<String, MarkerClusterer> markers;

    public OsmMarkerContainer(OsmJotiMap map){
        this.map = map;
        markers = new HashMap<>();
    }

    public IMarker add(final ScoutingGroepInfo info){
        MarkerOptions options = new MarkerOptions();
        Bitmap bm = BitmapFactory.decodeResource(Japp.getAppResources(), R.drawable.scouting_groep_icon_30x22 );
        options.position(info.getPosition());

        MarkerIdentifier identifier = new MarkerIdentifier.Builder()
                .setType(MarkerIdentifier.TYPE_SC)
                .add("name", info.naam)
                .add("adres", info.adres)
                .add("team", info.team)
                .create();
        options.title(new Gson().toJson(identifier));

        IMarker marker = map.addMarker(new Pair<MarkerOptions, Bitmap>(options, bm));
        marker.remove();
        marker.setOnClickListener(new IMarker.OnClickListener() {
            @Override
            public boolean OnClick(IMarker m) {
                if (circle == null) {
                    circle = map.addCircle(new CircleOptions()
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
        this.add(info.team, ((OsmMarker) marker).getOSMMarker());
        return marker;
    }

    private void add(String key, Marker osmMarker) {
        if (!markers.containsKey(key)){
            markers.put(key, new RadiusMarkerClusterer(Japp.getInstance().getApplicationContext()));
            map.getOSMMap().getOverlays().add(markers.get(key));
        }
        markers.get(key).add(osmMarker);
    }

    public void clear() {
        for (MarkerClusterer markerClusterer : markers.values()){
            map.getOSMMap().getOverlays().remove(markerClusterer);
        }
        markers.clear();
    }
}
