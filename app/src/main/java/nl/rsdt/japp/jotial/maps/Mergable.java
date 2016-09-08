package nl.rsdt.japp.jotial.maps;

import nl.rsdt.japp.jotial.maps.management.transformation.AbstractTransducer;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 4-9-2016
 * Description...
 */
public interface Mergable<T extends AbstractTransducer.Result> {
    void merge(T other);
}
