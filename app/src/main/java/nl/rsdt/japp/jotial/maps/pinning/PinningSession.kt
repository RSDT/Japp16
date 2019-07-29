package nl.rsdt.japp.jotial.maps.pinning

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import nl.rsdt.japp.R
import nl.rsdt.japp.jotial.maps.wrapper.IJotiMap
import nl.rsdt.japp.jotial.maps.wrapper.IMarker

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 8-9-2016
 * Description...
 */
class PinningSession : Snackbar.Callback(), IJotiMap.OnMapClickListener, DialogInterface.OnClickListener, View.OnClickListener, IJotiMap.CancelableCallback {

    /**
     * The GoogleMap used to create markers.
     */
    private var jotiMap: IJotiMap? = null

    /**
     * The Marker that indicates the location.
     */
    private var marker: IMarker? = null

    /**
     * The callback that gets invoked when the pinning is completed.
     */
    private var callback: OnPinningCompletedCallback? = null

    /**
     * The view where the Snackbar is going to be made on.
     */
    private var targetView: View? = null

    /**
     * The Snackbar that informs the user.
     */
    private var snackbar: Snackbar? = null

    /**
     * The MaterialDialog that asks for the users confirmation and for entering details.
     */
    private var dialog: AlertDialog? = null


    private fun initialize() {
        snackbar = Snackbar.make(targetView!!, R.string.swipe_or_cancle, Snackbar.LENGTH_INDEFINITE)
        snackbar!!.setAction(R.string.done, this)
        snackbar!!.addCallback(this)

        marker = jotiMap!!.addMarker(Pair(MarkerOptions()
                .visible(false)
                .position(LatLng(0.0, 0.0)), null))
    }

    fun start() {
        jotiMap!!.setOnMapClickListener(this)
        snackbar!!.show()
    }

    fun end() {
        onDestroy()
    }


    fun onMapReady(jotiMap: IJotiMap) {
        this.jotiMap = jotiMap
    }

    override fun onMapClick(latLng: LatLng): Boolean {
        if (!marker!!.isVisible) marker!!.isVisible = true
        marker!!.position = latLng
        return false
    }

    override fun onClick(view: View) {
        if (marker!!.isVisible) {
            jotiMap!!.animateCamera(marker!!.position, 12, this)
        } else {
            if (snackbar != null) {
                snackbar!!.dismiss()
                snackbar = null
            }
            snackbar = Snackbar.make(targetView!!, R.string.select_valid_location, Snackbar.LENGTH_INDEFINITE)
            snackbar!!.addCallback(this)
            snackbar!!.setAction(R.string.done, this)
            snackbar!!.show()
        }
    }


    override fun onFinish() {
        if (dialog != null) {
            dialog!!.show()
            (dialog!!.findViewById<View>(R.id.pinning_dialog_title) as TextView).setText(R.string.confirm_mark)
        }
    }

    override fun onCancel() {
        if (dialog != null) {
            dialog!!.show()
            (dialog!!.findViewById<View>(R.id.pinning_dialog_title) as TextView).setText(R.string.confirm_mark)
        }
    }

    override fun onDismissed(snackbar: Snackbar?, event: Int) {
        super.onDismissed(snackbar, event)

        when (event) {
            BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_SWIPE -> if (callback != null) {
                callback!!.onPinningCompleted(null)
            }
            BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_ACTION -> {
            }
            BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_CONSECUTIVE -> {
            }
            BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_MANUAL -> {
            }
            BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_TIMEOUT -> {
            }
        }
    }

    override fun onClick(dialogInterface: DialogInterface, i: Int) {
        when (i) {
            DialogInterface.BUTTON_POSITIVE -> if (callback != null) {
                val title = (dialog!!.findViewById<View>(R.id.pinning_dialog_title_edit) as TextView).text.toString()
                val description = (dialog!!.findViewById<View>(R.id.pinning_dialog_description_edit) as TextView).text.toString()
                callback!!.onPinningCompleted(Pin.create(jotiMap!!, Pin.Data(title, description, marker!!.position, R.drawable.ic_place_white_48dp)))
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

    class Builder {

        internal var buffer = PinningSession()

        /**
         * Sets the GoogleMap of the SightingSession.
         */
        fun setJotiMap(jotiMap: IJotiMap): Builder {
            buffer.jotiMap = jotiMap
            return this
        }

        /**
         * Sets the callback of the SightingSession.
         */
        fun setCallback(callback: OnPinningCompletedCallback): Builder {
            buffer.callback = callback
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
            val inflater = LayoutInflater.from(context)
            @SuppressLint("InflateParams") val view = inflater.inflate(R.layout.pinning_input_dialog, null)
            buffer.dialog = AlertDialog.Builder(context)
                    .setCancelable(false)
                    .setPositiveButton(R.string.confirm, buffer)
                    .setNegativeButton(R.string.cancel, buffer)
                    .setView(view)
                    .create()
            return this
        }

        /**
         * Creates the PinningSession.
         */
        fun create(): PinningSession {
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
    interface OnPinningCompletedCallback {
        fun onPinningCompleted(pin: Pin?)
    }

}
