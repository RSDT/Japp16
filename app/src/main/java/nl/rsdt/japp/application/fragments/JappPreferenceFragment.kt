package nl.rsdt.japp.application.fragments

import android.os.Bundle
import android.preference.*
import android.view.View
import nl.rsdt.japp.BuildConfig
import nl.rsdt.japp.R
import nl.rsdt.japp.application.Japp
import nl.rsdt.japp.application.JappPreferences
import nl.rsdt.japp.jotial.data.structures.area348.AutoInzittendeInfo
import nl.rsdt.japp.jotial.data.structures.area348.HunterInfo
import nl.rsdt.japp.jotial.maps.deelgebied.Deelgebied
import nl.rsdt.japp.jotial.net.apis.AutoApi
import org.acra.ktx.sendWithAcra
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 10-7-2016
 * Description...
 */
class JappPreferenceFragment : PreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.release_preferences)
        if (BuildConfig.DEBUG) {
            addPreferencesFromResource(R.xml.debug_preferences)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupIconChange()
        val screen = preferenceScreen
        val map = screen.findPreference(JappPreferences.PREF_CAT_MAP) as PreferenceCategory
        val type = ListPreference(this.activity)
        if (JappPreferences.useOSM()) {
            type.key = "pref_map_osm_source"
            type.setTitle(R.string.pref_map_osm_source_title)
            type.setSummary(R.string.pref_map_osm_source_sum)
            type.setEntries(R.array.pref_map_osm_source_options)
            type.setEntryValues(R.array.pref_map_osm_source_options_data)
            type.setDefaultValue(getString(R.string.pref_map_osm_source_options_def))
        } else {
            type.key = "pref_map_type"
            type.setTitle(R.string.pref_map_type_title)
            type.setSummary(R.string.pref_map_type_sum)
            type.setEntries(R.array.pref_map_type_options)
            type.setEntryValues(R.array.pref_map_type_options_data)
            type.setDefaultValue(getString(R.string.pref_map_type_options_def))
            type.setDialogTitle(R.string.pref_map_type_dialog_title)

            val style = ListPreference(this.activity)
            style.key = "pref_map_style"
            style.setTitle(R.string.pref_map_style_title)
            style.setSummary(R.string.pref_map_style_sum)
            style.setEntries(R.array.pref_map_style_options)
            style.setEntryValues(R.array.pref_map_style_options_data)
            style.setDefaultValue(getString(R.string.pref_map_style_options_def))
            style.setDialogTitle(R.string.pref_map_style_dialog_title)
            map.addPreference(style)
        }
        map.addPreference(type)
        val preference = findPreference(JappPreferences.DEBUG_VERSION_NAME) as EditTextPreference
        val version = BuildConfig.VERSION_NAME
        preference.text = version
    }

    private fun setupIconChange() {
        val preference = findPreference(JappPreferences.ACCOUNT_ICON)
        preference.setIcon(HunterInfo.getAssociatedDrawable(JappPreferences.accountIcon, JappPreferences.taak.name))
        preference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, o ->
            val value = Integer.valueOf(o as String)
            val api = Japp.getApi(AutoApi::class.java)
            api.getInfoById(JappPreferences.accountKey, JappPreferences.accountId).enqueue(object : retrofit2.Callback<AutoInzittendeInfo?>{
                override fun onFailure(call: Call<AutoInzittendeInfo?>, t: Throwable) {
                    preference.setIcon(HunterInfo.getAssociatedDrawable(value, Deelgebied.Xray.name))
                    t.sendWithAcra()
                }

                override fun onResponse(call: Call<AutoInzittendeInfo?>, response: Response<AutoInzittendeInfo?>) {
                    if (response.isSuccessful){
                        val dg = Deelgebied.parse(response.body()?.taak?:"X") ?: Deelgebied.Xray
                        preference.setIcon(HunterInfo.getAssociatedDrawable(value, dg.name))
                    }else{
                        preference.setIcon(HunterInfo.getAssociatedDrawable(value, Deelgebied.Xray.name))
                    }
                }

            })
            true
        }
    }
    companion object {

        val TAG = "JappPreferenceFragment"
    }

}
