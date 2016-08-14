package nl.rsdt.japp.jotial.maps;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.rsdt.anl.RequestPool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;


import nl.rsdt.japp.application.Japp;
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
import nl.rsdt.japp.service.cloud.data.UpdateInfo;
import nl.rsdt.japp.service.cloud.messaging.UpdateManager;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 24-7-2016
 * Manages the map
 */
public class MapManager extends RequestPool implements OnMapReadyCallback, Searchable, UpdateManager.UpdateMessageListener {

    /**
     * Defines the tag of this class.
     * */
    public static final String TAG = "MapManager";

    /**
     * The GoogleMap
     * */
    private GoogleMap googleMap;

    /**
     * The HashMap with the MapItemControllers.
     * */
    private HashMap<String, MapItemController> controllers = new HashMap<>();

    /**
     * Initializes a new instance of MapManager.
     * */
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

    /**
     *
     * */
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

    /**
     * Gets invoked when the Activity is created.
     *
     * @param savedInstanceState The Bundle where we have potential put things.
     * */
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

    /**
     * Gets invoked when the Activity is being saved.
     *
     * @param saveInstanceState The Bundle where we can save things in.
     * */
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

    @Nullable
    /**
     * Gets the MapItemController associated with the given id.
     *
     * @param id The id of the MapItemController, for example VosAlphaController.CONTROLLER_ID .
     * @return The MapItemController associated with the id, returns null if none.
     * */
    public MapItemController get(String id)
    {
        return controllers.get(id);
    }

    /**
     * Gets all the controllers.
     * */
    public Collection<MapItemController> getAll()
    {
        return controllers.values();
    }

    @Override
    /**
     * Gets invoked when a UpdateMessage is received.
     * */
    public void onUpdateMessageReceived(UpdateInfo info) {
        MapItemController controller = null;
        switch (info.type) {
            case "foto":
                controller = get(FotoOpdrachtController.CONTROLLER_ID);
                break;
        }

        if(controller != null) {
            controller.onUpdateMessage(this, info);
        }

    }

    /**
     * Updates all the controllers, without any smart fetching logic.
     * */
    public void update()
    {
        for (Map.Entry<String, MapItemController> pair : controllers.entrySet()) {
            MapItemController controller = pair.getValue();
            if(controller != null)
            {
                controller.onUpdateInvoked(this);
            }
        }
        executeAsync();
    }

    @Override
    /**
     * Gets invoked when the GoogleMap is ready for use.
     * */
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        /**
         * TODO: Should we hide MapToolbar so we can move the FAB menu down?
         * */
        //googleMap.getUiSettings().setMapToolbarEnabled(false);

        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(52.015379, 6.025979), 10));

        for (Map.Entry<String, MapItemController> pair : controllers.entrySet()) {
            MapItemController controller = pair.getValue();
            if(controller != null)
            {
                controller.onMapReady(googleMap);
            }
        }
    }

    /**
     * Gets invoked when the Activity is beaning destroyed.
     * */
    public void onDestroy()
    {
        /**
         * Remove this as a listener for UpdateMessages.
         * */
        Japp.getUpdateManager().remove(this);

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

    /**
     * Animates the camera according the given CameraUpdate.
     * */
    public void animateCamera(CameraUpdate update)
    {
        googleMap.moveCamera(update);
    }

}
