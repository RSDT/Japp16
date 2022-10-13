package nl.rsdt.japp.jotial.maps.deelgebied

import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Color
import android.location.Location
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.maps.android.PolyUtil
import nl.rsdt.japp.R
import nl.rsdt.japp.application.JappPreferences
import nl.rsdt.japp.jotial.data.structures.area348.MetaColorInfo
import nl.rsdt.japp.jotial.maps.kml.KmlDeelgebied
import nl.rsdt.japp.jotial.maps.kml.KmlFile
import nl.rsdt.japp.jotial.maps.kml.KmlReader
import org.acra.ktx.sendWithAcra
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 13-7-2016
 * Class for the Deelgebieden,
 * Deelgebied.initialize(Resources) must be called in order for this class to function.
 */
class Deelgebied
/**
 * Initializes a new instance of Deelgebied.
 *
 * @param name The name of the Deelgebied.
 */
private constructor(
        /**
         * The name of this Deelgebied.
         */
        /**
         * Gets the name of the Deelgebied.
         */
        val name: String, colorName: String) : SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val team = this.name.toLowerCase()[0].toString()
        when (key){
            JappPreferences.AREAS_COLOR + team ->{
                changeColor(JappPreferences.getColorName(team))
                for (onColorChangListener in onColorChangeListeners ){
                    onColorChangListener.onColorChange(this)
                }
            }
        }
    }

    /**
     * The hunt drawable associated with this Deelgebied.
     */
    lateinit var dgColor: MetaColorInfo.ColorNameInfo.DeelgebiedColor
    /**
     * Gets the hunt drawable associated with this Deelgebied.
     */
    var drawableHunt: Int = 0
        private set

    /**
     * The spot drawable associated with this Deelgebied.
     */
    /**
     * Gets the spot drawable associated with this Deelgebied.
     */
    var drawableSpot: Int = 0
        private set

    /**
     * The color of this Deelgebied.
     */
    /**
     * Gets the color of the Deelgebied.
     */
    var color: Int = 0
        private set

    /**
     * The list of coordinates that is the area of the Deelgebied.
     */
    /**
     * Gets the list of coordinates that is the area of the Deelgebied.
     */
    var coordinates = ArrayList<LatLng>()
        private set
    private val onColorChangeListeners = LinkedList<OnColorChangeListener>()
    fun addListener(onColorChangeListener: OnColorChangeListener) {
        onColorChangeListeners.add(onColorChangeListener)
    }

    fun removeListener(onColorChangeListener: OnColorChangeListener) {
        onColorChangeListeners.remove(onColorChangeListener)
    }

    /**
     * Gets the color of the Deelgebied, with a given alpha.
     *
     * @param alpha The alpha from 0 - 255.
     * @return The color of the Deelgebied with a given alpha.
     */
    fun alphaled(alpha: Int): Int {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
    }

    fun getDeelgebiedAsync(onInitialized: OnInitialized) {
        if (deelgebiedenInitialized) {
            onInitialized.onInitialized(this)
        } else {
            if (!onInitializedList.containsKey(this.name)) onInitializedList[this.name] = LinkedList()
            onInitializedList[this.name]!!.add(onInitialized)
        }
    }

    init {
        changeColor(colorName)
    }

    fun changeColor(colorName: String){
        this.dgColor = MetaColorInfo.ColorNameInfo.DeelgebiedColor.valueOf(colorName)
        when (this.dgColor) {

            MetaColorInfo.ColorNameInfo.DeelgebiedColor.Groen -> {
                this.drawableHunt = R.drawable.vos_groen_4
                this.drawableSpot = R.drawable.vos_groen_3
                this.color = Color.argb(255, 0, 255, 0)
            }
            MetaColorInfo.ColorNameInfo.DeelgebiedColor.Rood -> {
                this.drawableHunt = R.drawable.vos_rood_4
                this.drawableSpot = R.drawable.vos_rood_3
                this.color = Color.argb(255, 255, 0, 0)
            }
            MetaColorInfo.ColorNameInfo.DeelgebiedColor.Paars -> {
                this.drawableHunt = R.drawable.vos_paars_4
                this.drawableSpot = R.drawable.vos_paars_3
                this.color = Color.argb(255, 255, 0, 255)
            }
            MetaColorInfo.ColorNameInfo.DeelgebiedColor.Oranje -> {
                this.drawableHunt = R.drawable.vos_oranje_4
                this.drawableSpot = R.drawable.vos_oranje_3
                this.color = Color.argb(255, 255, 162, 0)
            }
            MetaColorInfo.ColorNameInfo.DeelgebiedColor.Blauw -> {
                this.drawableHunt = R.drawable.vos_blauw_4
                this.drawableSpot = R.drawable.vos_blauw_3
                this.color = Color.argb(255, 0, 0, 255)
            }
            MetaColorInfo.ColorNameInfo.DeelgebiedColor.Onbekend, MetaColorInfo.ColorNameInfo.DeelgebiedColor.Zwart -> {
                this.drawableHunt = R.drawable.vos_zwart_4
                this.drawableSpot = R.drawable.vos_zwart_3
                this.color = Color.argb(255, 0, 0, 0)
            }
            MetaColorInfo.ColorNameInfo.DeelgebiedColor.Turquoise -> {
                this.drawableHunt = R.drawable.vos_turquoise_4
                this.drawableSpot = R.drawable.vos_turquoise_3
                this.color = Color.argb(255, 0, 255, 255)
            }
            else -> {
                this.drawableHunt = R.drawable.vos_zwart_4
                this.drawableSpot = R.drawable.vos_zwart_3
                this.color = Color.argb(255, 0, 0, 0)
            }
        }
    }
    /**
     * Checks if the DeelgebiedData contains a location.
     *
     * @param location The location to check.
     * @return Value indicating if the location is within the DeelgebiedData.
     */
    fun containsLocation(location: LatLng): Boolean {
        return PolyUtil.containsLocation(location, coordinates, false)
    }

    /**
     * Checks if the DeelgebiedData contains a location.
     *
     * @param location The location to check.
     * @return Value indicating if the location is within the DeelgebiedData.
     */
    fun containsLocation(location: Location): Boolean {
        return PolyUtil.containsLocation(LatLng(location.latitude, location.longitude), coordinates, false)
    }

    interface OnInitialized {
        fun onInitialized(deelgebied: Deelgebied)
    }

    companion object {

        /**
         * Defines the Alpha Deelgebied.
         */
        private val Alpha = Deelgebied("alpha", JappPreferences.getColorName("a")?: MetaColorInfo.ColorNameInfo.DeelgebiedColor.Groen.toString())

        /**
         * Defines the Bravo Deelgebied.
         */
        private val Bravo = Deelgebied("bravo", JappPreferences.getColorName("b")?: MetaColorInfo.ColorNameInfo.DeelgebiedColor.Oranje.toString())

        /**
         * Defines the Charlie Deelgebied.
         */
        private val Charlie = Deelgebied("charlie", JappPreferences.getColorName("c")?: MetaColorInfo.ColorNameInfo.DeelgebiedColor.Rood.toString())

        /**
         * Defines the Delta Deelgebied.
         */
        private val Delta = Deelgebied("delta", JappPreferences.getColorName("d")?: MetaColorInfo.ColorNameInfo.DeelgebiedColor.Turquoise.toString())

        /**
         * Defines the Echo Deelgebied.
         */
        private val Echo = Deelgebied("echo", JappPreferences.getColorName("e")?: MetaColorInfo.ColorNameInfo.DeelgebiedColor.Blauw.toString())

        /**
         * Defines the Foxtrot Deelgebied.
         */

        private val Foxtrot = Deelgebied("foxtrot", JappPreferences.getColorName("f") ?: MetaColorInfo.ColorNameInfo.DeelgebiedColor.Paars.toString())

        /**
         * Defines the Xray Deelgebied.
         */
        val Xray = Deelgebied("xray", JappPreferences.getColorName("x")?: MetaColorInfo.ColorNameInfo.DeelgebiedColor.Zwart.toString())
        private val TAG = "Deelgebied"

        /**
         * Gets a array of all the Deelgebieden.
         */
        fun all(): Array<Deelgebied> {
            return arrayOf(Alpha, Bravo, Charlie, Delta, Echo, Foxtrot, Xray)
        }

        @Volatile
        var deelgebiedenInitialized = false
        private val onInitializedList = HashMap<String, LinkedList<OnInitialized>>()


        /**
         * Initializes the Deelgebieden, loading their coordinates from the Resources.
         */
        @Synchronized
        fun initialize(resources: Resources) {
            KmlReader.parseFromMeta(object : KmlReader.Callback {
                override fun onException(e: Throwable) {
                    e.sendWithAcra()
                    Log.e(Deelgebied.TAG, e.toString())
                    val gebieden = all()
                    var current: Deelgebied
                    for (g in gebieden.indices) {
                        current = gebieden[g]

                        val stream: InputStream?
                        when (current.name) {
                            "alpha" -> stream = resources.openRawResource(R.raw.alpha)
                            "bravo" -> stream = resources.openRawResource(R.raw.bravo)
                            "charlie" -> stream = resources.openRawResource(R.raw.charlie)
                            "delta" -> stream = resources.openRawResource(R.raw.delta)
                            "echo" -> stream = resources.openRawResource(R.raw.echo)
                            //case "foxtrot":
                            //stream = resources.openRawResource(R.raw.foxtrot);
                            //break;
                            else -> {
                                stream = null
                                Log.i("Deelgebied", "No polygon data was found for " + current.name)
                            }
                        }

                        if (stream != null) {
                            val r = BufferedReader(InputStreamReader(stream))
                            val total = StringBuilder()
                            var line: String? = r.readLine()
                            try {
                                while (line  != null) {
                                    total.append(line).append('\n')
                                    line = r.readLine()
                                }
                            } catch (err: IOException) {
                                Log.e("Deelgebied", "Error occurred while reading stream", err)
                                e.sendWithAcra()
                            }

                            val data = total.toString()

                            current.coordinates = Gson().fromJson(data, object : TypeToken<ArrayList<LatLng>>() {

                            }.type)
                        }
                    }
                    deelgebiedenInitialized = true
                    for ((key, value) in onInitializedList) {
                        for (oi in value) {
                            oi.onInitialized(Objects.requireNonNull<Deelgebied>(Deelgebied.parse(key)))
                        }
                    }
                }

                override fun onSucces(kml: KmlFile) {
                    val gebieden = all()
                    var current: Deelgebied
                    for (g in gebieden.indices) {
                        current = gebieden[g]

                        val kmldg: KmlDeelgebied?
                        when (current.name) {
                            "alpha" -> kmldg = kml.alpha
                            "bravo" -> kmldg = kml.bravo
                            "charlie" -> kmldg = kml.charlie
                            "delta" -> kmldg = kml.delta
                            "echo" -> kmldg = kml.echo
                            "foxtrot" -> kmldg = kml.foxtrot
                            else -> kmldg = null
                        }

                        if (kmldg != null) {

                            val tmp = ArrayList<LatLng>(kmldg.boundry.size)
                            for (i in 0 until kmldg.boundry.size) {
                                val kmldgLoc = kmldg.boundry[i]
                                val tmploc = LatLng(kmldgLoc.lat?:0.0, kmldgLoc.lon?:0.0)
                                while (tmp.size - 1 < i) {
                                    tmp.add(LatLng(0.0,0.0))
                                }
                                tmp[i] = tmploc
                            }
                            current.coordinates = tmp
                        } else {
                            Log.i("Deelgebied", "No polygon data was found for " + current.name)
                        }
                    }
                    deelgebiedenInitialized = true
                    for ((key, value) in onInitializedList) {
                        for (oi in value) {
                            parse(key)?.also { oi.onInitialized(it) }
                        }
                    }
                }
            })
            waitUntilInitzialized()
        }

        /**
         * Resolves the Deelgebied on the location.
         *
         * @param location The location where the Deelgebied can be resolved on.
         * @return The Deelgebied resolved from the location, returns null if none.
         */
        fun resolveOnLocation(location: LatLng): Deelgebied? {
            waitUntilInitzialized()
            val data = all()
            var current: Deelgebied

            for (i in data.indices) {
                current = data[i]
                if (current.containsLocation(location)) {
                    return current
                }
            }
            return null
        }

        /**
         * Resolves the Deelgebied on the location.
         *
         * @param location The location where the Deelgebied can be resolved on.
         * @return The Deelgebied resolved from the location, returns null if none.
         */
        fun resolveOnLocation(location: Location): Deelgebied? {
            waitUntilInitzialized()
            val data = all()
            var current: Deelgebied

            for (i in data.indices) {
                current = data[i]
                if (current.containsLocation(location)) {
                    return current
                }
            }
            return null
        }

        @Synchronized
        fun waitUntilInitzialized() {
            /*
        while (!deelgebiedenInitialized){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {

                e.printStackTrace();
            }
        }*/
        }

        fun parse(name: String?): Deelgebied {

            waitUntilInitzialized()
            return if (name == null){
                Xray
            }else{
                when (name.lowercase(Locale.ROOT)) {
                    "alpha", "Alpha", "a", "A" -> Alpha
                    "bravo", "Bravo", "b", "B" -> Bravo
                    "charlie", "Charlie", "c", "C" -> Charlie
                    "delta", "Delta", "d", "D" -> Delta
                    "echo", "Echo", "e", "E" -> Echo
                    "foxtrot", "Foxtrot", "f", "F" -> Foxtrot
                    "xray", "Xray", "X-ray", "x-ray", "x", "X" -> Xray
                    else -> Xray
                }
            }
        }
    }
    interface OnColorChangeListener{
        fun onColorChange(deelgebied: Deelgebied)
    }
}
