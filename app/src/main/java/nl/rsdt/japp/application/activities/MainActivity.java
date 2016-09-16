package nl.rsdt.japp.application.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.List;

import nl.rsdt.japp.R;
import nl.rsdt.japp.application.Japp;
import nl.rsdt.japp.application.JappPreferences;
import nl.rsdt.japp.application.misc.SearchSuggestionsAdapter;
import nl.rsdt.japp.application.navigation.FragmentNavigationManager;

import nl.rsdt.japp.application.navigation.NavigationManager;
import nl.rsdt.japp.application.showcase.JappShowcaseSequence;
import nl.rsdt.japp.application.showcase.ShowcaseSequence;
import nl.rsdt.japp.jotial.auth.Authentication;
import nl.rsdt.japp.jotial.maps.MapManager;
import nl.rsdt.japp.jotial.maps.misc.KmlLoader;
import nl.rsdt.japp.service.cloud.data.NoticeInfo;
import nl.rsdt.japp.service.cloud.data.UpdateInfo;
import nl.rsdt.japp.service.cloud.messaging.JappFirebaseInstanceIdService;
import nl.rsdt.japp.service.cloud.messaging.MessageManager;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SharedPreferences.OnSharedPreferenceChangeListener, MessageManager.UpdateMessageListener {

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

    /**
     * Manages the searching.
     * */
    private SearchManager searchManager = new SearchManager();

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

            /**
             * Send the token to the server.
             * */
            JappFirebaseInstanceIdService.sendToken();

        }

        /**
         * Show the user the app via a sequence.
         * */
        JappShowcaseSequence sequence = new JappShowcaseSequence(this);
        sequence.setCallback(new ShowcaseSequence.OnSequenceCompletedCallback<MainActivity>() {
            @Override
            public void onSequenceCompleted(ShowcaseSequence<MainActivity> sequence) {
                sequence.end();
            }
        });
        sequence.start();

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

        /**
         * TODO: fix search system
         * */
        //searchManager.onCreateOptionsMenu(menu);
        return true;
    }

    public class SearchManager implements SearchView.OnQueryTextListener, SearchView.OnSuggestionListener {

        protected Menu menu;

        public void onCreateOptionsMenu(Menu menu) {
            // Get the SearchView and set the searchable configuration
            SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
            searchView.setOnQueryTextListener(this);
            searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
            searchView.setSuggestionsAdapter(new SearchSuggestionsAdapter(MainActivity.this, mapManager));
            searchView.setOnSuggestionListener(this);
            this.menu = menu;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }

        @Override
        public boolean onSuggestionSelect(int position) {
            return false;
        }

        @Override
        public boolean onSuggestionClick(int position) {
            SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
            SearchSuggestionsAdapter adapter = (SearchSuggestionsAdapter) searchView.getSuggestionsAdapter();
            SearchSuggestionsAdapter.SuggestionsCursor cursor = (SearchSuggestionsAdapter.SuggestionsCursor)adapter.getItem(0);
            List<String> entries = cursor.getEntries();
            String chosen = entries.get(position);
            searchView.setQuery(chosen, false);
            onQueryTextSubmit(chosen);
            searchView.clearFocus();
            return false;
        }




    }

    @Override
    /**
     * TODO: don't use final here
     * */
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
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
        } else if (id == R.id.nav_log_out) {
            Authentication.startLoginActivity(this);
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

        if(searchManager != null) {
            searchManager = null;
        }
    }

}
