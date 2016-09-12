package nl.rsdt.japp.application.navigation;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBar;

import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import nl.rsdt.japp.R;
import nl.rsdt.japp.application.activities.MainActivity;
import nl.rsdt.japp.application.fragments.AboutFragment;
import nl.rsdt.japp.application.fragments.HomeFragment;
import nl.rsdt.japp.application.fragments.JappMapFragment;
import nl.rsdt.japp.application.fragments.JappPreferenceFragment;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 8-7-2016
 * Description...
 */
public class FragmentNavigationManager {

    public static final String FRAGMENT_HOME = "FRAGMENT_HOME";

    public static final String FRAGMENT_MAP = "FRAGMENT_MAP";

    public static final String FRAGMENT_SETTINGS = "FRAGMENT_SETTINGS";

    public static final String FRAGMENT_ABOUT = "FRAGMENT_ABOUT";

    private static final String BUNDLE_KEY_FRAGMENT = "BUNDLE_KEY_FRAGMENT";

    private ArrayList<String> backstack = new ArrayList<>();

    private NavigationView nView;

    private ActionBar actionbar;

    private FragmentManager manager;

    private String currentFragmentTag = "";

    private HomeFragment homeFragment;

    private JappMapFragment mapFragment;

    private JappPreferenceFragment preferenceFragment;

    private AboutFragment aboutFragment;

    public boolean hasBackStack() {
        return !backstack.isEmpty();
    }

    public void initialize(MainActivity mainActivity)
    {
        this.nView = (NavigationView) mainActivity.findViewById(R.id.nav_view);
        this.actionbar = mainActivity.getSupportActionBar();
        this.manager = mainActivity.getFragmentManager();
        setupFragments();
    }

    public void onSaveInstanceState(Bundle saveInstanceState)
    {
        if(!currentFragmentTag.isEmpty())
        {
            saveInstanceState.putString(BUNDLE_KEY_FRAGMENT, currentFragmentTag);
        }
    }

    public void onSavedInstance(Bundle savedInstanceState)
    {
        if(savedInstanceState != null)
        {
            String value = savedInstanceState.getString(BUNDLE_KEY_FRAGMENT, currentFragmentTag);
            switchTo(value);
        }
    }

    public void switchTo(String fragment) {
       switchTo(fragment, true);
    }

    private void switchTo(String fragment, boolean addToStack) {
        if(currentFragmentTag == null || currentFragmentTag.equals(fragment)) return;

        FragmentTransaction ft = manager.beginTransaction();

        switch (fragment)
        {
            case FRAGMENT_HOME:
                ft.show(homeFragment);
                break;
            case FRAGMENT_MAP:
                ft.show(mapFragment);
                break;
            case FRAGMENT_SETTINGS:
                ft.show(preferenceFragment);
                break;
            case FRAGMENT_ABOUT:
                ft.show(aboutFragment);
                break;
        }

        switch (currentFragmentTag)
        {
            case FRAGMENT_HOME:
                ft.hide(homeFragment);
                break;
            case FRAGMENT_MAP:
                ft.hide(mapFragment);
                break;
            case FRAGMENT_SETTINGS:
                ft.hide(preferenceFragment);
                break;
            case FRAGMENT_ABOUT:
                ft.hide(aboutFragment);
                break;
        }

        ft.commit();
        if(addToStack) {
            backstack.add(currentFragmentTag);
        }
        currentFragmentTag = fragment;
        updateToolbarTitle();
        updateCheckedState();
    }

    public void setupMap(OnMapReadyCallback callback)
    {
        if(mapFragment.getGoogleMap() == null) {
            if (mapFragment == null) {
                mapFragment = (JappMapFragment) manager.findFragmentByTag(FRAGMENT_MAP);
                mapFragment.getMapAsync(callback);
            } else {
                mapFragment.getMapAsync(callback);
            }
        }
    }

    public void onBackPressed() {
        if(!backstack.isEmpty()) {
            int last = backstack.size() - 1;
            switchTo(backstack.get(last), false);
            backstack.remove(last);
        }
    }

    private void updateToolbarTitle() {
        if(currentFragmentTag != null && actionbar != null) {
            switch (currentFragmentTag)
            {
                case FRAGMENT_HOME:
                    actionbar.setTitle("Thuis");
                    break;
                case FRAGMENT_MAP:
                    actionbar.setTitle("Kaart");
                    break;
                case FRAGMENT_SETTINGS:
                    actionbar.setTitle("Instellingen");
                    break;
                case FRAGMENT_ABOUT:
                    actionbar.setTitle("Over Japp");
                    break;
            }
        }
    }

    private void updateCheckedState() {

        switch(currentFragmentTag)
        {
            case FRAGMENT_HOME:
                nView.getMenu().findItem(R.id.nav_home).setChecked(true);
                break;
            case FRAGMENT_MAP:
                nView.getMenu().findItem(R.id.nav_map).setChecked(true);
                break;
            case FRAGMENT_SETTINGS:
                nView.getMenu().findItem(R.id.nav_settings).setChecked(true);
                break;
            case FRAGMENT_ABOUT:
                nView.getMenu().findItem(R.id.nav_about).setChecked(true);
                break;
        }
    }


    private void setupFragments()
    {
        FragmentTransaction ft = manager.beginTransaction();

        homeFragment = (HomeFragment) manager.findFragmentByTag(HomeFragment.TAG);
        if(homeFragment == null)
        {
            homeFragment = new HomeFragment();
            ft.add(R.id.container, homeFragment, HomeFragment.TAG);
        }
        ft.hide(homeFragment);


        mapFragment = (JappMapFragment) manager.findFragmentByTag(JappMapFragment.TAG);
        if(mapFragment == null)
        {
            mapFragment = new JappMapFragment();
            ft.add(R.id.container, mapFragment, JappMapFragment.TAG);
        }
        ft.show(mapFragment);
        currentFragmentTag = FRAGMENT_MAP;
        updateToolbarTitle();
        updateCheckedState();

        preferenceFragment  = (JappPreferenceFragment)manager.findFragmentByTag(JappPreferenceFragment.TAG);
        if(preferenceFragment == null)
        {
            preferenceFragment = new JappPreferenceFragment();
            ft.add(R.id.container, preferenceFragment, JappPreferenceFragment.TAG);
        }
        ft.hide(preferenceFragment);

        aboutFragment = (AboutFragment)manager.findFragmentByTag(AboutFragment.TAG);
        if(aboutFragment == null)
        {
            aboutFragment = new AboutFragment();
            ft.add(R.id.container, aboutFragment, AboutFragment.TAG);
        }
        ft.hide(aboutFragment);

        ft.commit();
    }

    public void onDestroy()
    {
        if(manager != null)
        {
            manager = null;
        }

        if(homeFragment != null)
        {
            homeFragment = null;
        }

        if(mapFragment != null)
        {
            mapFragment = null;
        }

        if(preferenceFragment != null)
        {
            preferenceFragment = null;
        }

        if(aboutFragment != null)
        {
            aboutFragment = null;
        }


    }


}
