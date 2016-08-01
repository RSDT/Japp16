package nl.rsdt.japp.jotial;

import android.os.Bundle;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 31-7-2016
 * Description...
 */
public interface Recreatable {

    void onCreate(Bundle savedInstanceState);

    void onSaveInstanceState(Bundle saveInstanceState);

}
