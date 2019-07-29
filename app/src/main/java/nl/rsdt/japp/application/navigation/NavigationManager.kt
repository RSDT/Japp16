package nl.rsdt.japp.application.navigation

import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.navigation.NavigationView
import nl.rsdt.japp.R
import nl.rsdt.japp.application.JappPreferences
import nl.rsdt.japp.application.activities.MainActivity
import nl.rsdt.japp.jotial.io.AppData
import nl.rsdt.japp.jotial.net.API
import nl.rsdt.japp.jotial.net.DownloadDrawableTask
import java.net.URL
import java.util.ArrayList

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 14-8-2016
 * Description...
 */
class NavigationManager : FragmentNavigationManager(), SharedPreferences.OnSharedPreferenceChangeListener {

    private var navigationView: NavigationView? = null

    val usernameView: TextView
        get() = navigationView!!.getHeaderView(0).findViewById(R.id.nav_name)

    val rankView: TextView
        get() = navigationView!!.getHeaderView(0).findViewById(R.id.nav_rank)

    val avatarView: ImageView
        get() = navigationView!!.getHeaderView(0).findViewById(R.id.nav_avatar)

    fun setUsernameText(text: String?) {
        usernameView.text = text
    }

    fun setRankText(text: String?) {
        rankView.text = text
    }

    fun setAvatarDrawable(drawable: Drawable) {
        avatarView.setImageDrawable(drawable)
    }

    init {
        JappPreferences.visiblePreferences.registerOnSharedPreferenceChangeListener(this)
        JappPreferences.appPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun initialize(activity: MainActivity) {
        super.initialize(activity)
        navigationView = activity.findViewById(R.id.nav_view)
        setUsernameText(JappPreferences.accountUsername)
        setRankText(JappPreferences.accountRank)

        if (AppData.hasSave(ACCOUNT_AVATAR_STORAGE)) {
            val drawable = AppData.getDrawable(ACCOUNT_AVATAR_STORAGE)
            if (drawable != null) {
                setAvatarDrawable(drawable)
            }
        } else {
            download()
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key) {
            JappPreferences.ACCOUNT_USERNAME -> usernameView.text = sharedPreferences.getString(key, "Unknown")
            JappPreferences.ACCOUNT_RANK -> rankView.text = sharedPreferences.getString(key, "Unknown")
            JappPreferences.ACCOUNT_AVATAR -> download()
        }
    }

    private fun download() {
        val filename = JappPreferences.accountAvatarName
        if (!filename!!.isEmpty()) {
            try {
                val task = DownloadDrawableTask(object : DownloadDrawableTask.OnDowloadDrawablesCompletedCallback {
                    override fun onDownloadDrawablesCompleted(drawables: ArrayList<Drawable>) {
                        if (!drawables.isEmpty()) {
                            setAvatarDrawable(drawables[0])
                            AppData.saveDrawableInBackground(drawables[0], ACCOUNT_AVATAR_STORAGE)
                        }
                    }
                })
                task.execute(URL(API.SITE_2016_ROOT + "/img/avatar/" + filename))
            } catch (e: Exception) {
                Log.e(TAG, e.toString(), e)
            }

        }
    }


    override fun onDestroy() {
        super.onDestroy()

        JappPreferences.visiblePreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    companion object {

        val TAG = "NavigationManager"

        val ACCOUNT_AVATAR_STORAGE = "ACCOUNT_AVATAR"
    }
}
