package nl.rsdt.japp.jotial.maps.sighting

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson

import nl.rsdt.japp.R
import nl.rsdt.japp.application.Japp
import nl.rsdt.japp.jotial.maps.deelgebied.Deelgebied
import nl.rsdt.japp.jotial.maps.management.MarkerIdentifier
import nl.rsdt.japp.jotial.maps.wrapper.IJotiMap
import nl.rsdt.japp.jotial.maps.wrapper.IMarker

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 13-7-2016
 * Class for Sighting
 */
class SightingSession : Snackbar.Callback(), View.OnClickListener, DialogInterface.OnClickListener, IJotiMap.SnapshotReadyCallback, IJotiMap.OnMapClickListener, IJotiMap.CancelableCallback {

    /**
     * The type of the sighting.
     */
    private var type: String? = null

    /**
     * The GoogleMap used to create markers.
     */
    private var jotiMap: IJotiMap? = null

    /**
     * The Marker that indicates the location.
     */
    private var marker: IMarker? = null

    /**
     * The Context used for creating dialogs etc.
     */
    private var context: Context? = null

    /**
     * The view where the Snackbar is going to be made on.
     */
    private var targetView: View? = null

    /**
     * The Snackbar that informs the user.
     */
    private var snackbar: Snackbar? = null

    /**
     * The AlertDialog that asks for the users confirmation.
     */
    private var dialog: AlertDialog? = null

    /**
     * The last LatLng that was selected.
     */
    private var lastLatLng: LatLng? = null

    /**
     * The callback for when the sighting is completed;
     */
    private var callback: OnSightingCompletedCallback? = null

    /**
     * The Deelgebied where the lastLatLng is in, null if none.
     */
    private var deelgebied: Deelgebied? = null

    /**
     * Initializes the SightingSession.
     */
    private fun initialize() {
        val bm: Bitmap?
        when (type) {
            SIGHT_HUNT -> bm = BitmapFactory.decodeResource(Japp.instance!!.resources, R.drawable.vos_zwart_4)
            SIGHT_SPOT -> bm = BitmapFactory.decodeResource(Japp.instance!!.resources, R.drawable.vos_zwart_3)
            else -> bm = null
        }
        snackbar = Snackbar.make(targetView!!, R.string.sighting_standard_text, Snackbar.LENGTH_INDEFINITE)
        snackbar!!.setAction(R.string.sighting_snackbar_action_text, this)
        snackbar!!.addCallback(this)

        marker = jotiMap!!.addMarker(Pair(MarkerOptions()
                .visible(false)
                .position(LatLng(0.0, 0.0)), bm))

        val inflater = LayoutInflater.from(context)
        @SuppressLint("InflateParams") val view = inflater.inflate(R.layout.sighting_input_dialog, null)
        dialog = AlertDialog.Builder(context)
                .setCancelable(false)
                .setPositiveButton(R.string.confirm, this)
                .setNegativeButton(R.string.cancel, this)
                .setView(view)
                .create()
    }

    override fun onDismissed(snackbar: Snackbar?, event: Int) {
        super.onDismissed(snackbar, event)
        if (event == BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_SWIPE) {
            if (callback != null) {
                callback!!.onSightingCompleted(null, null, null)
            }
            destroy()
        }
    }

    /**
     * Starts the SightingSession.
     */
    fun start() {
        jotiMap!!.setOnMapClickListener(this)
        snackbar!!.show()
    }

    private fun updateDeelgebied(latLng: LatLng):Boolean{
        var updateMarker = false
        if (deelgebied == null) {
            deelgebied = Deelgebied.resolveOnLocation(latLng)
            updateMarker = true
        } else {
            if (!deelgebied!!.containsLocation(latLng)) {
                deelgebied = null
                deelgebied = Deelgebied.resolveOnLocation(latLng)
                updateMarker = true
            }
        }
        return updateMarker
    }

    override fun onMapClick(latLng: LatLng): Boolean {
        lastLatLng = latLng

        marker!!.position = latLng

        val updateMarker = updateDeelgebied(latLng)

        if (deelgebied != null && updateMarker) {
            var icon: String? = null
            when (type) {
                SIGHT_HUNT -> {
                    marker!!.setIcon(deelgebied!!.drawableHunt)
                    icon = deelgebied!!.drawableHunt.toString()
                }
                SIGHT_SPOT -> {
                    marker!!.setIcon(deelgebied!!.drawableSpot)
                    icon = deelgebied!!.drawableSpot.toString()
                }
            }

            val identifier = MarkerIdentifier.Builder()
                    .setType(MarkerIdentifier.TYPE_SIGHTING)
                    .add("text", type)
                    .add("icon", icon)
                    .create()
            marker!!.title = Gson().toJson(identifier)
        }

        if (!marker!!.isVisible) {
            marker!!.isVisible = true
        }
        return false
    }


    override fun onClick(dialogInterface: DialogInterface, i: Int) {
        when (i) {
            AlertDialog.BUTTON_POSITIVE -> if (callback != null) {
                callback!!.onSightingCompleted(lastLatLng, deelgebied, (dialog!!.findViewById<View>(R.id.sighting_dialog_info_edit) as TextView).text.toString())
                destroy()
            }
            AlertDialog.BUTTON_NEGATIVE -> {
                snackbar!!.setText(R.string.sighting_standard_text)
                snackbar!!.show()
            }
        }
    }

    override fun onClick(view: View) {
        if (deelgebied == null) {
            deelgebied = Deelgebied.Xray
        }
        jotiMap!!.animateCamera(lastLatLng?:LatLng(0.0, 0.0), 12, this)
    }

    override fun onFinish() {

            dialog?.show()
            (dialog?.findViewById<View>(R.id.sighting_dialog_title) as TextView?)?.text = context!!.getString(R.string.confirm_type, type)
            (dialog?.findViewById<View>(R.id.sighting_dialog_team_label) as TextView?)?.text = context!!.getString(R.string.deelgebied_name, deelgebied!!.name)
        jotiMap?.snapshot(this@SightingSession)
    }

    override fun onCancel() {
            dialog?.show()
            (dialog?.findViewById<View>(R.id.sighting_dialog_title) as TextView).text = context!!.getString(R.string.confirm_type, type)
            (dialog?.findViewById<View>(R.id.sighting_dialog_team_label) as TextView).text = context!!.getString(R.string.deelgebied_name, deelgebied!!.name)
        jotiMap?.snapshot(this@SightingSession)
    }


    override fun onSnapshotReady(bitmap: Bitmap) {
        (dialog?.findViewById<View>(R.id.sighting_dialog_snapshot) as ImageView).setImageDrawable(BitmapDrawable(Japp.appResources, bitmap))
    }

    /**
     * Destroys the SightingSession.
     */
    fun destroy() {
        type = null

        if (jotiMap != null) {
            jotiMap!!.setOnMapClickListener(null)
            jotiMap = null
        }

        if (marker != null) {
            marker!!.remove()
            marker = null
        }

        if (dialog != null) {
            dialog!!.dismiss()
            dialog = null
        }

        if (snackbar != null) {
            snackbar!!.dismiss()
            snackbar = null
        }

        lastLatLng = null

        callback = null

        deelgebied = null

    }

    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 13-7-2016
     * Builder for the SightingSession.
     */
    class Builder {

        /**
         * Buffer to hold the SightingSession.
         */
        private val buffer = SightingSession()

        /**
         * Sets the GoogleMap of the SightingSession.
         */
        fun setGoogleMap(jotiMap: IJotiMap?): Builder {
            buffer.jotiMap = jotiMap
            return this
        }

        /**
         * Sets the Type of the SightingSession.
         */
        fun setType(type: String): Builder {
            buffer.type = type
            return this
        }

        /**
         * Sets the TargetView of the SightingSession.
         */
        fun setTargetView(view: View): Builder {
            buffer.targetView = view
            return this
        }

        /**
         * Sets the Context for the Dialog of the SightingSession.
         */
        fun setDialogContext(context: Context): Builder {
            buffer.context = context
            return this
        }

        /**
         * Sets the callback of the SightingSession.
         */
        fun setOnSightingCompletedCallback(callback: OnSightingCompletedCallback): Builder {
            buffer.callback = callback
            return this
        }

        /**
         * Creates the SightingSession.
         */
        fun create(): SightingSession {
            buffer.initialize()
            return buffer
        }
    }

    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 13-7-2016
     * Callback for when a SightingSession is completed.
     */
    interface OnSightingCompletedCallback {
        /**
         * Gets invoked when a SightingSession has been completed.
         *
         * @param chosen The chosen LatLng.
         * @param deelgebied The Deelgebied where the LatLng is in, null if none.
         * @param optionalInfo The optional info the user can provide.
         */
        fun onSightingCompleted(chosen: LatLng?, deelgebied: Deelgebied?, optionalInfo: String?)
    }

    companion object {

        /**
         * Defines the SightingSession type HUNT.
         */
        val SIGHT_HUNT = "HUNT"

        /**
         * Defines the SightingSession type SPOT.
         */
        val SIGHT_SPOT = "SPOT"
    }

}
