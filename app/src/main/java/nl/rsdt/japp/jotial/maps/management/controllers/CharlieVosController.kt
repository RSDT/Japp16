package nl.rsdt.japp.jotial.maps.management.controllers

import nl.rsdt.japp.jotial.maps.wrapper.IJotiMap


/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Description...
 */
class CharlieVosController(jotiMap: IJotiMap) : VosController(jotiMap) {

    override val team: String
        get() = "c"

    override val id: String
        get() = CONTROLLER_ID

    override val storageId: String
        get() = STORAGE_ID

    override val bundleId: String
        get() = BUNDLE_ID

    companion object {

        val CONTROLLER_ID = "CharlieVosController"

        val STORAGE_ID = "STORAGE_VOS_C"

        val BUNDLE_ID = "VOS_C"

        val REQUEST_ID = "REQUEST_VOS_C"
    }

}

