package nl.rsdt.japp.jotial;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Defines a interface for classes that can be destroyed.
 */
public interface Destroyable {

    /**
     * Gets invoked when the Destroyable is being destroyed.
     * */
    void onDestroy();

}
