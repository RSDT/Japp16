package nl.rsdt.japp.application.fragments;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.View;

import nl.rsdt.japp.BuildConfig;
import nl.rsdt.japp.R;
import nl.rsdt.japp.application.JappPreferences;
import nl.rsdt.japp.jotial.data.structures.area348.HunterInfo;

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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupIconChange();
        EditTextPreference preference = (EditTextPreference)findPreference(JappPreferences.DEBUG_VERSION_NAME);
        preference.setText(getString(R.string.versionName));
    }

    private void setupIconChange() {
        Preference preference = findPreference(JappPreferences.ACCOUNT_ICON);
        preference.setIcon(HunterInfo.getAssociatedDrawable(JappPreferences.getAccountIcon()));
        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                int value = Integer.valueOf((String)o);
                preference.setIcon(HunterInfo.getAssociatedDrawable(value));
                return true;
            }
        });
    }

}
