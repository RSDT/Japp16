package nl.rsdt.japp.jotial.auth

import android.app.Activity
import android.content.Intent
import com.google.gson.annotations.SerializedName
import nl.rsdt.japp.R
import nl.rsdt.japp.application.Japp
import nl.rsdt.japp.application.JappPreferences
import nl.rsdt.japp.application.activities.LoginActivity
import nl.rsdt.japp.jotial.net.apis.AuthApi
import retrofit2.Call
import retrofit2.Callback

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 13-7-2016
 * Description...
 */
class Authentication : Callback<Authentication.KeyObject> {

    private var callback: OnAuthenticationCompletedCallback? = null

    private var username: String? = null

    private var password: String? = null

    fun executeAsync() {
        val api = Japp.getApi(AuthApi::class.java)
        api.login(LoginBody(username, password)).enqueue(this)
    }

    override fun onResponse(call: Call<KeyObject>, response: retrofit2.Response<KeyObject>) {
        if (response.code() == 200) {

            val key = response.body()!!.key

            /**
             * Change the key in the release_preferences.
             */
            val pEditor = JappPreferences.visiblePreferences.edit()
            pEditor.putString(JappPreferences.ACCOUNT_KEY, key)
            pEditor.apply()

            if (callback != null) {
                callback!!.onAuthenticationCompleted(AuthenticationResult(key, 200, Japp.appResources.getString(R.string.login_succes)))
            }
        } else {
            val message: String
            if (response.code() == 404) {
                message = Japp.appResources.getString(R.string.wrong_data)
            } else {
                message = Japp.appResources.getString(R.string.error_on_login)
            }
            if (callback != null) {
                callback!!.onAuthenticationCompleted(AuthenticationResult("", response.code(), message))
            }
        }
    }

    override fun onFailure(call: Call<KeyObject>, t: Throwable) {
        if (callback != null) {
            callback!!.onAuthenticationCompleted(AuthenticationResult("", 0, Japp.appResources.getString(R.string.error_on_login)))
        }
    }


    class AuthenticationResult private constructor(val key: String?, val code: Int, val message: String) {

        val isSucceeded: Boolean
            get() = key != null && !key.isEmpty()
    }

    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 2-9-2016
     * Description...
     */
    inner class LoginBody(private val gebruiker: String, private val ww: String)

    inner class KeyObject {
        @SerializedName("SLEUTEL")
        private val key: String? = null
    }

    inner class ValidateObject {
        @SerializedName("exists")
        private val exists: Boolean = false

        fun exists(): Boolean {
            return exists
        }
    }

    class Builder {
        internal var buffer = Authentication()

        fun setCallback(callback: OnAuthenticationCompletedCallback): Builder {
            buffer.callback = callback
            return this
        }

        fun setUsername(username: String): Builder {
            buffer.username = username
            return this
        }

        fun setPassword(password: String): Builder {
            buffer.password = AeSimpleSHA1.trySHA1(password)
            return this
        }

        fun create(): Authentication {
            return buffer
        }
    }

    interface OnAuthenticationCompletedCallback {
        fun onAuthenticationCompleted(result: AuthenticationResult)
    }

    companion object {

        val REQUEST_TAG = "Authentication"

        /**
         * TODO: don't use final here
         */
        fun validate(activity: Activity) {
            val api = Japp.getApi(AuthApi::class.java)


        }

        fun startLoginActivity(activity: Activity) {
            val intent = Intent(activity, LoginActivity::class.java)
            activity.startActivity(intent)
            activity.finish()
        }
    }

}