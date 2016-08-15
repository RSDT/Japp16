package nl.rsdt.japp.jotial;

import android.os.Bundle;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Defines a interface for classes that can be recreated.
 */
public interface Recreatable {

    /**
     * Gets invoked on the Activity's onCreate().
     * */
    void onCreate(Bundle savedInstanceState);

    /**
     * Gets invoked when the state should be saved.
     * */
    void onSaveInstanceState(Bundle saveInstanceState);

}
