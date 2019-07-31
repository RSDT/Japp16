package nl.rsdt.japp.application.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import nl.rsdt.japp.R
import nl.rsdt.japp.application.Japp
import nl.rsdt.japp.application.JappPreferences
import nl.rsdt.japp.jotial.auth.Authentication
import nl.rsdt.japp.jotial.availability.GooglePlayServicesChecker
import nl.rsdt.japp.jotial.availability.LocationPermissionsChecker
import nl.rsdt.japp.jotial.availability.StoragePermissionsChecker
import nl.rsdt.japp.jotial.data.structures.area348.MetaColorInfo
import nl.rsdt.japp.jotial.io.AppData
import nl.rsdt.japp.jotial.maps.MapStorage
import nl.rsdt.japp.jotial.maps.deelgebied.Deelgebied
import nl.rsdt.japp.jotial.net.apis.AuthApi
import nl.rsdt.japp.jotial.net.apis.MetaApi
import nl.rsdt.japp.service.cloud.data.NoticeInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 8-7-2016
 * Description...
 */
class SplashActivity : Activity(), MapStorage.OnMapDataLoadedCallback {

    internal var permission_check: Int = 0

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
         * Checks if the Fresh-Start feature is enabled if so the data of the app is cleared.
         * */
        if (JappPreferences.isFreshStart) {
            /*
             * Clear preferences.
             * */
            JappPreferences.clear()

            /*
             * Clear all the data files
             * */
            AppData.clear()


            val thread = Thread(Runnable {
                try {
                    /*
                         * Resets Instance ID and revokes all tokens.
                         * */
                    FirebaseInstanceId.getInstance().deleteInstanceId()
                } catch (e: IOException) {
                    Log.e(TAG, e.toString(), e)
                }

                /*
                     * Get a new token.
                     * */
                FirebaseInstanceId.getInstance().token
            })
            thread.run()
        }

        /**
         * Subscribe to the updates topic.
         */
        FirebaseMessaging.getInstance().subscribeToTopic("updates")

        if (JappPreferences.shacoEnabled() && (JappPreferences.accountUsername == "David" || JappPreferences.accountUsername == "test")) {
            val player = MediaPlayer.create(this, R.raw.shaco_tank_engine)
            player.start()
        }

        val metaApi = Japp.getApi(MetaApi::class.java)
        metaApi.getMetaColor(JappPreferences.accountKey).enqueue(object : Callback<MetaColorInfo> {
            override fun onResponse(call: Call<MetaColorInfo>, response: retrofit2.Response<MetaColorInfo>) {
                val colorInfo = response.body()
                JappPreferences.setColorHex("a", colorInfo?.ColorCode?.a)
                JappPreferences.setColorHex("b", colorInfo?.ColorCode?.b)
                JappPreferences.setColorHex("c", colorInfo?.ColorCode?.c)
                JappPreferences.setColorHex("d", colorInfo?.ColorCode?.d)
                JappPreferences.setColorHex("e", colorInfo?.ColorCode?.e)
                JappPreferences.setColorHex("f", colorInfo?.ColorCode?.f)
                JappPreferences.setColorHex("x", colorInfo?.ColorCode?.x)
                JappPreferences.setColorName("a", colorInfo?.ColorName?.a)
                JappPreferences.setColorName("b", colorInfo?.ColorName?.b)
                JappPreferences.setColorName("c", colorInfo?.ColorName?.c)
                JappPreferences.setColorName("d", colorInfo?.ColorName?.d)
                JappPreferences.setColorName("e", colorInfo?.ColorName?.e)
                JappPreferences.setColorName("f", colorInfo?.ColorName?.f)
                JappPreferences.setColorName("x", colorInfo?.ColorName?.x)
            }

            override fun onFailure(call: Call<MetaColorInfo>, t: Throwable) {
                Log.e(SplashActivity.TAG, t.toString())
            }
        })
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            if (extras.containsKey("title") && extras.containsKey("body")) {
                val dialog = AlertDialog.Builder(this)
                        .setTitle(extras.getString("title"))
                        .setMessage(extras.getString("body"))
                        .setIcon(NoticeInfo.parseDrawable(extras.getString("icon")))
                        .setPositiveButton(getString(R.string.continue_to_app)) { dialogInterface, i -> start() }
                        .create()
                dialog.setOnShowListener { dialog -> (dialog as AlertDialog).getButton(AlertDialog.BUTTON_NEGATIVE).isEnabled = false }
                dialog.show()
            } else {
                start()
            }
        } else {
            start()
        }
    }

    private fun start() {
       /* while(
                StoragePermissionsChecker.check(this) != StoragePermissionsChecker.PERMISSIONS_REQUEST_REQUIRED &&
                LocationPermissionsChecker.check(this) != LocationPermissionsChecker.PERMISSIONS_REQUEST_REQUIRED){
            Log.i(TAG,"permissions not acquired")
        }*/
        /*
         * Check if we have the permissions we need.
         * */
        val storage = MapStorage.instance
        permission_check = LocationPermissionsChecker.check(this)
        //StoragePermissionsChecker.check(this)
        Deelgebied.initialize(this.resources)

        storage.add(this)
        storage.load()

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (LocationPermissionsChecker.permissionRequestResultContainsLocation(permissions)) {
            if (LocationPermissionsChecker.hasPermissionOfPermissionRequestResult(requestCode, permissions, grantResults)) {
                if (GooglePlayServicesChecker.check(this) != GooglePlayServicesChecker.FAILURE) {
                    determineAndStartNewActivity()
                }
            } else {
                val dialog = AlertDialog.Builder(this)
                        .setTitle(getString(R.string.location_permission))
                        .setMessage(getString(R.string.location_permision_title))
                        .setPositiveButton(getString(R.string.oke)) { dialogInterface, i -> permission_check = LocationPermissionsChecker.check(this@SplashActivity) }
                        .create()
                dialog.setOnShowListener { dialog -> (dialog as AlertDialog).getButton(AlertDialog.BUTTON_NEGATIVE).isEnabled = false }
                dialog.show()
            }
        }

    }

    override fun onMapDataLoaded() {
        val storage = MapStorage.instance
        storage.remove(this)
        continueToNext()
    }

    fun continueToNext() {
        if (permission_check != LocationPermissionsChecker.PERMISSIONS_REQUEST_REQUIRED) {
            determineAndStartNewActivity()
        }
    }

    private fun determineAndStartNewActivity() {

        if (false) {
            val intenti = Intent(this, IntroActivity::class.java)
            startActivity(intenti)
            finish()
            return
        }


        val key = JappPreferences.accountKey
        if (key?.isEmpty() != false) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            if (JappPreferences.isFirstRun) {

                val intent = Intent(this, IntroActivity::class.java)
                startActivity(intent)
                finish()
            } else {

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    companion object {

        val TAG = "SplashActivity"

        val LOAD_ID = "LOAD_RESULTS"
    }


}
