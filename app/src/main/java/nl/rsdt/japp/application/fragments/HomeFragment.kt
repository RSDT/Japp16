package nl.rsdt.japp.application.fragments

import android.app.Activity
import android.app.Fragment
import android.app.NotificationManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import nl.rsdt.japp.R
import nl.rsdt.japp.jotial.data.structures.area348.VosStatusInfo
import nl.rsdt.japp.jotial.io.AppData
import nl.rsdt.japp.jotial.net.apis.official.VosApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 9-7-2016
 * Description...
 */
class HomeFragment : Fragment(), Callback<VosStatusInfo> {

    protected var lastStatusInfo: VosStatusInfo? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lastStatusInfo = AppData.getObject<VosStatusInfo>(STORAGE_KEY, VosStatusInfo::class.java)
        if (lastStatusInfo != null) {
            updateView(lastStatusInfo)
        }
        refresh()
    }

    fun refresh() {
        val retrofit = Retrofit.Builder()
                .baseUrl(getString(R.string.jotihunt_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val api = retrofit.create(VosApi::class.java)
        api.status.enqueue(this)
    }

    override fun onResponse(call: Call<VosStatusInfo>, response: Response<VosStatusInfo>) {
        val info = response.body()
        if (info != null) {
            updateView(info)
            if (lastStatusInfo != null) {
                for (status in info.status) {
                    for (oldStatus in lastStatusInfo!!.status) {
                        if (status.team == oldStatus.team) {
                            if (status.status != oldStatus.status) {
                                showNotification(status)
                            }
                        }
                    }
                }
            } else {
                showNotification(info)
            }

            lastStatusInfo = info
            AppData.saveObjectAsJsonInBackground(info, STORAGE_KEY)
        }
    }

    fun updateView(info: VosStatusInfo) {
        for (status in info.status) {
            val view = view
            if (view != null) {
                var layout: LinearLayout? = null
                when (status.team) {
                    "Alpha" -> layout = view.findViewById(R.id.fragment_home_alpha)
                    "Bravo" -> layout = view.findViewById(R.id.fragment_home_bravo)
                    "Charlie" -> layout = view.findViewById(R.id.fragment_home_charlie)
                    "Delta" -> layout = view.findViewById(R.id.fragment_home_delta)
                    "Echo" -> layout = view.findViewById(R.id.fragment_home_echo)
                    "Foxtrot" -> layout = view.findViewById(R.id.fragment_home_foxtrot)
                }
                if (layout != null) {
                    when (status.status) {
                        VosStatusInfo.State.RED -> layout.setBackgroundColor(ContextCompat.getColor(this.activity, android.R.color.holo_red_light))
                        VosStatusInfo.State.ORANGE -> layout.setBackgroundColor(ContextCompat.getColor(this.activity, android.R.color.holo_orange_light))
                        VosStatusInfo.State.GREEN -> layout.setBackgroundColor(ContextCompat.getColor(this.activity, android.R.color.holo_green_light))
                    }
                }
            }
        }
    }

    fun showNotification(info: VosStatusInfo) {
        val style = NotificationCompat.InboxStyle()
        for (status in info.status) {
            style.addLine(status.team + " : " + status.status)
        }

        val builder = NotificationCompat.Builder(this.activity, VOSSTATUSCHANNEL)
        builder.setContentTitle(getString(R.string.vos_update_title))
                .setContentText(getString(R.string.vos_update_body))
                .setSmallIcon(R.drawable.fox3)
                .setStyle(style).priority = NotificationCompat.PRIORITY_HIGH
        val activity = this.activity
        if (activity != null) {
            val mNotifyMgr = this.activity.getSystemService(Activity.NOTIFICATION_SERVICE) as NotificationManager
            mNotifyMgr.notify(NOTIFICATION_ID, builder.build())
        }
    }

    fun showNotification(status: VosStatusInfo.Status) {

        val builder = NotificationCompat.Builder(this.activity, VOSSTATUSCHANNEL)
        when (status.status) {
            VosStatusInfo.State.RED -> builder.setContentTitle(getString(R.string.team_status_update, status.team))
                    .setContentText(getString(R.string.connot_hunt_team, status.team)).color = Color.rgb(244, 66, 66)
            VosStatusInfo.State.ORANGE -> builder.setContentTitle(getString(R.string.team_status_update, status.team))
                    .setContentText(getString(R.string.orange_message, status.team)).color = Color.rgb(214, 118, 8)
            VosStatusInfo.State.GREEN -> builder.setContentTitle(getString(R.string.team_status_update, status.team))
                    .setContentText(getString(R.string.groen_message, status.team)).color = Color.rgb(113, 244, 66)
        }
        builder.setGroup(GROUP_KEY)
        val notification = builder.setSmallIcon(R.drawable.fox3)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()
        val mNotifyMgr = this.activity.getSystemService(Activity.NOTIFICATION_SERVICE) as NotificationManager
        mNotifyMgr.notify(NOTIFICATION_ID, notification)
    }

    override fun onFailure(call: Call<VosStatusInfo>, t: Throwable) {
        Log.e(TAG, t.toString(), t)
    }

    companion object {

        val TAG = "HomeFragment"

        val STORAGE_KEY = "VosStatusInfo"

        val GROUP_KEY = "StatusUpdate"

        val VOSSTATUSCHANNEL = "vos_status"

        val NOTIFICATION_ID = 275
    }


}
