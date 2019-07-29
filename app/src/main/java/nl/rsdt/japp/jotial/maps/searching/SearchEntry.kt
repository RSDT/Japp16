package nl.rsdt.japp.jotial.maps.searching

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 29-7-2016
 * Description...
 */
class SearchEntry {

    var id: String? = null
        private set

    var infoId: Int = 0
        private set

    var value: String? = null
        private set

    class Builder {
        internal var entry = SearchEntry()

        fun setId(string: String): Builder {
            entry.id = string
            return this
        }

        fun setInfoId(id: Int): Builder {
            entry.infoId = id
            return this
        }

        fun setValue(value: String): Builder {
            entry.value = value
            return this
        }

        fun create(): SearchEntry {
            return entry
        }

    }


}
