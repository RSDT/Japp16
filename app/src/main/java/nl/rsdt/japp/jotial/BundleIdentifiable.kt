package nl.rsdt.japp.jotial

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Defines a interface for classes that can be identified in a Bundle.
 */
interface BundleIdentifiable {

    /**
     * Gets the id of the BundleIdentifiable.
     */
    val bundleId: String
}
