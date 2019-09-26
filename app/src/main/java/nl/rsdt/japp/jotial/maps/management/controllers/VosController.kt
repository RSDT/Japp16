package nl.rsdt.japp.jotial.maps.management.controllers

import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.util.Pair
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import nl.rsdt.japp.application.Japp
import nl.rsdt.japp.application.JappPreferences
import nl.rsdt.japp.jotial.data.structures.area348.BaseInfo
import nl.rsdt.japp.jotial.data.structures.area348.VosInfo
import nl.rsdt.japp.jotial.io.AppData
import nl.rsdt.japp.jotial.maps.management.MapItemController
import nl.rsdt.japp.jotial.maps.management.MapItemUpdatable
import nl.rsdt.japp.jotial.maps.management.MarkerIdentifier
import nl.rsdt.japp.jotial.maps.management.StandardMapItemController
import nl.rsdt.japp.jotial.maps.management.transformation.AbstractTransducer
import nl.rsdt.japp.jotial.maps.misc.VosUtils
import nl.rsdt.japp.jotial.maps.sighting.SightingIcon
import nl.rsdt.japp.jotial.maps.wrapper.ICircle
import nl.rsdt.japp.jotial.maps.wrapper.IJotiMap
import nl.rsdt.japp.jotial.maps.wrapper.IMarker
import nl.rsdt.japp.jotial.net.apis.VosApi
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.GregorianCalendar
import java.util.Locale
import java.util.Calendar
import java.util.Date
import kotlin.Comparator
import kotlin.collections.ArrayList

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Description...
 */
abstract class VosController(jotiMap: IJotiMap) : StandardMapItemController<VosInfo, VosController.VosTransducer.Result>(jotiMap), SharedPreferences.OnSharedPreferenceChangeListener {

    private val circleHandlers: MutableList<CircleHandler> = mutableListOf()

    private var handler: Handler = Handler()

    private var runnable: UpdateCircleRunnable = UpdateCircleRunnable()

    abstract val team: String

    override val transducer: VosTransducer
        get() = VosTransducer(storageId, bundleId)

    init {
        JappPreferences.visiblePreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun update(mode: String): Call<ArrayList<VosInfo>>? {
        val call = object : Call<ArrayList<VosInfo>> {
            private val api = Japp.getApi(VosApi::class.java)
            private val apiCall = api.getAll(JappPreferences.accountKey, team)
            private val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            @Throws(IOException::class)
            override fun execute(): Response<ArrayList<VosInfo>> {
                return apiCall.execute()
            }

            override fun enqueue(callback: Callback<ArrayList<VosInfo>>) {
                val callback2 = object : Callback<ArrayList<VosInfo>> {
                    override fun onResponse(call: Call<ArrayList<VosInfo>>, response: Response<ArrayList<VosInfo>>) {
                        if (JappPreferences.onlyToday()) {
                            val delete = ArrayList<VosInfo>()
                            for (info in response.body()!!) {
                                try {
                                    val date = format.parse(info.datetime)
                                    val c = GregorianCalendar()
                                    c.set(Calendar.HOUR_OF_DAY, 0)
                                    c.set(Calendar.MINUTE, 0)
                                    c.set(Calendar.SECOND, 0)
                                    val d = GregorianCalendar()
                                    d.time = date
                                    if (c.after(d)) {
                                        delete.add(info)
                                    }
                                } catch (e: ParseException) {
                                    throw RuntimeException(e)
                                }

                            }
                            response.body()!!.removeAll(delete)
                        }
                        callback.onResponse(call, response)
                    }

                    override fun onFailure(call: Call<ArrayList<VosInfo>>, t: Throwable) {
                        callback.onFailure(call, t)
                    }
                }
                apiCall.enqueue(callback2)
            }

            override fun isExecuted(): Boolean {
                return apiCall.isExecuted
            }

            override fun cancel() {
                apiCall.cancel()
            }

            override fun isCanceled(): Boolean {
                return apiCall.isCanceled
            }

            override fun clone(): Call<ArrayList<VosInfo>> {
                return apiCall.clone()
            }

            override fun request(): Request? {
                return null
            }
        }
        when (mode) {
            MapItemUpdatable.MODE_ALL -> return call
            MapItemUpdatable.MODE_LATEST -> return call
        }
        return null
    }

    override fun searchFor(query: String): IMarker? {
        val results = ArrayList<BaseInfo>()
        var info: VosInfo?
        for (i in items.indices) {
            info = items[i]

            var current: String?
            val items = arrayOf(info.note, info.extra)
            for (x in items.indices) {
                current = items[x]
                if (current?.toLowerCase(Locale.ROOT)?.startsWith(query) == true) results.add(info)
            }
        }
        return null
    }

    override fun processResult(result: VosTransducer.Result) {
        super.processResult(result)
        handler.post(runnable)
        for (marker in markers){
            val circleHandler = CircleHandler(marker)
            circleHandlers.add(circleHandler)
            marker.setOnClickListener(circleHandler)
        }
    }

    override fun provide(): MutableList<String> {
        val entries = ArrayList<String>()
        var info: VosInfo?
        for (i in items.indices) {
            info = items[i]
            entries.add(info.note  ?: "null")
            entries.add(info.extra ?: "null")
        }
        return entries
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        super.onSharedPreferenceChanged(sharedPreferences, key)
        if (JappPreferences.AUTO_ENLARGMENT == key) {
            handler.removeCallbacks(runnable)
            if (sharedPreferences.getBoolean(JappPreferences.AUTO_ENLARGMENT, true)) {
                handler.post(runnable)
            } else {
                handler.removeCallbacks(runnable)
            }
        }
    }

    override fun onUpdateInvoked() {
        super.onUpdateInvoked()
        for (handler in circleHandlers){
            handler.destroy()
        }
        circleHandlers.clear()

    }
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)

        JappPreferences.visiblePreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    class VosTransducer(private val storageId: String, private val bundleId: String) : AbstractTransducer<ArrayList<VosInfo>, VosTransducer.Result>() {

        override fun load(): ArrayList<VosInfo>? {
            return AppData.getObject<ArrayList<VosInfo>>(storageId, object : TypeToken<ArrayList<VosInfo>>() {

            }.type)
        }

        override fun transduceToBundle(bundle: Bundle) {
            load()?.also { bundle.putParcelable(bundleId, generate(it)) }
        }

        override fun generate(data: ArrayList<VosInfo>): Result {
            if (data.isEmpty()) return Result()
            data.sortWith(Comparator { info1, info2 ->
                var firstDate: Date? = null
                var secondDate: Date? = null
                try {
                    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT)
                    firstDate = format.parse(info1.datetime)
                    secondDate = format.parse(info2.datetime)
                } catch (e: ParseException) {
                    Log.e(TAG, e.toString(), e)
                }

                if (firstDate != null && secondDate != null) {
                    firstDate.compareTo(secondDate)
                } else 0
            })

            val result = Result()
            result.bundleId = bundleId
            result.addItems(data)

            if (isSaveEnabled) {
                /**
                 * Saves the generated data.
                 */
                AppData.saveObjectAsJson(data, storageId)
            }

            val pOptions = PolylineOptions()
            pOptions.color(data[0].associatedColor)
            pOptions.width(5f)

            var current: VosInfo

            for (i in data.indices) {
                current = data[i]

                val identifier = MarkerIdentifier.Builder()
                        .setType(MarkerIdentifier.TYPE_VOS)
                        .add("team", current.team)
                        .add("note", current.note)
                        .add("extra", current.extra)
                        .add("time", current.datetime)
                        .add("icon", current.associatedDrawable.toString())
                        .add("color", current.getAssociatedColor(130).toString())
                        .create()

                val mOptions = MarkerOptions()
                mOptions.anchor(0.5f, 0.5f)
                mOptions.title(Gson().toJson(identifier))
                mOptions.position(current.latLng)

                val last = data.size - 1
                if (i == last) {
                    if (current.icon == SightingIcon.DEFAULT || current.icon == SightingIcon.INVALID) {
                        current.icon = SightingIcon.LAST_LOCATION
                    }

                    val diff = VosUtils.parseDate((current.datetime))?.let{ VosUtils.calculateTimeDifferenceInHoursFromNow(it).toDouble()}?:0.0

                    /**
                     * If the VosInfo is younger or equal than 2 hours: show a circle indicating where the vos team could have walked
                     */
                    if (diff > 0 && diff <= 2) {
                        val cOptions = CircleOptions()
                        cOptions.center(current.latLng)
                        cOptions.fillColor(current.getAssociatedColor(80))
                        cOptions.strokeWidth(0f)
                        cOptions.radius(current.datetime?.let{VosUtils.calculateRadius(it, JappPreferences.walkSpeed).toDouble()}?:0.0)
                        result.add(cOptions)
                    }
                }

                val options = BitmapFactory.Options()
                when (current.icon) {
                    SightingIcon.DEFAULT -> options.inSampleSize = 4
                    SightingIcon.HUNT -> options.inSampleSize = 2
                    SightingIcon.SPOT -> options.inSampleSize = 2
                    SightingIcon.LAST_LOCATION -> options.inSampleSize = 2
                    else -> options.inSampleSize = 2
                }
                val bitmap = BitmapFactory.decodeResource(Japp.appResources, current.associatedDrawable, options)
                mOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                result.add(Pair(mOptions, bitmap))

                pOptions.add(current.latLng)
            }
            result.add(pOptions)

            return result
        }


        class Result : AbstractTransducer.StandardResult<VosInfo> {
            constructor()

            /**
             * Reconstructs the result.
             *
             * @param in The parcel where the result was written to
             */
            protected constructor(`in`: Parcel) : super(`in`) {
                items = `in`.createTypedArrayList(VosInfo.CREATOR)
            }


            override fun writeToParcel(dest: Parcel, i: Int) {
                super.writeToParcel(dest, i)
                dest.writeTypedList(items)
            }

            companion object CREATOR: Parcelable.Creator<Result> {
                    override fun createFromParcel(`in`: Parcel): Result {
                        return Result(`in`)
                    }

                    override fun newArray(size: Int): Array<Result?> {
                        return arrayOfNulls(size)
                    }
            }
        }

    }

    private inner class CircleHandler(private val marker:IMarker): Runnable, IMarker.OnClickListener{
        private var circle: ICircle? = null
        private val circleOptions: CircleOptions
        private var isOpen: Boolean = false
        val color = Color.GRAY
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        val alpha = JappPreferences.areasColorAlpha
        init {
            circleOptions = CircleOptions().apply {
                this.center(marker.position)
                this.strokeColor(Color.BLACK)
                this.fillColor(Color.argb(alpha,red,green,blue))
                this.strokeWidth(2f)
                this.radius(getCircleRadius().toDouble())
            }
            if (isOpen){
                handler.post(this)
            }
        }

        fun getCircleRadius(): Float{
            return try {
                val identifier = marker.identifier
                val time = identifier?.properties?.get("time")
                val date = VosUtils.parseDate(time ?: "1970-01-01 00:00:00")
                val diff = date?.let { VosUtils.calculateTimeDifferenceInHoursFromNow(it).toDouble() }
                        ?: 0.0
                if (diff < 30||true) {
                    date?.let { VosUtils.calculateRadius(it, JappPreferences.walkSpeed) } ?: 0f
                } else {
                    0f
                }
            } catch(e: Exception){
                Log.e(TAG, e.toString())
                0f
            }
        }
        override fun run() {


            /**
             * Calculate the difference in time between the vos date and now.
             */
            val radius = getCircleRadius()
            circle?.setRadius(radius)

            if (isOpen){
                handler.postDelayed(this, JappPreferences.autoEnlargementIntervalInMs.toLong())
            }
        }

        override fun OnClick(m: IMarker): Boolean {
            isOpen = !isOpen
            if (isOpen) {
                circle = jotiMap.addCircle(circleOptions)
                circle?.setVisible(true)
                handler.post(this)
            }else{
                circle?.setRadius(0f)
                circle?.setVisible(false)
                circle?.remove()
                circle = null
            }
            marker.showInfoWindow()
            return false
        }

        fun destroy() {
            try {
                marker.setOnClickListener(null)
            }catch (e:java.lang.Exception){

            }
            isOpen = false
            try {
                handler.removeCallbacks(this)
            }catch (e:java.lang.Exception){

            }
            circle?.remove()

        }

    }
    private inner class UpdateCircleRunnable : Runnable {

        override fun run() {
            val circles = ArrayList(this@VosController.circlesController.keys)
            if (circles.isNotEmpty()) {
                val circle = circles[0]

                val last = items.size - 1
                val info = items[last]

                /**
                 * Parse the date.
                 */
                val date = VosUtils.parseDate(info.datetime?: "1970-01-01 00:00:00:")

                /**
                 * Calculate the difference in time between the vos date and now.
                 */
                val diff = date?.let{ VosUtils.calculateTimeDifferenceInHoursFromNow(it).toDouble()}?:0.0

                /**
                 * If the VosInfo is younger or equal than 2 hours: show a circle indicating where the vos team could have walked
                 */
                if (diff > 0.0 && diff <= 2.0) {
                    circle.setRadius(VosUtils.calculateRadius(date!!, JappPreferences.walkSpeed))
                    if (JappPreferences.isAutoEnlargementEnabled) {
                        handler.postDelayed(this, JappPreferences.autoEnlargementIntervalInMs.toLong())
                    }
                } else {
                    circle.remove()
                    circles.remove(circle)
                }
            }
        }
    }

}
