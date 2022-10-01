package nl.rsdt.japp.service

import android.app.Activity
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationSettingsResult
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.maps.model.LatLng
import nl.rsdt.japp.R
import nl.rsdt.japp.application.Japp
import nl.rsdt.japp.application.JappPreferences
import nl.rsdt.japp.application.activities.MainActivity
import nl.rsdt.japp.jotial.data.bodies.HunterPostBody
import nl.rsdt.japp.jotial.data.structures.area348.AutoInzittendeInfo
import nl.rsdt.japp.jotial.data.structures.area348.HunterInfo
import nl.rsdt.japp.jotial.maps.NavigationLocationManager
import nl.rsdt.japp.jotial.maps.deelgebied.Deelgebied
import nl.rsdt.japp.jotial.maps.locations.LocationProviderService
import nl.rsdt.japp.jotial.net.apis.AutoApi
import nl.rsdt.japp.jotial.net.apis.HunterApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 8-7-2016
 * Description...
 */
class LocationService : LocationProviderService<Binder>(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val binder = LocationBinder()

    private var wasSending = false

    private var listener: OnResolutionRequiredListener? = null

    internal var lastUpdate = Calendar.getInstance()
    private var request: LocationProviderService.LocationRequest? = null

    private val locationSettingReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent?.action?.matches("android.location.PROVIDERS_CHANGED".toRegex()) == true) {
                // Make an action or refresh an already managed state.
                val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                if (!gps) {
                    val notificationIntent = Intent(this@LocationService, MainActivity::class.java)
                    notificationIntent.action = ACTION_REQUEST_LOCATION_SETTING
                    notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    val pendingIntent = PendingIntent.getActivity(this@LocationService, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    showLocationNotification(getString(R.string.japp_not_sending_location), getString(R.string.torn_on_gps), Color.rgb(244, 66, 66), pendingIntent)
                }
            }
        }
    }

    fun setListener(listener: OnResolutionRequiredListener?) {
        this.listener = listener
    }

    override fun onCreate() {
        super.onCreate()
        JappPreferences.visiblePreferences.registerOnSharedPreferenceChangeListener(this)

        registerReceiver(locationSettingReceiver, IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION))

        val locationManager = NavigationLocationManager()
        request = LocationProviderService.LocationRequest(
            accuracy = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY,
            interval = 60_000,
            fastestInterval = 60_000)
        if (JappPreferences.isUpdatingLocationToServer) {
            request?.let { addRequest(it) }
        }
        locationManager.setCallback(object : NavigationLocationManager.OnNewLocation {
            override fun onNewLocation(location: nl.rsdt.japp.jotial.data.firebase.Location) {
                if (JappPreferences.isNavigationPhone) {
                    try {
                        val mesg = getString(R.string.location_received, location.createdBy, location.lat, location.lon)
                        showToast(mesg, Toast.LENGTH_LONG)
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
                showToast(mesg, Toast.LENGTH_LONG)
            }
        })
    }

    private fun showToast(message: String, length: Int) {
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            Toast.makeText(applicationContext,
                    message,
                    length).show()
        }
    }

    fun showLocationNotification(title: String, color: Int) {
        showLocationNotification(title, getString(R.string.open_japp), color)
    }

    fun showLocationNotification(title: String, description: String, color: Int) {
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val intent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        showLocationNotification(title, description, color, intent)
    }

    fun showLocationNotification(title: String, description: String, color: Int, intent: PendingIntent) {
        val notification = NotificationCompat.Builder(this, LOCATION_NOTIFICATION_CHANNEL)
                .setContentTitle(title)
                .setContentText(description)
                .setSmallIcon(R.drawable.ic_my_location_white_48dp)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                .setColor(color)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(intent)
                .build()
        val mNotifyMgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotifyMgr.notify(1923, notification)
    }

    override fun onLocationChanged(location: Location) {
        super.onLocationChanged(location)
        Japp.lastLocation = location
        val dif = Calendar.getInstance().timeInMillis - lastUpdate.timeInMillis

        val shouldSend = JappPreferences.isUpdatingLocationToServer

        if (shouldSend != wasSending) {

            val title: String
            val color: Int
            if (shouldSend) {
                title = getString(R.string.japp_sends_location)
                color = Color.rgb(113, 244, 66)
            } else {
                title = getString(R.string.japp_not_sending_location)
                color = Color.rgb(244, 66, 66)
            }
            showLocationNotification(title, color)
        }

        if (shouldSend) {
            if (dif >= Math.round(JappPreferences.locationUpdateIntervalInMs)) {
                sendLocation(location)
            }
        }
    }

    override fun onResult(result: LocationSettingsResult) {
        val status = result.status
        when (status.statusCode) {
            LocationSettingsStatusCodes.SUCCESS -> {
                wasSending = JappPreferences.isUpdatingLocationToServer
                if (!wasSending) {
                    showLocationNotification(getString(R.string.japp_sends_location), Color.rgb(244, 66, 66))
                } else {
                    showLocationNotification(getString(R.string.japp_not_sending_location), Color.rgb(113, 244, 66))
                }
            }
            LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> if (listener != null) {
                listener!!.onResolutionRequired(status)
            }
        }
    }

    fun handleResolutionResult(code: Int) {
        when (code) {
            Activity.RESULT_OK -> {
                restartLocationUpdates()
                val wasSending = JappPreferences.isUpdatingLocationToServer
                if (!wasSending) {
                    showLocationNotification(getString(R.string.japp_not_sending_location), getString(R.string.turn_on_location_in_app), Color.rgb(244, 66, 66))
                } else {
                    showLocationNotification(getString(R.string.japp_sends_location), Color.rgb(113, 244, 66))
                }
            }
            Activity.RESULT_CANCELED -> {
                val notificationIntent = Intent(this, MainActivity::class.java)
                notificationIntent.action = ACTION_REQUEST_LOCATION_SETTING
                notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                val intent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

                showLocationNotification(getString(R.string.japp_not_sending_location), getString(R.string.click_to_activate_location_setting), Color.rgb(244, 66, 66), intent)
            }
        }
    }
    private fun sendlocation2(location: Location, builder: HunterPostBody){
        val api = Japp.getApi(HunterApi::class.java)
        api.post(builder).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.i(TAG, getString(R.string.location_sent))
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e(TAG, t.toString(), t)
            }
        })
    }
    private fun sendLocation(location: Location) {
        val builder = HunterPostBody.default
        builder.setLatLng(LatLng(location.latitude, location.longitude))
        if (JappPreferences.accountIcon == 0 && JappPreferences.prependDeelgebied){
            val api = Japp.getApi(AutoApi::class.java)
            api.getInfoById(JappPreferences.accountKey, JappPreferences.accountId).enqueue(
                    object: Callback<AutoInzittendeInfo?>{
                        override fun onFailure(call: Call<AutoInzittendeInfo?>, t: Throwable) {
                            sendlocation2(location, builder)
                        }

                        override fun onResponse(call: Call<AutoInzittendeInfo?>, response: Response<AutoInzittendeInfo?>) {
                            if (response.isSuccessful) {
                                val dg = Deelgebied.parse(response.body()?.taak ?: "X")
                                        ?: Deelgebied.Xray
                                builder.prependDeelgebiedToName(dg)
                            }
                            sendlocation2(location, builder)
                        }

                    }
            )

        }else{
            sendlocation2(location, builder)
        }
        lastUpdate = Calendar.getInstance()
        wasSending = true
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, s: String) {
        when (s) {
            JappPreferences.UPDATE_LOCATION -> {
                val shouldSend = JappPreferences.isUpdatingLocationToServer
                val title: String
                val color: Int
                if (shouldSend) {
                    title = getString(R.string.japp_sends_location)
                    color = Color.rgb(113, 244, 66)
                    request?.let {removeRequest(it)}
                } else {
                    title = getString(R.string.japp_not_sending_location)
                    color = Color.rgb(244, 66, 66)
                    request?.let {removeRequest(it)}
                    request?.let {addRequest(it)}
                }
                showLocationNotification(title, color)
            }
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        //JappPreferences.visiblePreferences.registerOnSharedPreferenceChangeListener(this)
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        JappPreferences.visiblePreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    inner class LocationBinder : Binder() {
        val instance: LocationService
            get() = this@LocationService
    }

    interface OnResolutionRequiredListener {
        fun onResolutionRequired(status: Status)
    }

    companion object {

        val TAG = "LocationService"

        val ACTION_REQUEST_LOCATION_SETTING = "ACTION_REQUEST_LOCATION_SETTING"
        private val LOCATION_NOTIFICATION_CHANNEL = "notification_chan"
    }


}
