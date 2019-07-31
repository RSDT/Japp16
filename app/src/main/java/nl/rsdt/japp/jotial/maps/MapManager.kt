package nl.rsdt.japp.jotial.maps

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import nl.rsdt.japp.application.Japp
import nl.rsdt.japp.application.JappPreferences
import nl.rsdt.japp.application.activities.SplashActivity
import nl.rsdt.japp.jotial.maps.clustering.ScoutingGroepClusterManager
import nl.rsdt.japp.jotial.maps.clustering.ScoutingGroepController
import nl.rsdt.japp.jotial.maps.management.MapItemController
import nl.rsdt.japp.jotial.maps.management.MapItemUpdatable
import nl.rsdt.japp.jotial.maps.management.controllers.*
import nl.rsdt.japp.jotial.maps.searching.Searchable
import nl.rsdt.japp.jotial.maps.wrapper.IJotiMap
import nl.rsdt.japp.jotial.maps.wrapper.IMarker
import nl.rsdt.japp.service.cloud.data.NoticeInfo
import nl.rsdt.japp.service.cloud.data.UpdateInfo
import nl.rsdt.japp.service.cloud.messaging.MessageManager
import java.util.*

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 24-7-2016
 * Manages the map
 */
class MapManager : Searchable, MessageManager.UpdateMessageListener, SharedPreferences.OnSharedPreferenceChangeListener {

    /**
     * The value that determines if the MapManager is already recreated once.
     */
    private var isRecreated = false

    /**
     * The GoogleMap
     */
    var jotiMap: IJotiMap? = null
        private set

    /**
     * The HashMap with the MapItemControllers.
     */
    private var controllers: HashMap<String, MapItemController<*, *>> = HashMap()

    /**
     * The Controller for the ScoutingGroep map items.
     */
    private var sgController: ScoutingGroepController? = ScoutingGroepController()


    /**
     * Gets all the controllers.
     */
    val all: Collection<MapItemController<*, *>>
        get() = controllers.values

    /**
     * Initializes a new instance of MapManager.
     */
    init {
        controllers[AlphaVosController.CONTROLLER_ID] = AlphaVosController()
        controllers[BravoVosController.CONTROLLER_ID] = BravoVosController()
        controllers[CharlieVosController.CONTROLLER_ID] = CharlieVosController()
        controllers[DeltaVosController.CONTROLLER_ID] = DeltaVosController()
        controllers[EchoVosController.CONTROLLER_ID] = EchoVosController()
        controllers[FoxtrotVosController.CONTROLLER_ID] = FoxtrotVosController()
        controllers[XrayVosController.CONTROLLER_ID] = XrayVosController()
        controllers[HunterController.CONTROLLER_ID] = HunterController()
        controllers[FotoOpdrachtController.CONTROLLER_ID] = FotoOpdrachtController()

        JappPreferences.visiblePreferences.registerOnSharedPreferenceChangeListener(this)
    }

    /**
     *
     */
    fun onIntentCreate(intent: Intent?) {
        // NOTE: no longer used
        if (intent != null && intent.hasExtra(SplashActivity.LOAD_ID)) {
            val bundle = intent.getBundleExtra(SplashActivity.LOAD_ID)
            if (bundle != null) {
                for ((_, controller) in controllers!!) {
                    controller.onIntentCreate(bundle)
                }
                sgController!!.onIntentCreate(bundle)
                bundle.clear()
                intent.removeExtra(SplashActivity.LOAD_ID)
            }
        }
    }

    /**
     * Gets invoked when the Activity is created.
     *
     * @param savedInstanceState The Bundle where we have potential put things.
     */
    fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            for ((_, controller) in controllers!!) {
                controller.onCreate(savedInstanceState)
            }
            sgController!!.onCreate(savedInstanceState)

            isRecreated = savedInstanceState.getBoolean(RECREATED_KEY)
        } else {
            val storage = MapStorage.instance
            val data = storage.data
            if (data != null) {
                for ((_, controller) in controllers!!) {
                    controller.onIntentCreate(data)
                }
                sgController!!.onIntentCreate(data)
                storage.clear()
            }
        }
    }

    /**
     * Gets invoked when the Activity is being saved.
     *
     * @param saveInstanceState The Bundle where we can save things in.
     */
    fun onSaveInstanceState(saveInstanceState: Bundle?) {
        if (saveInstanceState != null) {
            for ((_, controller) in controllers!!) {
                controller.onSaveInstanceState(saveInstanceState)
            }
            sgController!!.onSaveInstanceState(saveInstanceState)

            /**
             * Indicate the MapManager has already been created once.
             */
            saveInstanceState.putBoolean(RECREATED_KEY, true)
        }
    }

    /**
     * Gets the MapItemController associated with the given id.
     *
     * @param id The id of the MapItemController, for example VosAlphaController.CONTROLLER_ID .
     * @return The MapItemController associated with the id, returns null if none.
     */
    operator fun <T : MapItemController<*, *>> get(id: String): T {
        return controllers[id] as T
    }


    private
            /**
             * Gets the MapItemController associated with the given id.
             *
             * @param id The id of the MapItemController, for example VosAlphaController.CONTROLLER_ID .
             * @return The MapItemController associated with the id, returns null if none.
             */
    fun <T : VosController> getVosControllerByDeelgebied(deelgebied: String): T? {
        when (deelgebied) {
            "alpha" -> return get(AlphaVosController.CONTROLLER_ID)
            "bravo" -> return get(BravoVosController.CONTROLLER_ID)
            "charlie" -> return get(CharlieVosController.CONTROLLER_ID)
            "delta" -> return get(DeltaVosController.CONTROLLER_ID)
            "echo" -> return get(EchoVosController.CONTROLLER_ID)
            "foxtrot" -> return get(FoxtrotVosController.CONTROLLER_ID)
        }
        return null
    }



            /**
             * Gets invoked when a UpdateMessage is received.
             */
    override fun onUpdateMessageReceived(info: UpdateInfo?) {
        if (info == null || info.type == null || info.action == null) return
        var updatable: MapItemUpdatable<*>? = null
        when (info.type) {
            "hunter" -> updatable = get(HunterController.CONTROLLER_ID)
            "foto" -> updatable = get(FotoOpdrachtController.CONTROLLER_ID)
            "vos_a" -> updatable = get(AlphaVosController.CONTROLLER_ID)
            "vos_b" -> updatable = get(BravoVosController.CONTROLLER_ID)
            "vos_c" -> updatable = get(CharlieVosController.CONTROLLER_ID)
            "vos_d" -> updatable = get(DeltaVosController.CONTROLLER_ID)
            "vos_e" -> updatable = get(EchoVosController.CONTROLLER_ID)
            "vos_f" -> updatable = get(FoxtrotVosController.CONTROLLER_ID)
            "vos_x" -> updatable = get(XrayVosController.CONTROLLER_ID)
            "sc" -> updatable = sgController
        }

        updatable?.onUpdateMessage(info)
    }

    override fun onNoticeMessageReceived(info: NoticeInfo?) {

    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key) {
            JappPreferences.AREAS -> {

                for (controller in controllers!!.values) {
                    if (controller is VosController) {
                        controller.visiblity = false
                    }
                }

                val enabled = JappPreferences.areasEnabled
                for (area in enabled!!) {
                    val controller = getVosControllerByDeelgebied<VosController>(area)
                    controller?.visiblity = true
                }


                //TODO: make this nicer and less hacky
                if (sgController!!.clusterManager is ScoutingGroepClusterManager) {
                    val clusterManager = sgController!!.clusterManager as ScoutingGroepClusterManager?
                    clusterManager!!.reRender()
                }
            }
            JappPreferences.MAP_TYPE -> if (jotiMap != null) {
                val value = sharedPreferences.getString(key, GoogleMap.MAP_TYPE_NORMAL.toString())
                jotiMap!!.setGMapType(Integer.valueOf(value!!))
            }
            JappPreferences.MAP_STYLE -> if (jotiMap != null) {
                val style = Integer.valueOf(sharedPreferences.getString(key, MapStyle.DAY.toString())!!)
                if (style == MapStyle.AUTO) {

                } else {
                    jotiMap!!.setMapStyle(MapStyleOptions.loadRawResourceStyle(Japp.instance!!, MapStyle.getAssociatedRaw(style)))
                }
            }
            JappPreferences.MAP_CONTROLS -> {
                val set = sharedPreferences.getStringSet(key, null)
                if (set != null) {
                    if (jotiMap != null) {
                        jotiMap!!.uiSettings.setZoomControlsEnabled(set.contains(MapControls.ZOOM.toString()))
                        jotiMap!!.uiSettings.setCompassEnabled(set.contains(MapControls.COMPASS.toString()))
                        jotiMap!!.uiSettings.setIndoorLevelPickerEnabled(set.contains(MapControls.LEVEL.toString()))
                        jotiMap!!.uiSettings.setMapToolbarEnabled(set.contains(MapControls.TOOLBAR.toString()))
                    }
                }
            }
        }
    }

    /**
     * Updates all the controllers, without any smart fetching logic.
     */
    fun update() {
        for ((_, controller) in controllers) {
            controller.onUpdateInvoked()
        }
        sgController!!.onUpdateInvoked()
    }

    /**
     * Gets invoked when the GoogleMap is ready for use.
     *
     * @param jotiMap
     */
    fun onMapReady(jotiMap: IJotiMap) {
        this.jotiMap = jotiMap

        /**
         * Set the type and the style of the map.
         */
        jotiMap.setGMapType(JappPreferences.mapType)
        jotiMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(Japp.instance!!, MapStyle.getAssociatedRaw(JappPreferences.mapStyle)))

        val controls = JappPreferences.mapControls
        if (controls != null) {
            jotiMap.uiSettings.setZoomControlsEnabled(controls.contains(MapControls.ZOOM.toString()))
            jotiMap.uiSettings.setCompassEnabled(controls.contains(MapControls.COMPASS.toString()))
            jotiMap.uiSettings.setIndoorLevelPickerEnabled(controls.contains(MapControls.LEVEL.toString()))
            jotiMap.uiSettings.setMapToolbarEnabled(controls.contains(MapControls.TOOLBAR.toString()))
        }

        /**
         * Checks if this is the first instance of MapManager.
         */
        if (!isRecreated) {

            /**
             * Move the camera to the default position(Netherlands).
             */
            jotiMap.animateCamera(LatLng(52.015379, 6.025979), 10)

            /**
             * Update the controllers.
             */
            update()
        }

        /**
         * Loop trough each controller and call the OnMapReadyCallback.
         */
        for ((_, controller) in controllers) {
            controller.onMapReady(jotiMap)
        }
        sgController!!.onMapReady(jotiMap)

        for (controller in controllers.values) {
            if (controller is VosController) {
                controller.visiblity = false
            }
        }

        val enabled = JappPreferences.areasEnabled
        for (area in enabled!!) {
            val controller = getVosControllerByDeelgebied<VosController>(area)
            controller?.visiblity = true
        }


    }

    /**
     * Gets invoked when the Activity is beaning destroyed.
     */
    fun onDestroy() {
        /**
         * Unregister this as listener to prevent memory leaks.
         */
        JappPreferences.visiblePreferences.unregisterOnSharedPreferenceChangeListener(this)

        /**
         * Remove this as a listener for UpdateMessages.
         */
        Japp.updateManager.remove(this)

        if (jotiMap != null) {
            jotiMap = null
        }

        if (sgController != null) {
            //sgController.onDestroy();
            sgController = null
        }
        for (entry in controllers) {
            val controller = entry.value
            controller.onDestroy()
        }
    }

    override fun searchFor(query: String): IMarker? {
        return null
    }


    override fun provide(): MutableList<String> {
        val entries = ArrayList<String>()
        for ((_, controller) in controllers) {
            entries.addAll(controller.provide())
        }
        return entries
    }

    companion object {

        /**
         * Defines the tag of this class.
         */
        val TAG = "MapManager"

        /**
         * Defines a key for storing the isRecreated value.
         */
        private val RECREATED_KEY = "RECREATED"
    }
}
