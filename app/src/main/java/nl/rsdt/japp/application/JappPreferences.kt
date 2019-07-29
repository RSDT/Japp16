package nl.rsdt.japp.application

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.android.gms.maps.GoogleMap
import nl.rsdt.japp.jotial.maps.MapStyle
import java.util.*


/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 13-7-2016
 * Class for release_preferences
 */
object JappPreferences {

    val FIRST_RUN = "pref_first_run"

    val ACCOUNT_ID = "pref_account_id"

    val ACCOUNT_USERNAME = "pref_account_username"

    val ACCOUNT_NAME = "pref_account_name"

    val ACCOUNT_SURNAME = "pref_account_surname"

    val ACCOUNT_EMAIL = "pref_account_email"

    val ACCOUNT_MEMBER_SINCE = "pref_account_since"

    val ACCOUNT_LAST_VISIT = "pref_account_last"

    val ACCOUNT_ACTIVE = "pref_account_active"

    val ACCOUNT_AVATAR = "pref_account_avatar"

    val ACCOUNT_RANK = "pref_account_rank"

    val ACCOUNT_ICON = "pref_account_icon"

    val ACCOUNT_KEY = "pref_developer_account_key"

    val UPDATES_AUTO = "pref_updates_auto"

    val MAP_TYPE = "pref_map_type"

    val MAP_STYLE = "pref_map_style"

    val MAP_HUNT_NAME = "pref_map_hunt_name"

    val MAP_CONTROLS = "pref_map_controls"

    val FOLLOW_ZOOM = "pref_follow_zoom"

    val FOLLOW_AOA = "pref_follow_aoa"

    val PREF_CAT_MAP = "pref_cat_map"

    val LOCATION_UPDATE_INTERVAL = "pref_advanced_location_update_interval"

    val HUNTER_UPDATE_INTERVAL = "pref_advanced_hunter_update_interval"

    val DEBUG_VERSION_NAME = "pref_developer_debug_version_name"

    val FCM_TOKEN = "pref_fcm_token"

    val DEBUG_FRESH_START = "pref_developer_debug_fresh_start"

    val UPDATE_LOCATION = "pref_updates_location"

    val AUTO_ENLARGMENT = "pref_advanced_auto_enlargement"

    val AUTO_ENLARGMENT_INTERVAL = "pref_advanced_auto_enlargement_interval"

    val WALK_SPEED = "pref_advanced_auto_enlargement_walking_speed"

    val AREAS = "pref_advanced_areas"

    val AREAS_EDGES = "pref_advanced_areas_edges"

    val AREAS_EDGES_WIDTH = "pref_advanced_areas_edges_width"

    val AREAS_COLOR = "pref_advanced_areas_color"

    val AREAS_COLOR_ALPHA = "pref_advanced_areas_color_alpha"

    val USE_OSM = "pref_advanced_osm"

    val SHACO_ENABLED = "pref_developer_random_shaco"

    val NAVIGATION_APP = "pref_navigation_app"

    val NAVIGATION_FOLLOW_NORTH = "pref_navigation_follow_north"

    val IS_NAVIGATION_PHONE = "pref_navigation_phone"

    private val HUNTER_ALL = "pref_developer_hunter_all"

    private val LOAD_OLD_DATA = "pref_developer_load_old_data"

    private val OSM_MAP_TYPE = "pref_map_osm_source"

    val FILL_CIRCLES = "pref_advanced_circles_color"
    private val ONLY_TODAY = "pref_advanced_only_today"
    private val COLOR_NAME = "pref_color_name_"
    private val COLOR_HEX = "pref_color_hex_"


    /**
     * Gets the visible release_preferences of Japp.
     */
    val visiblePreferences: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(Japp.instance)

    /**
     * Gets the non-visible release_preferences of Japp.
     */
    val appPreferences: SharedPreferences
        get() = Japp.instance!!.getSharedPreferences("nl.rsdt.japp", Context.MODE_PRIVATE)

    /**
     * Gets the value indicating if this is the first run.
     */
    /**
     * Sets the value determining if this is the first run.
     */
    var isFirstRun: Boolean
        get() = appPreferences.getBoolean(FIRST_RUN, true)
        set(value) = appPreferences.edit().putBoolean(FIRST_RUN, value).apply()

    /**
     * Gets the username of the active account.
     */
    val accountUsername: String?
        get() = visiblePreferences.getString(ACCOUNT_USERNAME, "unknown")

    /**
     * Gets the rank of the active account.
     */
    val accountRank: String?
        get() = visiblePreferences.getString(ACCOUNT_RANK, "Guest")

    val accountId: Int
        get() = appPreferences.getInt(ACCOUNT_ID, -1)

    /**
     * Gets the icon that the user has selected to be displayed to the others.
     */
    val accountIcon: Int
        get() = Integer.valueOf(visiblePreferences.getString(ACCOUNT_ICON, "0")!!)

    /**
     * Gets the API-key associated with the active account.
     */
    val accountKey: String?
        get() = visiblePreferences.getString(ACCOUNT_KEY, "")

    /**
     * Gets the filename of the avatar on the area348 server of the active account.
     */
    val accountAvatarName: String?
        get() = appPreferences.getString(ACCOUNT_AVATAR, "")

    /**
     * Gets the value indicating if auto update is enabled.
     */
    /**
     * Sets the value indicating if auto update is enabled.
     */
    var isAutoUpdateEnabled: Boolean
        get() = visiblePreferences.getBoolean(UPDATES_AUTO, true)
        set(value) = visiblePreferences.edit().putBoolean(UPDATES_AUTO, value).apply()

    var mapType: Int
        get() = Integer.valueOf(visiblePreferences.getString(MAP_TYPE, GoogleMap.MAP_TYPE_NORMAL.toString())!!)
        set(type) = visiblePreferences.edit().putString(MAP_TYPE, type.toString()).apply()

    val mapStyle: Int
        get() = Integer.valueOf(visiblePreferences.getString(MAP_STYLE, MapStyle.DAY.toString())!!)

    val huntname: String?
        get() = visiblePreferences.getString(MAP_HUNT_NAME, "")

    val mapControls: Set<String>?
        get() = visiblePreferences.getStringSet(MAP_CONTROLS, null)

    var followZoom: Float
        get() = appPreferences.getFloat(FOLLOW_ZOOM, 20f)
        set(zoom) = appPreferences.edit().putFloat(FOLLOW_ZOOM, zoom).apply()

    val followAngleOfAttack: Float
        get() = appPreferences.getFloat(FOLLOW_AOA, 45f)

    val locationUpdateIntervalInMinutes: Float
        get() = java.lang.Float.valueOf(visiblePreferences.getString(LOCATION_UPDATE_INTERVAL, "1.0")!!)

    val locationUpdateIntervalInMs: Float
        get() = java.lang.Float.valueOf(visiblePreferences.getString(LOCATION_UPDATE_INTERVAL, "1.0")!!) * 60f * 1000f

    val hunterUpdateIntervalInMs: Float
        get() = java.lang.Float.valueOf(visiblePreferences.getString(HUNTER_UPDATE_INTERVAL, "1.0")!!) * 60f * 1000f

    var fcmToken: String?
        get() = appPreferences.getString(FCM_TOKEN, "")
        set(token) = appPreferences.edit().putString(FCM_TOKEN, token).apply()

    val isFreshStart: Boolean
        get() = visiblePreferences.getBoolean(DEBUG_FRESH_START, false)

    val isUpdatingLocationToServer: Boolean
        get() = visiblePreferences.getBoolean(UPDATE_LOCATION, true)

    val isAutoEnlargementEnabled: Boolean
        get() = visiblePreferences.getBoolean(AUTO_ENLARGMENT, true)

    val autoEnlargementInterval: Float
        get() = java.lang.Float.valueOf(visiblePreferences.getString(AUTO_ENLARGMENT_INTERVAL, "0.1f")!!)

    val autoEnlargementIntervalInMs: Float
        get() = java.lang.Float.valueOf(visiblePreferences.getString(AUTO_ENLARGMENT_INTERVAL, "0.2f")!!) * 60f * 1000f

    val walkSpeed: Float
        get() = java.lang.Float.parseFloat(visiblePreferences.getString(WALK_SPEED, "6.0f")!!)

    val areasEnabled: Set<String>?
        get() = visiblePreferences.getStringSet(AREAS, HashSet())

    val areasEdgesEnabled: Boolean
        get() = visiblePreferences.getBoolean(AREAS_EDGES, true)

    val areasEdgesWidth: Int
        get() = Integer.valueOf(visiblePreferences.getString(AREAS_EDGES_WIDTH, "5")!!)

    val areasColorEnabled: Boolean
        get() = visiblePreferences.getBoolean(AREAS_COLOR, true)

    val areasColorAlpha: Int
        get() = Integer.valueOf(visiblePreferences.getString(AREAS_COLOR_ALPHA, "25")!!)

    val getAllHunters: Boolean
        get() = visiblePreferences.getBoolean(HUNTER_ALL, false)
    val osmMapSource: OsmMapType
        get() {
            val value = visiblePreferences.getString(OSM_MAP_TYPE, "Default")
            return if ("0" == value) {
                OsmMapType.Default
            } else OsmMapType.valueOf(value)
        }

    val isNavigationPhone: Boolean
        get() = visiblePreferences.getBoolean(IS_NAVIGATION_PHONE, false)

    fun setFollowAoa(aoa: Float) {
        appPreferences.edit().putFloat(FOLLOW_AOA, aoa).apply()
    }

    fun useOSM(): Boolean {
        return visiblePreferences.getBoolean(USE_OSM, true)
    }

    fun shacoEnabled(): Boolean {
        return visiblePreferences.getBoolean(SHACO_ENABLED, false)
    }

    fun navigationApp(): NavigationApp {
        return NavigationApp.fromString(visiblePreferences.getString(NAVIGATION_APP, "Google Maps")!!)
    }

    fun followNorth(): Boolean {
        return visiblePreferences.getBoolean(NAVIGATION_FOLLOW_NORTH, false)
    }

    fun clear() {
        visiblePreferences.edit().clear().apply()
        appPreferences.edit().clear().apply()
    }

    fun loadOldData(): Boolean {
        return visiblePreferences.getBoolean(LOAD_OLD_DATA, true)
    }

    fun fillCircles(): Boolean {
        return visiblePreferences.getBoolean(FILL_CIRCLES, true)
    }

    fun onlyToday(): Boolean {
        return visiblePreferences.getBoolean(ONLY_TODAY, false)
    }

    fun getColorName(team: String): String? {
        return visiblePreferences.getString(COLOR_NAME + team, "Zwart")
    }

    fun setColorName(team: String, color: String) {
        visiblePreferences.edit().putString(COLOR_NAME + team, color).apply()
    }

    fun setColorHex(team: String, hex: String) {
        var hex = hex
        if (!hex.startsWith("#")) {
            hex = "#$hex"
        }
        visiblePreferences.edit().putString(COLOR_HEX + team, hex).apply()
    }

    fun getColorHex(team: String): String? {
        return visiblePreferences.getString(COLOR_HEX + team, "#FFFFFF")
    }

    enum class OsmMapType {
        Default,
        OpenTopo,
        Mapnik,
        HikeBike,
        Public_Transport,
        Base_NL,
        Fiets_NL,
        Road_NL,
        OpenSeaMap,
        CloudMade_Small,
        CloudMade_Normal,
        USGS_Topo,
        USGS_Sat,
        ChartBundle_ENRH,
        ChartBundle_ENRL,
        ChartBundle_WAC
    }

    enum class NavigationApp {
        GoogleMaps, Waze, OSMAnd, OSMAndWalk, Geo;


        companion object {
            fun fromString(appName: String): NavigationApp {
                return if (appName == "Waze") {
                    Waze
                } else if (appName == "Google Maps") {
                    GoogleMaps
                } else if (appName == "OsmAnd") {
                    OSMAnd
                } else if (appName == "Geo") {
                    OSMAnd
                } else if (appName == "OsmAndWalk") {
                    OSMAndWalk
                } else {
                    GoogleMaps //default google maps
                }
            }
        }
    }

}
