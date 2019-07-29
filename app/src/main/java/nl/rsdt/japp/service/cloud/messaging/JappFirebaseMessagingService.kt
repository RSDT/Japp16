package nl.rsdt.japp.service.cloud.messaging

import android.util.Log

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

import nl.rsdt.japp.application.Japp
import nl.rsdt.japp.application.JappPreferences
import nl.rsdt.japp.jotial.data.bodies.FcmPostBody
import nl.rsdt.japp.jotial.net.apis.FcmApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 4-8-2016
 * Description...
 */
class JappFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)
        Japp.updateManager.onMessage(remoteMessage)
    }

    override fun onNewToken(token: String?) {
        JappPreferences.fcmToken = token

        if (!JappPreferences.isFirstRun) {
            sendToken(token)
        }
    }

    companion object {
        val TAG = "MessagingService"

        @JvmOverloads
        fun sendToken(token: String? = JappPreferences.fcmToken) {
            val api = Japp.getApi(FcmApi::class.java)
            api.postToken(FcmPostBody.default.setToken(token)).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    Log.i(TAG, "Refreshed token to the server")
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e(TAG, t.toString(), t)
                }
            })
        }
    }
}
