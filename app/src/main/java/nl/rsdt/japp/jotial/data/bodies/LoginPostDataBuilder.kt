package nl.rsdt.japp.jotial.data.bodies

import com.google.gson.JsonObject
import org.json.JSONObject
import java.util.*

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 13-7-2016
 * Description...
 */
class LoginPostDataBuilder {

    private var user: String = ""

    private var password: String = ""

    fun setUsername(username: String): LoginPostDataBuilder {
        this.user = username
        return this
    }

    fun setPassword(password: String): LoginPostDataBuilder {
        this.password = password
        return this
    }

    fun build(): String {
        val `object` = JsonObject()
        `object`.addProperty("gebruiker", user)
        `object`.addProperty("ww", password)
        return `object`.toString()
    }

    fun buildAsParams(): JSONObject {
        val map = HashMap<String, String>()
        map["gebruiker"] = user
        map["ww"] = password
        return JSONObject(map)
    }

}
