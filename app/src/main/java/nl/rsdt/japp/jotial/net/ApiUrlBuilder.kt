package nl.rsdt.japp.jotial.net


import nl.rsdt.japp.application.JappPreferences

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 10-7-2016
 * Class that makes Url building a lot easier.
 */
class ApiUrlBuilder : UrlBuilder {

    /**
     * The root of the Url.
     */
    private var root: String? = null

    /**
     * The keyword of the url that defines the type of request such as vos.
     */
    private var keyword: String? = null

    /**
     * The value indicating if the API-key should be appended after the keyword.
     */
    private val appendKey: Boolean

    /**
     * Initializes a new instance of ApiUrlBuilder.
     */
    constructor() {
        root = API.API_V2_ROOT
        url += root
        this.appendKey = true
    }

    /**
     * Initializes a new instance of ApiUrlBuilder.
     * @param appendKey Value indicating if the API-key should be appended after the keyword.
     */
    constructor(appendKey: Boolean) {
        this.appendKey = appendKey
        root = API.API_V2_ROOT
        url += root
    }

    override
            /**
             * Appends a String to the Url.
             */
    fun append(arg: String): UrlBuilder {
        if (keyword == null) {
            keyword = arg

            super.append("/$arg")

            if (appendKey) {
                super.append("/" + JappPreferences.accountKey)
            }
            return this
        }
        return super.append("/$arg")
    }

}
