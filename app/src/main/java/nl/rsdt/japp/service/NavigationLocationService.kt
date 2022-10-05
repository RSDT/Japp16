package nl.rsdt.japp.service

import android.app.Service
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Toast

import nl.rsdt.japp.R
import nl.rsdt.japp.application.JappPreferences
import nl.rsdt.japp.jotial.data.nav.Location
import nl.rsdt.japp.jotial.maps.NavigationLocationManager

class NavigationLocationService : Service(), NavigationLocationManager.OnNewLocation{
    private var binder: Binder = NavigationLocationBinder()

    init {
        val locationManager = NavigationLocationManager()
        locationManager.setCallback(this)
    }
    override fun onCreate(){
        AutoSocketHandler.init()
    }
    override fun onNewLocation(location: Location) {
        if (JappPreferences.isNavigationPhone) {
            try {
                val mesg = getString(R.string.location_received, location.username, location.lat, location.lon)
                showToast(mesg, Toast.LENGTH_SHORT)
                when (JappPreferences.navigationApp()) {
                    JappPreferences.NavigationApp.GoogleMaps -> {
                        val uristr = getString(R.string.google_uri, java.lang.Double.toString(location.lat), java.lang.Double.toString(location.lon))
                        val gmmIntentUri = Uri.parse(uristr)
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        startActivity(mapIntent)
                    }
                    JappPreferences.NavigationApp.Waze -> {
                        val uri = getString(R.string.waze_uri, java.lang.Double.toString(location.lat), java.lang.Double.toString(location.lon))
                        val wazeIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                        wazeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(wazeIntent)
                    }
                    JappPreferences.NavigationApp.OSMAnd -> {
                        val osmUri = getString(R.string.osmand_uri, java.lang.Double.toString(location.lat), java.lang.Double.toString(location.lon))
                        val osmIntent = Intent(Intent.ACTION_VIEW, Uri.parse(osmUri))
                        osmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(osmIntent)
                    }
                    JappPreferences.NavigationApp.OSMAndWalk -> {
                        val osmuriwalk = getString(R.string.osmandwalk_uri, java.lang.Double.toString(location.lat), java.lang.Double.toString(location.lon))
                        val osmWalkIntent = Intent(Intent.ACTION_VIEW, Uri.parse(osmuriwalk))
                        osmWalkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(osmWalkIntent)
                    }
                    JappPreferences.NavigationApp.Geo -> {
                        val geoUri = getString(R.string.geo_uri, java.lang.Double.toString(location.lat), java.lang.Double.toString(location.lon))
                        val geoIntent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
                        geoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(geoIntent)
                    }
                }
            } catch (e: ActivityNotFoundException) {
                println(e.toString())
                val mesg = getString(R.string.navigation_app_not_installed, JappPreferences.navigationApp().toString())
                showToast(mesg, Toast.LENGTH_SHORT)
            }
        }
    }
    override fun onNotInCar() {
        val mesg = getString(R.string.fout_not_in_car)
        showToast(mesg, Toast.LENGTH_SHORT)
    }

    override fun onBind(intent: Intent): IBinder? {
        // TODO: Return the communication channel to the service.
        return binder
    }

    private fun showToast(message: String, length: Int) {
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            Toast.makeText(applicationContext,
                    message,
                    length).show()
        }
    }

    internal inner class NavigationLocationBinder : Binder() {
        val instance: NavigationLocationService
            get() = this@NavigationLocationService
    }
}
