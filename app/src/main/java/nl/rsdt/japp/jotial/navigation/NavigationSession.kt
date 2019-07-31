package nl.rsdt.japp.jotial.navigation

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import nl.rsdt.japp.R
import nl.rsdt.japp.jotial.maps.management.MarkerIdentifier
import nl.rsdt.japp.jotial.maps.wrapper.IJotiMap
import nl.rsdt.japp.jotial.maps.wrapper.IMarker

/**
 * Created by mattijn on 16/08/17.
 */

class NavigationSession(private val targetView: View) : Snackbar.Callback(), IJotiMap.OnMapClickListener, DialogInterface.OnClickListener, View.OnClickListener, IJotiMap.CancelableCallback, IJotiMap.OnMarkerClickListener {

    /**
     * The GoogleMap used to create markers.
     */
    private var jotiMap: IJotiMap? = null

    /**
     * The Marker that indicates the location.
     */
    private var marker: IMarker? = null

    /**
     * The callback that gets invoked when the navigation is completed.
     */
    private var callback: NavigationSession.OnNavigationCompletedCallback? = null

    /**
     * The view where the Snackbar is going to be made on.
     */

    /**
     * The Snackbar that informs the user.
     */
    private var snackbar: Snackbar? = null

    /**
     * The MaterialDialog that asks for the users confirmation and for entering details.
     */
    private var dialog: android.app.AlertDialog? = null
    private var navigator: Navigator? = null
    private var lastmoved: Long = 0
    private fun initialize() {
        snackbar = Snackbar.make(targetView, R.string.swipe_or_cancle, Snackbar.LENGTH_INDEFINITE)
        snackbar!!.setAction(R.string.done, this)
        snackbar!!.addCallback(this)

        val identifier = MarkerIdentifier.Builder()
                .setType(MarkerIdentifier.TYPE_NAVIGATE)
                .create()

        marker = jotiMap!!.addMarker(Pair(MarkerOptions()
                .title(Gson().toJson(identifier))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.binoculars))
                .visible(true)
                .position(LatLng(0.0, 0.0)), null))
    }

    fun start() {
        jotiMap!!.setMarkerOnClickListener(this)
        //navigator.start();
        jotiMap!!.setOnMapClickListener(this)
        snackbar!!.show()
    }

    fun end() {
        jotiMap!!.setMarkerOnClickListener(null)
        navigator!!.clear()
        onDestroy()
    }


    override fun onMapClick(latLng: LatLng): Boolean {
        moveMarker(latLng, false)
        return true
    }

    override fun onClick(view: View) {
        if (marker!!.isVisible) {
            jotiMap!!.animateCamera(marker!!.position, 12, this)
        } else {
            if (snackbar != null) {
                snackbar!!.dismiss()
                snackbar = null
            }
            snackbar = Snackbar.make(targetView, R.string.select_valid_location, Snackbar.LENGTH_INDEFINITE)
            snackbar!!.addCallback(this)
            snackbar!!.setAction(R.string.done, this)
            snackbar!!.show()
        }
    }


    override fun onFinish() {
        if (dialog != null) {
            dialog!!.show()
            (dialog!!.findViewById<View>(R.id.navigation_dialog_title) as TextView).setText(R.string.Open_nav_in_other_app)
        }
    }

    override fun onCancel() {
        if (dialog != null) {
            dialog!!.show()
            (dialog!!.findViewById<View>(R.id.navigation_dialog_title) as TextView).setText(R.string.Open_nav_in_other_app)
        }
    }

    override fun onDismissed(snackbar: Snackbar?, event: Int) {
        super.onDismissed(snackbar, event)

        if (event == Snackbar.Callback.DISMISS_EVENT_SWIPE) {
            if (callback != null) {
                callback!!.onNavigationCompleted(null, false)
            }
        }
    }

    override fun onClick(dialogInterface: DialogInterface, i: Int) {
        when (i) {
            DialogInterface.BUTTON_POSITIVE -> if (callback != null) {
                //String title = ((TextView) dialog.findViewById(R.id.navigation_dialog_title_edit)).getText().toString();
                //String description = ((TextView) dialog.findViewById(R.id.navigation_dialog_description_edit)).getText().toString();
                if (marker != null) {
                    val pos = marker!!.position
                    marker!!.remove()
                    callback!!.onNavigationCompleted(pos, false)
                }
            }
            DialogInterface.BUTTON_NEUTRAL -> if (callback != null) {
                //String title = ((TextView) dialog.findViewById(R.id.navigation_dialog_title_edit)).getText().toString();
                //String description = ((TextView) dialog.findViewById(R.id.navigation_dialog_description_edit)).getText().toString();
                if (marker != null) {
                    val pos = marker!!.position
                    marker!!.remove()
                    callback!!.onNavigationCompleted(pos, true) // // TODO: 30/09/17
                }
            }
            DialogInterface.BUTTON_NEGATIVE -> {
                snackbar!!.setText(R.string.swipe_or_cancle)
                snackbar!!.show()
            }
        }
    }

    private fun onDestroy() {

        if (marker != null) {
            marker!!.remove()
            marker = null
        }

        if (jotiMap != null) {
            jotiMap!!.setOnMapClickListener(null)
            jotiMap = null
        }

        if (dialog != null) {
            dialog!!.dismiss()
            dialog = null
        }

        if (snackbar != null) {
            snackbar!!.dismiss()
            snackbar = null
        }


        callback = null
    }

    private fun moveMarker(latLng: LatLng, priority: Boolean) {
        if (priority || System.currentTimeMillis() - lastmoved > 800) {// // TODO: 01/10/17 magic number
            navigator!!.setEndLocation(latLng, dialog?.context)
            if (!marker!!.isVisible) marker!!.isVisible = true
            marker!!.position = latLng
            this.lastmoved = System.currentTimeMillis()
        }
    }

    override fun OnClick(m: IMarker): Boolean {
        m.showInfoWindow()
        moveMarker(m.position, true)
        return false
    }

    class Builder {

        private lateinit var navigator: Navigator
        private var jotiMap: IJotiMap? = null
        private var callback: OnNavigationCompletedCallback? = null

        private lateinit var dialogContext: Context
        private var dialog: AlertDialog.Builder? = null
        private var targetView: View? = null


        /**
         * Sets the GoogleMap of the SightingSession.
         */
        fun setJotiMap(jotiMap: IJotiMap?): NavigationSession.Builder {
            this.jotiMap = jotiMap
            this.navigator = Navigator(jotiMap)
            return this
        }

        /**
         * Sets the callback of the SightingSession.
         */
        fun setCallback(callback: NavigationSession.OnNavigationCompletedCallback): NavigationSession.Builder {
            this.callback = callback
            return this
        }

        /**
         * Sets the TargetView of the SightingSession.
         */
        fun setTargetView(view: View): NavigationSession.Builder {
            this.targetView = view
            return this
        }

        /**
         * Sets the Context for the Dialog of the SightingSession.
         */
        fun setDialogContext(context: Context): NavigationSession.Builder {
            dialogContext = context
            val inflater = LayoutInflater.from(context)
            @SuppressLint("InflateParams") val view = inflater.inflate(R.layout.navigation_input_dialog, null)
            dialog = AlertDialog.Builder(context)
                    .setView(view)


            return this
        }

        /**
         * Creates the NavigationSession.
         */
        fun create(): NavigationSession {
            val buffer = NavigationSession(targetView!!)
            buffer.callback = callback
            buffer.navigator = this.navigator
            buffer.jotiMap = jotiMap
            buffer.dialog = dialog?.setCancelable(false)
                    ?.setPositiveButton(R.string.navigate_self, buffer)
                    ?.setNeutralButton(R.string.navigate_other, buffer)
                    ?.setNegativeButton(R.string.cancel, buffer)
                    ?.create()
            buffer.initialize()
            return buffer
        }

    }

    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 8-9-2016
     * Description...
     */
    interface OnNavigationCompletedCallback {
        fun onNavigationCompleted(navigateTo: LatLng?, toNavigationPhone: Boolean)
    }
}