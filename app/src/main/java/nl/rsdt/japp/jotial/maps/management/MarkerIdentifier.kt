package nl.rsdt.japp.jotial.maps.management

import java.util.*

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 17-9-2016
 * Description...
 */
class MarkerIdentifier {

    var type: String? = null
        private set

    val properties = HashMap<String, String?>()

    class Builder {
        internal var identifier = MarkerIdentifier()

        fun setType(type: String): Builder {
            identifier.type = type
            return this
        }

        fun add(key: String, value: String?): Builder {
            identifier.properties[key] = value
            return this
        }

        fun remove(key: String): Builder {
            identifier.properties.remove(key)
            return this
        }

        fun create(): MarkerIdentifier {
            return identifier
        }
    }

    companion object {

        val TYPE_VOS = "VOS"

        val TYPE_HUNTER = "HUNTER"

        val TYPE_FOTO = "FOTO"

        val TYPE_SC = "SC"

        val TYPE_SC_CLUSTER = "SC_CLUSTER"

        val TYPE_SIGHTING = "SIGHTING"

        val TYPE_PIN = "PIN"

        val TYPE_NAVIGATE = "NAVIGATE"

        val TYPE_ME = "ME"
        val TYPE_NAVIGATE_CAR = "NAVIGATE_CAR"
    }

}
