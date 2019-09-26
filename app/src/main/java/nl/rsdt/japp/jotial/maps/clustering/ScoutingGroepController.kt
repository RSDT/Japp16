package nl.rsdt.japp.jotial.maps.clustering

import android.os.Bundle
import android.util.Log
import com.google.gson.reflect.TypeToken
import nl.rsdt.japp.application.Japp
import nl.rsdt.japp.application.JappPreferences
import nl.rsdt.japp.jotial.IntentCreatable
import nl.rsdt.japp.jotial.Recreatable
import nl.rsdt.japp.jotial.data.structures.area348.ScoutingGroepInfo
import nl.rsdt.japp.jotial.io.AppData
import nl.rsdt.japp.jotial.maps.clustering.osm.OsmScoutingGroepClusterManager
import nl.rsdt.japp.jotial.maps.management.MapItemUpdatable
import nl.rsdt.japp.jotial.maps.wrapper.IJotiMap
import nl.rsdt.japp.jotial.maps.wrapper.google.GoogleJotiMap
import nl.rsdt.japp.jotial.maps.wrapper.osm.OsmJotiMap
import nl.rsdt.japp.jotial.net.apis.ScoutingGroepApi
import nl.rsdt.japp.service.cloud.data.UpdateInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 29-8-2016
 * Description...
 */
class ScoutingGroepController : Recreatable, IntentCreatable, MapItemUpdatable<ArrayList<ScoutingGroepInfo>>, Callback<ArrayList<ScoutingGroepInfo>> {

    var clusterManager: ClusterManagerInterface? = null
        protected set


    internal var buffer: ArrayList<ScoutingGroepInfo>? = ArrayList()

    override fun onIntentCreate(bundle: Bundle) {
        if (bundle.containsKey(BUNDLE_ID)) {
            val localBuffer: ArrayList<ScoutingGroepInfo> = bundle.getParcelableArrayList(BUNDLE_ID)
            buffer = localBuffer
            if (clusterManager != null) {
                clusterManager!!.addItems(localBuffer)
                clusterManager!!.cluster()
                buffer = null
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState?.containsKey(BUNDLE_ID) ==true) {
            val items = savedInstanceState.getParcelableArrayList<ScoutingGroepInfo>(BUNDLE_ID)
            if (items != null && !items.isEmpty()) {
                if (clusterManager != null) {
                    clusterManager!!.addItems(items)
                    clusterManager!!.cluster()
                } else {
                    buffer = items
                }
            }
        }
    }

    override fun onSaveInstanceState(saveInstanceState: Bundle?) {
        if (clusterManager != null) {
            val items = clusterManager!!.items
            if (items is ArrayList<*>) {
                saveInstanceState?.putParcelableArrayList(BUNDLE_ID, items as ArrayList<ScoutingGroepInfo?>)
            } else {
                saveInstanceState?.putParcelableArrayList(BUNDLE_ID, ArrayList(items))
            }
        }
    }

    fun onMapReady(jotiMap: IJotiMap) {
        when (jotiMap) {
            is GoogleJotiMap -> clusterManager = ScoutingGroepClusterManager(Japp.instance!!, jotiMap?.googleMap!!)
            is OsmJotiMap -> clusterManager = OsmScoutingGroepClusterManager(jotiMap)
            else -> clusterManager = NoneClusterManager()
        }
        val lBuffer = buffer
        if (lBuffer != null) {
            clusterManager!!.addItems(lBuffer)
            clusterManager!!.cluster()
            buffer = null
        }
    }

    override fun onResponse(call: Call<ArrayList<ScoutingGroepInfo>>, response: Response<ArrayList<ScoutingGroepInfo>>) {
        if (response.code() == 200) {
            if (clusterManager != null) {
                if (clusterManager!!.items.isEmpty()) {
                    response.body()?.also { clusterManager!!.addItems(it) }
                } else {
                    clusterManager!!.clearItems()
                    response.body()?.also { clusterManager!!.addItems(it) }
                }

            } else {
                buffer = response.body()
            }
            AppData.saveObjectAsJsonInBackground(response.body(), STORAGE_ID)
        }
    }

    override fun onFailure(call: Call<ArrayList<ScoutingGroepInfo>>, t: Throwable) {
        Log.e(TAG, t.toString(), t)
    }


    override fun update(mode: String): Call<ArrayList<ScoutingGroepInfo>>? {
        val api = Japp.getApi(ScoutingGroepApi::class.java)
        when (mode) {
            MapItemUpdatable.MODE_ALL -> return api.getAll(JappPreferences.accountKey)
            MapItemUpdatable.MODE_LATEST -> return api.getAll(JappPreferences.accountKey)
        }
        return null
    }

    override fun onUpdateInvoked() {
        val call = update(MapItemUpdatable.MODE_ALL)
        call?.enqueue(this)

    }

    override fun onUpdateMessage(info: UpdateInfo) {
        val call: Call<ArrayList<ScoutingGroepInfo>>?
        when (info.type) {
            UpdateInfo.ACTION_NEW -> call = update(MapItemUpdatable.MODE_ALL)
            UpdateInfo.ACTION_UPDATE -> call = update(MapItemUpdatable.MODE_ALL)
            else -> call = null
        }
        call?.enqueue(this)
    }

    companion object {

        val TAG = "ScoutingGroepController"

        val STORAGE_ID = "SC"

        val BUNDLE_ID = "SC"

        fun loadAndPutToBundle(bundle: Bundle) {
            val list = AppData.getObject<ArrayList<ScoutingGroepInfo>>(STORAGE_ID,
                    object : TypeToken<ArrayList<ScoutingGroepInfo>>() {}.type)
            if (list != null && list.isNotEmpty()) {
                bundle.putParcelableArrayList(BUNDLE_ID, list)
            }
        }
    }

}
