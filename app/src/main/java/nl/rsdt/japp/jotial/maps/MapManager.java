package nl.rsdt.japp.jotial.maps;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.rsdt.japp.application.Japp;
import nl.rsdt.japp.application.JappPreferences;
import nl.rsdt.japp.application.activities.SplashActivity;
import nl.rsdt.japp.jotial.maps.clustering.ScoutingGroepController;
import nl.rsdt.japp.jotial.maps.management.MapItemController;
import nl.rsdt.japp.jotial.maps.management.MapItemUpdatable;
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
import nl.rsdt.japp.jotial.maps.wrapper.IJotiMap;
import nl.rsdt.japp.jotial.maps.wrapper.IMarker;
import nl.rsdt.japp.service.cloud.data.NoticeInfo;
import nl.rsdt.japp.service.cloud.data.UpdateInfo;
import nl.rsdt.japp.service.cloud.messaging.MessageManager;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 24-7-2016
 * Manages the map
 */
public class MapManager implements Searchable, MessageManager.UpdateMessageListener, SharedPreferences.OnSharedPreferenceChangeListener {

    /**
     * Defines the tag of this class.
     * */
    public static final String TAG = "MapManager";

    /**
     * Defines a key for storing the isRecreated value.
     * */
    private static final String RECREATED_KEY = "RECREATED";

    /**
     * The value that determines if the MapManager is already recreated once.
     * */
    private boolean isRecreated = false;

    /**
     * The GoogleMap
     * */
    private IJotiMap jotiMap;

    public IJotiMap getJotiMap() {
        return jotiMap;
    }

    /**
     * The HashMap with the MapItemControllers.
     * */
    private HashMap<String, MapItemController> controllers = new HashMap<>();

    /**
     * The Controller for the ScoutingGroep map items.
     * */
    private ScoutingGroepController sgController = new ScoutingGroepController();

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

        JappPreferences.getVisiblePreferences().registerOnSharedPreferenceChangeListener(this);
    }

    /**
     *
     * */
    public void onIntentCreate(Intent intent)
    {
        // NOTE: no longer used
        if(intent != null && intent.hasExtra(SplashActivity.LOAD_ID))
        {
            Bundle bundle = intent.getBundleExtra(SplashActivity.LOAD_ID);
            if(bundle != null) {
                for (Map.Entry<String, MapItemController> pair : controllers.entrySet()) {
                    MapItemController controller = pair.getValue();
                    if(controller != null)
                    {
                        controller.onIntentCreate(bundle);
                    }
                }
                sgController.onIntentCreate(bundle);
                bundle.clear();
                intent.removeExtra(SplashActivity.LOAD_ID);
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
            sgController.onCreate(savedInstanceState);

            isRecreated = savedInstanceState.getBoolean(RECREATED_KEY);
        } else {
            MapStorage storage = MapStorage.getInstance();
            Bundle data = storage.getData();
            if(data != null) {
                for (Map.Entry<String, MapItemController> pair : controllers.entrySet()) {
                    MapItemController controller = pair.getValue();
                    if(controller != null)
                    {
                        controller.onIntentCreate(data);
                    }
                }
                sgController.onIntentCreate(data);
                storage.clear();
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
            sgController.onSaveInstanceState(saveInstanceState);

            /**
             * Indicate the MapManager has already been created once.
             * */
            saveInstanceState.putBoolean(RECREATED_KEY, true);
        }
    }

    @SuppressWarnings("unchecked")
    @Nullable
    /**
     * Gets the MapItemController associated with the given id.
     *
     * @param id The id of the MapItemController, for example VosAlphaController.CONTROLLER_ID .
     * @return The MapItemController associated with the id, returns null if none.
     * */
    public <T extends MapItemController> T get(String id) {
        return (T)controllers.get(id);
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
        if(info == null || info.type == null || info.action == null) return;
        MapItemUpdatable updatable = null;
        switch (info.type) {
            case "hunter":
                updatable = get(HunterController.CONTROLLER_ID);
                break;
            case "foto":
                updatable = get(FotoOpdrachtController.CONTROLLER_ID);
                break;
            case "vos_a":
                updatable = get(AlphaVosController.CONTROLLER_ID);
                break;
            case "vos_b":
                updatable = get(BravoVosController.CONTROLLER_ID);
                break;
            case "vos_c":
                updatable = get(CharlieVosController.CONTROLLER_ID);
                break;
            case "vos_d":
                updatable = get(DeltaVosController.CONTROLLER_ID);
                break;
            case "vos_e":
                updatable = get(EchoVosController.CONTROLLER_ID);
                break;
            case "vos_f":
                updatable = get(FoxtrotVosController.CONTROLLER_ID);
                break;
            case "vos_x":
                updatable = get(XrayVosController.CONTROLLER_ID);
                break;
            case "sc":
                updatable = sgController;
                break;
        }

        if(updatable != null) {
            updatable.onUpdateMessage(info);
        }
    }

    @Override
    public void onNoticeMessageReceived(NoticeInfo info) {

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case JappPreferences.MAP_TYPE:
                if(jotiMap != null) {
                    String value = sharedPreferences.getString(key, String.valueOf(GoogleMap.MAP_TYPE_NORMAL));
                    jotiMap.setGMapType(Integer.valueOf(value));
                }
                break;
            case JappPreferences.MAP_STYLE:
                if(jotiMap != null) {
                    int style = Integer.valueOf(sharedPreferences.getString(key, String.valueOf(MapStyle.DAY)));
                    if(style == MapStyle.AUTO) {

                    } else {
                        jotiMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(Japp.getInstance(), MapStyle.getAssociatedRaw(style)));
                    }
                }
                break;
            case JappPreferences.MAP_CONTROLS:
                Set<String> set = sharedPreferences.getStringSet(key, null);
                if(set != null) {
                    if(jotiMap != null) {
                        jotiMap.getUiSettings().setZoomControlsEnabled(set.contains(String.valueOf(MapControls.ZOOM)));
                        jotiMap.getUiSettings().setCompassEnabled(set.contains(String.valueOf(MapControls.COMPASS)));
                        jotiMap.getUiSettings().setIndoorLevelPickerEnabled(set.contains(String.valueOf(MapControls.LEVEL)));
                        jotiMap.getUiSettings().setMapToolbarEnabled(set.contains(String.valueOf(MapControls.TOOLBAR)));
                    }
                }
                break;
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
                controller.onUpdateInvoked();
            }
        }
        sgController.onUpdateInvoked();
    }

    /**
     * Gets invoked when the GoogleMap is ready for use.
     *
     * @param jotiMap*/
    public void onMapReady(IJotiMap jotiMap) {
        this.jotiMap = jotiMap;

        /**
         * Set the type and the style of the map.
         * */
        jotiMap.setGMapType(JappPreferences.getMapType());
        jotiMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(Japp.getInstance(), MapStyle.getAssociatedRaw(JappPreferences.getMapStyle())));

        Set<String> controls = JappPreferences.getMapControls();
        if(controls != null) {
            //// TODO: 07/08/17 why the for loop?
            for(String property : controls) {
                jotiMap.getUiSettings().setZoomControlsEnabled(controls.contains(String.valueOf(MapControls.ZOOM)));
                jotiMap.getUiSettings().setCompassEnabled(controls.contains(String.valueOf(MapControls.COMPASS)));
                jotiMap.getUiSettings().setIndoorLevelPickerEnabled(controls.contains(String.valueOf(MapControls.LEVEL)));
                jotiMap.getUiSettings().setMapToolbarEnabled(controls.contains(String.valueOf(MapControls.TOOLBAR)));
            }
        }

        /**
         * Checks if this is the first instance of MapManager.
         * */
        if(!isRecreated) {
            /**
             * Move the camera to the default position(Netherlands).
             * */
            jotiMap.animateCamera(new LatLng(52.015379, 6.025979), 10);

            /**
             * Update the controllers.
             * */
            update();
        }

        /**
         * Loop trough each controller and call the OnMapReadyCallback.
         * */
        for (Map.Entry<String, MapItemController> pair : controllers.entrySet()) {
            MapItemController controller = pair.getValue();
            if(controller != null)
            {
                controller.onMapReady(jotiMap);
            }
        }
        sgController.onMapReady(jotiMap);
    }

    /**
     * Gets invoked when the Activity is beaning destroyed.
     * */
    public void onDestroy()
    {
        /**
         * Unregister this as listener to prevent memory leaks.
         * */
        JappPreferences.getVisiblePreferences().unregisterOnSharedPreferenceChangeListener(this);

        /**
         * Remove this as a listener for UpdateMessages.
         * */
        Japp.getUpdateManager().remove(this);

        if(jotiMap != null)
        {
            jotiMap = null;
        }

        if(sgController != null) {
            //sgController.onDestroy();
            sgController = null;
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

                if(controller != null) {
                    controller.onDestroy();
                }

                iterator.remove();
            }

            controllers = null;
        }

    }

    @Override
    public IMarker searchFor(String query) {
        return null;
    }


    @Override
    public List<String> provide() {
        ArrayList<String> entries = new ArrayList<>();
        for (Map.Entry<String, MapItemController> pair : controllers.entrySet()) {
            MapItemController controller = pair.getValue();
            if(controller != null) {
                entries.addAll(controller.provide());
            }
        }
        return entries;
    }
}
