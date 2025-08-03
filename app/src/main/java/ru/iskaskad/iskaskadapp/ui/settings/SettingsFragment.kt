package ru.iskaskad.iskaskadapp.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import ru.iskaskad.iskaskadapp.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}