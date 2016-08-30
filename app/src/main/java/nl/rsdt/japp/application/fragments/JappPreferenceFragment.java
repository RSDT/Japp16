package nl.rsdt.japp.application.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import nl.rsdt.japp.BuildConfig;
import nl.rsdt.japp.R;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 10-7-2016
 * Description...
 */
public class JappPreferenceFragment extends PreferenceFragment {

    public static final String TAG = "JappPreferenceFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.release_preferences);
        if(BuildConfig.DEBUG) {
            addPreferencesFromResource(R.xml.debug_preferences);
        }
    }

}
