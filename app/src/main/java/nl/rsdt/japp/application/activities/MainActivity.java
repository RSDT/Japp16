package nl.rsdt.japp.application.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;


import com.android.internal.util.Predicate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.Marker;
import com.rsdt.anl.RequestPool;
import com.rsdt.anl.WebResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import nl.rsdt.japp.R;
import nl.rsdt.japp.application.JappPreferences;
import nl.rsdt.japp.application.misc.SearchSuggestionsAdapter;
import nl.rsdt.japp.application.navigation.FragmentNavigationManager;

import nl.rsdt.japp.jotial.data.structures.area348.BaseInfo;
import nl.rsdt.japp.jotial.maps.MapManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SharedPreferences.OnSharedPreferenceChangeListener, SearchView.OnQueryTextListener {

    private Menu menu;

    private MapManager mapManager = new MapManager();

    private FragmentNavigationManager fragmentNavigationManager = new FragmentNavigationManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences preferences = getSharedPreferences("nl.rsdt.japp", MODE_PRIVATE);
        if(!preferences.contains(JappPreferences.FIRST_RUN))
        {
            preferences.edit().putBoolean(JappPreferences.FIRST_RUN, false).apply();
        }

        mapManager.onIntentCreate(getIntent());
        mapManager.onCreate(savedInstanceState);
        mapManager.addListener(new RequestPool.ExtendedRequestPoolListener(new Predicate<WebResponse>() {
            @Override
            public boolean apply(WebResponse response) {
                return response.getResponseCode() != 200;
            }
        }) {
            @Override
            public void onWebRequestCompleted(WebResponse response) {
                switch (response.getResponseCode())
                {
                    case 401:
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                }
            }
        });

        JappPreferences.getVisiblePreferences().registerOnSharedPreferenceChangeListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.nav_name)).setText(JappPreferences.getAccountUsername());
        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.nav_rank)).setText(JappPreferences.getAccountRank());
        //((ImageView)navigationView.getHeaderView(0).findViewById(R.id.nav_avatar)).setImageDrawable(AppData.getDrawable());

        fragmentNavigationManager.initialize(getFragmentManager());
        fragmentNavigationManager.onSavedInstance(savedInstanceState);


         /* mapManager.query(new WebRequest.Builder()
                .setId(VosController.REQUEST_ID)
                .setUrl(new ApiUrlBuilder().append("vos").append("a").append("all").build())
                .setMethod(WebRequestMethod.GET)
                .create());
        mapManager.query(new WebRequest.Builder()
                .setId(VosController.REQUEST_ID)
                .setUrl(new ApiUrlBuilder().append("vos").append("b").append("all").build())
                .setMethod(WebRequestMethod.GET)
                .create());

        mapManager.executeAsync(); */

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        fragmentNavigationManager.onSaveInstanceState(savedInstanceState);

        mapManager.onSaveInstanceState(savedInstanceState);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key)
        {
            case JappPreferences.ACCOUNT_USERNAME:
                NavigationView nView = (NavigationView)findViewById(R.id.nav_view);
                ((TextView)nView.getHeaderView(0).findViewById(R.id.nav_name)).setText(sharedPreferences.getString(key, "Unknown"));
                break;
            case JappPreferences.ACCOUNT_RANK:
                NavigationView view = (NavigationView)findViewById(R.id.nav_view);
                ((TextView)view.getHeaderView(0).findViewById(R.id.nav_name)).setText(sharedPreferences.getString(key, "Unknown"));
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
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

        fragmentNavigationManager.setupMap(mapManager);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            fragmentNavigationManager.switchTo(FragmentNavigationManager.FRAGMENT_HOME);
        } else if (id == R.id.nav_map) {
            fragmentNavigationManager.switchTo(FragmentNavigationManager.FRAGMENT_MAP);
        } else if (id == R.id.nav_settings) {
            fragmentNavigationManager.switchTo(FragmentNavigationManager.FRAGMENT_SETTINGS);
        } else if (id == R.id.nav_about) {
            fragmentNavigationManager.switchTo(FragmentNavigationManager.FRAGMENT_ABOUT);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onDestroy()
    {
        super.onDestroy();

        if(fragmentNavigationManager != null)
        {
            fragmentNavigationManager.onDestroy();
            fragmentNavigationManager = null;
        }

        if(mapManager != null)
        {
            mapManager.onDestroy();
            mapManager = null;
        }

        JappPreferences.getVisiblePreferences().unregisterOnSharedPreferenceChangeListener(this);
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
