package nl.rsdt.japp.jotial.maps.pinning

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import nl.rsdt.japp.R
import nl.rsdt.japp.jotial.Recreatable
import nl.rsdt.japp.jotial.io.AppData
import nl.rsdt.japp.jotial.maps.management.MarkerIdentifier
import nl.rsdt.japp.jotial.maps.wrapper.IJotiMap
import java.util.*

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 8-9-2016
 * Description...
 */
class PinningManager : Recreatable, GoogleMap.OnInfoWindowLongClickListener {

    protected var context: Context

    protected var jotiMap: IJotiMap? = null

    protected var pins: ArrayList<Pin>? = ArrayList()

    protected var buffer: ArrayList<Pin.Data>? = ArrayList()

    fun intialize(context: Context) {
        this.context = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val pins: ArrayList<Pin.Data>?
        if (savedInstanceState != null) {
            pins = savedInstanceState.getParcelableArrayList<Data>(BUNDLE_KEY)
        } else {
            pins = AppData.getObject<ArrayList<Data>>(STORAGE_ID, object : TypeToken<ArrayList<Pin.Data>>() {

            }.type)
        }

        if (pins != null && !pins.isEmpty()) {
            if (jotiMap != null) {
                process(pins)
            } else {
                buffer = pins
            }
        }
    }

    override fun onSaveInstanceState(state: Bundle) {
        val list = ArrayList<Pin.Data>()
        var current: Pin?
        for (i in pins!!.indices) {
            current = pins!![i]

            if (current != null) {
                list.add(current.data)
            }

        }
        state.putParcelableArrayList(BUNDLE_KEY, list)
    }

    fun add(pin: Pin) {
        pins!!.add(pin)
        save(true)
    }

    fun remove(pin: Pin) {
        pins!!.remove(pin)
        save(true)
    }

    fun findAssocaited(marker: Marker): Pin? {
        var current: Pin?
        for (i in pins!!.indices) {
            current = pins!![i]
            if (current != null) {
                if (current.marker.id == marker.id) {
                    return current
                }
            }
        }
        return null
    }

    fun onMapReady(jotiMap: IJotiMap) {
        this.jotiMap = jotiMap
        jotiMap.setOnInfoWindowLongClickListener(this)

        if (buffer != null) {
            process(buffer)
            buffer!!.clear()
            buffer = null
        }
    }

    private fun process(input: ArrayList<Pin.Data>) {
        if (jotiMap != null) {
            var buffer: Pin.Data?
            for (i in input.indices) {
                buffer = input[i]

                if (buffer != null) {
                    pins!!.add(Pin.create(jotiMap!!, buffer))
                }
            }
        }
    }

    private fun save(background: Boolean) {
        /**
         * Save the pins
         */
        val list = ArrayList<Pin.Data>()
        var current: Pin?
        for (i in pins!!.indices) {
            current = pins!![i]

            if (current != null) {
                list.add(current.data)
            }
        }
        if (background) {
            AppData.saveObjectAsJsonInBackground(list, STORAGE_ID)
        } else {
            AppData.saveObjectAsJson(list, STORAGE_ID)
        }

    }

    fun onDestroy() {
        save(false)

        if (jotiMap != null) {
            jotiMap = null
        }

        if (pins != null) {
            pins!!.clear()
            pins = null
        }

        if (buffer != null) {
            buffer!!.clear()
            buffer = null
        }

    }

    override fun onInfoWindowLongClick(marker: Marker) {
        val identifier = Gson().fromJson(marker.title, MarkerIdentifier::class.java)
        if (identifier != null) {
            if (MarkerIdentifier.TYPE_PIN == identifier.type) {
                AlertDialog.Builder(context)
                        .setTitle(R.string.remove_mark)
                        .setMessage(R.string.confirm_removal_mark)
                        .setPositiveButton("Ja") { dialogInterface, i ->
                            val assoicated = findAssocaited(marker)
                            if (assoicated != null) {
                                pins!!.remove(assoicated)
                            }
                            marker.remove()
                            save(true)
                        }
                        .setNegativeButton(R.string.no, null)
                        .create()
                        .show()
            }
        }
    }

    companion object {

        protected val STORAGE_ID = "PinData"

        protected val BUNDLE_KEY = "PinningManager"
    }

}
