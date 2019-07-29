package nl.rsdt.japp.application.fragments

import android.app.Fragment
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import nl.rsdt.japp.R
import nl.rsdt.japp.application.Japp
import nl.rsdt.japp.application.JappPreferences
import nl.rsdt.japp.jotial.availability.StoragePermissionsChecker
import nl.rsdt.japp.jotial.data.bodies.VosPostBody
import nl.rsdt.japp.jotial.data.firebase.Location
import nl.rsdt.japp.jotial.data.structures.area348.AutoInzittendeInfo
import nl.rsdt.japp.jotial.maps.NavigationLocationManager
import nl.rsdt.japp.jotial.maps.deelgebied.Deelgebied
import nl.rsdt.japp.jotial.maps.management.MarkerIdentifier
import nl.rsdt.japp.jotial.maps.movement.MovementManager
import nl.rsdt.japp.jotial.maps.pinning.PinningManager
import nl.rsdt.japp.jotial.maps.pinning.PinningSession
import nl.rsdt.japp.jotial.maps.sighting.SightingIcon
import nl.rsdt.japp.jotial.maps.sighting.SightingSession
import nl.rsdt.japp.jotial.maps.wrapper.IJotiMap
import nl.rsdt.japp.jotial.maps.wrapper.IPolygon
import nl.rsdt.japp.jotial.maps.wrapper.google.GoogleJotiMap
import nl.rsdt.japp.jotial.maps.wrapper.osm.OsmJotiMap
import nl.rsdt.japp.jotial.navigation.NavigationSession
import nl.rsdt.japp.jotial.net.apis.AutoApi
import nl.rsdt.japp.jotial.net.apis.VosApi
import nl.rsdt.japp.service.LocationService
import nl.rsdt.japp.service.ServiceManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 8-7-2016
 * Description...
 */
class JappMapFragment : Fragment(), IJotiMap.OnMapReadyCallback, SharedPreferences.OnSharedPreferenceChangeListener {

    private val serviceManager = ServiceManager<LocationService, LocationService.LocationBinder>(LocationService::class.java)

    var jotiMap: IJotiMap? = null
        private set
    private var googleMapView: MapView? = null
    private var navigationLocationManager: NavigationLocationManager? = null

    private var callback: IJotiMap.OnMapReadyCallback? = null

    private val pinningManager = PinningManager()

    var movementManager: MovementManager? = MovementManager()
        private set

    private val areas = HashMap<String, IPolygon>()

    private var osmActive = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigationLocationManager = NavigationLocationManager()
        JappPreferences.visiblePreferences.registerOnSharedPreferenceChangeListener(this)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle): View? {

        pinningManager.intialize(activity)
        pinningManager.onCreate(savedInstanceState)

        movementManager!!.onCreate(savedInstanceState)

        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_map, container, false)

        return createMap(savedInstanceState, v)
    }

    private fun createMap(savedInstanceState: Bundle, v: View): View {
        val useOSM = JappPreferences.useOSM()

        val view: View
        if (useOSM) {
            view = createOSMMap(savedInstanceState, v)
        } else {
            view = createGoogleMap(savedInstanceState, v)
        }

        val menu = view.findViewById<FloatingActionMenu>(R.id.fab_menu)
        menu.bringToFront()

        return view
    }

    private fun createOSMMap(savedInstanceState: Bundle?, v: View): View {
        StoragePermissionsChecker.check(activity)
        osmActive = true

        val osmView = org.osmdroid.views.MapView(activity)
        (v as ViewGroup).addView(osmView)

        val ctx = activity.application
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        when (JappPreferences.osmMapSource) {
            JappPreferences.OsmMapType.Mapnik -> osmView.setTileSource(TileSourceFactory.MAPNIK)
            JappPreferences.OsmMapType.OpenSeaMap -> osmView.setTileSource(TileSourceFactory.OPEN_SEAMAP)
            JappPreferences.OsmMapType.HikeBike -> osmView.setTileSource(TileSourceFactory.HIKEBIKEMAP)
            JappPreferences.OsmMapType.OpenTopo -> osmView.setTileSource(TileSourceFactory.OpenTopo)
            JappPreferences.OsmMapType.Fiets_NL -> osmView.setTileSource(TileSourceFactory.FIETS_OVERLAY_NL)
            JappPreferences.OsmMapType.Default -> osmView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
            JappPreferences.OsmMapType.CloudMade_Normal -> osmView.setTileSource(TileSourceFactory.CLOUDMADESTANDARDTILES)
            JappPreferences.OsmMapType.CloudMade_Small -> osmView.setTileSource(TileSourceFactory.CLOUDMADESMALLTILES)
            JappPreferences.OsmMapType.ChartBundle_ENRH -> osmView.setTileSource(TileSourceFactory.ChartbundleENRH)
            JappPreferences.OsmMapType.ChartBundle_ENRL -> osmView.setTileSource(TileSourceFactory.ChartbundleENRH)
            JappPreferences.OsmMapType.ChartBundle_WAC -> osmView.setTileSource(TileSourceFactory.ChartbundleWAC)
            JappPreferences.OsmMapType.USGS_Sat -> osmView.setTileSource(TileSourceFactory.USGS_SAT)
            JappPreferences.OsmMapType.USGS_Topo -> osmView.setTileSource(TileSourceFactory.USGS_TOPO)
            JappPreferences.OsmMapType.Public_Transport -> osmView.setTileSource(TileSourceFactory.PUBLIC_TRANSPORT)
            JappPreferences.OsmMapType.Road_NL -> osmView.setTileSource(TileSourceFactory.ROADS_OVERLAY_NL)
            JappPreferences.OsmMapType.Base_NL -> osmView.setTileSource(TileSourceFactory.BASE_OVERLAY_NL)
            else -> osmView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        }
        osmView.controller.setCenter(GeoPoint(51.958852, 5.954517))
        osmView.controller.setZoom(11)
        osmView.setBuiltInZoomControls(true)
        osmView.setMultiTouchControls(true)
        osmView.isFlingEnabled = true
        osmView.isTilesScaledToDpi = true

        if (savedInstanceState != null) {
            val osmbundle = savedInstanceState.getBundle(OSM_BUNDLE)
            if (osmbundle != null) {
                osmView.controller.setZoom(osmbundle.getInt(OSM_ZOOM))
                osmView.controller.setCenter(GeoPoint(osmbundle.getDouble(OSM_LAT), osmbundle.getDouble(OSM_LNG)))
                osmView.rotation = osmbundle.getFloat(OSM_OR)
            }
        }

        movementManager!!.setSnackBarView(osmView)
        setupHuntButton(v).isEnabled = true
        setupSpotButton(v).isEnabled = true
        setupPinButton(v).isEnabled = true
        setupFollowButton(v)
        setupNavigationButton(v)
        jotiMap = OsmJotiMap.getJotiMapInstance(osmView)
        return v
    }

    private fun createGoogleMap(savedInstanceState: Bundle?, v: View): View {
        osmActive = false
        googleMapView = MapView(activity)
        (v as ViewGroup).addView(googleMapView)

        val ctx = activity.application
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(BUNDLE_MAP)) {
                googleMapView!!.onCreate(savedInstanceState.getBundle(BUNDLE_MAP))
            } else {
                googleMapView!!.onCreate(null)
            }
        } else {
            googleMapView!!.onCreate(null)
        }
        movementManager!!.setSnackBarView(googleMapView)
        setupHuntButton(v)
        setupSpotButton(v)
        setupPinButton(v)
        setupFollowButton(v)
        setupNavigationButton(v)
        jotiMap = GoogleJotiMap.getJotiMapInstance(googleMapView)
        if (savedInstanceState != null) {
            val osmbundle = savedInstanceState.getBundle(OSM_BUNDLE)
            if (osmbundle != null) {
                jotiMap!!.setPreviousZoom(osmbundle.getInt(OSM_ZOOM))
                jotiMap!!.setPreviousCameraPosition(osmbundle.getDouble(OSM_LAT), osmbundle.getDouble(OSM_LNG))
                jotiMap!!.setPreviousRotation(osmbundle.getFloat(OSM_OR))
            }
        }
        return v
    }


    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        movementManager!!.onSaveInstanceState(savedInstanceState)
        pinningManager.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putBoolean(BUNDLE_OSM_ACTIVE, osmActive)
        if (!osmActive) {
            val mapBundle = Bundle()
            googleMapView!!.onSaveInstanceState(mapBundle)

            savedInstanceState.putBundle(BUNDLE_MAP, mapBundle)
        } else if (jotiMap is OsmJotiMap) {
            val osmMap = (jotiMap as OsmJotiMap).osmMap
            val osmMapBundle = Bundle()
            osmMapBundle.putInt(OSM_ZOOM, osmMap.zoomLevel)
            osmMapBundle.putDouble(OSM_LAT, osmMap.mapCenter.latitude)
            osmMapBundle.putDouble(OSM_LNG, osmMap.mapCenter.longitude)
            osmMapBundle.putFloat(OSM_OR, osmMap.mapOrientation)
            savedInstanceState.putBundle(OSM_BUNDLE, osmMapBundle)
        }
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState)
    }


    fun getMapAsync(callback: IJotiMap.OnMapReadyCallback) {
        jotiMap!!.getMapAsync(this)
        this.callback = callback
    }

    override fun onStart() {
        super.onStart()
        if (!osmActive) {
            googleMapView!!.onStart()
        }
    }

    override fun onStop() {
        super.onStop()
        if (!osmActive) {
            googleMapView!!.onStop()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!osmActive) {
            googleMapView!!.onResume()
        } else {
            Configuration.getInstance().load(activity, PreferenceManager.getDefaultSharedPreferences(activity))
        }
        movementManager!!.setListener { status ->
            try {
                // Show the dialog by calling startResolutionForResult(),
                // and check the result in onActivityResult().
                status.startResolutionForResult(
                        activity,
                        REQUEST_CHECK_SETTINGS)
            } catch (e: IntentSender.SendIntentException) {
                // Ignore the error.
            }
        }
        serviceManager.add(movementManager)
        movementManager!!.onResume()
        if (!serviceManager.isBound) {
            serviceManager.bind(this.activity)
        }
    }

    override fun onPause() {
        super.onPause()
        if (!osmActive && googleMapView != null) {
            googleMapView!!.onPause()
        }
        movementManager!!.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!osmActive) {
            googleMapView!!.onDestroy()
        }
        if (movementManager != null) {
            movementManager!!.onDestroy()
            serviceManager.remove(movementManager)
            movementManager = null
        }

        JappPreferences.visiblePreferences.registerOnSharedPreferenceChangeListener(this)

        serviceManager.unbind(activity)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        if (!osmActive) {
            googleMapView!!.onLowMemory()
        }
    }

    override fun onMapReady(jotiMap: IJotiMap) {
        this.jotiMap = jotiMap
        jotiMap.clear()

        movementManager!!.onMapReady(jotiMap)

        setupDeelgebieden()

        pinningManager.onMapReady(jotiMap)
        if (callback != null) {
            callback!!.onMapReady(jotiMap)
        }

        val markerOptions = MarkerOptions()
                .position(LatLng(0.0, 0.0))
                .visible(false)
        val icon: Bitmap? = null
        val marker = jotiMap.addMarker(Pair(markerOptions, icon))
        navigationLocationManager!!.setCallback(object : NavigationLocationManager.OnNewLocation {
            override fun onNewLocation(location: Location?) {
                val identifier = MarkerIdentifier.Builder()
                identifier.setType(MarkerIdentifier.TYPE_NAVIGATE_CAR)

                identifier.add("addedBy", location!!.createdBy)
                identifier.add("createdOn", location.createdOn.toString())
                marker.title = Gson().toJson(identifier.create())
                marker.isVisible = true
                marker.position = LatLng(location.lat, location.lon)
            }

            override fun onNotInCar() {
                marker.isVisible = false
                marker.position = LatLng(0.0, 0.0)
            }
        })
    }

    private fun setupDeelgebieden() {
        val enabled = JappPreferences.areasEnabled
        for (area in enabled!!) {
            if (!areas.containsKey(area)) {
                setupDeelgebied(Deelgebied.parse(area))
            } else { // vraag me niet hoe maar dit fixed #112
                val poly = areas[area]
                poly!!.remove()
                areas.remove(area)
                setupDeelgebied(Deelgebied.parse(area))
            }
        }
        if (jotiMap is OsmJotiMap) {
            (jotiMap as OsmJotiMap).osmMap.invalidate()
        }
    }

    fun setupDeelgebied(deelgebied: Deelgebied?) {
        if (!deelgebied!!.coordinates.isEmpty()) {
            setUpDeelgebiedReal(deelgebied)
        } else {
            deelgebied.getDeelgebiedAsync { deelgebied1 -> setupDeelgebied(deelgebied1) }
        }
    }

    private fun setUpDeelgebiedReal(deelgebied: Deelgebied) {
        val options = PolygonOptions().addAll(deelgebied.coordinates)
        if (JappPreferences.areasColorEnabled) {
            val alphaPercent = JappPreferences.areasColorAlpha
            val alpha = (100 - alphaPercent).toFloat() / 100 * 255
            options.fillColor(deelgebied.alphaled(Math.round(alpha)))
        } else {
            options.fillColor(Color.TRANSPARENT)
        }

        options.strokeColor(deelgebied.color)
        if (JappPreferences.areasEdgesEnabled) {
            options.strokeWidth(JappPreferences.areasEdgesWidth.toFloat())
        } else {
            options.strokeWidth(0f)
        }
        options.visible(true)

        areas[deelgebied.name] = jotiMap!!.addPolygon(options)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        var polygon: IPolygon
        when (key) {
            JappPreferences.USE_OSM -> {
            }
            JappPreferences.AREAS -> {
                if (jotiMap == null) break
                val enabled = JappPreferences.areasEnabled
                for (area in enabled!!) {
                    if (!areas.containsKey(area)) {
                        setupDeelgebied(Deelgebied.parse(area))
                    }
                }

                val toBeRemoved = ArrayList<String>()
                for (area in areas.keys) {
                    if (!enabled.contains(area)) {
                        val poly = areas[area]
                        poly!!.remove()
                        toBeRemoved.add(area)
                    }
                }

                for (area in toBeRemoved) {
                    areas.remove(area)
                }
                if (jotiMap is OsmJotiMap) {
                    (jotiMap as OsmJotiMap).osmMap.invalidate()
                }
            }
            JappPreferences.AREAS_EDGES -> {
                val edges = JappPreferences.areasEdgesEnabled
                for ((_, value) in areas) {
                    polygon = value
                    if (edges) {
                        polygon.setStrokeWidth(JappPreferences.areasEdgesWidth)
                    } else {
                        polygon.setStrokeWidth(0)
                    }
                }
            }
            JappPreferences.AREAS_EDGES_WIDTH -> {
                val edgesEnabled = JappPreferences.areasEdgesEnabled
                for ((_, value) in areas) {
                    polygon = value
                    if (edgesEnabled) {
                        polygon.setStrokeWidth(JappPreferences.areasEdgesWidth)
                    }
                }
            }
            JappPreferences.AREAS_COLOR -> {
                val color = JappPreferences.areasColorEnabled
                for ((key1, value) in areas) {
                    polygon = value
                    if (color) {
                        val alphaPercent = JappPreferences.areasColorAlpha
                        val alpha = (100 - alphaPercent).toFloat() / 100 * 255
                        polygon.setFillColor(Deelgebied.parse(key1)!!.alphaled(Math.round(alpha)))
                    } else {
                        polygon.setFillColor(Color.TRANSPARENT)
                    }
                }
            }
            JappPreferences.AREAS_COLOR_ALPHA -> {
                val areasColorEnabled = JappPreferences.areasColorEnabled
                for ((key1, value) in areas) {
                    polygon = value
                    if (areasColorEnabled) {
                        val alphaPercent = JappPreferences.areasColorAlpha
                        val alpha = (100 - alphaPercent).toFloat() / 100 * 255
                        polygon.setFillColor(Deelgebied.parse(key1)!!.alphaled(Math.round(alpha)))
                    }
                }
            }
        }
    }

    fun setupSpotButton(v: View): FloatingActionButton {
        val spotButton = v.findViewById<FloatingActionButton>(R.id.fab_spot)
        spotButton.setOnClickListener(object : View.OnClickListener {

            var session: SightingSession? = null

            override fun onClick(view: View) {
                /*--- Hide the menu ---*/
                val v = getView()
                val menu = v!!.findViewById<FloatingActionMenu>(R.id.fab_menu)
                menu.hideMenu(true)

                /*--- Build a SightingSession and start it ---*/
                session = SightingSession.Builder()
                        .setType(SightingSession.SIGHT_SPOT)
                        .setGoogleMap(jotiMap)
                        .setTargetView(this@JappMapFragment.activity.findViewById(R.id.container))
                        .setDialogContext(this@JappMapFragment.activity)
                        .setOnSightingCompletedCallback { chosen, deelgebied, optionalInfo ->
                            /*--- Show the menu ---*/
                            val menu = getView()!!.findViewById<FloatingActionMenu>(R.id.fab_menu)
                            menu.showMenu(true)

                            if (chosen != null) {
                                /*--- Construct a JSON string with the data ---*/
                                val builder = VosPostBody.default
                                builder.setIcon(SightingIcon.SPOT)
                                builder.setLatLng(chosen)
                                builder.setTeam(deelgebied.name.substring(0, 1))
                                builder.setInfo(optionalInfo)

                                val api = Japp.getApi(VosApi::class.java)
                                api.post(builder).enqueue(object : Callback<Void> {
                                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                        val snackbarView = this@JappMapFragment.activity.findViewById<View>(R.id.container)
                                        when (response.code()) {
                                            200 -> Snackbar.make(snackbarView, getString(R.string.sent_succesfull), Snackbar.LENGTH_LONG).show()
                                            404 -> Snackbar.make(snackbarView, R.string.wrong_data, Snackbar.LENGTH_LONG).show()
                                            else -> Snackbar.make(snackbarView, getString(R.string.problem_with_sending, Integer.toString(response.code())), Snackbar.LENGTH_LONG).show()
                                        }
                                    }

                                    override fun onFailure(call: Call<Void>, t: Throwable) {
                                        val snackbarView = this@JappMapFragment.activity.findViewById<View>(R.id.container)
                                        Snackbar.make(snackbarView, getString(R.string.problem_with_sending, t.toString()), Snackbar.LENGTH_LONG).show()
                                    }
                                })

                                /**
                                 * TODO: send details?
                                 * Log the spot in firebase.
                                 */

                                /**
                                 * TODO: send details?
                                 * Log the spot in firebase.
                                 */
                                Japp.getAnalytics()!!.logEvent("EVENT_SPOT", Bundle())
                            }
                            session = null
                        }
                        .create()
                session!!.start()
            }
        })
        return spotButton
    }

    fun setupFollowButton(v: View): FloatingActionButton {
        val followButton = v.findViewById<FloatingActionButton>(R.id.fab_follow)
        followButton.setOnClickListener(object : View.OnClickListener {

            var session: MovementManager.FollowSession? = null

            override fun onClick(view: View) {
                val v = getView()

                val followButton = v!!.findViewById<FloatingActionButton>(R.id.fab_follow)

                /*--- Hide the menu ---*/
                val menu = v.findViewById<FloatingActionMenu>(R.id.fab_menu)

                /**
                 * TODO: use color to identify follow state?
                 */
                if (session != null) {
                    // followButton.setColorNormal(Color.parseColor("#DA4336"));
                    followButton.labelText = getString(R.string.follow_me)
                    session!!.end()
                    session = null
                } else {
                    menu.close(true)
                    //followButton.setColorNormal(Color.parseColor("#5cd65c"));
                    followButton.labelText = this@JappMapFragment.getString(R.string.stop_following)
                    session = movementManager!!.newSession(jotiMap!!.previousCameraPosition, JappPreferences.followZoom, JappPreferences.followAngleOfAttack)
                }
            }

        })
        return followButton
    }

    private fun setupPinButton(v: View): FloatingActionButton {
        val pinButton = v.findViewById<FloatingActionButton>(R.id.fab_mark)
        pinButton.setOnClickListener(object : View.OnClickListener {

            var session: PinningSession? = null

            override fun onClick(view: View) {
                val v = getView()

                val menu = v!!.findViewById<FloatingActionMenu>(R.id.fab_menu)

                if (session != null) {
                    /*--- Show the menu ---*/
                    menu.showMenu(true)
                    session!!.end()
                    session = null
                } else {
                    /*--- Hide the menu ---*/
                    menu.hideMenu(true)

                    session = PinningSession.Builder()
                            .setJotiMap(jotiMap)
                            .setCallback { pin ->
                                if (pin != null) {
                                    pinningManager.add(pin)
                                }

                                val menu = getView()!!.findViewById<FloatingActionMenu>(R.id.fab_menu)
                                menu.showMenu(true)

                                session!!.end()
                                session = null
                            }
                            .setTargetView(this@JappMapFragment.activity.findViewById(R.id.container))
                            .setDialogContext(this@JappMapFragment.activity)
                            .create()
                    session!!.start()
                }

            }
        })
        return pinButton
    }

    private fun setupNavigationButton(v: View): FloatingActionButton {
        val navigationButton = v.findViewById<FloatingActionButton>(R.id.fab_nav)
        navigationButton.setOnClickListener(object : View.OnClickListener {

            var session: NavigationSession? = null

            override fun onClick(view: View) {
                val v = getView()

                val menu = v!!.findViewById<FloatingActionMenu>(R.id.fab_menu)

                if (session != null) {
                    /*--- Show the menu ---*/
                    menu.showMenu(true)
                    session!!.end()
                    session = null
                } else {
                    /*--- Hide the menu ---*/
                    menu.hideMenu(true)

                    session = NavigationSession.Builder()
                            .setJotiMap(jotiMap)
                            .setCallback { navigateTo, toNavigationPhone ->
                                val menu = getView()!!.findViewById<FloatingActionMenu>(R.id.fab_menu)
                                menu.showMenu(true)

                                session!!.end()
                                session = null
                                if (navigateTo != null) {
                                    if (!toNavigationPhone) {
                                        try {
                                            when (JappPreferences.navigationApp()) {
                                                JappPreferences.NavigationApp.GoogleMaps -> {
                                                    val uristr = getString(R.string.google_uri, java.lang.Double.toString(navigateTo.latitude), java.lang.Double.toString(navigateTo.longitude))
                                                    val gmmIntentUri = Uri.parse(uristr)
                                                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                                    mapIntent.setPackage("com.google.android.apps.maps")
                                                    startActivity(mapIntent)
                                                }
                                                JappPreferences.NavigationApp.Waze -> {
                                                    val uri = getString(R.string.waze_uri, java.lang.Double.toString(navigateTo.latitude), java.lang.Double.toString(navigateTo.longitude))
                                                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
                                                }
                                                JappPreferences.NavigationApp.OSMAnd -> {
                                                    val osmuri = getString(R.string.osmand_uri, java.lang.Double.toString(navigateTo.latitude), java.lang.Double.toString(navigateTo.longitude))
                                                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(osmuri)))
                                                }
                                                JappPreferences.NavigationApp.OSMAndWalk -> {
                                                    val osmuriwalk = getString(R.string.osmandwalk_uri, java.lang.Double.toString(navigateTo.latitude), java.lang.Double.toString(navigateTo.longitude))
                                                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(osmuriwalk)))
                                                }
                                                JappPreferences.NavigationApp.Geo -> {
                                                    val geouri = getString(R.string.geo_uri, java.lang.Double.toString(navigateTo.latitude), java.lang.Double.toString(navigateTo.longitude))
                                                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(geouri)))
                                                }
                                            }
                                        } catch (e: ActivityNotFoundException) {
                                            println(e.toString())
                                            val snackbarView = this@JappMapFragment.activity.findViewById<View>(R.id.container)
                                            Snackbar.make(snackbarView,
                                                    getString(R.string.navigation_app_not_installed, JappPreferences.navigationApp().toString()),
                                                    Snackbar.LENGTH_LONG).show()
                                        }

                                    } else {
                                        val id = JappPreferences.accountId
                                        if (id >= 0) {
                                            val autoApi = Japp.getApi(AutoApi::class.java)
                                            autoApi.getInfoById(JappPreferences.accountKey, id).enqueue(object : Callback<AutoInzittendeInfo> {
                                                override fun onResponse(call: Call<AutoInzittendeInfo>, response: Response<AutoInzittendeInfo>) {
                                                    if (response.code() == 200) {
                                                        val autoInfo = response.body()
                                                        if (autoInfo != null) {
                                                            val database = FirebaseDatabase.getInstance()
                                                            val ref = database.getReference(NavigationLocationManager.FDB_NAME + "/" + autoInfo.autoEigenaar)
                                                            ref.setValue(Location(navigateTo, JappPreferences.accountUsername))
                                                        }
                                                    }
                                                    if (response.code() == 404) {
                                                        val snackbarView = this@JappMapFragment.activity.findViewById<View>(R.id.container)
                                                        Snackbar.make(snackbarView, getString(R.string.fout_not_in_car), Snackbar.LENGTH_LONG).show()
                                                    }
                                                }

                                                override fun onFailure(call: Call<AutoInzittendeInfo>, t: Throwable) {

                                                }
                                            })
                                        }
                                    }
                                }
                            }
                            .setTargetView(this@JappMapFragment.activity.findViewById(R.id.container))
                            .setDialogContext(this@JappMapFragment.activity)
                            .create()
                    session!!.start()
                }

            }
        })
        return navigationButton
    }

    private fun setupHuntButton(v: View): FloatingActionButton {
        val huntButton = v.findViewById<FloatingActionButton>(R.id.fab_hunt)
        huntButton.setOnClickListener(object : View.OnClickListener {

            var session: SightingSession

            override fun onClick(view: View) {

                /*--- Hide the menu ---*/
                val v = getView()
                val menu = v!!.findViewById<FloatingActionMenu>(R.id.fab_menu)
                menu.hideMenu(true)


                /*--- Build a SightingSession and start it ---*/
                session = SightingSession.Builder()
                        .setType(SightingSession.SIGHT_HUNT)
                        .setGoogleMap(jotiMap)
                        .setTargetView(this@JappMapFragment.activity.findViewById(R.id.container))
                        .setDialogContext(this@JappMapFragment.activity)
                        .setOnSightingCompletedCallback { chosen, deelgebied, optionalInfo ->
                            /*--- Show the menu ---*/
                            val menu = getView()!!.findViewById<FloatingActionMenu>(R.id.fab_menu)
                            menu.showMenu(true)

                            if (chosen != null) {
                                /*--- Construct a JSON string with the data ---*/
                                val builder = VosPostBody.default
                                builder.setIcon(SightingIcon.HUNT)
                                builder.setLatLng(chosen)
                                builder.setTeam(deelgebied.name.substring(0, 1))
                                builder.setInfo(optionalInfo)

                                val api = Japp.getApi(VosApi::class.java)
                                api.post(builder).enqueue(object : Callback<Void> {
                                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                        val snackbarView = this@JappMapFragment.activity.findViewById<View>(R.id.container)
                                        when (response.code()) {
                                            200 -> Snackbar.make(snackbarView, R.string.sent_succesfull, Snackbar.LENGTH_LONG).show()
                                            404 -> Snackbar.make(snackbarView, getString(R.string.wrong_data), Snackbar.LENGTH_LONG).show()
                                            else -> Snackbar.make(snackbarView, getString(R.string.problem_sending, Integer.toString(response.code())), Snackbar.LENGTH_LONG).show()
                                        }
                                    }

                                    override fun onFailure(call: Call<Void>, t: Throwable) {
                                        val snackbarView = this@JappMapFragment.activity.findViewById<View>(R.id.container)
                                        Snackbar.make(snackbarView, getString(R.string.problem_sending, t.toString()), Snackbar.LENGTH_LONG).show()//// TODO: 08/08/17 magic string
                                    }
                                })

                                /**
                                 * TODO: send details?
                                 * Log the hunt in firebase.
                                 */

                                /**
                                 * TODO: send details?
                                 * Log the hunt in firebase.
                                 */
                                Japp.getAnalytics()!!.logEvent("EVENT_HUNT", Bundle()) //// TODO: 08/08/17 magic string
                            }
                        }
                        .create()
                session.start()
            }
        })
        return huntButton
    }

    companion object {

        val TAG = "JappMapFragment"

        private val BUNDLE_MAP = "BUNDLE_MAP"

        private val BUNDLE_OSM_ACTIVE = "BUNDLE_OSM_ACTIVE_B"
        private val OSM_ZOOM = "OSM_ZOOM"
        private val OSM_LAT = "OSM_LAT"
        private val OSM_LNG = "OSM_LNG"
        private val OSM_OR = "OSM_OR"
        private val OSM_BUNDLE = "OSM_BUNDLE"

        val REQUEST_CHECK_SETTINGS = 32
    }
}
