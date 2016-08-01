package nl.rsdt.japp.jotial.maps.management.transformation;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 24-7-2016
 * Description...
 */
public interface Transducable<T> {

    AbstractTransducer<T> getTransducer();

}
