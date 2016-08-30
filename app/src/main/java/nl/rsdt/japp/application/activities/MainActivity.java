package nl.rsdt.japp.application.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;


import com.android.internal.util.Predicate;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.Marker;
import com.rsdt.anl.RequestPool;
import com.rsdt.anl.WebResponse;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import nl.rsdt.japp.R;
import nl.rsdt.japp.application.Japp;
import nl.rsdt.japp.application.JappPreferences;
import nl.rsdt.japp.application.misc.SearchSuggestionsAdapter;
import nl.rsdt.japp.application.navigation.FragmentNavigationManager;

import nl.rsdt.japp.application.navigation.NavigationManager;
import nl.rsdt.japp.application.showcase.ShowCaseTour;
import nl.rsdt.japp.jotial.data.structures.area348.BaseInfo;
import nl.rsdt.japp.jotial.maps.MapManager;
import nl.rsdt.japp.jotial.maps.locations.LocationProviderService;
import nl.rsdt.japp.service.LocationServiceManager;
import nl.rsdt.japp.service.cloud.data.UpdateInfo;
import nl.rsdt.japp.service.cloud.messaging.UpdateManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SharedPreferences.OnSharedPreferenceChangeListener, SearchView.OnQueryTextListener, UpdateManager.UpdateMessageListener {

    public static final String TAG = "MainActivity";

    /**
     * TODO: make the search system more clean
     * */
    private Menu menu;

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

        /**
         * Checks if this is the first run of the app.
         * */
        if(JappPreferences.isFirstRun())
        {
            /**
             * Set the the first run value to false.
             * */
            JappPreferences.setFirstRun(false);
        }

        /**
         * Do some things for the first run.
         * */
        ShowCaseTour tour = new ShowCaseTour(this);
        tour.showcase();

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
    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.
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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        navigationManager.onSaveInstanceState(savedInstanceState);
        mapManager.onSaveInstanceState(savedInstanceState);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Get the SearchView and set the searchable configuration
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setSuggestionsAdapter(new SearchSuggestionsAdapter(this, mapManager));

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                SearchView searchView = (SearchView) MainActivity.this.menu.findItem(R.id.search).getActionView();
                SearchSuggestionsAdapter adapter = (SearchSuggestionsAdapter) searchView.getSuggestionsAdapter();
                SearchSuggestionsAdapter.SuggestionsCursor cursor = (SearchSuggestionsAdapter.SuggestionsCursor)adapter.getItem(0);
                List<String> entries = cursor.getEntries();
                String chosen = entries.get(position);
                searchView.setQuery(chosen, false);
                onQueryTextSubmit(chosen);
                searchView.clearFocus();
                return false;
            }
        });
        this.menu = menu;
        return true;
    }

    @Override
    /**
     * TODO: don't use final here
     * */
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                item.setEnabled(false);
                Japp.getRequestQueue().addRequestFinishedListener(new RequestQueue.RequestFinishedListener<JSONArray>() {
                    @Override
                    public void onRequestFinished(Request<JSONArray> request) {
                        item.setEnabled(true);
                        Japp.getRequestQueue().removeRequestFinishedListener(this);
                    }
                });
                mapManager.update();
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
            super.onBackPressed();
        }
    }

    public void onResume()
    {
        super.onResume();
        navigationManager.setupMap(mapManager);
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
        } else if (id == R.id.nav_about) {
            navigationManager.switchTo(FragmentNavigationManager.FRAGMENT_ABOUT);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onStop() {
        super.onStop();
    }

    public void onDestroy()
    {
        super.onDestroy();

        /**
         * Cancel all the requests associated with this activity.
         * */
        Japp.getRequestQueue().cancelAll(TAG);

        /**
         * Remove this as UpdateMessageListener.
         * */
        Japp.getUpdateManager().remove(this);

        /**
         * Unregister this as OnSharedPreferenceChangeListener.
         * */
        JappPreferences.getVisiblePreferences().unregisterOnSharedPreferenceChangeListener(this);

        if(navigationManager != null)
        {
            navigationManager.onDestroy();
            navigationManager = null;
        }

        if(mapManager != null)
        {
            mapManager.onDestroy();
            mapManager = null;
        }

    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        ArrayList<BaseInfo> possibles =  mapManager.searchFor(query.toLowerCase(Locale.ROOT));
        if(possibles.size() == 1) {
            Marker marker = mapManager.getAssociatedMarker(possibles.get(0));
            mapManager.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 20));
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }


}
