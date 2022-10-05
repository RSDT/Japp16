package nl.rsdt.japp.jotial.net

import org.acra.ktx.sendWithAcra
import java.net.URL

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 22-6-2016
 * Description...
 */
open class UrlBuilder {

    protected var url = ""

    open fun append(arg: String): UrlBuilder {
        url += arg
        return this
    }

    fun buildAsUrl(): URL? {
        try {
            return URL(url)
        } catch (e: Exception) {
            e.sendWithAcra()
            return null
        }

    }

    fun buildAsString(): String {
        return url
    }

}
