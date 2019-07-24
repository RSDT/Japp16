package nl.rsdt.japp.application.fragments;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
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
        PreferenceScreen screen = getPreferenceScreen();
        PreferenceCategory map = (PreferenceCategory) screen.findPreference(JappPreferences.PREF_CAT_MAP);
        ListPreference type = new ListPreference(this.getActivity());
        if(JappPreferences.useOSM()) {
            type.setKey("pref_map_osm_source");
            type.setTitle(R.string.pref_map_osm_source_title);
            type.setSummary(R.string.pref_map_osm_source_sum);
            type.setEntries(R.array.pref_map_osm_source_options);
            type.setEntryValues(R.array.pref_map_osm_source_options_data);
            type.setDefaultValue(getString(R.string.pref_map_osm_source_options_def));
        } else {
            type.setKey("pref_map_type");
            type.setTitle(R.string.pref_map_type_title);
            type.setSummary(R.string.pref_map_type_sum);
            type.setEntries(R.array.pref_map_type_options);
            type.setEntryValues(R.array.pref_map_type_options_data);
            type.setDefaultValue(getString(R.string.pref_map_type_options_def));
            type.setDialogTitle(R.string.pref_map_type_dialog_title);

            ListPreference style = new ListPreference(this.getActivity());
            style.setKey("pref_map_style");
            style.setTitle(R.string.pref_map_style_title);
            style.setSummary(R.string.pref_map_style_sum);
            style.setEntries(R.array.pref_map_style_options);
            style.setEntryValues(R.array.pref_map_style_options_data);
            style.setDefaultValue(getString(R.string.pref_map_style_options_def));
            style.setDialogTitle(R.string.pref_map_style_dialog_title);
            map.addPreference(style);
        }
        map.addPreference(type);
        EditTextPreference preference = (EditTextPreference)findPreference(JappPreferences.DEBUG_VERSION_NAME);
        //TODO set version preference value
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
