package nl.rsdt.japp.jotial.maps.searching


import nl.rsdt.japp.jotial.maps.wrapper.IMarker

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 29-7-2016
 * Description...
 */
interface Searchable {

    fun provide(): List<String>

    fun searchFor(query: String): IMarker

}
