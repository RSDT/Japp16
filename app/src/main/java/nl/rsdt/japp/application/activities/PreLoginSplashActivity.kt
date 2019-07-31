package nl.rsdt.japp.application.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
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
class PreLoginSplashActivity : Activity() {

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

        validate()
    }

    fun validate() {
        val api = Japp.getApi(AuthApi::class.java)
        api.validateKey(JappPreferences.accountKey).enqueue(object : Callback<Authentication.ValidateObject> {
            override fun onResponse(call: Call<Authentication.ValidateObject>, response: Response<Authentication.ValidateObject>) {
                if (response.code() == 200) {
                    val `object` = response.body()
                    if (`object` != null) {
                        if (!`object`.exists()) {
                            Authentication.startLoginActivity(this@PreLoginSplashActivity)
                        } else {
                            determineAndStartNewActivity()
                        }
                    }
                } else {
                    Authentication.startLoginActivity(this@PreLoginSplashActivity)
                }

            }

            override fun onFailure(call: Call<Authentication.ValidateObject>, t: Throwable) {
                if (t is UnknownHostException) {
                    determineAndStartNewActivity()
                } else if (t is SocketTimeoutException) {
                    AlertDialog.Builder(this@PreLoginSplashActivity)
                            .setTitle(getString(R.string.err_verification))
                            .setMessage(R.string.splash_activity_socket_timed_out)
                            .setPositiveButton(R.string.continue_to_app) { dialogInterface, i -> determineAndStartNewActivity() }
                            .create()
                            .show()
                } else {
                    AlertDialog.Builder(this@PreLoginSplashActivity)
                            .setTitle(getString(R.string.err_verification))
                            .setMessage(t.toString())
                            .setPositiveButton(getString(R.string.try_again)) { dialogInterface, i -> validate() }
                            .create()
                            .show()
                }
                Log.e(TAG, t.toString(), t)
            }
        })
    }

    fun determineAndStartNewActivity() {
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

                val intent = Intent(this, SplashActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    companion object {

        val TAG = "PreLoginSplashActivity"

        val LOAD_ID = "LOAD_RESULTS"
    }


}
