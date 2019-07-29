package nl.rsdt.japp.jotial.maps.management.controllers

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.util.Pair
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import nl.rsdt.japp.application.Japp
import nl.rsdt.japp.application.JappPreferences
import nl.rsdt.japp.jotial.data.structures.area348.HunterInfo
import nl.rsdt.japp.jotial.io.AppData
import nl.rsdt.japp.jotial.maps.management.MapItemController
import nl.rsdt.japp.jotial.maps.management.MarkerIdentifier
import nl.rsdt.japp.jotial.maps.management.transformation.AbstractTransducer
import nl.rsdt.japp.jotial.maps.wrapper.IMarker
import nl.rsdt.japp.jotial.net.apis.HunterApi
import retrofit2.Call
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Description...
 */
class HunterController : MapItemController<HashMap<String, ArrayList<HunterInfo>>, HunterController.HunterTransducer.Result>() {

    /**
     * Handler for updating the Hunters.
     */
    private var handler: Handler? = Handler()

    private val runnable: HunterUpdateRunnable

    protected var data: HashMap<String, ArrayList<HunterInfo>>? = HashMap()

    override val id: String
        get() = CONTROLLER_ID

    override val storageId: String
        get() = STORAGE_ID

    override val bundleId: String
        get() = BUNDLE_ID

    override val transducer: HunterTransducer
        get() = HunterTransducer()

    init {
        runnable = HunterUpdateRunnable()
        handler!!.postDelayed(runnable, JappPreferences.hunterUpdateIntervalInMs.toLong())
    }

    override fun onIntentCreate(bundle: Bundle) {
        super.onIntentCreate(bundle)
        if (bundle != null) {
            val result = bundle.getParcelable<HunterTransducer.Result>(BUNDLE_ID)
            if (result != null) {
                data = result.data
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(BUNDLE_COUNT)) {
                val keys = savedInstanceState.getStringArrayList(BUNDLE_COUNT)
                if (keys != null) {
                    var key: String
                    for (i in keys.indices) {
                        key = keys[i]
                        val list = savedInstanceState.getParcelableArrayList<HunterInfo>(key)
                        if (list != null && !list.isEmpty()) {
                            data!![key] = list
                        }
                    }
                    val result = data?.let { transducer.generate(it) }
                    if (jotiMap != null) {
                        result?.let { processResult(it) }
                    } else {
                        buffer = result
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(saveInstanceState: Bundle?) {
        val keys = ArrayList<String>()
        for ((key, value) in data!!) {
            saveInstanceState?.putParcelableArrayList(key, value)
            keys.add(key)
        }
        saveInstanceState?.putStringArrayList(BUNDLE_COUNT, keys)
    }

    override fun searchFor(query: String): IMarker? {
        var marker: IMarker?
        for (m in markers!!.indices) {
            marker = markers!![m]

            if (marker != null) {
            }
        }
        return null
    }


    override fun provide(): MutableList<String> {
        return ArrayList(data!!.keys)
    }

    override fun update(unused: String): Call<HashMap<String, ArrayList<HunterInfo>>> {
        var name = JappPreferences.huntname
        if (name!!.isEmpty()) {
            name = JappPreferences.accountUsername
        }

        val api = Japp.getApi(HunterApi::class.java)
        val getAll = JappPreferences.getAllHunters
        return if (getAll||name == null) {
            api.getAll(JappPreferences.accountKey)
        } else {
            api.getAllExcept(JappPreferences.accountKey, name)
        }
    }

    override fun merge(other: HunterTransducer.Result) {
        clear()
        processResult(other)
    }

    override fun processResult(result: HunterTransducer.Result) {
        clear()
        super.processResult(result)
        data = result.data
    }

    override fun clear() {
        super.clear()
        data!!.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (data != null) {
            /**
             * TODO: clearing the data might result in the data clearance in the saved instance state bundle
             */
            data!!.clear()
            data = null
        }

        if (handler != null) {
            handler!!.removeCallbacks(runnable)
            handler = null
        }
    }

    class HunterTransducer : AbstractTransducer<HashMap<String, ArrayList<HunterInfo>>, HunterTransducer.Result>() {

        override fun load(): HashMap<String, ArrayList<HunterInfo>>? {
            return AppData.getObject<HashMap<String, ArrayList<HunterInfo>>>(STORAGE_ID, object : TypeToken<HashMap<String, ArrayList<HunterInfo>>>() {

            }.type)
        }

        override fun transduceToBundle(bundle: Bundle) {
            load()?.also { bundle.putParcelable(BUNDLE_ID, generate(it)) }
        }

        override fun generate(data: HashMap<String, ArrayList<HunterInfo>>): Result {
            if (data == null || data.isEmpty()) return Result()

            val result = Result()
            result.bundleId = BUNDLE_ID
            result.data = data

            if (isSaveEnabled) {
                AppData.saveObjectAsJson(data, STORAGE_ID)
            }

            var currentData: ArrayList<HunterInfo>


            for ((_, value) in data) {
                currentData = value

                currentData.sortWith(Comparator { info1, info2 ->
                    var firstDate: Date? = null
                    var secondDate: Date? = null
                    try {
                        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT)
                        firstDate = format.parse(info1?.datetime)
                        secondDate = format.parse(info2?.datetime)
                    } catch (e: ParseException) {
                        Log.e(MapItemController.TAG, e.toString(), e)
                    }

                    if (firstDate != null && secondDate != null) {
                        firstDate.compareTo(secondDate)
                    } else 0
                })

                /**
                 * Setup the polyline for the Hunter.
                 */
                val pOptions = PolylineOptions()
                pOptions.width(5f)
                pOptions.color(Color.BLACK)

                /**
                 * Loop through each info.
                 */
                for (i in currentData.indices) {
                    pOptions.add(currentData[i].latLng)
                }

                result.add(pOptions)

                /**
                 * The lastest info should be the first one in the array.
                 */
                val lastestInfo = currentData[currentData.size - 1]

                val identifier = MarkerIdentifier.Builder()
                        .setType(MarkerIdentifier.TYPE_HUNTER)
                        .add("icon", lastestInfo?.associatedDrawable.toString())
                        .add("hunter", lastestInfo?.hunter)
                        .add("time", lastestInfo?.datetime)
                        .create()

                /**
                 * Setup the marker for the Hunter.
                 */
                val mOptions = MarkerOptions()
                mOptions.title(Gson().toJson(identifier))
                mOptions.position(lastestInfo?.latLng?: LatLng(0.0,0.0))

                val options = BitmapFactory.Options()
                options.inSampleSize = 2
                val bitmap = BitmapFactory.decodeResource(Japp.appResources, lastestInfo.associatedDrawable, options)


                result.add(Pair(mOptions, bitmap))

            }
            return result

        }

        class Result : AbstractTransducer.Result {

            var data = HashMap<String, ArrayList<HunterInfo>>()

            constructor()

            protected constructor(`in`: Parcel) : super(`in`) {
                val keys = `in`.createStringArray()
                if (keys != null) {
                    var key: String
                    for (i in keys.indices) {
                        key = keys[i]
                        val list = `in`.createTypedArrayList(HunterInfo.CREATOR)
                        if (list != null && !list.isEmpty()) {
                            data[key] = list
                        }
                    }
                }

            }

            override fun writeToParcel(dest: Parcel, flags: Int) {
                super.writeToParcel(dest, flags)

                val keys = arrayOfNulls<String>(data.size)
                data.keys.toTypedArray()
                dest.writeStringArray(keys)
                for ((_, value) in data) {
                    dest.writeTypedList(value)
                }

            }

            override fun describeContents(): Int {
                return 0
            }

            companion object CREATOR: Parcelable.Creator<Result>{
                    override fun createFromParcel(`in`: Parcel): Result {
                        return Result(`in`)
                    }

                    override fun newArray(size: Int): Array<Result?> {
                        return arrayOfNulls(size)
                    }
                }


        }

    }

    private inner class HunterUpdateRunnable : Runnable {

        override fun run() {
            onUpdateInvoked()
            handler!!.postDelayed(this, JappPreferences.hunterUpdateIntervalInMs.toLong())
        }
    }

    companion object {

        val CONTROLLER_ID = "HunterOpdrachtController"

        val STORAGE_ID = "STORAGE_HUNTER"

        val BUNDLE_ID = "HUNTER"

        val BUNDLE_COUNT = "HUNTER_COUNT"
    }

}
