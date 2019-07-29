package nl.rsdt.japp.application.activities

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import nl.rsdt.japp.R
import nl.rsdt.japp.application.Japp
import nl.rsdt.japp.application.JappPreferences
import nl.rsdt.japp.application.fragments.CarFragment
import nl.rsdt.japp.application.fragments.HomeFragment
import nl.rsdt.japp.application.fragments.JappMapFragment
import nl.rsdt.japp.application.navigation.FragmentNavigationManager
import nl.rsdt.japp.application.navigation.NavigationManager
import nl.rsdt.japp.application.showcase.JappShowcaseSequence
import nl.rsdt.japp.application.showcase.ShowcaseSequence
import nl.rsdt.japp.jotial.auth.Authentication
import nl.rsdt.japp.jotial.data.structures.area348.MetaColorInfo
import nl.rsdt.japp.jotial.maps.MapManager
import nl.rsdt.japp.jotial.maps.window.CustomInfoWindowAdapter
import nl.rsdt.japp.jotial.maps.wrapper.IJotiMap
import nl.rsdt.japp.jotial.maps.wrapper.google.GoogleJotiMap
import nl.rsdt.japp.jotial.net.apis.MetaApi
import nl.rsdt.japp.service.LocationService
import nl.rsdt.japp.service.cloud.data.NoticeInfo
import nl.rsdt.japp.service.cloud.data.UpdateInfo
import nl.rsdt.japp.service.cloud.messaging.JappFirebaseMessagingService
import nl.rsdt.japp.service.cloud.messaging.MessageManager
import retrofit2.Call
import retrofit2.Callback
import java.lang.Exception

class MainActivity : AppCompatActivity(), IJotiMap.OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener, SharedPreferences.OnSharedPreferenceChangeListener, MessageManager.UpdateMessageListener {

    /**
     * Manages the GoogleMap.
     */
    private var mapManager: MapManager = MapManager()

    /**
     * Manages the navigation between the fragments.
     */
    private var navigationManager: NavigationManager = NavigationManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * Subscribe to the updates topic.
         */
        FirebaseMessaging.getInstance().subscribeToTopic("updates")

        /**
         * Set a interceptor so that requests that give a 401 will result in a login activity.
         */
        /*
        Japp.setInterceptor(new Interceptor() {

            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Response response = chain.proceed(request);
                if(response.code() == 401) {
                    Authentication.startLoginActivity(MainActivity.this);
                }
                return response;
            }
        });
        */

        /**
         * Add this as a listener for UpdateMessages.
         */
        Japp.updateManager.add(this)

        /**
         * Register a on changed listener to the visible release_preferences.
         */
        JappPreferences.visiblePreferences.registerOnSharedPreferenceChangeListener(this)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        if (JappPreferences.shacoEnabled() && (JappPreferences.accountUsername == "David" || JappPreferences.accountUsername == "test")) {
            val player = MediaPlayer.create(this, R.raw.shaco_tank_engine)
            player.start()
        }


        /**
         * Checks if this is the first run of the app.
         */
        if (JappPreferences.isFirstRun) {


            /**
             * Send the token to the server.
             */
            JappFirebaseMessagingService.sendToken()

            /**
             * Show the user around the app via a sequence.
             */
            val sequence = JappShowcaseSequence(this)
            sequence.callback = object: ShowcaseSequence.OnSequenceCompletedCallback<MainActivity>{
                override fun onSequenceCompleted(sequence: ShowcaseSequence<MainActivity>) {
                    sequence.end()
                }
            }
            sequence.start()

            /**
             * Set the the first run value to false.
             */
            JappPreferences.isFirstRun = false
        }

        /**
         * Setup the MapManager.
         */
        mapManager.onIntentCreate(intent)
        mapManager.onCreate(savedInstanceState)

        /**
         * Setup the NavigationDrawer.
         */
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        /**
         * Setup the NavigationView.
         */
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        /**
         * Initialize the NavigationManager.
         */
        navigationManager.initialize(this)
        navigationManager.onSavedInstance(savedInstanceState)

    }

    public override fun onSaveInstanceState(savedInstanceState: Bundle?) {
        navigationManager.onSaveInstanceState(savedInstanceState)
        mapManager.onSaveInstanceState(savedInstanceState)

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState)
    }

    override fun onMapReady(map: IJotiMap) {
        /**
         * First set the custom InfoWindowAdapter and then invoke the onMapReady on the MapManager.
         */
        if (map is GoogleJotiMap) {
            map.setInfoWindowAdapter(CustomInfoWindowAdapter(layoutInflater, map.googleMap!!)) //// TODO: 07/08/17 change the GoogleMap to a JotiMap
        } else {
            //// TODO: 09/08/17 do stuff
        }
        mapManager.onMapReady(map)
    }

    /**
     * TODO: don't use final here
     */
    override fun onUpdateMessageReceived(info: UpdateInfo?) {
        if (JappPreferences.isAutoUpdateEnabled) {
            Snackbar.make(findViewById(R.id.container), getString(R.string.updating_type, info?.type), Snackbar.LENGTH_LONG).show()
            mapManager.onUpdateMessageReceived(info)
        } else {
            Snackbar.make(findViewById(R.id.container), getString(R.string.update_available, info?.type), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.update)) { mapManager.onUpdateMessageReceived(info) }
                    .show()
        }
    }

    /**
     * TODO: don't use final here
     */
    override fun onNoticeMessageReceived(info: NoticeInfo?) {
        this.runOnUiThread {
            AlertDialog.Builder(this@MainActivity)
                    .setTitle(info?.title)
                    .setMessage(info?.body)
                    .setIcon(info?.drawable?:0)
                    .create()
                    .show()
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (JappPreferences.USE_OSM == key) {
            recreate()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the options menu from XML
        val inflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)

        /**
         * TODO: fix search system
         */
        //searchManager.onCreateOptionsMenu(menu);
        return true
    }

    override
            /**
             * TODO: don't use final here
             */
    fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.refresh) {
            mapManager.update()
            /**
             * Update the vos status on the home fragment.
             */
            val fragment = navigationManager.getFragment(FragmentNavigationManager.FRAGMENT_HOME) as HomeFragment?
            fragment?.refresh()
            val carfragment = navigationManager.getFragment(FragmentNavigationManager.FRAGMENT_CAR) as CarFragment?
            carfragment?.refresh()
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            if (navigationManager.hasBackStack()) {
                navigationManager.onBackPressed()
            } else {
                super.onBackPressed()
            }
        }
    }

    public override fun onStart() {
        super.onStart()
    }
    public override fun onResume() {
        super.onResume()
        navigationManager.setupMap(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val action = intent.action
        if (action != null && action == LocationService.ACTION_REQUEST_LOCATION_SETTING) {
            val mapFragment = navigationManager.getFragment(FragmentNavigationManager.FRAGMENT_MAP) as JappMapFragment?
            mapFragment?.movementManager?.requestLocationSettingRequest()
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == JappMapFragment.REQUEST_CHECK_SETTINGS) {
            val mapFragment = navigationManager.getFragment(FragmentNavigationManager.FRAGMENT_MAP) as JappMapFragment?
            mapFragment?.movementManager?.postResolutionResultToService(resultCode)
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.nav_home) {
            navigationManager.switchTo(FragmentNavigationManager.FRAGMENT_HOME)
        } else if (id == R.id.nav_map) {
            navigationManager.switchTo(FragmentNavigationManager.FRAGMENT_MAP)
        } else if (id == R.id.nav_settings) {
            navigationManager.switchTo(FragmentNavigationManager.FRAGMENT_SETTINGS)
        } else if (id == R.id.nav_car) {
            navigationManager.switchTo(FragmentNavigationManager.FRAGMENT_CAR)
        } else if (id == R.id.nav_about) {
            navigationManager.switchTo(FragmentNavigationManager.FRAGMENT_ABOUT)
        } else if (id == R.id.nav_log_out) {
            Authentication.startLoginActivity(this)
        }

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    public override fun onStop() {
        super.onStop()
    }

    public override fun onDestroy() {
        super.onDestroy()

        Japp.setInterceptor(null)

        /**
         * Remove this as UpdateMessageListener.
         */
        Japp.updateManager.remove(this)

        /**
         * Unregister this as OnSharedPreferenceChangeListener.
         */
        JappPreferences.visiblePreferences.unregisterOnSharedPreferenceChangeListener(this)

        mapManager.onDestroy()

        navigationManager.onDestroy()

    }

    companion object {

        /**
         * Defines a tag for this class.
         */
        val TAG = "MainActivity"
    }

}
