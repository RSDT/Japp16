package nl.rsdt.japp.jotial.maps

import nl.rsdt.japp.jotial.maps.management.transformation.AbstractTransducer

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 4-9-2016
 * Description...
 */
interface Mergable<T : AbstractTransducer.Result> {
    fun merge(other: T)
}
