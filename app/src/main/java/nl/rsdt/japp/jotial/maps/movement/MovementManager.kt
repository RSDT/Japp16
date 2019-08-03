package nl.rsdt.japp.jotial.maps.movement

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
import java.util.*

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 2-8-2016
 * Description...
 */
class MovementManager : ServiceManager.OnBindCallback<LocationService.LocationBinder>, LocationListener {

    private var service: LocationService? = null

    private var jotiMap: IJotiMap? = null

    private var marker: IMarker? = null

    private var tail: IPolyline? = null

    private var bearing: Float = 0.toFloat()

    private var lastLocation: Location? = null

    private var activeSession: FollowSession? = null

    private var deelgebied: Deelgebied? = null

    private var snackBarView: View? = null

    private var list: MutableList<LatLng>? = null

    private var listener: LocationService.OnResolutionRequiredListener? = null

    fun setListener(listener: LocationService.OnResolutionRequiredListener) {
        this.listener = listener
    }

    fun setSnackBarView(snackBarView: View) {
        this.snackBarView = snackBarView
    }

    fun newSession(before: ICameraPosition, zoom: Float, aoa: Float): FollowSession? {
        if (activeSession != null) {
            activeSession!!.end()
            activeSession = null
        }
        activeSession = FollowSession(before, zoom, aoa)
        return activeSession
    }

    fun onCreate(savedInstanceState: Bundle?) {
        list = if (savedInstanceState != null) {
            savedInstanceState.getParcelableArrayList(BUNDLE_KEY)
        } else {
            AppData.getObject<ArrayList<LatLng>>(STORAGE_KEY, object : TypeToken<ArrayList<LatLng>>() {

            }.type)
        }
    }


    fun onSaveInstanceState(saveInstanceState: Bundle?) {
        if (tail != null) {
            saveInstanceState?.putParcelableArrayList(BUNDLE_KEY, ArrayList(tail!!.points))
        }
    }

    override fun onLocationChanged(location: Location) {
        val ldeelgebied = deelgebied
        if (marker != null) {
            if (lastLocation != null) {
                bearing = lastLocation!!.bearingTo(location)

                /**
                 * Animate the marker to the new position
                 */
                AnimateMarkerTool.animateMarkerToICS(marker, LatLng(location.latitude, location.longitude), LatLngInterpolator.Linear(), 1000)
                marker!!.setRotation(bearing)
            } else {
                marker!!.position = LatLng(location.latitude, location.longitude)
            }

            var points: MutableList<LatLng> = tail!!.points
            if (points.size > 150) {
                points = points.subList(points.size / 3, points.size)
            }
            points.add(LatLng(location.latitude, location.longitude))
            tail!!.points = points
        }
        val refresh = if (ldeelgebied != null) {
            if (!ldeelgebied.containsLocation(location)) {

                /**
                 * Unsubscribe from the current deelgebied messages
                 */
                FirebaseMessaging.getInstance().unsubscribeFromTopic(ldeelgebied.name)
                true
            } else{
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

                val color = deelgebied!!.color
                var coordinates: List<LatLng>
                try {
                    coordinates = tail!!.points
                } catch (e: NullPointerException) {
                    coordinates = LinkedList()
                }

                tail!!.remove()
                tail = jotiMap!!.addPolyline(
                        PolylineOptions()
                                .width(3f)
                                .color(Color.rgb(255 - Color.red(color), 255 - Color.green(color), 255 - Color.blue(color)))
                                .addAll(coordinates))
            }
        }
        if (marker != null) {
            /**
             * Make the marker visible
             */
            if (!marker!!.isVisible) {
                marker!!.isVisible = true
            }
        }
        if (JappPreferences.autoTaak) {
            val autoApi = Japp.getApi(AutoApi::class.java)
            autoApi.getInfoById(JappPreferences.accountKey, JappPreferences.accountId).enqueue(object : Callback<AutoInzittendeInfo> {
                override fun onFailure(call: Call<AutoInzittendeInfo>, t: Throwable) {
                    //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onResponse(call: Call<AutoInzittendeInfo>, response: Response<AutoInzittendeInfo>) {
                    val newTaak = """${deelgebied?.name}${Japp.appResources.getString(R.string.automatisch)}"""
                    if (deelgebied != null && newTaak.toLowerCase() != response.body()?.taak?.toLowerCase()) {
                        val body: AutoUpdateTaakPostBody = AutoUpdateTaakPostBody.default
                        body.setTaak(newTaak)
                        autoApi.updateTaak(body).enqueue(object : Callback<Void> {
                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                //TODO("not implemented")
                            }

                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                if (response.isSuccessful){
                                    Snackbar.make(snackBarView!!, """taak upgedate: ${deelgebied?.name}""", Snackbar.LENGTH_LONG).show()
                                }
                            }
                        })
                    }
                }

            })
        }
        if (activeSession != null) {
            activeSession!!.onLocationChanged(location)
        }

        lastLocation = location
    }

    override fun onBind(binder: LocationService.LocationBinder) {
        val service = binder.instance
        service.setListener(listener)
        service.add(this)
        service.request = LocationRequest()
                .setInterval(700)
                .setFastestInterval(100)
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

        tail = jotiMap.addPolyline(
                PolylineOptions()
                        .width(3f)
                        .color(Color.BLUE))

        list?.also { list ->
            if (!list.isEmpty()) {
                tail!!.points = list
                val last: Int
                last = if (list.size > 1) {
                    list.size - 1
                } else {
                    0
                }
                marker!!.position = list[last]
                marker!!.isVisible = true
            }
        }
    }

    inner class FollowSession(private val before: ICameraPosition, zoom: Float, aoa: Float) : LocationSource.OnLocationChangedListener {

        private var zoom = 19f

        private var aoa = 45f

        init {
            this.zoom = zoom
            this.aoa = aoa

            /**
             * Enable controls.
             */
            jotiMap!!.uiSettings.setAllGesturesEnabled(true)
            jotiMap!!.uiSettings.setCompassEnabled(true)

            jotiMap!!.setOnCameraMoveStartedListener(object : GoogleMap.OnCameraMoveStartedListener {
                override fun onCameraMoveStarted(i: Int) {
                    if (i == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                        val position = jotiMap!!.previousCameraPosition
                        setZoom(position.zoom)
                        setAngleOfAttack(position.tilt)
                    }
                }

            })

        }

        fun setZoom(zoom: Float) {
            this.zoom = zoom
        }

        fun setAngleOfAttack(aoa: Float) {
            this.aoa = aoa
        }

        override fun onLocationChanged(location: Location) {
            /**
             * Animate the camera to the new position
             */
            if (JappPreferences.followNorth()) {
                jotiMap?.cameraToLocation(true, location, zoom, aoa, 0f)
            } else {
                jotiMap?.cameraToLocation(true, location, zoom, aoa, bearing)
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
            jotiMap!!.uiSettings.setCompassEnabled(false)

            /**
             * Remove callback
             */
            jotiMap!!.setOnCameraMoveStartedListener(null)

            /**
             * Move the camera to the before position
             */
            //googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(before));

            activeSession = null
        }
    }

    fun onResume() {
        service?.request = LocationRequest()
                    .setInterval(700)
                    .setFastestInterval(100)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    }

    fun onPause() {
        service?.apply { request = standard }
    }

    private fun save(background: Boolean) {
        if (tail != null) {
            if (background) {
                AppData.saveObjectAsJsonInBackground(tail!!.points, STORAGE_KEY)
            } else {
                AppData.saveObjectAsJson(tail!!.points, STORAGE_KEY)
            }
        }
    }

    fun onDestroy() {
            marker?.remove()

        if (tail != null) {
            save(false)
            tail?.remove()
            tail = null
        }

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
