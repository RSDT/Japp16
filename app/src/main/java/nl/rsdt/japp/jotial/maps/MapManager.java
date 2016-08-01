package nl.rsdt.japp.jotial.maps;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.rsdt.anl.RequestPool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import nl.rsdt.japp.application.activities.SplashActivity;
import nl.rsdt.japp.jotial.data.structures.area348.BaseInfo;
import nl.rsdt.japp.jotial.maps.management.MapItemController;
import nl.rsdt.japp.jotial.maps.management.controllers.AlphaVosController;
import nl.rsdt.japp.jotial.maps.management.controllers.BravoVosController;
import nl.rsdt.japp.jotial.maps.management.controllers.CharlieVosController;
import nl.rsdt.japp.jotial.maps.management.controllers.DeltaVosController;
import nl.rsdt.japp.jotial.maps.management.controllers.EchoVosController;
import nl.rsdt.japp.jotial.maps.management.controllers.FotoOpdrachtController;
import nl.rsdt.japp.jotial.maps.management.controllers.FoxtrotVosController;
import nl.rsdt.japp.jotial.maps.management.controllers.HunterController;
import nl.rsdt.japp.jotial.maps.management.controllers.XrayVosController;
import nl.rsdt.japp.jotial.maps.searching.Searchable;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 24-7-2016
 * Description...
 */
public class MapManager extends RequestPool implements OnMapReadyCallback, Searchable {

    private GoogleMap googleMap;

    private HashMap<String, MapItemController> controllers = new HashMap<>();

    public MapManager()
    {
        controllers.put(AlphaVosController.CONTROLLER_ID, new AlphaVosController());
        controllers.put(BravoVosController.CONTROLLER_ID, new BravoVosController());
        controllers.put(CharlieVosController.CONTROLLER_ID, new CharlieVosController());
        controllers.put(DeltaVosController.CONTROLLER_ID, new DeltaVosController());
        controllers.put(EchoVosController.CONTROLLER_ID, new EchoVosController());
        controllers.put(FoxtrotVosController.CONTROLLER_ID, new FoxtrotVosController());
        controllers.put(XrayVosController.CONTROLLER_ID, new XrayVosController());
        controllers.put(HunterController.CONTROLLER_ID, new HunterController());
        controllers.put(FotoOpdrachtController.CONTROLLER_ID, new FotoOpdrachtController());

        for (Map.Entry<String, MapItemController> pair : controllers.entrySet()) {
            MapItemController controller = pair.getValue();
            if(controller != null)
            {
                controller.initialize(this);
            }
        }
    }

    public void onIntentCreate(Intent intent)
    {
        if(intent != null && intent.hasExtra(SplashActivity.LOAD_ID))
        {
            Bundle bundle = intent.getBundleExtra(SplashActivity.LOAD_ID);
            for (Map.Entry<String, MapItemController> pair : controllers.entrySet()) {
                MapItemController controller = pair.getValue();
                if(controller != null)
                {
                    controller.onIntentCreate(bundle);
                }
            }
        }
    }

    public void onCreate(Bundle savedInstanceState)
    {
        if(savedInstanceState != null)
        {
            for (Map.Entry<String, MapItemController> pair : controllers.entrySet()) {
                MapItemController controller = pair.getValue();
                if(controller != null)
                {
                    controller.onCreate(savedInstanceState);
                }
            }
        }
    }

    public void onSaveInstanceState(Bundle saveInstanceState)
    {
        if(saveInstanceState != null)
        {
            for (Map.Entry<String, MapItemController> pair : controllers.entrySet()) {
                MapItemController controller = pair.getValue();
                if(controller != null)
                {
                    controller.onSaveInstanceState(saveInstanceState);
                }
            }
        }
    }

    public MapItemController get(String id)
    {
        return controllers.get(id);
    }

    public Collection<MapItemController> getAll()
    {
        return controllers.values();
    }


    public void update(boolean userInvoked)
    {
        for (Map.Entry<String, MapItemController> pair : controllers.entrySet()) {
            MapItemController controller = pair.getValue();
            if(controller != null)
            {
                controller.onUpdateInvoked(this, userInvoked);
            }
        }
        executeAsync();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(52.015379, 6.025979), 10));

        for (Map.Entry<String, MapItemController> pair : controllers.entrySet()) {
            MapItemController controller = pair.getValue();
            if(controller != null)
            {
                controller.onMapReady(googleMap);
            }
        }
        update(false);
    }

    public void onDestroy()
    {
        if(googleMap != null)
        {
            googleMap = null;
        }

        if(controllers != null)
        {
            Iterator iterator = controllers.entrySet().iterator();
            HashMap.Entry entry;
            MapItemController controller;
            while(iterator.hasNext())
            {
                entry = (HashMap.Entry)iterator.next();
                controller = (MapItemController)entry.getValue();

                if(controller != null)
                {
                    removeListener(controller);
                    controller.onDestroy();
                    controller = null;
                }

                iterator.remove();
            }

            controllers = null;
        }

    }

    @Override
    public ArrayList<BaseInfo> searchFor(String query) {
        ArrayList<BaseInfo> results = new ArrayList<>();
        for (Map.Entry<String, MapItemController> pair : controllers.entrySet()) {
            MapItemController controller = pair.getValue();
            if(controller != null)
            {
                results.addAll(controller.searchFor(query));
            }
        }
        return results;
    }

    @Override
    public Marker getAssociatedMarker(BaseInfo info) {
        for (Map.Entry<String, MapItemController> pair : controllers.entrySet()) {
            MapItemController controller = pair.getValue();
            if(controller != null) {
                Marker marker = controller.getAssociatedMarker(info);
                if(marker != null) return marker;
            }
        }
        return null;
    }

    @Override
    public List<String> getEntries() {
        ArrayList<String> entries = new ArrayList<>();
        for (Map.Entry<String, MapItemController> pair : controllers.entrySet()) {
            MapItemController controller = pair.getValue();
            if(controller != null) {
                entries.addAll(controller.getEntries());
            }
        }
        return entries;
    }

    public void animateCamera(CameraUpdate update)
    {
        googleMap.moveCamera(update);
    }
}
