package nl.rsdt.japp.jotial.maps.window

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.gson.Gson
import nl.rsdt.japp.R
import nl.rsdt.japp.application.JappPreferences
import nl.rsdt.japp.jotial.maps.management.MarkerIdentifier

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 29-8-2016
 * Description...
 */
class CustomInfoWindowAdapter(private val inflater: LayoutInflater, private val googleMap: GoogleMap) : GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowCloseListener {

    private var session: ScoutingGroepClickSession? = null

    init {
        this.googleMap.setOnInfoWindowCloseListener(this)
    }

    override fun getInfoWindow(marker: Marker): View {
        val context = inflater.context
        @SuppressLint("InflateParams") val view = inflater.inflate(R.layout.custom_info_window, null)

        val layout = view.findViewById<LinearLayout>(R.id.custom_info_window_text_fields)
        val params = layout.layoutParams as LinearLayout.LayoutParams
        params.rightMargin = 10

        val indicator = view.findViewById<ImageView>(R.id.custom_info_window_indicator_image)

        var create = false
        var end = false

        val identifier = Gson().fromJson(marker.title, MarkerIdentifier::class.java)
        if (identifier != null) {

            val text = StringBuilder()

            val buffer = TextView(context)
            buffer.setTextColor(Color.DKGRAY)
            buffer.layoutParams = params

            val properties = identifier.properties
            when (identifier.type) {
                MarkerIdentifier.TYPE_VOS -> {
                    text.append(properties["note"])
                    text.append("\n")
                    text.append(properties["extra"])
                    text.append("\n")
                    text.append(properties["time"])
                    text.append("\n")

                    indicator.setImageDrawable(ContextCompat.getDrawable(context, Integer.parseInt(properties["icon"]!!)))
                }
                MarkerIdentifier.TYPE_FOTO -> {
                    text.append(properties["info"])
                    text.append("\n")
                    text.append(properties["extra"])
                    text.append("\n")

                    indicator.setImageDrawable(ContextCompat.getDrawable(context, Integer.parseInt(properties["icon"]!!)))
                }
                MarkerIdentifier.TYPE_HUNTER -> {
                    text.append(properties["hunter"])
                    text.append("\n")
                    text.append(properties["time"])
                    text.append("\n")

                    indicator.setImageDrawable(ContextCompat.getDrawable(context, Integer.parseInt(properties["icon"]!!)))
                }
                MarkerIdentifier.TYPE_SC -> {
                    text.append(properties["name"])
                    text.append("\n")
                    text.append(properties["adres"])
                    text.append("\n")

                    indicator.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.scouting_groep_icon_30x22))

                    if (session != null) {
                        if (session!!.type != MarkerIdentifier.TYPE_SC) {
                            end = true
                            create = true
                        } else {
                            end = true
                        }
                    } else {
                        create = true
                    }
                }
                MarkerIdentifier.TYPE_SC_CLUSTER -> {
                    text.append(properties["size"])
                    text.append("\n")

                    indicator.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.scouting_groep_icon_30x22))
                }
                MarkerIdentifier.TYPE_SIGHTING -> {
                    text.append(properties["text"])
                    text.append("\n")

                    indicator.setImageDrawable(ContextCompat.getDrawable(context, Integer.parseInt(properties["icon"]!!)))
                }
                MarkerIdentifier.TYPE_PIN -> {
                    text.append(properties["title"])
                    text.append("\n")
                    text.append(properties["description"])
                    text.append("\n")
                    text.append(R.string.keep_pressed_to_remove)
                    text.append("\n")

                    indicator.setImageDrawable(ContextCompat.getDrawable(context, Integer.parseInt(properties["icon"]!!)))
                }
                MarkerIdentifier.TYPE_NAVIGATE -> {
                    text.append(R.string.navigate_to_here)
                    text.append("\n")

                    indicator.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.binoculars))
                }
                MarkerIdentifier.TYPE_NAVIGATE_CAR -> {
                    text.append(R.string.navigation_phone_navigates_here)
                    text.append("\n")
                    text.append("geplaatst door: ")
                    text.append(properties["addedBy"])
                }
                MarkerIdentifier.TYPE_ME -> {
                    var name = JappPreferences.huntname
                    if (name!!.isEmpty()) {
                        name = JappPreferences.accountUsername
                    }

                    text.append(name)
                    text.append("\n")

                    indicator.setImageDrawable(ContextCompat.getDrawable(context, Integer.parseInt(properties["icon"]!!)))
                }
            }//todo voeg leuk icoon toe

            buffer.text = text.toString()
            layout.addView(buffer)

            if (end) {
                if (session != null) {
                    session!!.end()
                    session = null
                }
            }

            if (create) {
                session = ScoutingGroepClickSession.Builder()
                        .setMarker(marker)
                        .setGoogleMap(googleMap)
                        .create()
                session!!.start()
            }
        }

        return view
    }

    private fun createTextView(context: Context, params: ViewGroup.LayoutParams, text: String): TextView {
        val buffer = TextView(context)
        buffer.text = text
        buffer.setTextColor(Color.DKGRAY)
        buffer.layoutParams = params
        return buffer
    }

    override fun getInfoContents(marker: Marker): View? {
        return null
    }

    override fun onInfoWindowClose(marker: Marker) {
        if (session != null) {
            session!!.end()
            session = null
        }
    }
}
