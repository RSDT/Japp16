package nl.rsdt.japp.application;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import nl.rsdt.japp.R;
import nl.rsdt.japp.application.navigation.FragmentNavigationManager;
import nl.rsdt.japp.jotial.io.AppData;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SharedPreferences.OnSharedPreferenceChangeListener {


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

        JappPreferences.getUserPreferences().registerOnSharedPreferenceChangeListener(this);

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

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        fragmentNavigationManager.onSaveInstanceState(savedInstanceState);

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

        fragmentNavigationManager.setupMap(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(52.015379, 6.025979), 10));
            }
        });
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

        fragmentNavigationManager.onDestroy();

        JappPreferences.getUserPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }


}
