package nl.rsdt.japp.application.activities

import android.Manifest
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
import nl.rsdt.japp.jotial.data.structures.area348.MetaColorInfo
import nl.rsdt.japp.jotial.io.AppData
import nl.rsdt.japp.jotial.maps.MapManager
import nl.rsdt.japp.jotial.maps.MapStorage
import nl.rsdt.japp.jotial.maps.deelgebied.Deelgebied
import nl.rsdt.japp.jotial.net.apis.MetaApi
import nl.rsdt.japp.service.cloud.data.NoticeInfo
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest
import retrofit2.Call
import retrofit2.Callback
import java.io.IOException


/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 8-7-2016
 * Description...
 */
class SplashActivity : Activity(), MapStorage.OnMapDataLoadedCallback, EasyPermissions.PermissionCallbacks {


    private var started: Boolean = false
    private var locationGranted = false
    private var storageGranted = false

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
                Log.e(TAG, t.toString())
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
                        .setPositiveButton(R.string.continue_to_app) { _,_-> start() }
                        .create()
                dialog.setOnShowListener { dialog -> (dialog as AlertDialog).getButton(AlertDialog.BUTTON_NEGATIVE).isEnabled = false }
                dialog.show()
            } else {
                requestPermissions()
            }
        } else {
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        EasyPermissions.requestPermissions(
                PermissionRequest.Builder(this, RC_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                        .setRationale(R.string.location_rationale)
                        .setPositiveButtonText(R.string.rationale_ask_ok)
                        .setNegativeButtonText(R.string.rationale_ask_cancel)
                        .build())
    }




    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
        val dialog = AlertDialog.Builder(this).apply {
            title = "Permission Denied!"
            setMessage(R.string.location_storage_rationale)
            setPositiveButton(R.string.oke) { _,_ -> finishAffinity()}
        }.create()
        dialog.show()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == RC_LOCATION){
            locationGranted = true
            EasyPermissions.requestPermissions(
                    PermissionRequest.Builder(this, RC_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .setRationale(R.string.storage_rationale)
                            .setPositiveButtonText(R.string.storage_ask_ok)
                            .setNegativeButtonText(R.string.storage_ask_cancel)
                            .build())
        }
        if (requestCode == RC_STORAGE){
            storageGranted = true
        }

        if (!started && storageGranted && locationGranted){
            started = true
            start()
        }


    }
    private fun start() {
        val storage = MapStorage.instance
        val mapManager = MapManager.instance
        Deelgebied.initialize(this.resources)
        storage.add(this)
        storage.load(mapManager)
    }

    override fun onMapDataLoaded() {
        val storage = MapStorage.instance
        storage.remove(this)
        continueToNext()
    }

    fun continueToNext() {
        determineAndStartNewActivity()
    }

    private fun determineAndStartNewActivity() {

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
        private const val RC_LOCATION: Int = 1
        private const val RC_STORAGE: Int = 2
        private const val RC_LOCATION_STORAGE = RC_STORAGE or RC_LOCATION
    }


}
