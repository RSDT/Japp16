package nl.rsdt.japp.application.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.EditTextPreference
import android.util.Log
import com.android.volley.Request as VRequest
import com.android.volley.Response as VResponse
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.iid.FirebaseInstanceId
import nl.rsdt.japp.BuildConfig
import nl.rsdt.japp.R
import nl.rsdt.japp.application.Japp
import nl.rsdt.japp.application.JappPreferences
import nl.rsdt.japp.jotial.auth.Authentication
import nl.rsdt.japp.jotial.io.AppData
import nl.rsdt.japp.jotial.net.apis.AuthApi
import org.json.JSONObject
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
        checkLatestRelease()
        validate()
    }

    private fun checkLatestRelease() {
        var currentVersionName = BuildConfig.VERSION_NAME
        val queue = Volley.newRequestQueue(this)
        val url = "https://api.github.com/repos/rsdt/japp/releases/latest"
        var dialog = AlertDialog.Builder(this).setCancelable(true)
        // Request a string response from the provided URL.
        val stringRequest = StringRequest(VRequest.Method.GET, url,
                VResponse.Listener<String> { response ->
                    var json = JSONObject(response)
                    var newVersionName = json["name"]
                    if (currentVersionName != newVersionName){
                        dialog.setTitle("Er is een nieuwe versie van de app: ${newVersionName}")
                        dialog.setPositiveButton("Naar Download") { dialogInterface: DialogInterface, i: Int ->
                            var url = "https://github.com/RSDT/Japp16/releases/latest"
                            var myIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            startActivity(myIntent)
                        }
                        dialog.create()
                        dialog.show()
                    }
                },
                VResponse.ErrorListener {
                    dialog.setTitle("error bij het controleren op updates")
                    dialog.create()
                    dialog.show()
                })
        // Add the request to the RequestQueue.
        queue.add(stringRequest)

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
                            .setPositiveButton(R.string.try_again) { _, _ -> determineAndStartNewActivity() }
                            .create()
                            .show()
                } else {
                    AlertDialog.Builder(this@PreLoginSplashActivity)
                            .setTitle(getString(R.string.err_verification))
                            .setMessage(t.toString())
                            .setPositiveButton(getString(R.string.try_again)) { _, _ -> validate() }
                            .create()
                            .show()
                }
                Log.e(TAG, t.toString(), t)
            }
        })
    }

    fun determineAndStartNewActivity() {


        val key = JappPreferences.accountKey
        if (key?.isEmpty() != false) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {

                val intent = Intent(this, SplashActivity::class.java)
                startActivity(intent)
                finish()
        }

    }


    companion object {

        val TAG = "PreLoginSplashActivity"

        val LOAD_ID = "LOAD_RESULTS"
    }


}
