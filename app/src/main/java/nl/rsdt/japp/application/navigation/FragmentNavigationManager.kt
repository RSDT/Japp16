package nl.rsdt.japp.application.navigation

import android.app.Fragment
import android.app.FragmentManager
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import com.google.android.material.navigation.NavigationView
import nl.rsdt.japp.R
import nl.rsdt.japp.application.activities.MainActivity
import nl.rsdt.japp.application.fragments.*
import nl.rsdt.japp.jotial.maps.wrapper.IJotiMap
import java.util.*

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 8-7-2016
 * Description...
 */
open class FragmentNavigationManager {

    private val backstack = ArrayList<String>()

    private var nView: NavigationView? = null

    private var actionbar: ActionBar? = null

    private var manager: FragmentManager? = null

    private var currentFragmentTag: String = ""

    private var homeFragment: HomeFragment? = null

    private var mapFragment: JappMapFragment? = null

    private var carFragment: CarFragment? = null

    private var preferenceFragment: JappPreferenceFragment? = null

    private var aboutFragment: AboutFragment? = null

    private var helpFragment: HelpFragment? = null

    fun getFragment(tag: String): Fragment? {
        when (tag) {
            FRAGMENT_HOME -> return homeFragment
            FRAGMENT_CAR -> return carFragment
            FRAGMENT_MAP -> return mapFragment
            FRAGMENT_SETTINGS -> return preferenceFragment
            FRAGMENT_ABOUT -> return aboutFragment
            FRAGMENT_HELP -> return helpFragment
        }
        return null
    }

    fun hasBackStack(): Boolean {
        return backstack.isNotEmpty()
    }

    open fun initialize(mainActivity: MainActivity) {
        this.nView = mainActivity.findViewById(R.id.nav_view)
        this.actionbar = mainActivity.supportActionBar
        this.manager = mainActivity.fragmentManager
        setupFragments()
    }

    fun onSaveInstanceState(saveInstanceState: Bundle?) {
        if (!currentFragmentTag.isEmpty()) {
            saveInstanceState?.putString(BUNDLE_KEY_FRAGMENT, currentFragmentTag)
        }
    }

    fun onSavedInstance(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            val value = savedInstanceState.getString(BUNDLE_KEY_FRAGMENT, currentFragmentTag)
            switchTo(value)
        }
    }

    fun switchTo(fragment: String) {
        switchTo(fragment, true)
    }

    private fun switchTo(fragment: String, addToStack: Boolean) {
        if (currentFragmentTag == fragment) return

        val ft = manager?.beginTransaction()

        when (fragment) {
            FRAGMENT_HOME -> ft?.show(homeFragment)
            FRAGMENT_MAP -> ft?.show(mapFragment)
            FRAGMENT_CAR -> ft?.show(carFragment)
            FRAGMENT_SETTINGS -> ft?.show(preferenceFragment)
            FRAGMENT_ABOUT -> ft?.show(aboutFragment)
            FRAGMENT_HELP -> ft?.show(helpFragment)
        }

        when (currentFragmentTag) {
            FRAGMENT_HOME -> ft?.hide(homeFragment)
            FRAGMENT_MAP -> ft?.hide(mapFragment)
            FRAGMENT_CAR -> ft?.hide(carFragment)
            FRAGMENT_SETTINGS -> ft?.hide(preferenceFragment)
            FRAGMENT_ABOUT -> ft?.hide(aboutFragment)
            FRAGMENT_HELP -> ft?.hide(helpFragment)
        }

        ft?.commit()
        if (addToStack) {
            backstack.add(currentFragmentTag)
        }
        currentFragmentTag = fragment
        updateToolbarTitle()
        updateCheckedState()
    }

    fun setupMap(callback: IJotiMap.OnMapReadyCallback) {
        if (mapFragment == null) {//// TODO: 09/08/17 dit is nooit anders krijg je een runtime exception bij de vorige if statement
            mapFragment = manager?.findFragmentByTag(FRAGMENT_MAP) as JappMapFragment
        }

        mapFragment?.getMapAsync(callback)

    }

    fun onBackPressed() {
        if (backstack.isNotEmpty()) {
            val last = backstack.size - 1
            switchTo(backstack[last], false)
            backstack.removeAt(last)
        }
    }

    private fun updateToolbarTitle() {
        if (actionbar != null) {
            when (currentFragmentTag) {
                FRAGMENT_HOME -> actionbar?.setTitle(R.string.home)
                FRAGMENT_MAP -> actionbar?.setTitle(R.string.map)
                FRAGMENT_CAR -> actionbar?.setTitle(R.string.car)
                FRAGMENT_SETTINGS -> actionbar?.setTitle(R.string.settings)
                FRAGMENT_ABOUT -> actionbar?.setTitle(R.string.about)
                FRAGMENT_HELP -> actionbar?.setTitle(R.string.help)
            }
        }
    }

    private fun updateCheckedState() {

        when (currentFragmentTag) {
            FRAGMENT_HOME -> nView?.menu?.findItem(R.id.nav_home)?.isChecked = true
            FRAGMENT_MAP -> nView?.menu?.findItem(R.id.nav_map)?.isChecked = true
            FRAGMENT_CAR -> nView?.menu?.findItem(R.id.nav_car)?.isChecked = true
            FRAGMENT_SETTINGS -> nView?.menu?.findItem(R.id.nav_settings)?.isChecked = true
            FRAGMENT_ABOUT -> nView?.menu?.findItem(R.id.nav_about)?.isChecked = true
            FRAGMENT_HELP -> nView?.menu?.findItem(R.id.nav_help)?.isChecked = true
        }
    }


    private fun setupFragments() {
        val ft = manager?.beginTransaction()

        homeFragment = manager?.findFragmentByTag(HomeFragment.TAG) as HomeFragment?
        if (homeFragment == null) {
            homeFragment = HomeFragment()
            ft?.add(R.id.container, homeFragment, HomeFragment.TAG)
        }
        ft?.hide(homeFragment)

        carFragment = manager?.findFragmentByTag(CarFragment.TAG) as CarFragment?
        if (carFragment == null) {
            carFragment = CarFragment()
            ft?.add(R.id.container, carFragment, CarFragment.TAG)
        }
        ft?.hide(carFragment)

        mapFragment = manager?.findFragmentByTag(JappMapFragment.TAG) as JappMapFragment?
        if (mapFragment == null) {
            mapFragment = JappMapFragment()
            ft?.add(R.id.container, mapFragment, JappMapFragment.TAG)
        }
        ft?.show(mapFragment)
        currentFragmentTag = FRAGMENT_MAP
        updateToolbarTitle()
        updateCheckedState()

        preferenceFragment = manager?.findFragmentByTag(JappPreferenceFragment.TAG) as JappPreferenceFragment?
        if (preferenceFragment == null) {
            preferenceFragment = JappPreferenceFragment()
            ft?.add(R.id.container, preferenceFragment, JappPreferenceFragment.TAG)
        }
        ft?.hide(preferenceFragment)

        aboutFragment = manager?.findFragmentByTag(AboutFragment.TAG) as AboutFragment?
        if (aboutFragment == null) {
            aboutFragment = AboutFragment()
            ft?.add(R.id.container, aboutFragment, AboutFragment.TAG)
        }
        ft?.hide(aboutFragment)

        helpFragment = manager?.findFragmentByTag(HelpFragment.TAG) as HelpFragment?
        if (helpFragment == null) {
            helpFragment = HelpFragment()
            ft?.add(R.id.container, helpFragment, HelpFragment.TAG)
        }
        ft?.hide(helpFragment)

        ft?.commit()
    }

    open fun onDestroy() {
        if (manager != null) {
            manager = null
        }

        if (homeFragment != null) {
            homeFragment = null
        }

        if (mapFragment != null) {
            mapFragment = null
        }
        if (carFragment != null) {
            carFragment = null
        }
        if (preferenceFragment != null) {
            preferenceFragment = null
        }

        if (aboutFragment != null) {
            aboutFragment = null
        }
        if (helpFragment != null) {
            helpFragment = null
        }


    }

    companion object {

        val FRAGMENT_HOME = "FRAGMENT_HOME"

        val FRAGMENT_MAP = "FRAGMENT_MAP"

        val FRAGMENT_SETTINGS = "FRAGMENT_SETTINGS"

        val FRAGMENT_ABOUT = "FRAGMENT_ABOUT"

        val FRAGMENT_CAR = "FRAGMENT_CAR"

        val FRAGMENT_HELP = "FRAGMENT_HELP"

        private val BUNDLE_KEY_FRAGMENT = "BUNDLE_KEY_FRAGMENT"
    }


}
