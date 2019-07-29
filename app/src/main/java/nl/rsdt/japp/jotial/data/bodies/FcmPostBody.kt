package nl.rsdt.japp.jotial.data.bodies

import com.google.gson.annotations.SerializedName

import nl.rsdt.japp.application.JappPreferences

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 7-9-2016
 * Description...
 */
class FcmPostBody(@field:SerializedName("SLEUTEL")
                  private var key: String?, @field:SerializedName("gebruiker")
                  private var user: String?, @field:SerializedName("token")
                  private var token: String?) {

    fun getKey(): String? {
        return key
    }

    fun getUser(): String? {
        return user
    }

    fun getToken(): String? {
        return token
    }

    fun setKey(key: String): FcmPostBody {
        this.key = key
        return this
    }

    fun setUser(user: String): FcmPostBody {
        this.user = user
        return this
    }

    fun setToken(token: String?): FcmPostBody {
        this.token = token
        return this
    }

    companion object {

        val default: FcmPostBody
            get() = FcmPostBody(JappPreferences.accountKey, JappPreferences.accountUsername, "")
    }

}
