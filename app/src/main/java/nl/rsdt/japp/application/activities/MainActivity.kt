package nl.rsdt.japp.application.activities

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import nl.rsdt.japp.R
import nl.rsdt.japp.application.Japp
import nl.rsdt.japp.application.JappPreferences
import nl.rsdt.japp.application.fragments.CarFragment
import nl.rsdt.japp.application.fragments.HomeFragment
import nl.rsdt.japp.application.fragments.JappMapFragment
import nl.rsdt.japp.application.navigation.FragmentNavigationManager
import nl.rsdt.japp.application.navigation.NavigationManager
import nl.rsdt.japp.jotial.auth.Authentication
import nl.rsdt.japp.jotial.data.nav.Join
import nl.rsdt.japp.jotial.data.nav.Location
import nl.rsdt.japp.jotial.data.nav.Resend
import nl.rsdt.japp.jotial.data.structures.area348.AutoInzittendeInfo
import nl.rsdt.japp.jotial.maps.MapManager
import nl.rsdt.japp.jotial.maps.window.CustomInfoWindowAdapter
import nl.rsdt.japp.jotial.maps.wrapper.IJotiMap
import nl.rsdt.japp.jotial.maps.wrapper.google.GoogleJotiMap
import nl.rsdt.japp.jotial.net.apis.AutoApi
import nl.rsdt.japp.service.AutoSocketHandler
import nl.rsdt.japp.service.LocationService
import nl.rsdt.japp.service.cloud.data.NoticeInfo
import nl.rsdt.japp.service.cloud.data.UpdateInfo
import nl.rsdt.japp.service.cloud.messaging.MessageManager
import org.acra.ACRA
import org.acra.ktx.sendWithAcra
import org.acra.log.ACRALog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), IJotiMap.OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener, SharedPreferences.OnSharedPreferenceChangeListener, MessageManager.UpdateMessageListener {

    /**
     * Manages the GoogleMap.
     */
    private var mapManager: MapManager = MapManager.instance

    /**
     * Manages the navigation between the fragments.
     */
    private var navigationManager: NavigationManager = NavigationManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        JappPreferences.isFirstRun = false
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
        Japp.updateManager?.add(this)

        /**
         * Register a on changed listener to the visible release_preferences.
         */
        JappPreferences.visiblePreferences.registerOnSharedPreferenceChangeListener(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

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

    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        navigationManager.onSaveInstanceState(savedInstanceState)
        mapManager.onSaveInstanceState(savedInstanceState)

            val id = JappPreferences.accountId
            if (id >= 0) {
                val autoApi = Japp.getApi(AutoApi::class.java)
                autoApi.getInfoById(JappPreferences.accountKey, id).enqueue(object :
                    Callback<AutoInzittendeInfo> {
                    override fun onResponse(call: Call<AutoInzittendeInfo>, response: Response<AutoInzittendeInfo>) {
                        if (response.code() == 200) {
                            val autoInfo = response.body()
                            if (autoInfo != null) {
                                val auto  = autoInfo.autoEigenaar!!
                                AutoSocketHandler.join(Join(JappPreferences.accountUsername, auto))
                            }
                        }
                    }

                    override fun onFailure(call: Call<AutoInzittendeInfo>, t: Throwable) {
                        t.sendWithAcra()
                        Log.e(TAG, t.toString())
                    }
                })
            }

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
        mapManager.update()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.refresh) {
            mapManager.update()
            /**
             * Update the vos status on the home fragment.
             */
            val fragment = navigationManager.getFragment(FragmentNavigationManager.FRAGMENT_HOME) as HomeFragment?
            if (fragment?.isAdded==true) {
                fragment.refresh()
            }
            val carfragment = navigationManager.getFragment(FragmentNavigationManager.FRAGMENT_CAR) as CarFragment?
            carfragment?.refresh()
            val id = JappPreferences.accountId
            if (id >= 0) {
                val autoApi = Japp.getApi(AutoApi::class.java)
                autoApi.getInfoById(JappPreferences.accountKey, id).enqueue(object :
                    Callback<AutoInzittendeInfo> {
                    override fun onResponse(call: Call<AutoInzittendeInfo>, response: Response<AutoInzittendeInfo>) {
                        if (response.code() == 200) {
                            val autoInfo = response.body()
                            if (autoInfo != null) {
                                val auto  = autoInfo.autoEigenaar!!
                                AutoSocketHandler.join(Join(JappPreferences.accountUsername, auto))
                                AutoSocketHandler.resend(Resend(auto))
                            }
                        }
                    }

                    override fun onFailure(call: Call<AutoInzittendeInfo>, t: Throwable) {
                        Log.e(TAG, t.message?:"error")
                        t.sendWithAcra()
                    }
                })
            }
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
        } else if (id == R.id.nav_help) {
            navigationManager.switchTo(FragmentNavigationManager.FRAGMENT_HELP)
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
        Japp.updateManager?.remove(this)

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
