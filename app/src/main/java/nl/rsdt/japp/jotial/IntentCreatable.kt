package nl.rsdt.japp.jotial

import android.os.Bundle

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Defines a interface for classes that can be created by intent.
 */
interface IntentCreatable {

    /**
     * Gets invoked when the Activity is created.
     */
    fun onIntentCreate(bundle: Bundle)

}
