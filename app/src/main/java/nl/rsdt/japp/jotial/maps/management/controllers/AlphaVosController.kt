package nl.rsdt.japp.jotial.maps.management.controllers


/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Description...
 */
class AlphaVosController : VosController() {

    override val team: String
        get() = "a"

    override val id: String
        get() = CONTROLLER_ID

    override val storageId: String
        get() = STORAGE_ID

    override val bundleId: String
        get() = BUNDLE_ID

    companion object {

        val CONTROLLER_ID = "AlphaVosController"

        val STORAGE_ID = "STORAGE_VOS_A"

        val BUNDLE_ID = "VOS_A"

        val REQUEST_ID = "REQUEST_VOS_A"
    }

}
