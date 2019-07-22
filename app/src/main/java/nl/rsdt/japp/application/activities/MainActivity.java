package nl.rsdt.japp.application.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.messaging.FirebaseMessaging;

import nl.rsdt.japp.R;
import nl.rsdt.japp.application.Japp;
import nl.rsdt.japp.application.JappPreferences;
import nl.rsdt.japp.application.fragments.HomeFragment;
import nl.rsdt.japp.application.fragments.JappMapFragment;
import nl.rsdt.japp.application.fragments.TmpCarFragment;
import nl.rsdt.japp.application.navigation.FragmentNavigationManager;
import nl.rsdt.japp.application.navigation.NavigationManager;
import nl.rsdt.japp.application.showcase.JappShowcaseSequence;
import nl.rsdt.japp.application.showcase.ShowcaseSequence;
import nl.rsdt.japp.jotial.auth.Authentication;
import nl.rsdt.japp.jotial.data.structures.area348.MetaColorInfo;
import nl.rsdt.japp.jotial.maps.MapManager;
import nl.rsdt.japp.jotial.maps.window.CustomInfoWindowAdapter;
import nl.rsdt.japp.jotial.maps.wrapper.IJotiMap;
import nl.rsdt.japp.jotial.maps.wrapper.google.GoogleJotiMap;
import nl.rsdt.japp.jotial.net.apis.MetaApi;
import nl.rsdt.japp.service.LocationService;
import nl.rsdt.japp.service.cloud.data.NoticeInfo;
import nl.rsdt.japp.service.cloud.data.UpdateInfo;
import nl.rsdt.japp.service.cloud.messaging.JappFirebaseInstanceIdService;
import nl.rsdt.japp.service.cloud.messaging.MessageManager;
import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity
        implements IJotiMap.OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener, SharedPreferences.OnSharedPreferenceChangeListener, MessageManager.UpdateMessageListener {

    /**
     * Defines a tag for this class.
     * */
    public static final String TAG = "MainActivity";

    /**
     * Manages the GoogleMap.
     * */
    private MapManager mapManager = new MapManager();

    /**
     * Manages the navigation between the fragments.
     * */
    private NavigationManager navigationManager = new NavigationManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * Subscribe to the updates topic.
         * */
        FirebaseMessaging.getInstance().subscribeToTopic("updates");

        /**
         * Set a interceptor so that requests that give a 401 will result in a login activity.
         * */
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
         * */
        Japp.getUpdateManager().add(this);

        /**
         * Register a on changed listener to the visible release_preferences.
         * */
        JappPreferences.getVisiblePreferences().registerOnSharedPreferenceChangeListener(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(JappPreferences.shacoEnabled() &&
                (JappPreferences.getAccountUsername().equals("David") || JappPreferences.getAccountUsername().equals("test"))
                ) {
            MediaPlayer player = MediaPlayer.create(this, R.raw.shaco_tank_engine);
            player.start();
        }

        /**
         * Checks if this is the first run of the app.
         * */
        if(JappPreferences.isFirstRun())
        {


            /**
             * Send the token to the server.
             * */
            JappFirebaseInstanceIdService.sendToken();

            /**
             * Show the user around the app via a sequence.
             * */
            JappShowcaseSequence sequence = new JappShowcaseSequence(this);
            sequence.setCallback(new ShowcaseSequence.OnSequenceCompletedCallback<MainActivity>() {
                @Override
                public void onSequenceCompleted(ShowcaseSequence<MainActivity> sequence) {
                    sequence.end();
                }
            });
            sequence.start();
            MetaApi metaApi = Japp.getApi(MetaApi.class);
            //MetaInfo metaInfo = metaApi.getMetaInfo(JappPreferences.getAccountKey());
            metaApi.getMetaColor(JappPreferences.getAccountKey()).enqueue(new Callback<MetaColorInfo>() {
                @Override
                public void onResponse(Call<MetaColorInfo> call, retrofit2.Response<MetaColorInfo> response) {
                    MetaColorInfo colorInfo = response.body();
                    JappPreferences.setColorHex("a", colorInfo.ColorCode.a);
                    JappPreferences.setColorHex("b", colorInfo.ColorCode.b);
                    JappPreferences.setColorHex("c", colorInfo.ColorCode.c);
                    JappPreferences.setColorHex("d", colorInfo.ColorCode.d);
                    JappPreferences.setColorHex("e", colorInfo.ColorCode.e);
                    JappPreferences.setColorHex("f", colorInfo.ColorCode.f);
                    JappPreferences.setColorHex("x", colorInfo.ColorCode.x);
                    JappPreferences.setColorName("a",colorInfo.ColorName.a);
                    JappPreferences.setColorName("b",colorInfo.ColorName.b);
                    JappPreferences.setColorName("c",colorInfo.ColorName.c);
                    JappPreferences.setColorName("d",colorInfo.ColorName.d);
                    JappPreferences.setColorName("e",colorInfo.ColorName.e);
                    JappPreferences.setColorName("f",colorInfo.ColorName.f);
                    JappPreferences.setColorName("x",colorInfo.ColorName.x);
                }

                @Override
                public void onFailure(Call<MetaColorInfo> call, Throwable t) {

                }
            });

            /**
             * Set the the first run value to false.
             * */
            JappPreferences.setFirstRun(false);
        }

        /**
         * Setup the MapManager.
         * */
        mapManager.onIntentCreate(getIntent());
        mapManager.onCreate(savedInstanceState);

        /**
         * Setup the NavigationDrawer.
         * */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        /**
         * Setup the NavigationView.
         * */
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /**
         * Initialize the NavigationManager.
         * */
        navigationManager.initialize(this);
        navigationManager.onSavedInstance(savedInstanceState);

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        navigationManager.onSaveInstanceState(savedInstanceState);
        mapManager.onSaveInstanceState(savedInstanceState);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onMapReady(IJotiMap jotiMap) {
        /**
         * First set the custom InfoWindowAdapter and then invoke the onMapReady on the MapManager.
         * */
        if (jotiMap instanceof GoogleJotiMap) {
            GoogleJotiMap googleJotiMap = (GoogleJotiMap) jotiMap;
            jotiMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(getLayoutInflater(), googleJotiMap.getGoogleMap())); //// TODO: 07/08/17 change the GoogleMap to a JotiMap
        }else {
            //// TODO: 09/08/17 do stuff
        }
        mapManager.onMapReady(jotiMap);
    }

    /**
     * TODO: don't use final here
     * */
    public void onUpdateMessageReceived(final UpdateInfo info) {
        if(JappPreferences.isAutoUpdateEnabled()) {
            Snackbar.make(findViewById(R.id.container), "Updating " + info.type, Snackbar.LENGTH_LONG).show();
            mapManager.onUpdateMessageReceived(info);
        } else {
            Snackbar.make(findViewById(R.id.container), "Update beschikbaar voor " + info.type, Snackbar.LENGTH_LONG)
                    .setAction("Update!", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mapManager.onUpdateMessageReceived(info);
                        }
                    })
                    .show();
        }
    }

    /**
     * TODO: don't use final here
     * */
    @Override
    public void onNoticeMessageReceived(final NoticeInfo info) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(info.getTitle())
                        .setMessage(info.getBody())
                        .setIcon(info.getDrawable())
                        .create()
                        .show();
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case JappPreferences.USE_OSM:
                recreate();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        /**
         * TODO: fix search system
         * */
        //searchManager.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    /**
     * TODO: don't use final here
     * */
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                mapManager.update();
                /**
                 * Update the vos status on the home fragment.
                 * */
                HomeFragment fragment = (HomeFragment) navigationManager.getFragment(FragmentNavigationManager.FRAGMENT_HOME);
                if(fragment != null) {
                    fragment.refresh();
                }
                TmpCarFragment carfragment = (TmpCarFragment) navigationManager.getFragment(FragmentNavigationManager.FRAGMENT_CAR);
                if(carfragment != null) {
                    carfragment.refresh();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(navigationManager.hasBackStack()) {
                navigationManager.onBackPressed();
            } else {
                super.onBackPressed();
            }
        }
    }

    public void onResume()
    {
        super.onResume();
        navigationManager.setupMap(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        String action = intent.getAction();
        if(action != null && action.equals(LocationService.ACTION_REQUEST_LOCATION_SETTING)) {
            JappMapFragment mapFragment = (JappMapFragment) navigationManager.getFragment(FragmentNavigationManager.FRAGMENT_MAP);
            if(mapFragment != null) {
                mapFragment.getMovementManager().requestLocationSettingRequest();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode) {
            case JappMapFragment.REQUEST_CHECK_SETTINGS:
                JappMapFragment mapFragment = (JappMapFragment) navigationManager.getFragment(FragmentNavigationManager.FRAGMENT_MAP);
                if(mapFragment != null) {
                    mapFragment.getMovementManager().postResolutionResultToService(resultCode);
                }
                break;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            navigationManager.switchTo(FragmentNavigationManager.FRAGMENT_HOME);
        } else if (id == R.id.nav_map) {
            navigationManager.switchTo(FragmentNavigationManager.FRAGMENT_MAP);
        } else if (id == R.id.nav_settings) {
            navigationManager.switchTo(FragmentNavigationManager.FRAGMENT_SETTINGS);
        } else if (id == R.id.nav_car) {
            navigationManager.switchTo(FragmentNavigationManager.FRAGMENT_CAR);
        } else if (id == R.id.nav_about) {
            navigationManager.switchTo(FragmentNavigationManager.FRAGMENT_ABOUT);
        } else if (id == R.id.nav_log_out) {
            Authentication.startLoginActivity(this);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onStop() {
        super.onStop();
    }

    public void onDestroy()
    {
        super.onDestroy();

        Japp.setInterceptor(null);

        /**
         * Remove this as UpdateMessageListener.
         * */
        Japp.getUpdateManager().remove(this);

        /**
         * Unregister this as OnSharedPreferenceChangeListener.
         * */
        JappPreferences.getVisiblePreferences().unregisterOnSharedPreferenceChangeListener(this);

        if(mapManager != null)
        {
            mapManager.onDestroy();
            mapManager = null;
        }

        if(navigationManager != null)
        {
            navigationManager.onDestroy();
            navigationManager = null;
        }

    }

}
