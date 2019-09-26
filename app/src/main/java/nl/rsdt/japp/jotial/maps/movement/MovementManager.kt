package nl.rsdt.japp.jotial.maps.movement

import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Pair
import android.view.View
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.LocationSource
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import nl.rsdt.japp.R
import nl.rsdt.japp.application.Japp
import nl.rsdt.japp.application.JappPreferences
import nl.rsdt.japp.jotial.data.bodies.AutoUpdateTaakPostBody
import nl.rsdt.japp.jotial.data.structures.area348.AutoInzittendeInfo
import nl.rsdt.japp.jotial.io.AppData
import nl.rsdt.japp.jotial.maps.deelgebied.Deelgebied
import nl.rsdt.japp.jotial.maps.management.MarkerIdentifier
import nl.rsdt.japp.jotial.maps.misc.AnimateMarkerTool
import nl.rsdt.japp.jotial.maps.misc.LatLngInterpolator
import nl.rsdt.japp.jotial.maps.wrapper.ICameraPosition
import nl.rsdt.japp.jotial.maps.wrapper.IJotiMap
import nl.rsdt.japp.jotial.maps.wrapper.IMarker
import nl.rsdt.japp.jotial.maps.wrapper.IPolyline
import nl.rsdt.japp.jotial.net.apis.AutoApi
import nl.rsdt.japp.service.LocationService
import nl.rsdt.japp.service.ServiceManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 2-8-2016
 * Description...
 */
class MovementManager : ServiceManager.OnBindCallback<LocationService.LocationBinder>, LocationListener, SharedPreferences.OnSharedPreferenceChangeListener {
    private val MAX_SIZE_LONG_TAIL = 60
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == JappPreferences.TAIL_LENGTH){
            smallTailPoints.maxSize = JappPreferences.tailLength
            smallTail?.points = smallTailPoints
            hourTailPoints.maxSize = JappPreferences.tailLength
            hourTail?.points = hourTailPoints
            hourTailPoints.onremoveCallback = ::addToLongTail
        }
    }


    private val fastestInterval: Long = 100
    private val locationInterval: Long = 1500
    private var service: LocationService? = null

    private var jotiMap: IJotiMap? = null

    private var marker: IMarker? = null

    private var smallTail: IPolyline? = null

    private val smallTailPoints: TailPoints<LatLng> = TailPoints(JappPreferences.tailLength)

    private var hourTail: IPolyline? = null

    private val hourTailPoints: TailPoints<LatLng> = TailPoints(MAX_SIZE_LONG_TAIL)

    private var bearing: Float = 0f

    private var lastLocation: Location? = null

    private var lastHourLocationTime: Long = System.currentTimeMillis()

    private var activeSession: FollowSession? = null

    private var deelgebied: Deelgebied? = null

    private var snackBarView: View? = null

    private var listener: LocationService.OnResolutionRequiredListener? = null

    fun addToLongTail(location: LatLng, addedOn: Long){
        if (lastHourLocationTime - addedOn > 60 * 60 * 1000){
            lastHourLocationTime = addedOn
            hourTailPoints.add(location)
            hourTail?.points = hourTailPoints
        }
    }

    fun setListener(listener: LocationService.OnResolutionRequiredListener) {
        this.listener = listener
    }

    fun setSnackBarView(snackBarView: View) {
        this.snackBarView = snackBarView
    }

    fun newSession(jotiMap: IJotiMap,before: ICameraPosition, zoom: Float, aoa: Float): FollowSession? {
        if (activeSession != null) {
            activeSession!!.end()
            activeSession = null
        }
        activeSession = FollowSession(jotiMap,before, zoom, aoa)
        return activeSession
    }

    fun onCreate(savedInstanceState: Bundle?) {
        val list:ArrayList<Pair<LatLng,Long>>? = AppData.getObject<ArrayList<Pair<LatLng,Long>>>(
                STORAGE_KEY,
                object : TypeToken<ArrayList<Pair<LatLng,Long>>>() {}.type)
        if (list != null) {
            smallTailPoints.setPoints(list)
            smallTail?.points = smallTailPoints
        }
    }


    fun onSaveInstanceState(saveInstanceState: Bundle?) {
        save()
    }

    override fun onLocationChanged(l: Location) {
        Japp.lastLocation = l
        var location = Japp.lastLocation?:l
        var nextLocation = Japp.lastLocation?:l
        do {
            onNewLocation(location)
            location = nextLocation
            nextLocation = Japp.lastLocation?:l
        } while(location != nextLocation)
    }
    
    fun onNewLocation(location: Location){
        val ldeelgebied = deelgebied
        if (marker != null) {
            if (lastLocation != null) {
                bearing = lastLocation!!.bearingTo(location)

                /**
                 * Animate the marker to the new position
                 */
                AnimateMarkerTool.animateMarkerToICS(marker, LatLng(location.latitude, location.longitude), LatLngInterpolator.Linear(), 1000)
                marker?.setRotation(bearing)
            } else {
                marker?.position = LatLng(location.latitude, location.longitude)
            }
            smallTailPoints.add(LatLng(location.latitude, location.longitude))
            smallTail?.points = smallTailPoints
        }

        val refresh = if (ldeelgebied != null) {
            if (!ldeelgebied.containsLocation(location)) {

                /**
                 * Unsubscribe from the current deelgebied messages
                 */
                FirebaseMessaging.getInstance().unsubscribeFromTopic(ldeelgebied.name)
                true
            } else {
                false
            }
        } else {
            true
        }


        if (refresh) {
            deelgebied = Deelgebied.resolveOnLocation(location)
            if (deelgebied != null && snackBarView != null) {
                Snackbar.make(snackBarView!!, """Welkom in deelgebied ${deelgebied?.name}""", Snackbar.LENGTH_LONG).show()

                /**
                 * Subscribe to the new deelgebied messages.
                 */
                FirebaseMessaging.getInstance().subscribeToTopic(deelgebied?.name)


                val coordinatesSmall: List<LatLng> = smallTailPoints
                smallTail?.remove()
                hourTail?.remove()
                smallTail = jotiMap!!.addPolyline(
                        PolylineOptions()
                                .width(3f)
                                .color(getTailColor()?:Color.BLUE)
                                .addAll(coordinatesSmall))
                val coordinatesHour: List<LatLng> = smallTailPoints
                hourTail = jotiMap!!.addPolyline(
                        PolylineOptions()
                                .width(3f)
                                .color(getTailColor()?:Color.BLUE)
                                .addAll(coordinatesHour))
            }
        }
        /**
         * Make the marker visible
         */
        if (marker?.isVisible == false) {
            marker?.isVisible = true
        }
        updateAutoTaak(location)

        if (activeSession != null) {
            activeSession!!.onLocationChanged(location)
        }

        lastLocation = location
    }

    private fun getTailColor(): Int? {
        val color = deelgebied?.color
        return if (color != null)
            Color.rgb(255 - Color.red(color), 255 - Color.green(color), 255 - Color.blue(color))
        else
            null
    }

    private fun updateAutoTaak(location: Location) {
        if (JappPreferences.autoTaak) {
            val autoApi = Japp.getApi(AutoApi::class.java)
            autoApi.getInfoById(JappPreferences.accountKey, JappPreferences.accountId).enqueue(object : Callback<AutoInzittendeInfo> {
                override fun onFailure(call: Call<AutoInzittendeInfo>, t: Throwable) {
                    //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onResponse(call: Call<AutoInzittendeInfo>, response: Response<AutoInzittendeInfo>) {
                    val newTaak = """${deelgebied?.name}${Japp.getString(R.string.automatisch)}"""
                    if (deelgebied != null && newTaak.toLowerCase() != response.body()?.taak?.toLowerCase()) {
                        val body: AutoUpdateTaakPostBody = AutoUpdateTaakPostBody.default
                        body.setTaak(newTaak)
                        autoApi.updateTaak(body).enqueue(object : Callback<Void> {
                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                //TODO("not implemented")
                            }

                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                if (response.isSuccessful) {
                                    Snackbar.make(snackBarView!!, """taak upgedate: ${deelgebied?.name}""", Snackbar.LENGTH_LONG).show()
                                }
                            }
                        })
                    }
                }

            })
        }
    }

    override fun onBind(binder: LocationService.LocationBinder) {
        val service = binder.instance
        service.setListener(listener)
        service.add(this)
        service.request = LocationRequest()
                .setInterval(locationInterval)
                .setFastestInterval(fastestInterval)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        this.service = service
    }

    fun postResolutionResultToService(code: Int) {
        service?.handleResolutionResult(code)
    }

    fun requestLocationSettingRequest() {
        service?.checkLocationSettings()
    }

    fun onMapReady(jotiMap: IJotiMap) {
        this.jotiMap = jotiMap

        val identifier = MarkerIdentifier.Builder()
                .setType(MarkerIdentifier.TYPE_ME)
                .add("icon", R.drawable.me.toString())
                .create()

        marker = jotiMap.addMarker(Pair(
                MarkerOptions()
                        .position(LatLng(52.021818, 6.059603))
                        .visible(false)
                        .flat(true)
                        .title(Gson().toJson(identifier)), BitmapFactory.decodeResource(Japp.instance!!.resources, R.drawable.me)))

        smallTail = jotiMap.addPolyline(
                PolylineOptions()
                        .width(3f)
                        .color(getTailColor()?:Color.BLUE))
        hourTail = jotiMap.addPolyline(
                PolylineOptions()
                        .width(3f)
                        .color(getTailColor()?:Color.BLUE))

            if (smallTailPoints.isNotEmpty()) {
                smallTail!!.points = smallTailPoints
                val last = smallTailPoints.size - 1
                marker?.position = smallTailPoints[last]
                marker?.isVisible = true
            }
    }

    inner class FollowSession(private val jotiMap: IJotiMap, private val before: ICameraPosition, zoom: Float, aoa: Float) : LocationSource.OnLocationChangedListener {

        private var zoom = 19f

        private var aoa = 45f

        init {
            this.zoom = zoom
            this.aoa = aoa

            /**
             * Enable controls.
             */
            jotiMap.uiSettings.setAllGesturesEnabled(true)
            jotiMap.uiSettings.setCompassEnabled(true)

            jotiMap.setOnCameraMoveStartedListener(GoogleMap.OnCameraMoveStartedListener { i ->
                if (i == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                    val position = jotiMap.previousCameraPosition
                    setZoom(position.zoom)
                    setAngleOfAttack(position.tilt)
                }
            })

        }

        private fun setZoom(zoom: Float) {
            this.zoom = zoom
        }

        private fun setAngleOfAttack(aoa: Float) {
            this.aoa = aoa
        }

        override fun onLocationChanged(location: Location) {
            /**
             * Animate the camera to the new position
             */
            if (JappPreferences.followNorth()) {
                jotiMap.cameraToLocation(true, location, zoom, aoa, 0f)
            } else {
                jotiMap.cameraToLocation(true, location, zoom, aoa, bearing)
            }

        }

        fun end() {

            /**
             * Save the settings of the session to the release_preferences
             */
            JappPreferences.followZoom = zoom
            JappPreferences.setFollowAoa(aoa)

            /**
             * Disable controls
             */
            jotiMap.uiSettings.setCompassEnabled(false)

            /**
             * Remove callback
             */
            jotiMap.setOnCameraMoveStartedListener(null)

            /**
             * Move the camera to the before position
             */
            //googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(before));

            activeSession = null
        }
    }

    fun onResume() {
        service?.request = LocationRequest()
                    .setInterval(locationInterval)
                    .setFastestInterval(fastestInterval)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    }

    fun onPause() {
        service?.apply { request = standard }
    }

    private fun save() {
        val list1 = smallTailPoints.toArrayList()
        val list2 = hourTailPoints.toArrayList()
        val list3 = ArrayList<Pair<LatLng, Long>>()
        list3.addAll(list2)
        list3.addAll(list1)
        list3.sortBy { it.second }
        AppData.saveObjectAsJsonInBackground(list3, STORAGE_KEY)
    }

    fun onDestroy() {
        marker?.remove()
        smallTail?.remove()
        smallTail = null
        smallTailPoints.clear()

        hourTail?.remove()
        hourTailPoints.clear()
        hourTail = null
        if (jotiMap != null) {
            jotiMap = null
        }

        if (marker != null) {
            marker?.remove()
            marker = null
        }

        if (lastLocation != null) {
            lastLocation = null
        }

        if (activeSession != null) {
            activeSession = null
        }

        if (service != null) {
            service?.setListener(null)
            service?.remove(this)
            service = null
        }
        snackBarView = null
    }

    companion object {

        private val STORAGE_KEY = "TAIL"

        private val BUNDLE_KEY = "MovementManager"
    }
}

class TailPoints<T>(maxSize:Int) : List<T>{

    private var list: ArrayList<T> = ArrayList()
    private var addedOn: MutableMap<T,Long> = HashMap()
    private var currentFirst = 0
    var onremoveCallback: (T, Long) -> Unit= {element, timeAdded -> }
    internal var maxSize:Int = maxSize
        set(value) {
            rearrangeList()
            if (value < list.size){
                val toRemove = list.subList(value, list.size)
                for (el in toRemove){
                    addedOn.remove(el)
                }
                list.removeAll(toRemove)
            }
            field = value
        }

    private fun toListIndex(tailIndex: Int):Int{
        return (currentFirst + tailIndex) % maxSize
    }

    private fun toTailIndex(listIndex:Int ): Int{
        return when {
            listIndex > currentFirst -> listIndex - currentFirst
            listIndex < currentFirst -> listIndex + currentFirst
            else -> 0
        }
    }

    private fun incrementCurrentFirst() {
        currentFirst++
        if (currentFirst >= maxSize){
            currentFirst = 0
        }
    }

    private fun rearrangeList(){
        val first = list.subList(currentFirst, list.size)
        val second = list.subList(0, currentFirst)
        val result = ArrayList<T>()
        result.addAll(first)
        result.addAll(second)
        currentFirst = 0
        list = result
    }

    override fun iterator(): kotlin.collections.Iterator<T> {
        return Iterator(0)
    }

    override val size: Int
        get() = list.size

    override fun contains(element: T): Boolean {
        return list.contains(element)
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        return list.containsAll(elements)
    }

    override fun get(index: Int): T {
        return list[toListIndex(index)]
    }

    override fun indexOf(element: T): Int {
        return toTailIndex(list.indexOf(element))
    }

    override fun isEmpty(): Boolean {
        return list.isEmpty()
    }

    override fun lastIndexOf(element: T): Int {
        return toTailIndex(list.lastIndexOf(element))
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<T> {
        val fromIndexList = toListIndex(fromIndex)
        val toIndexList = toListIndex(toIndex)
        return when {
            fromIndexList == toIndexList -> listOf()
            fromIndexList < toIndexList -> list.subList(fromIndexList, toIndexList)
            else -> {
                val result = list.subList(0, toIndexList)
                result.addAll(list.subList(fromIndexList, maxSize))
                result
            }
        }
    }

    override fun listIterator(): ListIterator<T> {
        return Iterator(0)
    }

    override fun listIterator(index: Int): ListIterator<T> {
        return Iterator(index)
    }

    fun toArrayList():ArrayList<Pair<T, Long>>{
        rearrangeList()
        return ArrayList(list.map {Pair(it, addedOn[it]!!)})
    }

    fun add(element: T): Boolean {
        if (list.contains(element)){
            return false
        }
        addedOn[element] = System.currentTimeMillis()
        return if (list.size < maxSize){
            assert(currentFirst == 0) {currentFirst}
            list.add(element)


        } else {
            onremoveCallback(list[currentFirst], addedOn[list[currentFirst]]!!)
            addedOn.remove(list[currentFirst])
            list[currentFirst] = element
            incrementCurrentFirst()
            true
        }
    }

    fun clear() {
        list.clear()
        addedOn.clear()
        currentFirst = 0
    }

    fun setPoints(list: List<Pair<T,Long>>) {
        clear()
        if (list.size <= maxSize) {
            this.list.addAll(list.map { it.first })
            list.forEach{ addedOn[it.first] = it.second }
        }else{
            val overflow = list.size - maxSize
            val sublist = list.subList(overflow, list.size)
            this.list.addAll(sublist.map { it.first })
            sublist.forEach { addedOn[it.first] = it.second }
        }
        assert(this.list.size <= maxSize)
    }

    inner class Iterator(private var currentIndex: Int): ListIterator<T> {

        override fun hasNext(): Boolean {
            return currentIndex + 1 < size
        }

        override fun next(): T {
            val nextE = get(currentIndex)
            currentIndex++
            return nextE
        }

        override fun hasPrevious(): Boolean {
            return currentIndex - 1 > maxSize
        }

        override fun nextIndex(): Int {
            return currentIndex + 1
        }

        override fun previous(): T {
            val prevE = get(currentIndex)
            currentIndex--
            return prevE
        }

        override fun previousIndex(): Int {
            return currentIndex - 1
        }

    }
}
