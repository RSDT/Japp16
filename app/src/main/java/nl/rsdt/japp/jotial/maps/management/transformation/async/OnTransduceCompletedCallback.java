package nl.rsdt.japp.jotial.maps.management.transformation.async;

import nl.rsdt.japp.jotial.maps.management.transformation.AbstractTransducerResult;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Description...
 */
public interface OnTransduceCompletedCallback<T> {

    void onTransduceCompleted(AbstractTransducerResult<T> result);
}
