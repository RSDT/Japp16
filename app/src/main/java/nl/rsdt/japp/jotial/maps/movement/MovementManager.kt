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
import nl.rsdt.japp.jotial.io.AppData
import nl.rsdt.japp.jotial.maps.deelgebied.Deelgebied
import nl.rsdt.japp.jotial.maps.management.MarkerIdentifier
import nl.rsdt.japp.jotial.maps.misc.AnimateMarkerTool
import nl.rsdt.japp.jotial.maps.misc.LatLngInterpolator
import nl.rsdt.japp.jotial.maps.wrapper.ICameraPosition
import nl.rsdt.japp.jotial.maps.wrapper.IJotiMap
import nl.rsdt.japp.jotial.maps.wrapper.IMarker
import nl.rsdt.japp.jotial.maps.wrapper.IPolyline
import nl.rsdt.japp.service.LocationService
import nl.rsdt.japp.service.ServiceManager
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

    private var list: ArrayList<LatLng>? = null

    private var listener: LocationService.OnResolutionRequiredListener? = null

    fun setListener(listener: LocationService.OnResolutionRequiredListener) {
        this.listener = listener
    }

    fun setSnackBarView(snackBarView: View) {
        this.snackBarView = snackBarView
    }

    fun newSession(before: ICameraPosition, zoom: Float, aoa: Float): FollowSession {
        if (activeSession != null) {
            activeSession!!.end()
            activeSession = null
        }
        activeSession = FollowSession(before, zoom, aoa)
        return activeSession
    }

    fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            list = savedInstanceState.getParcelableArrayList(BUNDLE_KEY)
        } else {
            list = AppData.getObject<ArrayList<LatLng>>(STORAGE_KEY, object : TypeToken<ArrayList<LatLng>>() {

            }.type)
        }
    }


    fun onSaveInstanceState(saveInstanceState: Bundle) {
        if (tail != null) {
            saveInstanceState.putParcelableArrayList(BUNDLE_KEY, ArrayList(tail!!.points))
        }
    }

    override fun onLocationChanged(location: Location) {
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
        var refresh = false
        if (deelgebied != null) {
            if (!deelgebied!!.containsLocation(location)) {
                refresh = true

                /**
                 * Unsubscribe from the current deelgebied messages
                 */
                FirebaseMessaging.getInstance().unsubscribeFromTopic(deelgebied!!.name)
            }
        } else {
            refresh = true
        }

        if (refresh) {
            deelgebied = Deelgebied.resolveOnLocation(location)
            if (deelgebied != null && snackBarView != null) {
                Snackbar.make(snackBarView!!, "Welkom in deelgebied " + deelgebied!!.name, Snackbar.LENGTH_LONG).show()

                /**
                 * Subscribe to the new deelgebied messages.
                 */
                FirebaseMessaging.getInstance().subscribeToTopic(deelgebied!!.name)

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
        if (activeSession != null) {
            activeSession!!.onLocationChanged(location)
        }

        lastLocation = location
    }

    override fun onBind(binder: LocationService.LocationBinder) {
        service = binder.instance
        service!!.setListener(listener)
        service!!.add(this)
        service!!.setRequest(LocationRequest()
                .setInterval(700)
                .setFastestInterval(100)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY))
    }

    fun postResolutionResultToService(code: Int) {
        if (service != null) {
            service!!.handleResolutionResult(code)
        }
    }

    fun requestLocationSettingRequest() {
        if (service != null) {
            service!!.checkLocationSettings()
        }
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

        if (list != null && !list!!.isEmpty()) {
            tail!!.points = list
            val last: Int
            if (list!!.size > 1) {
                last = list!!.size - 1
            } else {
                last = 0
            }
            marker!!.position = list!![last]
            marker!!.isVisible = true
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

            jotiMap!!.setOnCameraMoveStartedListener { i ->
                if (i == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                    val position = jotiMap!!.previousCameraPosition
                    setZoom(position.zoom)
                    setAngleOfAttack(position.tilt)
                }
            }

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
                jotiMap!!.cameraToLocation(true, location, zoom, aoa, 0f)
            } else {
                jotiMap!!.cameraToLocation(true, location, zoom, aoa, bearing)
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
        if (service != null) {
            service!!.setRequest(LocationRequest()
                    .setInterval(700)
                    .setFastestInterval(100)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY))
        }
    }

    fun onPause() {
        if (service != null) {
            service!!.setRequest(service!!.standard)
        }
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

        if (marker != null) {
            marker!!.remove()
            marker = null
        }

        if (tail != null) {
            save(false)
            tail!!.remove()
            tail = null
        }

        if (jotiMap != null) {
            jotiMap = null
        }

        if (marker != null) {
            marker!!.remove()
            marker = null
        }

        if (lastLocation != null) {
            lastLocation = null
        }

        if (activeSession != null) {
            activeSession = null
        }

        if (service != null) {
            service!!.setListener(null)
            service!!.remove(this)
            service = null
        }

    }

    companion object {

        private val STORAGE_KEY = "TAIL"

        private val BUNDLE_KEY = "MovementManager"
    }

}
