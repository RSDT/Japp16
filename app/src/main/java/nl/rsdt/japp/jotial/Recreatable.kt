package nl.rsdt.japp.jotial

import android.os.Bundle

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Defines a interface for classes that can be recreated.
 */
interface Recreatable {

    /**
     * Gets invoked on the Activity's onCreate().
     */
    fun onCreate(savedInstanceState: Bundle?)

    /**
     * Gets invoked when the state should be saved.
     */
    fun onSaveInstanceState(saveInstanceState: Bundle?)

}
